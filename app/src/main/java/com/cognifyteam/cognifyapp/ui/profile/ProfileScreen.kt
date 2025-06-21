package com.cognifyteam.cognifyapp.ui.profile

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
// BARU: Import untuk ikon Logout
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
// BARU: Import tambahan
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cognifyteam.cognifyapp.R
import coil.compose.rememberAsyncImagePainter
import coil.annotation.ExperimentalCoilApi
import coil.compose.AsyncImagePainter
// BARU: Import yang dibutuhkan
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import com.cognifyteam.cognifyapp.ui.MainActivity // <-- Pastikan ini menunjuk ke MainActivity Anda
import com.cognifyteam.cognifyapp.ui.auth.AuthUiState
import com.cognifyteam.cognifyapp.ui.auth.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
// DIUBAH: Hapus parameter onLogout, kita tidak membutuhkannya lagi
fun ProfilePage(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val authState by authViewModel.uiState.observeAsState()
    val context = LocalContext.current // <-- BARU: Dapatkan context

    LaunchedEffect(authState) {
        // Jika state menjadi Unauthenticated (setelah logout)
        if (authState is AuthUiState.Unauthenticated) {
            // BARU: Buat intent untuk kembali ke MainActivity
            val intent = Intent(context, MainActivity::class.java).apply {
                // Hapus semua activity sebelumnya dari back stack
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            // Luncurkan MainActivity (layar login)
            context.startActivity(intent)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        // Ini akan memicu LaunchedEffect di atas
                        authViewModel.logout()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            ProfileHeader()
            AboutMeSection()
            EnrolledCoursesSection(navController = navController)
        }
    }
}


// ... (sisa file ProfilePage.kt tidak perlu diubah)

@Composable
fun ProfileHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 24.dp).fillMaxSize()
    ) {
        Box() {
            val imageUrl = "https://cultivatedculture.com/wp-content/uploads/2019/12/LinkedIn-Profile-Picture-Example-Rachel-Montan%CC%83ez.jpeg "
            val imageLoaderPainter = rememberAsyncImagePainter(model = imageUrl)

            val painterToShow = if (imageLoaderPainter.state is AsyncImagePainter.State.Error ||
                imageLoaderPainter.state is AsyncImagePainter.State.Loading && imageUrl.isNullOrBlank() // Optional: show placeholder if URL is initially blank and still loading
            ) {
                painterResource(id = R.drawable.logo_google) // Your default drawable
            } else {
                imageLoaderPainter
            }

            Image(
                painter = painterToShow,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                contentScale = ContentScale.Crop
            )

            IconButton(
                onClick = { /* Handle edit profile */ },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(32.dp)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                        CircleShape
                    )
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit Profile",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        // Name and Tagline
        Text(
            text = "Name Here",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = "Tag Line",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun AboutMeSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .padding(bottom = 24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "About Me",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Lorem ipsum dolor sit amet consectetur. Lectus viverra sed aliquam quis enim leo. Turpis nec facilisis placerat dolor ac donec. Odio semper quis rutrum quis lacus odio vivamus ultricies. Ultrices ultricies platea feugiat.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
fun MySkillsSection() {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "My Skills",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 16.dp)
        )


    }
}

@Composable
fun SkillChip(skill: String) {
    Surface (
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.padding(4.dp)
    ) {
        Text(
            text = skill,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun EnrolledCoursesSection(navController: NavController) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Text(
                text = "Enrolled Courses",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 12.dp)
            )
            TextButton(
                onClick = { /* Handle See All */ },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("See All")
            }
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(32.dp),

            modifier = Modifier.padding(bottom = 32.dp)
        ) {
            items(5) { index ->
                CourseCard(
                    title = when (index) {
                        0 -> "UI UX Design"
                        else -> "App Design"
                    },
                    author = "By Peter Parker",

                    rating = 4.5f,
                    progress = 85,
                    onCourseClick = {
                        navController.navigate("course_details/123")
                    }
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
    onCourseClick: () -> Unit
) {
    Row (){
        Column(
            modifier = Modifier
                .width(200.dp)
                .height(200.dp)
                .clickable(onClick = onCourseClick)
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = "https://images.unsplash.com/photo-1575936123452-b67c3203c357?fm=jpg&q=60&w=3000&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8aW1hZ2V8ZW58MHx8MHx8fDA%3D"),
                contentDescription = title,
                modifier = Modifier.height(120.dp).fillMaxWidth().clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Row {
                Column (
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = author,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                }
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 10.dp)
                    ) {
                        repeat(5) { i ->
                            val starColor = if (i < rating.toInt())
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "Star $i",
                                tint = starColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Text(
                        text = "($progress%)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }

    }

}