package com.cognifyteam.cognifyapp.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cognifyteam.cognifyapp.R

@Composable
fun HomeScreen() {
    val scrollState = rememberScrollState()

    LazyColumn (modifier = Modifier.fillMaxSize()) {
        item { HeaderSection() }
        item { SearchBar() }
        item { CategoriesSection() }
        item { ContinueWatchingSection() }
        item { PopularCoursesSection() }
        item { RecommendedForYouSection() }
    }
}

@Composable
fun HeaderSection() {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Welcome M Bilal",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            painter = painterResource(id = R.drawable.baseline_email_24),
            contentDescription = "Profile Icon",
            tint = Color.Blue,
            modifier = Modifier.size(32.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar() {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        label = { Text("Search Here") },
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.baseline_email_24),
                contentDescription = "Search Icon",
                tint = Color.Gray
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(50.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Gray,
            unfocusedBorderColor = Color.Gray
        )
    )
}

@Composable
fun CategoriesSection() {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Categories",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "See All",
                color = Color.Gray,
                modifier = Modifier.clickable {}
            )
        }

        LazyRow(
            modifier = Modifier.padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(4) { index ->
                CategoryItem(
                    iconRes = R.drawable.baseline_email_24,
                    categoryName = when (index) {
                        0 -> "UI Design"
                        1 -> "Health"
                        2 -> "Figma"
                        else -> "Business"
                    }
                )
            }
        }
    }
}

@Composable
fun CategoryItem(iconRes: Int, categoryName: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Blue)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = categoryName,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ContinueWatchingSection() {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Continue Watching",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "See All",
                color = Color.Gray,
                modifier = Modifier.clickable {}
            )
        }

        LazyRow(
            modifier = Modifier.padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(2) { index ->
                CourseCard(
                    title = when (index) {
                        0 -> "UI UX Design"
                        else -> "App Design"
                    },
                    subtitle = "By Peter Parker",
                    imageUrl = "https://www.deheus.id/siteassets/animal-nutrition/swine/de-heus-animal-nutrition_animals_swines_-pigs_sows_in_stables-1.jpg",
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
            .width(180.dp)
            .wrapContentHeight(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            AsyncImage(
                model = imageUrl, // URL gambar
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                placeholder = painterResource(id = R.drawable.baseline_email_24), // Gambar tempatan
                error = painterResource(id = R.drawable.baseline_password_24) // Gambar jika gagal dimuat
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(text = subtitle, fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(progress = progress / 100f)
            }
        }
    }
}

@Composable
fun PopularCoursesSection() {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Popular Courses",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "See All",
                color = Color.Gray,
                modifier = Modifier.clickable {}
            )
        }

        LazyRow(
            modifier = Modifier.padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(3) { index ->
                PopularCourseCard(
                    title = when (index) {
                        0 -> "UI UX Design"
                        1 -> "App Design"
                        else -> "3D & 3D Animation Course"
                    },
                    subtitle = "By Peter Parker",
                    description = "Lorem ipsum dolor sit amet consectetur.",
                    price = when (index) {
                        0 -> "$50"
                        1 -> "$50"
                        else -> "$50"
                    },
                    imageRes = R.drawable.baseline_email_24
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
    imageRes: Int
) {
    Card (
        modifier = Modifier
            .width(150.dp)
            .height(250.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = price,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Blue
                )
            }
        }
    }
}

@Composable
fun RecommendedForYouSection() {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recommended For You",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "See All",
                color = Color.Gray,
                modifier = Modifier.clickable {}
            )
        }

        LazyRow(
            modifier = Modifier.padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(3) { index ->
                CourseCard(
                    title = when (index) {
                        0 -> "UI UX Design"
                        1 -> "App Design"
                        else -> "3D & 3D Animation Course"
                    },
                    subtitle = "By Peter Parker",
                    imageUrl = "https://www.deheus.id/siteassets/animal-nutrition/swine/de-heus-animal-nutrition_animals_swines_-pigs_sows_in_stables-1.jpg",
                    progress = 0
                )
            }
        }
    }
}