package com.cognifyteam.cognifyapp.ui.profile

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.cognifyteam.cognifyapp.R
import com.cognifyteam.cognifyapp.data.models.User
import com.cognifyteam.cognifyapp.ui.MainActivity
import com.cognifyteam.cognifyapp.ui.auth.AuthUiState
import com.cognifyteam.cognifyapp.ui.auth.AuthViewModel
// --- BARU: Import ViewModel dan State yang dibutuhkan ---
import com.cognifyteam.cognifyapp.ui.course.CourseUiState
import com.cognifyteam.cognifyapp.ui.course.CourseViewModel

// Theme colors consistent with other screens
private val PrimaryColor = Color(0xFF1F2343)
private val BackgroundColor = Color.White
private val TextPrimary = Color.Black
private val TextSecondary = Color.Gray
private val SurfaceColor = Color(0xFFF8F9FA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePage(
    navController: NavController,
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel,
    userCoursesViewModel: UserCoursesViewModel,
    // --- BARU: Tambahkan CourseViewModel sebagai parameter ---
    courseViewModel: CourseViewModel,
    firebaseId: String
) {
    val isLoading by profileViewModel.isLoading.observeAsState(initial = false)
    val userProfile by profileViewModel.userProfile.observeAsState()
    val error by profileViewModel.error.observeAsState()

    val enrolledCoursesState by userCoursesViewModel.uiState.collectAsState()
    // --- BARU: Ambil state untuk course yang dibuat ---
    val createdCoursesState by courseViewModel.uiState.collectAsState()

    val authState by authViewModel.uiState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState) {
        if (authState is AuthUiState.Unauthenticated) {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            context.startActivity(intent)
        }
    }

    LaunchedEffect(key1 = firebaseId) {
        profileViewModel.loadProfile(firebaseId)
        userCoursesViewModel.loadEnrolledCourses(firebaseId)
        courseViewModel.loadCreateCourses(firebaseId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Profile",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = PrimaryColor
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        authViewModel.logout()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout",
                            tint = PrimaryColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundColor
                )
            )
        },
        containerColor = BackgroundColor
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else if (error != null) {
                Text(text = error!!, color = Color.Red)
            } else if (userProfile != null) {
                // --- BARU: Teruskan state baru ke ProfileContent ---
                ProfileContent(
                    user = userProfile!!,
                    enrolledCoursesState = enrolledCoursesState,
                    createdCoursesState = createdCoursesState,
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun ProfileContent(
    user: User,
    enrolledCoursesState: UserCoursesUiState,
    // --- BARU: Tambahkan parameter untuk state course yang dibuat ---
    createdCoursesState: CourseUiState,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        ProfileHeader(user = user)
        AboutMeSection()
        MySkillsSection()
        EnrolledCoursesSection(
            coursesState = enrolledCoursesState,
            navController = navController
        )
        // --- BARU: Panggil section baru di sini ---
        MyCreatedCoursesSection(
            coursesState = createdCoursesState,
            navController = navController
        )
        Spacer(modifier = Modifier.height(24.dp)) // Tambah spasi di bawah
    }
}


// =======================================================================
// --- BARU: SECTION DAN CARD UNTUK "MY CREATED COURSES" ---
// =======================================================================

@Composable
fun MyCreatedCoursesSection(coursesState: CourseUiState, navController: NavController) {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "My Created Courses",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            TextButton(
                onClick = { /* Handle See All Created Courses */ }
            ) {
                Text(
                    "See All",
                    color = PrimaryColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

//        // Gunakan when untuk menangani semua state dari CourseUiState
        when (coursesState) {
            is CourseUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is CourseUiState.Success -> {

                if (coursesState.courses.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(top = 12.dp)
                    ) {
                        items(coursesState.courses) { course ->
                            // Gunakan Card yang didesain untuk menampilkan course yang dibuat
                            CreatedCourseCard(
                                title = course.name,
                                description = course.description,
                                imageUrl =  "https://images.unsplash.com/photo-1542744173-8e7e53415bb0?w=300&h=200&fit=crop", // Ganti dengan URL gambar dari course
                                rating = course.rating,
                                onCourseClick = {
                                    // Arahkan ke halaman detail atau edit course
                                    // navController.navigate("edit_course/${course.courseId}")
                                }
                            )
                        }
                    }
                } else {
                    Text(
                        text = "You haven't created any courses yet.",
                        modifier = Modifier.padding(vertical = 16.dp),
                        color = TextSecondary
                    )
                }
            }
            is CourseUiState.Error -> {
                Text(
                    text = coursesState.message,
                    color = Color.Red,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
        }
    }
}

@Composable
fun CreatedCourseCard(
    title: String,
    description: String,
    imageUrl: String,
    rating : String,
    onCourseClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(240.dp) // Sedikit lebih pendek karena tidak ada progress bar
            .clickable(onClick = onCourseClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.robot),
                error = painterResource(id = R.drawable.robot)
            )

            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxSize(), // Gunakan sisa ruang
                verticalArrangement = Arrangement.SpaceBetween // Dorong deskripsi ke bawah
            ) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically // Agar ikon dan teks sejajar
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        modifier = Modifier.size(16.dp), // Sesuaikan ukuran ikon
                        tint = Color(0xFFF59E0B) // Warna kuning keemasan untuk bintang
                    )

                    Spacer(modifier = Modifier.width(4.dp)) // Beri sedikit jarak

                    Text(
                        text = rating,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}


// =======================================================================
// --- KODE LAMA (TIDAK ADA PERUBAHAN) ---
// =======================================================================

@Composable
fun ProfileHeader(user: User) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Box {
            AsyncImage(
                model = "https://cultivatedculture.com/wp-content/uploads/2019/12/LinkedIn-Profile-Picture-Example-Rachel-Montan%CC%83ez.jpeg",
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(3.dp, PrimaryColor, CircleShape),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.robot),
                error = painterResource(id = R.drawable.robot)
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(36.dp)
                    .background(PrimaryColor, CircleShape)
                    .clickable { /* Handle edit profile */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit Profile",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = user.name,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = user.role.replaceFirstChar { it.uppercase() }, // Contoh tagline dari role
            fontSize = 16.sp,
            color = TextSecondary,
            modifier = Modifier.padding(top = 4.dp)
        )

        // Stats Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(number = "12", label = "Courses")
            StatItem(number = "4.8", label = "Rating")
            StatItem(number = "156", label = "Hours")
        }
    }
}

