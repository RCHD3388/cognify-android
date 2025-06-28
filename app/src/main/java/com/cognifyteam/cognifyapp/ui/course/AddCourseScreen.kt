package com.cognifyteam.cognifyapp.ui.course

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.cognifyteam.cognifyapp.data.AppContainer

import com.cognifyteam.cognifyapp.ui.course.addcourse.SectionsManager
import java.io.File
import java.io.FileOutputStream


data class CourseFormState(
    val course_name: String = "",
    val course_description: String = "",
    val category: String = "",
    val price: String = "",
    val thumbnail_uri: Uri? = null,
    val errors: Map<String, String> = emptyMap()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCourseScreen(
    navController: NavController,
    appContainer: AppContainer
) {
    val viewModel: CourseViewModel = viewModel(
        factory = CourseViewModel.provideFactory(appContainer.courseRepository)
    )

    val loggedInUser by appContainer.authRepository.loggedInUser.collectAsState(initial = null)
    var formState by remember { mutableStateOf(CourseFormState()) }
    val createCourseState by viewModel.createCourseState.collectAsState()
    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            formState = formState.copy(thumbnail_uri = uri, errors = formState.errors - "thumbnail_uri")
        }
    }

    // Mengambil state sections dari ViewModel
    val sections by viewModel.sections.collectAsState()

    LaunchedEffect(key1 = createCourseState) {
        when (val state = createCourseState) {
            is CourseViewModel.CreateCourseState.Success -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                navController.popBackStack()
                viewModel.resetCreateCourseState()
            }
            is CourseViewModel.CreateCourseState.Error -> {
                Toast.makeText(context, "Error: ${state.message}", Toast.LENGTH_LONG).show()
                viewModel.resetCreateCourseState()
            }
            else -> { /* Do nothing for Idle or Loading */ }
        }
    }

    val categories = listOf(
        "Programming", "Design", "Business", "Marketing",
        "Photography", "Music", "Language", "Health & Fitness", "Other"
    )
    val isLoading = createCourseState is CourseViewModel.CreateCourseState.Loading

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FA))) {
        TopAppBar(
            title = { Text("Create Course", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Medium) },
            navigationIcon = {
                IconButton(onClick = { if (!isLoading) navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- UI UNTUK DETAIL COURSE ---
            CourseThumbnailSection(
                selectedImageUri = formState.thumbnail_uri,
                isError = formState.errors.containsKey("thumbnail_uri"),
                onThumbnailClick = { if (!isLoading) imagePickerLauncher.launch("image/*") }
            )
            formState.errors["thumbnail_uri"]?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            OutlinedTextField(
                value = formState.course_name,
                onValueChange = { formState = formState.copy(course_name = it, errors = formState.errors - "course_name") },
                label = { Text("Course Name *") },
                modifier = Modifier.fillMaxWidth(),
                isError = formState.errors.containsKey("course_name"),
                readOnly = isLoading,
                supportingText = { formState.errors["course_name"]?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
            )

            OutlinedTextField(
                value = formState.course_description,
                onValueChange = { formState = formState.copy(course_description = it, errors = formState.errors - "course_description") },
                label = { Text("Course Description *") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                isError = formState.errors.containsKey("course_description"),
                readOnly = isLoading,
                supportingText = { formState.errors["course_description"]?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
            )

            var showCategoryDropdown by remember { mutableStateOf(false) }
            DropdownSection(
                label = "Category *",
                selectedValue = formState.category,
                options = categories,
                expanded = showCategoryDropdown,
                onExpandedChange = { if (!isLoading) showCategoryDropdown = it },
                onOptionSelected = {
                    formState = formState.copy(category = it, errors = formState.errors - "category")
                    showCategoryDropdown = false
                },
                isError = formState.errors.containsKey("category"),
                errorMessage = formState.errors["category"]
            )

            OutlinedTextField(
                value = formState.price,
                onValueChange = { if (it.isEmpty() || it.all { char -> char.isDigit() }) formState = formState.copy(price = it, errors = formState.errors - "price") },
                label = { Text("Price *") },
                modifier = Modifier.fillMaxWidth(),
                isError = formState.errors.containsKey("price"),
                readOnly = isLoading,
                supportingText = { formState.errors["price"]?.let { Text(it, color = MaterialTheme.colorScheme.error) } ?: Text("Set to 0 for free course") }
            )

            // --- UI BARU UNTUK MENGELOLA SECTIONS ---
            Divider(modifier = Modifier.padding(vertical = 16.dp))
            SectionsManager(
                sections = sections,
                onAddSection = viewModel::addSection,
                onRemoveSection = viewModel::removeSection,
                onAddMaterial = viewModel::addMaterialToSection,
                onRemoveMaterial = viewModel::removeMaterialFromSection,
                onUpdateMaterial = viewModel::updateMaterialInSection
            )
            // --- AKHIR DARI UI BARU ---

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val errors = validateForm(formState)
                    if (errors.isEmpty()) {
                        val thumbnailFile = formState.thumbnail_uri?.let { uriToFile(context, it) }
                        val currentUserId = loggedInUser?.firebaseId

                        if (currentUserId == null || thumbnailFile == null) {
                            Toast.makeText(context, "User or thumbnail is missing.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val courseName = formState.course_name.trim()
                        val courseDescription = formState.course_description.trim()
                        val coursePrice = formState.price.toIntOrNull() ?: 0
                        val categoryId = (categories.indexOf(formState.category) + 1).toString()

                        // PANGGIL FUNGSI BARU DI VIEWMODEL UNTUK MENGIRIM SEMUA DATA
                        viewModel.createCourseWithContents(
                            course_name = courseName,
                            course_description = courseDescription,
                            course_owner = currentUserId,
                            course_price = coursePrice,
                            category_id = categoryId,
                            thumbnailFile = thumbnailFile
                        )

                    } else {
                        formState = formState.copy(errors = errors)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2)),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Create Course & Content", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Composable
fun CourseThumbnailSection(
    selectedImageUri: Uri?,
    onThumbnailClick: () -> Unit,
    isError: Boolean = false
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
                    color = if (isError) MaterialTheme.colorScheme.error else if (selectedImageUri != null) Color(0xFF4A90E2) else Color(0xFFE0E0E0),
                    shape = RoundedCornerShape(12.dp)
                )
                .background(Color(0xFFF5F5F5))
                .clickable { onThumbnailClick() },
            contentAlignment = Alignment.Center
        ) {
            if (selectedImageUri != null) {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "Course thumbnail",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add thumbnail",
                        modifier = Modifier.size(48.dp),
                        tint = if (isError) MaterialTheme.colorScheme.error else Color(0xFF9E9E9E)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Add Course Thumbnail",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isError) MaterialTheme.colorScheme.error else Color(0xFF6B6B6B),
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
                placeholder = { Text("Select category") },
                trailingIcon = { Icon(Icons.Default.ArrowDropDown, "Dropdown") },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                isError = isError
            )

            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { onExpandedChange(false) }) {
                options.forEach { option ->
                    DropdownMenuItem(text = { Text(option) }, onClick = { onOptionSelected(option) })
                }
            }
        }
        errorMessage?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 16.dp, top = 4.dp))
        }
    }
}

private fun uriToFile(context: Context, uri: Uri): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "temp_thumbnail_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        file
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun validateForm(formState: CourseFormState): Map<String, String> {
    val errors = mutableMapOf<String, String>()

    if (formState.course_name.isBlank()) errors["course_name"] = "Course name is required"
    if (formState.course_description.isBlank()) errors["course_description"] = "Course description is required"
    if (formState.category.isBlank()) errors["category"] = "Please select a category"
    if (formState.price.isBlank()) errors["price"] = "Price is required"
    if (formState.thumbnail_uri == null) {
        errors["thumbnail_uri"] = "Course thumbnail is required"
    }

    return errors
}




