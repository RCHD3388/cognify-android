package com.cognifyteam.cognifyapp.data.sources.local.dao

import androidx.room.*
import com.cognifyteam.cognifyapp.data.models.DiscussionEntity

@Dao
interface DiscussionDao {
    // Upsert untuk list of discussions
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDiscussions(discussions: List<DiscussionEntity>)

    // Query untuk mengambil semua post utama di sebuah kursus
    @Query("SELECT * FROM discussions WHERE courseId = :courseId AND parentId IS NULL ORDER BY createdAt DESC")
    suspend fun getMainDiscussionsByCourse(courseId: String): List<DiscussionEntity>

    // Query untuk mengambil semua balasan untuk sebuah post
    @Query("SELECT * FROM discussions WHERE parentId = :parentId ORDER BY createdAt ASC")
    suspend fun getRepliesForDiscussion(parentId: Int): List<DiscussionEntity>

    @Query("SELECT * FROM discussions WHERE id = :discussionId LIMIT 1")
    suspend fun getDiscussionById(discussionId: Int): DiscussionEntity?
}