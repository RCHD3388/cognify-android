package com.cognifyteam.cognifyapp.ui.home

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import coil.compose.AsyncImage
import com.cognifyteam.cognifyapp.R
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cognifyteam.cognifyapp.data.AppContainer
import com.cognifyteam.cognifyapp.data.models.User
import com.cognifyteam.cognifyapp.ui.auth.AuthViewModel
import com.cognifyteam.cognifyapp.ui.common.UserViewModel

private val PrimaryColor = Color(0xFF1F2343)
private val BackgroundColor = Color.White
private val TextPrimary = Color.Black
private val TextSecondary = Color.Gray
private val SurfaceColor = Color(0xFFF8F9FA)

@Composable
fun HomeScreen(navController: NavController, appContainer: AppContainer) {
    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModel.provideFactory(appContainer.authRepository)
    )
    val user by userViewModel.userState.collectAsState()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        item { HeaderSection(navController, appContainer, user) }
        item { SearchBar() }
        item { CategoriesSection() }
        item { ContinueWatchingSection() }
        item { PopularCoursesSection() }
        item { RecommendedForYouSection() }
        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
fun HeaderSection(navController: NavController, appContainer: AppContainer, user: User?) {
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModel.provideFactory(appContainer.authRepository)
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Welcome Back! 👋",
                fontSize = 16.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Normal
            )
            Text(
                text = user?.name ?: "...",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Course",
            tint = PrimaryColor,
            modifier = Modifier.size(28.dp).clickable {
                navController.navigate("course")
            }
        )
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(PrimaryColor.copy(alpha = 0.1f))
                .clickable { },
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profile",
                tint = PrimaryColor,
                modifier = Modifier.size(28.dp)
                    .clickable {
                        navController.navigate("search")
//                        authViewModel.logout()
                    }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar() {
    val searchText = remember { mutableStateOf("") }

    OutlinedTextField(
        value = searchText.value,
        onValueChange = { searchText.value = it },
        placeholder = { Text("Search courses...", color = TextSecondary) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = TextSecondary
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = PrimaryColor,
            unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedLabelColor = PrimaryColor,
            containerColor = SurfaceColor
        ),
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun CategoriesSection() {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
        SectionHeader(title = "Categories", onSeeAllClick = {})

        LazyRow(
            modifier = Modifier.padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(4) { index ->
                CategoryItem(
                    icon = when (index) {
//                        0 -> Icons.Outlined.DesignServices
//                        1 -> Icons.Outlined.FitnessCenter
//                        2 -> Icons.Outlined.Psychology
                        else -> Icons.Outlined.Home
                    },
                    categoryName = when (index) {
                        0 -> "UI Design"
                        1 -> "Health"
                        2 -> "Psychology"
                        else -> "Business"
                    },
                    backgroundColor = when (index) {
                        0 -> Color(0xFF6366F1)
                        1 -> Color(0xFF10B981)
                        2 -> Color(0xFFF59E0B)
                        else -> Color(0xFFEF4444)
                    }
                )
            }
        }
    }
}

@Composable
fun CategoryItem(icon: ImageVector, categoryName: String, backgroundColor: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(80.dp) // Fixed width untuk semua category
            .clickable { }
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = categoryName,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun ContinueWatchingSection() {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
        SectionHeader(title = "Continue Watching", onSeeAllClick = {})

        LazyRow(
            modifier = Modifier.padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(2) { index ->
                CourseCard(
                    title = when (index) {
                        0 -> "UI UX Design Fundamentals"
                        else -> "Mobile App Design"
                    },
                    subtitle = "By Peter Parker",
                    imageUrl = "https://images.unsplash.com/photo-1611224923853-80b023f02d71?w=300&h=200&fit=crop",
                    progress = when (index) {
                        0 -> 85
                        else -> 40
                    }
                )
            }
        }
    }
}

@Composable
fun CourseCard(title: String, subtitle: String, imageUrl: String, progress: Int) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(240.dp) // Fixed height untuk semua card
            .clickable { },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
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
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 2, // Maksimal 2 baris
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 4.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.weight(1f)) // Push progress ke bawah

                if (progress > 0) {
                    LinearProgressIndicator(
                        progress = progress / 100f,
                        modifier = Modifier.fillMaxWidth(),
                        color = PrimaryColor,
                        trackColor = PrimaryColor.copy(alpha = 0.1f)
                    )
                    Text(
                        text = "$progress% Complete",
                        fontSize = 10.sp,
                        color = PrimaryColor,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PopularCoursesSection() {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
        SectionHeader(title = "Popular Courses", onSeeAllClick = {})

        LazyRow(
            modifier = Modifier.padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(3) { index ->
                PopularCourseCard(
                    title = when (index) {
                        0 -> "UI UX Design"
                        1 -> "App Development"
                        else -> "3D Animation"
                    },
                    subtitle = "By Expert Instructor",
                    description = "Learn from industry experts with hands-on projects",
                    price = "$49",
                    rating = "4.8",
                    imageUrl = "https://images.unsplash.com/photo-1611224923853-80b023f02d71?w=300&h=200&fit=crop"
                )
            }
        }
    }
}

@Composable
fun PopularCourseCard(
    title: String,
    subtitle: String,
    description: String,
    price: String,
    rating: String,
    imageUrl: String
) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .height(280.dp) // Fixed height untuk semua popular course card
            .clickable { },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
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
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 2.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    maxLines = 3, // Maksimal 3 baris untuk description
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 6.dp),
                    lineHeight = 14.sp
                )
                Spacer(modifier = Modifier.weight(1f)) // Push price/rating ke bawah
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = price,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryColor
                    )
                    Text(
                        text = "⭐ $rating",
                        fontSize = 12.sp,
                        color = Color(0xFFF59E0B),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun RecommendedForYouSection() {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
        SectionHeader(title = "Recommended For You", onSeeAllClick = {})

        LazyRow(
            modifier = Modifier.padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(3) { index ->
                CourseCard(
                    title = when (index) {
                        0 -> "Advanced UI Design"
                        1 -> "React Native Development"
                        else -> "Motion Graphics"
                    },
                    subtitle = "By Industry Expert",
                    imageUrl = "https://images.unsplash.com/photo-1611224923853-80b023f02d71?w=300&h=200&fit=crop",
                    progress = 0
                )
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, onSeeAllClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "See All",
            color = PrimaryColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.clickable { onSeeAllClick() }
        )
    }
}