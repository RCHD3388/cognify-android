package com.cognifyteam.cognifyapp.data.models

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class UserWithCourses(
    @Embedded val user: UserEntity,
    @Relation(
        parentColumn = "firebaseId",
        entityColumn = "courseId",
        associateBy = Junction(UserCourseCrossRef::class)
    )
    val courses: List<CourseEntity>
)