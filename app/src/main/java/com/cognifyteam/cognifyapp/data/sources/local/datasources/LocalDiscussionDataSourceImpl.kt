package com.cognifyteam.cognifyapp.data.sources.local.datasources

import com.cognifyteam.cognifyapp.data.models.DiscussionEntity
import com.cognifyteam.cognifyapp.data.sources.local.dao.DiscussionDao

interface LocalDiscussionDataSource {
    /**
     * Menyimpan atau memperbarui daftar diskusi di database.
     */
    suspend fun upsertDiscussions(discussions: List<DiscussionEntity>)

    /**
     * Mengambil semua post utama (bukan balasan) untuk sebuah kursus dari database.
     */
    suspend fun getMainDiscussionsByCourse(courseId: String): List<DiscussionEntity>

    /**
     * Mengambil semua balasan untuk sebuah post diskusi utama.
     */
    suspend fun getRepliesForDiscussion(parentId: Int): List<DiscussionEntity>

    suspend fun getDiscussionById(discussionId: Int): DiscussionEntity?
}

class LocalDiscussionDataSourceImpl(
    private val discussionDao: DiscussionDao
) : LocalDiscussionDataSource {

    override suspend fun upsertDiscussions(discussions: List<DiscussionEntity>) {
        discussionDao.upsertDiscussions(discussions)
    }

    override suspend fun getMainDiscussionsByCourse(courseId: String): List<DiscussionEntity> {
        return discussionDao.getMainDiscussionsByCourse(courseId)
    }

    override suspend fun getRepliesForDiscussion(parentId: Int): List<DiscussionEntity> {
        return discussionDao.getRepliesForDiscussion(parentId)
    }

    override suspend fun getDiscussionById(discussionId: Int): DiscussionEntity? {
        return discussionDao.getDiscussionById(discussionId)
    }


}