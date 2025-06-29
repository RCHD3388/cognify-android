package com.cognifyteam.cognifyapp.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.cognifyteam.cognifyapp.R
import com.cognifyteam.cognifyapp.data.AppContainer
import com.cognifyteam.cognifyapp.data.models.Course
import com.cognifyteam.cognifyapp.data.models.User
import com.cognifyteam.cognifyapp.ui.FabState
import com.cognifyteam.cognifyapp.ui.TopBarState
import com.cognifyteam.cognifyapp.ui.common.UserViewModel
import com.cognifyteam.cognifyapp.ui.course.CourseListUiState
import com.cognifyteam.cognifyapp.ui.course.CourseViewModel
import com.cognifyteam.cognifyapp.ui.navigation.AppNavRoutes
import com.cognifyteam.cognifyapp.ui.profile.UserCoursesViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    appContainer: AppContainer,
    onFabStateChange: (FabState) -> Unit,
    onTopBarStateChange: (TopBarState) -> Unit,
    onShowSnackbar: (String) -> Unit
) {
    val courseViewModel: CourseViewModel = viewModel(
        factory = CourseViewModel.provideFactory(appContainer.courseRepository)
    )
    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModel.provideFactory(appContainer.authRepository)
    )

    val userCoursesViewModel: UserCoursesViewModel = viewModel(
        factory = UserCoursesViewModel.provideFactory(appContainer.courseRepository)
    )

    // Amati semua state yang dibutuhkan
    val recentCoursesState by courseViewModel.recentCoursesState.collectAsState()
    val highestRatedCoursesState by courseViewModel.highestRatedCoursesState.collectAsState()
    val enrolledCoursesState by userCoursesViewModel.uiState.collectAsState()
    val user by userViewModel.userState.collectAsState()

    // Muat semua data saat layar pertama kali dibuat
    LaunchedEffect(key1 = Unit) {
        onFabStateChange(FabState(isVisible = false))
        onTopBarStateChange(TopBarState(isVisible = false))
        courseViewModel.loadAllHomeCourses()
        userCoursesViewModel.initialize(user!!.firebaseId)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        item { HeaderSection(navController, user) }
//        item { SearchBar(navController) }
        item { CategoriesSection() }

        item {
            CourseRowSection(
                title = "Continue Learning", // atau "Recent Courses"
                uiState = enrolledCoursesState,
                navController = navController,
                cardType = CardType.Progress,
                emptyMessage = "No courses available"
            )
        }
        item {
            CourseRowSection(
                title = "Recent Created Courses", // atau "Bestseller"
                uiState = recentCoursesState,
                navController = navController,
                cardType = CardType.Popular,
                emptyMessage = "No courses available"
            )
        }
        item {
            CourseRowSection(
                title = "Highest Rated Courses",
                uiState = highestRatedCoursesState,
                navController = navController,
                cardType = CardType.Progress, // Bisa pakai card yang sama
                emptyMessage = "No courses available"
            )
        }
        item {
            SeeAllCoursesButton(
                onClick = {
                    // Navigate to all courses screen
                    navController.navigate("allcourse") // Sesuaikan dengan route yang ada
                }
            )
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

// Enum untuk membedakan jenis kartu yang akan ditampilkan
enum class CardType {
    Progress, Popular
}

// Composable generik untuk menampilkan baris kursus
@Composable
fun CourseRowSection(
    title: String,
    uiState: CourseListUiState,
    navController: NavController,
    cardType: CardType,
    emptyMessage: String
) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        SectionHeader(
            title = title,
            onSeeAllClick = { /* TODO: Navigasi ke halaman 'see all' untuk kategori ini */ },
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        when (uiState) {
            is CourseListUiState.Loading -> {
                Box(modifier = Modifier.fillMaxWidth().height(240.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is CourseListUiState.Error -> {
                Text(
                    text = uiState.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                )
            }
            is CourseListUiState.Success -> {
                if (uiState.courses.isEmpty()) {
                    // Tampilkan pesan jika daftar kursus kosong
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .padding(horizontal = 20.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = emptyMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    // Tampilkan LazyRow jika ada data
                    LazyRow(
                        modifier = Modifier.padding(top = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp)
                    ) {
                        items(uiState.courses, key = { it.courseId }) { course ->
                            when (cardType) {
                                CardType.Progress -> CourseCard(
                                    course = course,
                                    onClick = { navController.navigate("course_details/${course.courseId}") }
                                )
                                CardType.Popular -> PopularCourseCard(
                                    course = course,
                                    onClick = { navController.navigate("course_details/${course.courseId}") }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


// --- SEMUA COMPONENT LAIN ---

@Composable
fun HeaderSection(navController: NavController, user: User?) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("Welcome Back! 👋", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Normal)
            Text(user?.name ?: "...", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Course",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp).clickable { navController.navigate("course") }
        )
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Search",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp).clickable { navController.navigate("search") }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .height(56.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .clickable { navController.navigate(AppNavRoutes.SEARCH) }
            .padding(horizontal = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxSize()) {
            Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Search courses or users...", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun CategoriesSection() {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
        SectionHeader(title = "Categories", onSeeAllClick = {})
        LazyRow(modifier = Modifier.padding(top = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            // Data statis, bisa diganti dengan data dari API nanti
            val categories = listOf("UI Design" to Icons.Outlined.Search, "Health" to Icons.Outlined.Search, "Psychology" to Icons.Outlined.Search, "Business" to Icons.Outlined.Search)
            val colors = listOf(Color(0xFF6366F1), Color(0xFF10B981), Color(0xFFF59E0B), Color(0xFFEF4444))
            items(categories.size) { index ->
                val (name, icon) = categories[index]
                CategoryItem(icon = icon, categoryName = name, backgroundColor = colors[index])
            }
        }
    }
}

@Composable
fun CategoryItem(icon: ImageVector, categoryName: String, backgroundColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(80.dp).clickable { }) {
        Box(
            modifier = Modifier.size(64.dp).clip(RoundedCornerShape(16.dp)).background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = categoryName, tint = Color.White, modifier = Modifier.size(28.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(categoryName, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

// Modifikasi CourseCard untuk menerima objek Course
@Composable
fun CourseCard(course: Course, onClick: () -> Unit) {
    Card(
        modifier = Modifier.width(200.dp).height(240.dp).clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            AsyncImage(
                model = "http://10.0.2.2:3000${course.thumbnail}",
                contentDescription = course.name,
                modifier = Modifier.fillMaxWidth().height(120.dp),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.robot),
                error = painterResource(id = R.drawable.robot)
            )
            Column(modifier = Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(course.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, maxLines = 2, overflow = TextOverflow.Ellipsis, lineHeight = 18.sp)
                    Text(course.course_owner_name, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp), maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Column {
                    Text("${(50 * 100).toInt()}% Complete", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium, modifier = Modifier.align(Alignment.End).padding(bottom = 4.dp))
//                    LinearProgressIndicator(progress = course.progress, modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.primary, trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                }
            }
        }
    }
}

// Modifikasi PopularCourseCard untuk menerima objek Course
@Composable
fun PopularCourseCard(course: Course, onClick: () -> Unit) {
    Card(
        modifier = Modifier.width(180.dp).height(280.dp).clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            AsyncImage(
                model = "http://10.0.2.2:3000${course.thumbnail}",
                contentDescription = course.name,
                modifier = Modifier.fillMaxWidth().height(120.dp),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.robot),
                error = painterResource(id = R.drawable.robot)
            )
            Column(modifier = Modifier.padding(12.dp).fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(course.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(course.course_owner_name, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 2.dp), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(course.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 3, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 6.dp), lineHeight = 14.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Rp${course.price}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("⭐ ${course.rating}", fontSize = 12.sp, color = Color(0xFFF59E0B), fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun SeeAllCoursesButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
        ) {
            Text(
                text = "See All Courses",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun SectionHeader(title: String, onSeeAllClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}