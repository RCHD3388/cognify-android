package com.cognifyteam.cognifyapp.ui.course

import android.net.Uri // Import Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult // For image picking
import androidx.activity.result.contract.ActivityResultContracts // For image picking
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource // Tetap diperlukan untuk placeholder jika tidak ada gambar
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.cognifyteam.cognifyapp.data.AppContainer // Pastikan ini sesuai dengan struktur proyek Anda
import java.util.*
import coil.compose.AsyncImage // Import Coil's AsyncImage
import com.cognifyteam.cognifyapp.data.models.Course
import com.squareup.moshi.Json

// Data class for Course


// Form validation state
data class CourseFormState(
    val courseName: String = "",
    val courseDescription: String = "",
    val category: String = "",
    val price: String = "",
    val thumbnailUri: Uri? = null,
    val isLoading: Boolean = false,
    val errors: Map<String, String> = emptyMap()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCourseScreen(navController: NavController, appContainer: AppContainer) {
    var formState by remember { mutableStateOf(CourseFormState()) }
    var showCategoryDropdown by remember { mutableStateOf(false) }
    var showDifficultyDropdown by remember { mutableStateOf(false) } // Perbaikan: dari mutableSetOf menjadi mutableStateOf
    var showDurationDropdown by remember { mutableStateOf(false) }

    // Launcher untuk memilih gambar dari galeri
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent() // Contract untuk mendapatkan konten (gambar)
    ) { uri: Uri? ->
        // Ketika gambar berhasil dipilih, URI akan diterima di sini
        if (uri != null) {
            formState = formState.copy(thumbnailUri = uri)
        }
    }

    val categories = listOf(
        "Programming", "Design", "Business", "Marketing",
        "Photography", "Music", "Language", "Health & Fitness", "Other"
    )

    val difficultyLevels = listOf("Beginner", "Intermediate", "Advanced")
    val durations = listOf("1-2 hours", "3-5 hours", "6-10 hours", "10+ hours")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // Header
        TopAppBar(
            title = {
                Text(
                    "Create Course",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.navigate("home") }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White
            )
        )

        // Form Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Course Thumbnail Section - Sekarang menerima URI gambar
            CourseThumbnailSection(
                selectedImageUri = formState.thumbnailUri, // Meneruskan URI gambar
                onThumbnailClick = {
                    imagePickerLauncher.launch("image/*") // Meluncurkan image picker
                }
            )

            // Course Name
            OutlinedTextField(
                value = formState.courseName,
                onValueChange = {
                    formState = formState.copy(
                        courseName = it,
                        errors = formState.errors - "courseName"
                    )
                },
                label = { Text("Course Name *") },
                placeholder = { Text("Enter course name") },
                modifier = Modifier.fillMaxWidth(),
                isError = formState.errors.containsKey("courseName"),
                supportingText = {
                    formState.errors["courseName"]?.let { error ->
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4A90E2),
                    unfocusedBorderColor = Color(0xFFE0E0E0)
                )
            )

            // Course Description
            OutlinedTextField(
                value = formState.courseDescription,
                onValueChange = {
                    formState = formState.copy(
                        courseDescription = it,
                        errors = formState.errors - "courseDescription"
                    )
                },
                label = { Text("Course Description *") },
                placeholder = { Text("Describe what students will learn in this course") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                minLines = 4,
                maxLines = 6,
                isError = formState.errors.containsKey("courseDescription"),
                supportingText = {
                    formState.errors["courseDescription"]?.let { error ->
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4A90E2),
                    unfocusedBorderColor = Color(0xFFE0E0E0)
                )
            )

            // Category Dropdown
            DropdownSection(
                label = "Category *",
                selectedValue = formState.category,
                placeholder = "Select category",
                options = categories,
                expanded = showCategoryDropdown,
                onExpandedChange = { showCategoryDropdown = it },
                onOptionSelected = {
                    formState = formState.copy(
                        category = it,
                        errors = formState.errors - "category"
                    )
                    showCategoryDropdown = false
                },
                isError = formState.errors.containsKey("category"),
                errorMessage = formState.errors["category"]
            )





            // Price
            OutlinedTextField(
                value = formState.price,
                onValueChange = {
                    // Hanya izinkan angka dan titik desimal
                    if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                        formState = formState.copy(
                            price = it,
                            errors = formState.errors - "price"
                        )
                    }
                },
                label = { Text("Price (USD) *") },
                placeholder = { Text("0.00") },
                leadingIcon = { Text("$", modifier = Modifier.padding(start = 12.dp)) },
                modifier = Modifier.fillMaxWidth(),
                isError = formState.errors.containsKey("price"),
                supportingText = {
                    formState.errors["price"]?.let { error ->
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error
                        )
                    } ?: Text("Set to 0 for free course")
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4A90E2),
                    unfocusedBorderColor = Color(0xFFE0E0E0)
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Create Course Button
            Button(
                onClick = {
                    val errors = validateForm(formState)
                    if (errors.isEmpty()) {
                        // Handle course creation
                        formState = formState.copy(isLoading = true)
                        // TODO: Implementasi logika pengunggahan thumbnailUri ke backend
                        // Setelah berhasil diupload ke storage (misalnya Firebase Storage),
                        // Anda akan mendapatkan URL gambar dan menyimpannya di objek Course.
                        // Contoh placeholder:
                        /*
                        if (formState.thumbnailUri != null) {
                            // Misalnya, Anda memiliki fungsi di appContainer untuk mengunggah gambar
                            // appContainer.storageRepository.uploadImage(formState.thumbnailUri) { thumbnailUrl ->
                            //     val newCourse = createCourse(formState, "someUserId") // Ganti "someUserId" dengan ID pengguna sebenarnya
                            //     appContainer.courseRepository.addCourse(newCourse.copy(thumbnailUrl = thumbnailUrl))
                            //     formState = formState.copy(isLoading = false) // Berhenti loading setelah selesai
                            // }
                        } else {
                            // Lanjutkan tanpa thumbnail jika tidak dipilih
                            val newCourse = createCourse(formState, "someUserId")
                            // appContainer.courseRepository.addCourse(newCourse)
                            formState = formState.copy(isLoading = false)
                        }
                        */
                    } else {
                        formState = formState.copy(errors = errors)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !formState.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4A90E2)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (formState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "Create Course",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Draft Button
            OutlinedButton(
                onClick = {
                    // Handle save as draft
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !formState.isLoading,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF4A90E2)
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 1.dp
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "Save as Draft",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun CourseThumbnailSection(
    selectedImageUri: Uri?, // Sekarang menerima URI gambar
    onThumbnailClick: () -> Unit
) {
    Column {
        Text(
            text = "Course Thumbnail",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(
                    width = 2.dp,
                    color = if (selectedImageUri != null) Color(0xFF4A90E2) else Color(0xFFE0E0E0),
                    shape = RoundedCornerShape(12.dp)
                )
                .background(Color(0xFFF5F5F5))
                .clickable { onThumbnailClick() },
            contentAlignment = Alignment.Center
        ) {
            if (selectedImageUri != null) {
                // Tampilkan gambar yang dipilih menggunakan Coil's AsyncImage
                AsyncImage(
                    model = selectedImageUri, // Menggunakan URI sebagai model gambar
                    contentDescription = "Course thumbnail",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop // Sesuaikan skala gambar
                )
            } else {
                // Tampilkan placeholder upload
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add thumbnail",
                        modifier = Modifier.size(48.dp),
                        tint = Color(0xFF9E9E9E)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Add Course Thumbnail",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF6B6B6B),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "Recommended: 1280x720 pixels",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF9E9E9E),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSection(
    label: String,
    selectedValue: String,
    placeholder: String,
    options: List<String>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onOptionSelected: (String) -> Unit,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    Column {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = onExpandedChange
        ) {
            OutlinedTextField(
                value = selectedValue,
                onValueChange = { },
                readOnly = true,
                label = { Text(label) },
                placeholder = { Text(placeholder) },
                trailingIcon = {
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                isError = isError,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4A90E2),
                    unfocusedBorderColor = Color(0xFFE0E0E0)
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = { onOptionSelected(option) }
                    )
                }
            }
        }

        errorMessage?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

// Form validation function
fun validateForm(formState: CourseFormState): Map<String, String> {
    val errors = mutableMapOf<String, String>()

    if (formState.courseName.isBlank()) {
        errors["courseName"] = "Course name is required"
    } else if (formState.courseName.length < 3) {
        errors["courseName"] = "Course name must be at least 3 characters"
    }

    if (formState.courseDescription.isBlank()) {
        errors["courseDescription"] = "Course description is required"
    } else if (formState.courseDescription.length < 20) {
        errors["courseDescription"] = "Description must be at least 20 characters"
    }

    if (formState.category.isBlank()) {
        errors["category"] = "Please select a category"
    }



    if (formState.price.isBlank()) {
        errors["price"] = "Price is required"
    } else {
        try {
            val priceValue = formState.price.toDouble()
            if (priceValue < 0) {
                errors["price"] = "Price cannot be negative"
            }
        } catch (e: NumberFormatException) {
            errors["price"] = "Please enter a valid price"
        }
    }

    // Validasi untuk thumbnail - opsional, tergantung apakah thumbnail wajib
    // if (formState.thumbnailUri == null) {
    //     errors["thumbnail"] = "Course thumbnail is required"
    // }

    return errors
}

// Function to create course object
fun createCourse(formState: CourseFormState, userId: String): Course {
    Log.d("CourseFormState", "createCourse: $formState")
    return Course(
        courseId = "AWKOKOAW",
        name = formState.courseName,
        description = formState.courseDescription,
        rating = "0.0",
        price = formState.price.toString().toInt(),
        thumbnail = ""
    )
}
