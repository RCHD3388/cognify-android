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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.cognifyteam.cognifyapp.R
import com.cognifyteam.cognifyapp.data.models.Course // Pastikan impor ini ada
import com.cognifyteam.cognifyapp.data.models.User
import com.cognifyteam.cognifyapp.ui.FabState
import com.cognifyteam.cognifyapp.ui.MainActivity
import com.cognifyteam.cognifyapp.ui.TopBarState
import com.cognifyteam.cognifyapp.ui.auth.AuthUiState
import com.cognifyteam.cognifyapp.ui.auth.AuthViewModel
import com.cognifyteam.cognifyapp.ui.common.UserViewModel
import com.cognifyteam.cognifyapp.ui.course.CourseViewModel
import com.cognifyteam.cognifyapp.ui.course.CreatedCoursesUiState

// HAPUS KONSTANTA WARNA LOKAL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePage(
    navController: NavController,
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel,
    userCoursesViewModel: UserCoursesViewModel,
    userViewModel: UserViewModel,
    courseViewModel: CourseViewModel,
    firebaseId: String,
    onFabStateChange: (FabState) -> Unit,
    onTopBarStateChange: (TopBarState) -> Unit,
    onShowSnackbar: (String) -> Unit
) {
    val loggedInUser by userViewModel.userState.collectAsState()
    val isMyProfile = loggedInUser?.firebaseId == firebaseId

    val isLoading by profileViewModel.isLoading.observeAsState(initial = false)
    val userProfile by profileViewModel.userProfile.observeAsState()
    val error by profileViewModel.error.observeAsState()

    val enrolledCoursesState by userCoursesViewModel.uiState.collectAsState()
    val createdCoursesState by courseViewModel.createdCoursesUiState.collectAsState()
    val authState by authViewModel.uiState.observeAsState()
    val context = LocalContext.current

    // Efek untuk mengkonfigurasi TopBar dan FAB
    LaunchedEffect(key1 = firebaseId, key2 = isMyProfile) {
        profileViewModel.loadProfile(firebaseId)
        userCoursesViewModel.initialize(firebaseId)
        if (isMyProfile) {
            courseViewModel.loadCreateCourses(firebaseId)
        }
        onFabStateChange(FabState(isVisible = false))
        onTopBarStateChange(TopBarState(isVisible = true,
            title = if (isMyProfile) "My Profile" else "User Profile",
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                if (isMyProfile) {
                    IconButton(onClick = {
                        authViewModel.logout()
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                }
            }
        ))
    }

    // Efek untuk menangani navigasi setelah logout
    LaunchedEffect(authState) {
        if (authState is AuthUiState.Unauthenticated && isMyProfile) {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            context.startActivity(intent)
        }
    }


    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else if (error != null) {
            Text(text = error!!, color = MaterialTheme.colorScheme.error)
        } else if (userProfile != null) {
            ProfileContent(
                user = userProfile!!,
                isMyProfile = isMyProfile,
                enrolledCoursesState = enrolledCoursesState,
                createdCoursesState = createdCoursesState,
                navController = navController,
                profileViewModel = profileViewModel
            )
        }
    }
}

