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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cognifyteam.cognifyapp.data.AppContainer
import com.cognifyteam.cognifyapp.data.models.User
import com.cognifyteam.cognifyapp.ui.FabState
import com.cognifyteam.cognifyapp.ui.TopBarState
import com.cognifyteam.cognifyapp.ui.auth.AuthViewModel
import com.cognifyteam.cognifyapp.ui.common.UserViewModel
import com.cognifyteam.cognifyapp.ui.navigation.AppNavRoutes

private val PrimaryColor = Color(0xFF1F2343)
private val BackgroundColor = Color.White
private val TextPrimary = Color.Black
private val TextSecondary = Color.Gray
private val SurfaceColor = Color(0xFFF8F9FA)

@Composable
fun HomeScreen(
    navController: NavController,
    appContainer: AppContainer,
    onFabStateChange: (FabState) -> Unit,
    onTopBarStateChange: (TopBarState) -> Unit,
    onShowSnackbar: (String) -> Unit
) {
    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModel.provideFactory(appContainer.authRepository)
    )

    LaunchedEffect(key1 = Unit) {
        onFabStateChange(FabState(isVisible = false))
        onTopBarStateChange(TopBarState(isVisible = false)) // Sembunyikan TopBar utama
    }

    val user by userViewModel.userState.collectAsState()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Gunakan theme
    ) {
        item { HeaderSection(navController, user) }
        item { SearchBar(navController) }
        item { CategoriesSection() }
        item { ContinueWatchingSection(navController) }
        item { PopularCoursesSection(navController) }
        item { RecommendedForYouSection(navController) }
        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
fun HeaderSection(navController: NavController, user: User?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Welcome Back! 👋",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant, // Gunakan theme
                fontWeight = FontWeight.Normal
            )
            Text(
                text = user?.name ?: "...",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface // Gunakan theme
            )
        }
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Course",
            tint = MaterialTheme.colorScheme.primary, // Gunakan theme
            modifier = Modifier
                .size(28.dp)
                .clickable {
                    // navController.navigate("course") // Rute ini tidak ada di AppNavRoutes Anda
                }
        )
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Profile",
            tint = PrimaryColor,
            modifier = Modifier.size(28.dp)
                .clickable {
                    navController.navigate("search")
                }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(navController: NavController) {
    var searchText by remember { mutableStateOf("") }

    OutlinedTextField(
        value = searchText,
        onValueChange = { searchText = it },
        placeholder = { Text("Search courses...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .clickable(onClick = { navController.navigate(AppNavRoutes.SEARCH) }), // Navigasi saat diklik
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        readOnly = true, // Jadikan read-only, karena fungsinya hanya untuk navigasi
        enabled = false // Disable interaksi ketik
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
            val categories = listOf(
                "UI Design",
                "Health",
                "Psychology",
                "Business"
            )
            val colors = listOf(Color(0xFF6366F1), Color(0xFF10B981), Color(0xFFF59E0B), Color(0xFFEF4444))

            items(categories.size) { index ->
                CategoryItem(
                    icon = Icons.Outlined.Home, // Menggunakan ikon yang ada
                    categoryName = categories[index],
                    backgroundColor = colors[index]
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
            .width(80.dp)
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
                contentDescription = categoryName,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = categoryName,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun ContinueWatchingSection(navController: NavController) {
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
                    progress = when (index) { 0 -> 85 else -> 40 },
                    onClick = { navController.navigate("course_details/DUMMY_ID_1") }
                )
            }
        }
    }
}

@Composable
fun CourseCard(title: String, subtitle: String, imageUrl: String, progress: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(240.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.robot),
                error = painterResource(id = R.drawable.robot)
            )
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp
                    )
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (progress > 0) {
                    Column {
                        Text(
                            text = "$progress% Complete",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .align(Alignment.End)
                                .padding(bottom = 4.dp)
                        )
                        LinearProgressIndicator(
                            progress = progress / 100f,
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PopularCoursesSection(navController: NavController) {
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
                    imageUrl = "https://images.unsplash.com/photo-1611224923853-80b023f02d71?w=300&h=200&fit=crop",
                    onClick = { navController.navigate("course_details/DUMMY_ID_2") }
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
    imageUrl: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .height(280.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.robot),
                error = painterResource(id = R.drawable.robot)
            )
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(text = subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 2.dp), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(text = description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 3, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 6.dp), lineHeight = 14.sp)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = price, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text(text = "⭐ $rating", fontSize = 12.sp, color = Color(0xFFF59E0B), fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun RecommendedForYouSection(navController: NavController) {
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
                    progress = 0,
                    onClick = { navController.navigate("course_details/DUMMY_ID_3") }
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
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "See All",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.clickable { onSeeAllClick() }
        )
    }
}