package com.cognifyteam.cognifyapp.ui.course.addcourse



import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

// State untuk sebuah material di dalam UI
data class CourseFormState(
    val course_name: String = "",
    val course_description: String = "",
    val category: String = "",
    val price: String = "",
    val thumbnail_uri: Uri? = null,
    val errors: Map<String, String> = emptyMap()
)

data class MaterialState(
    var title: String = "",
    var description: String = "",
    var material_type: String = "document", // Default type
    var fileUri: Uri? = null
)

data class SectionState(
    var title: String = "",
    var description: String = "",
    val materials: SnapshotStateList<MaterialState> = mutableStateListOf()
)