@Composable
fun ProfileContent(
    user: User,
    isMyProfile: Boolean,
    enrolledCoursesState: UserCoursesUiState,
    createdCoursesState: CreatedCoursesUiState,
    navController: NavController,
    profileViewModel: ProfileViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        ProfileHeader(user = user, isMyProfile = isMyProfile)
        AboutMeSection(user = user, viewModel = profileViewModel, isMyProfile = isMyProfile)
        EnrolledCoursesSection(
            coursesState = enrolledCoursesState,
            navController = navController
        )
        if (isMyProfile) {
            MyCreatedCoursesSection(
                coursesState = createdCoursesState,
                navController = navController
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ProfileHeader(user: User, isMyProfile: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().padding(24.dp)
    ) {
        Box {
            AsyncImage(
                model = "https://cultivatedculture.com/wp-content/uploads/2019/12/LinkedIn-Profile-Picture-Example-Rachel-Montan%CC%83ez.jpeg",
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.robot),
                error = painterResource(id = R.drawable.robot)
            )
            if (isMyProfile) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                        .clickable { /* Handle edit profile picture */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit Profile",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = user.name, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Text(text = user.role.replaceFirstChar { it.uppercase() }, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))

        // Stats Row - Menggunakan data dinamis
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(number = "0", label = "Learning Path")
            StatItem(number = user.followersCount.toString(), label = "Followers")
            StatItem(number = user.followingCount.toString(), label = "Following")
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
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun AboutMeSection(user: User, viewModel: ProfileViewModel, isMyProfile: Boolean) {
    var isEditing by rememberSaveable { mutableStateOf(false) }
    var descriptionText by remember(user.description) { mutableStateOf(user.description ?: "") }
    val isLoadingUpdate by viewModel.isLoading.observeAsState(false)
    val updateResult by viewModel.updateResult.observeAsState()

    LaunchedEffect(updateResult) {
        updateResult?.onSuccess {
            isEditing = false
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "About Me", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                if (isMyProfile) {
                    if (isEditing) {
                        Row {
                            TextButton(onClick = { isEditing = false }, enabled = !isLoadingUpdate) {
                                Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            TextButton(
                                onClick = { viewModel.updateProfile(user.firebaseId, user.name, descriptionText) },
                                enabled = !isLoadingUpdate
                            ) {
                                Text("Save", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    } else {
                        IconButton(onClick = { isEditing = true }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Description", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isEditing) {
                OutlinedTextField(
                    value = descriptionText,
                    onValueChange = { descriptionText = it },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    label = { Text("Your description") }
                )
            } else {
                Text(
                    text = user.description ?: "No description provided yet. Click edit to add one.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
            }
            if (isEditing && isLoadingUpdate) {
                CircularProgressIndicator(modifier = Modifier.padding(top = 8.dp).size(24.dp))
            }
        }
    }
}

@Composable
fun EnrolledCoursesSection(coursesState: UserCoursesUiState, navController: NavController) {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Enrolled Courses", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            TextButton(onClick = { navController.navigate("allcourse") }) {
                Text("See All", color = MaterialTheme.colorScheme.primary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
        }
        when (coursesState) {
            is UserCoursesUiState.Loading -> {
                Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            }
            is UserCoursesUiState.Success -> {
                if (coursesState.courses.isNotEmpty()) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(top = 12.dp, bottom = 24.dp)) {
                        items(coursesState.courses) { course ->
                            CourseCard(
                                title = course.name,
                                author = course.description,
                                rating = course.rating.toFloatOrNull() ?: 0f,
                                progress = (30..85).random(),
                                imageUrl = "...",
                                onCourseClick = { navController.navigate("course_details/${course.courseId}") }
                            )
                        }
                    }
                } else {
                    Text("This user hasn't enrolled in any courses yet.", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 16.dp))
                }
            }
            is UserCoursesUiState.Error -> {
                Text(text = coursesState.message, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 16.dp))
            }
        }
    }
}

@Composable
fun MyCreatedCoursesSection(coursesState: CreatedCoursesUiState, navController: NavController) {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = "My Created Courses", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            TextButton(onClick = { /* Handle See All Created Courses */ }) {
                Text("See All", color = MaterialTheme.colorScheme.primary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
        }
        when (coursesState) {
            is CreatedCoursesUiState.Loading -> {
                Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            }
            is CreatedCoursesUiState.Success -> {
                if (coursesState.courses.isNotEmpty()) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(top = 12.dp)) {
                        items(coursesState.courses) { course ->
                            CreatedCourseCard(
                                title = course.name,
                                description = course.description,
                                imageUrl = course.thumbnail.orEmpty().ifBlank { "..." },
                                rating = course.rating,
                                onCourseClick = { navController.navigate("course_details/${course.courseId}") }
                            )
                        }
                    }
                } else {
                    Text("You haven't created any courses yet.", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 16.dp))
                }
            }
            is CreatedCoursesUiState.Error -> {
                Text(text = coursesState.message, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 16.dp))
            }
        }
    }
}

@Composable
fun CourseCard(title: String, author: String, rating: Float, progress: Int, imageUrl: String, onCourseClick: () -> Unit) {
    Card(
        modifier = Modifier.width(200.dp).height(240.dp).clickable(onClick = onCourseClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            AsyncImage(model = imageUrl, contentDescription = title, modifier = Modifier.fillMaxWidth().height(120.dp), contentScale = ContentScale.Crop, placeholder = painterResource(id = R.drawable.robot), error = painterResource(id = R.drawable.robot))
            Column(modifier = Modifier.padding(12.dp).fillMaxWidth()) {
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(text = author, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
                Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = "Rating", tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                        Text(text = rating.toString(), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(start = 4.dp))
                    }
                    Text(text = "$progress%", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                }
                Spacer(modifier = Modifier.weight(1f))
                LinearProgressIndicator(progress = progress / 100f, modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.primary, trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            }
        }
    }
}

@Composable
fun CreatedCourseCard(title: String, description: String, imageUrl: String, rating: String, onCourseClick: () -> Unit) {
    Card(
        modifier = Modifier.width(200.dp).height(240.dp).clickable(onClick = onCourseClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            AsyncImage(model = imageUrl, contentDescription = title, modifier = Modifier.fillMaxWidth().height(120.dp), contentScale = ContentScale.Crop, placeholder = painterResource(id = R.drawable.robot), error = painterResource(id = R.drawable.robot))
            Column(modifier = Modifier.padding(12.dp).fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = "Rating", modifier = Modifier.size(16.dp), tint = Color(0xFFF59E0B))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = rating, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(text = description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}