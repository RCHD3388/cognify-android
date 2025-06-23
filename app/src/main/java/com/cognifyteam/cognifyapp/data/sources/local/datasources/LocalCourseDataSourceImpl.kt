package com.cognifyteam.cognifyapp.data.sources.local.datasources

import com.cognifyteam.cognifyapp.data.models.CourseEntity
import com.cognifyteam.cognifyapp.data.models.UserCourseCrossRef
import com.cognifyteam.cognifyapp.data.models.UserWithCourses
import com.cognifyteam.cognifyapp.data.sources.local.dao.CourseDao

interface LocalCourseDataSource {
    /**
     * Menyimpan atau memperbarui daftar kursus di database.
     */
    suspend fun upsertCourses(courses: List<CourseEntity>)

    /**
     * Menyimpan relasi antara user dan kursus.
     */
    suspend fun insertUserCourseCrossRefs(crossRefs: List<UserCourseCrossRef>)

    /**
     * Mengambil data user beserta semua kursus yang diikutinya dari database.
     */
    suspend fun getUserWithCourses(firebaseId: String): UserWithCourses?
}

class LocalCourseDataSourceImpl(
    private val courseDao: CourseDao
) : LocalCourseDataSource {

    override suspend fun upsertCourses(courses: List<CourseEntity>) {
        courseDao.upsertCourses(courses)
    }

    override suspend fun insertUserCourseCrossRefs(crossRefs: List<UserCourseCrossRef>) {
        courseDao.insertUserCourseCrossRefs(crossRefs)
    }

    override suspend fun getUserWithCourses(firebaseId: String): UserWithCourses? {
        return courseDao.getUserWithCourses(firebaseId)
    }
}