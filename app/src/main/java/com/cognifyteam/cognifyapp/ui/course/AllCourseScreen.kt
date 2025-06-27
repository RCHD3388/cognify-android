package com.cognifyteam.cognifyapp.ui.course

import com.cognifyteam.cognifyapp.ui.profile.CourseCard
import com.cognifyteam.cognifyapp.ui.theme.CognifyApplicationTheme

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Data class untuk Course
data class Course(
    val id: String,
    val title: String,
    val author: String,
    val rating: Float,
    val progress: Int,
    val imageUrl: String
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalAnimationApi::class
)
@Composable
fun AllCoursesScreen(
    onBackClick: () -> Unit,
    onCourseClick: (Course) -> Unit,
    modifier: Modifier = Modifier
) {
    // Sample data - dalam implementasi nyata, ini akan datang dari ViewModel/Repository
    val sampleCourses = remember {
        listOf(
            Course("1", "Android Development with Kotlin", "John Doe", 4.8f, 75, "https://example.com/image1.jpg"),
            Course("2", "UI/UX Design Fundamentals", "Jane Smith", 4.6f, 45, "https://example.com/image2.jpg"),
            Course("3", "Machine Learning Basics", "Dr. Wilson", 4.9f, 90, "https://example.com/image3.jpg"),
            Course("4", "Web Development with React", "Mike Johnson", 4.7f, 60, "https://example.com/image4.jpg"),
            Course("5", "Data Science with Python", "Sarah Davis", 4.5f, 30, "https://example.com/image5.jpg"),
            Course("6", "Digital Marketing Strategy", "Alex Brown", 4.4f, 85, "https://example.com/image6.jpg"),
            Course("7", "Graphic Design Mastery", "Emma Wilson", 4.8f, 55, "https://example.com/image7.jpg"),
            Course("8", "Cloud Computing AWS", "David Lee", 4.6f, 40, "https://example.com/image8.jpg"),
            Course("9", "Cybersecurity Fundamentals", "Lisa Chen", 4.7f, 70, "https://example.com/image9.jpg"),
            Course("10", "Project Management", "Robert Taylor", 4.3f, 95, "https://example.com/image10.jpg")
        )
    }

    var searchQuery by remember { mutableStateOf("") }

    // Filter courses berdasarkan search query
    val filteredCourses = remember(searchQuery, sampleCourses) {
        if (searchQuery.isEmpty()) {
            sampleCourses
        } else {
            sampleCourses.filter { course ->
                course.title.contains(searchQuery, ignoreCase = true) ||
                        course.author.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Background gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surface
                        ),
                        startY = 0f,
                        endY = 600f
                    )
                )
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                // Enhanced TopAppBar with gradient background
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                        ),
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    TopAppBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Send,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = "All Courses",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = onBackClick,
                                modifier = Modifier
                                    .padding(4.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Back",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = { /* Handle search action */ },
                                modifier = Modifier
                                    .padding(4.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp)
                    )
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(top = 8.dp)
            ) {
                // Enhanced Search Bar
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .shadow(4.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Search courses or authors...") },
                        placeholder = { Text("Type to search...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.Transparent,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }

                // Enhanced Course count with animation
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically()
                ) {
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 20.dp, vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Send,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "${filteredCourses.size} courses available",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Enhanced Courses Grid or Empty State
                AnimatedContent(
                    targetState = filteredCourses.isEmpty(),
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) with
                                fadeOut(animationSpec = tween(300))
                    },
                    label = "courses_content"
                ) { isEmpty ->
                    if (isEmpty) {
                        // Enhanced Empty state
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Card(
                                modifier = Modifier.padding(32.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Send,
                                        contentDescription = null,
                                        modifier = Modifier.size(64.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "No courses found",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Try adjusting your search terms",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(20.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(filteredCourses) { course ->
                                // Animated course card wrapper
                                AnimatedVisibility(
                                    visible = true,
                                    enter = fadeIn() + scaleIn(initialScale = 0.8f),
                                    modifier = Modifier.animateItemPlacement()
                                ) {
                                    CourseCard(
                                        title = course.title,
                                        author = course.author,
                                        rating = course.rating,
                                        progress = course.progress,
                                        imageUrl = course.imageUrl,
                                        onCourseClick = { onCourseClick(course) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun AllCoursesScreenPreview() {
    CognifyApplicationTheme {
        AllCoursesScreen(
            onBackClick = { },
            onCourseClick = { }
        )
    }
}