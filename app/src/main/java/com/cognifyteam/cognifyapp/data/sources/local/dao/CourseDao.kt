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
}