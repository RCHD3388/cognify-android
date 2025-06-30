package com.cognifyteam.cognifyapp.data.sources.local.datasources

import android.util.Log
import com.cognifyteam.cognifyapp.data.models.CourseEntity
import com.cognifyteam.cognifyapp.data.models.MaterialEntity
import com.cognifyteam.cognifyapp.data.models.SectionEntity
import com.cognifyteam.cognifyapp.data.models.UserCourseCrossRef
import com.cognifyteam.cognifyapp.data.models.UserWithCourses
import com.cognifyteam.cognifyapp.data.sources.local.dao.CourseDao
import com.cognifyteam.cognifyapp.data.sources.local.dao.MaterialDao
import com.cognifyteam.cognifyapp.data.sources.local.dao.SectionDao

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

    //create course
    suspend fun createCourse(course: CourseEntity): Long

    //get user created courses
    suspend fun getUserCreatedCourses(firebaseId: String): List<CourseEntity>

    suspend fun insertSections(sections: List<SectionEntity>)

    suspend fun getSectionsForCourse(courseId: String): List<SectionEntity>

    suspend fun getMaterialsForSection(sectionId: String): List<MaterialEntity>

    suspend fun insertMaterials(materials: List<MaterialEntity>)

    suspend fun getCourseById(courseId: String): CourseEntity?

    suspend fun getAllCourses(): List<CourseEntity>

    suspend fun isUserEnrolledInCourse(firebaseId: String, courseId: String): Boolean
}

class LocalCourseDataSourceImpl(
    private val courseDao: CourseDao,
    private val SectionDao: SectionDao,
    private val MaterialDao: MaterialDao

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

    override suspend fun createCourse(course: CourseEntity): Long {
        return courseDao.createCourse(course)
    }

    override suspend fun getUserCreatedCourses(firebaseId: String): List<CourseEntity> {
        return courseDao.getUserCreatedCourses(firebaseId)
    }

    override suspend fun insertSections(sections: List<SectionEntity>) {
        return SectionDao.insertAll(sections)
    }

    override suspend fun getSectionsForCourse(courseId: String): List<SectionEntity> {
        return SectionDao.getSectionsForCourse(courseId)
    }
    override suspend fun getMaterialsForSection(sectionId: String): List<MaterialEntity> {
        return MaterialDao.getMaterialsForSection(sectionId)
    }
    override suspend fun insertMaterials(materials: List<MaterialEntity>) {
        return MaterialDao.insertAll(materials)
    }

    override suspend fun getCourseById(courseId: String): CourseEntity? {
        return courseDao.getCourseById(courseId)
    }

    override suspend fun getAllCourses(): List<CourseEntity> {
        return courseDao.getAllCourses()
    }

    override suspend fun isUserEnrolledInCourse(firebaseId: String, courseId: String): Boolean {
        return courseDao.isUserEnrolledInCourse(firebaseId, courseId)
    }
}