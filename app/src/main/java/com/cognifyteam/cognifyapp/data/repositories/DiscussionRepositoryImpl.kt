package com.cognifyteam.cognifyapp.data.repositories

import com.cognifyteam.cognifyapp.data.models.Discussion
import com.cognifyteam.cognifyapp.data.sources.local.datasources.LocalDiscussionDataSource
import com.cognifyteam.cognifyapp.data.sources.remote.CreatePostRequest
import com.cognifyteam.cognifyapp.data.sources.remote.CreateReplyRequest
import com.cognifyteam.cognifyapp.data.sources.remote.course.RemoteDiscussionDataSource

interface DiscussionRepository {
    suspend fun getDiscussionsForCourse(courseId: String): Result<List<Discussion>>
    suspend fun createPost(firebaseId: String, courseId: String, content: String): Result<Discussion>
    suspend fun createReply(parentId: Int, firebaseId: String, content: String): Result<Discussion>
}

class DiscussionRepositoryImpl(
    private val localDataSource: LocalDiscussionDataSource,
    private val remoteDataSource: RemoteDiscussionDataSource
) : DiscussionRepository {

    override suspend fun getDiscussionsForCourse(courseId: String): Result<List<Discussion>> {
        try {
            // 1. Ambil dari network
            val response = remoteDataSource.getDiscussionsForCourse(courseId)
            val discussionJsons = response.data.discussions

            // 2. Mapping dari JSON ke Domain Model (termasuk balasan rekursif)
            val discussions = discussionJsons.map { Discussion.fromJson(it) }

            // 3. Simpan ke database
            if (discussions.isNotEmpty()) {
                // Flatten list (menggabungkan post utama dan semua balasannya) untuk disimpan
                val allEntitiesToSave = discussions.flatMap { it.toEntity(courseId) }
                localDataSource.upsertDiscussions(allEntitiesToSave)
            }

            return Result.success(discussions)

        } catch (e: Exception) {
            // Jika network gagal, ambil dari cache
            return try {
                // Ambil post utama dari DB
                val mainDiscussionEntities = localDataSource.getMainDiscussionsByCourse(courseId)
                if (mainDiscussionEntities.isEmpty()) return Result.failure(Exception("No cached discussions"))

                // Untuk setiap post utama, ambil balasannya dari DB
                val discussionsWithReplies = mainDiscussionEntities.map { mainEntity ->
                    val replyEntities = localDataSource.getRepliesForDiscussion(mainEntity.id)
                    // Gabungkan menjadi Domain Model yang lengkap
                    Discussion(
                        id = mainEntity.id,
                        content = mainEntity.content,
                        createdAt = mainEntity.createdAt,
                        authorId = mainEntity.userId,
                        authorName = "Unknown (cache)", // Kita tidak simpan nama di sini, bisa diperbaiki dengan JOIN
                        replies = replyEntities.map { replyEntity ->
                            Discussion(
                                id = replyEntity.id,
                                content = replyEntity.content,
                                createdAt = replyEntity.createdAt,
                                authorId = replyEntity.userId,
                                authorName = "Unknown (cache)",
                                replies = emptyList() // Balasan tidak punya balasan lagi
                            )
                        }
                    )
                }
                Result.success(discussionsWithReplies)
            } catch (cacheError: Exception) {
                Result.failure(cacheError)
            }
        }
    }

    override suspend fun createPost(firebaseId: String, courseId: String, content: String): Result<Discussion> {
        return try {
            val request = CreatePostRequest(content = content, courseId = courseId)
            val response = remoteDataSource.createPost(firebaseId, request)
            val newPost = Discussion.fromJson(response.data)

            // Simpan ke cache lokal untuk konsistensi
            localDataSource.upsertDiscussions(newPost.toEntity(courseId))
            Result.success(newPost)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createReply(parentId: Int, firebaseId: String, content: String): Result<Discussion> {
        return try {
            // 1. Buat request dan panggil API
            val request = CreateReplyRequest(content = content)
            val response = remoteDataSource.createReply(parentId, firebaseId, request)

            // 2. Mapping hasil dari API menjadi Domain Model
            val newReply = Discussion.fromJson(response.data)

            // 3. Simpan balasan baru ke database lokal dengan aman
            // Dapatkan courseId dari post induk di cache.
            val parentPostEntity = localDataSource.getDiscussionById(parentId)

            if (parentPostEntity != null) {
                // Jika post induk ada di cache, kita punya courseId yang benar.
                // Gunakan mapper toReplyEntity yang baru dan lebih aman.
                val replyEntity = newReply.toReplyEntity(
                    courseId = parentPostEntity.courseId,
                    parentId = parentId
                )

                // upsertDiscussions menerima list, jadi kita bungkus dalam listOf()
                localDataSource.upsertDiscussions(listOf(replyEntity))
            } else {
                // Fallback: Jika post induk tidak ada di cache, kita tidak bisa menyimpan
                // balasan ini dengan benar. Kita bisa log error ini.
                // UI akan tetap menampilkan balasan baru, tetapi tidak akan tersimpan di cache.
                println("Warning: Parent post with id $parentId not found in cache. Reply was not cached.")
            }

            // 4. Selalu kembalikan hasil sukses dengan data dari API
            Result.success(newReply)

        } catch (e: Exception) {
            // 5. Jika terjadi error jaringan, kembalikan failure
            Result.failure(e)
        }
    }
}