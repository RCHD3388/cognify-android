package com.cognifyteam.cognifyapp.data.sources.local.dao

import androidx.room.*
import com.cognifyteam.cognifyapp.data.models.CourseEntity
import com.cognifyteam.cognifyapp.data.models.UserCourseCrossRef
import com.cognifyteam.cognifyapp.data.models.UserWithCourses

@Dao
interface CourseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCourses(courses: List<CourseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserCourseCrossRefs(crossRefs: List<UserCourseCrossRef>)

    @Transaction
    @Query("SELECT * FROM users WHERE firebaseId = :firebaseId")
    suspend fun getUserWithCourses(firebaseId: String): UserWithCourses?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createCourse(course: CourseEntity): Long

    @Query("SELECT * FROM courses WHERE course_owner = :firebaseId")
    suspend fun getUserCreatedCourses(firebaseId: String): List<CourseEntity>


    @Query("SELECT * FROM courses WHERE courseId = :courseId")
    suspend fun getCourseById(courseId: String): CourseEntity?

    @Query("SELECT * FROM courses ORDER BY name ASC") // Urutkan berdasarkan nama
    suspend fun getAllCourses(): List<CourseEntity>
}