@Composable
fun StatItem(number: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = number,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryColor
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextSecondary
        )
    }
}

@Composable
fun AboutMeSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "About Me",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Text(
                text = "Passionate mobile app developer with 3+ years of experience in creating innovative and user-friendly applications. Specialized in Android development using Kotlin and Jetpack Compose. Always eager to learn new technologies and solve complex problems.",
                fontSize = 14.sp,
                color = TextSecondary,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun MySkillsSection() {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
        Text(
            text = "My Skills",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        val skills = listOf(
            "Android Development", "Kotlin", "Jetpack Compose",
            "UI/UX Design", "Firebase", "REST APIs"
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            skills.take(3).forEach { skill ->
                SkillChip(skill = skill, modifier = Modifier.weight(1f))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            skills.drop(3).forEach { skill ->
                SkillChip(skill = skill, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun SkillChip(skill: String, modifier: Modifier = Modifier) {
    Surface(
        color = PrimaryColor.copy(alpha = 0.1f),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
    ) {
        Text(
            text = skill,
            fontSize = 12.sp,
            color = PrimaryColor,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun EnrolledCoursesSection(coursesState: UserCoursesUiState, navController: NavController) {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Enrolled Courses",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            TextButton(
                onClick = { /* Handle See All */ }
            ) {
                Text(
                    "See All",
                    color = PrimaryColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        when (coursesState) {
            is UserCoursesUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is UserCoursesUiState.Success -> {
                if (coursesState.courses.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(top = 12.dp, bottom = 24.dp)
                    ) {
                        items(coursesState.courses) { course ->
                            CourseCard(
                                title = course.name,
                                author = course.description,
                                rating = course.rating.toFloatOrNull() ?: 0f,
                                progress = (30..85).random(),
                                imageUrl = "https://images.unsplash.com/photo-1611224923853-80b023f02d71?w=300&h=200&fit=crop",
                                onCourseClick = {
                                    navController.navigate("course_details/${course.courseId}")
                                }
                            )
                        }
                    }
                } else {
                    Text(
                        text = "You haven't enrolled in any courses yet.",
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            }
            is UserCoursesUiState.Error -> {
                Text(
                    text = coursesState.message,
                    color = Color.Red,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
        }
    }
}

@Composable
fun CourseCard(
    title: String,
    author: String,
    rating: Float,
    progress: Int,
    imageUrl: String,
    onCourseClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(240.dp)
            .clickable(onClick = onCourseClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.robot),
                error = painterResource(id = R.drawable.robot)
            )

            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = author,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = rating.toString(),
                            fontSize = 12.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                    Text(
                        text = "$progress%",
                        fontSize = 12.sp,
                        color = PrimaryColor,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                LinearProgressIndicator(
                    progress = progress / 100f,
                    modifier = Modifier.fillMaxWidth(),
                    color = PrimaryColor,
                    trackColor = PrimaryColor.copy(alpha = 0.1f)
                )
            }
        }
    }
}