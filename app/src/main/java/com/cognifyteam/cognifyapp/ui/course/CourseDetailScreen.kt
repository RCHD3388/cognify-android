package com.cognifyteam.cognifyapp.ui.course
// Core Compose
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Layout Components
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size

import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow

// Material Components
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults

import androidx.compose.material3.MaterialTheme

// Icons
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Star

import androidx.compose.material.icons.filled.Person

// Image Handling
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale

import com.cognifyteam.cognifyapp.R

import androidx.compose.ui.text.font.FontWeight


import androidx.compose.ui.tooling.preview.Preview
@Composable
fun CourseScreen() {
    // Main scrollable container
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        CourseHeader()
        CourseBanner()
        CourseTabs()
        CourseDetails()
        CourseInstructor()
        CourseActions()
    }
}

// 1. Status Bar Component
@Composable
fun StatusBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1A1A1A))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "9:30",
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}


// 2. Course Header
@Composable
fun CourseHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { /* Back action */ }) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }
        Text(
            text = "Course Screen",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium
        )
        IconButton(onClick = { /* Menu action */ }) {
            Icon(Icons.Default.Menu, contentDescription = "Menu")
        }
    }
}

// 3. Course Banner
@Composable
fun CourseBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_google),
            contentDescription = "Course Preview",
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop
        )
    }
}

// 4. Course Tabs
@Composable
fun CourseTabs() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TabItem(text = "Overview", isSelected = true)
        TabItem(text = "Lessons", isSelected = false)
        TabItem(text = "Reviews", isSelected = false)
    }
}

@Composable
fun TabItem(text: String, isSelected: Boolean) {
    val textColor = if (isSelected) Color(0xFF4A90E2) else Color(0xFF6B6B6B)
    val backgroundColor = if (isSelected) Color.White else Color.Transparent

    Text(
        text = text,
        color = textColor,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier
            .background(backgroundColor)
            .padding(vertical = 12.dp)
    )
}

// 5. Course Details
@Composable
fun CourseDetails() {
    Column(modifier = Modifier.padding(16.dp)) {
        CourseTitleAndRating()
        InstructorInfo()
        DescriptionSection()
    }
}

@Composable
fun CourseTitleAndRating() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Mobile App UI UX",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.weight(1f))
        RatingWithCount(rating = 4.5f, count = 375)
    }
}

@Composable
fun RatingWithCount(rating: Float, count: Int) {
    Column () {
        Row {
            repeat(5) { index ->
                Icon(
                    imageVector = Icons.Outlined.Star,
                    contentDescription = "Star",
                    tint = if (index < rating.toInt()) Color(0xFFFFC107) else Color(0xFFE0E0E0),
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Text(
            text = "($count Reviews)",
            color = Color(0xFF6B6B6B),
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

@Composable
fun InstructorInfo() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Person, "Instructor", tint = Color(0xFF4A90E2))
        Text("Tom Makesman", color = Color(0xFF4A90E2), modifier = Modifier.padding(start = 4.dp))
        Spacer(Modifier.width(16.dp))
        Icon(Icons.Outlined.CheckCircle, "Lessons", tint = Color(0xFF4A90E2))
        Text("32 Lessons", color = Color(0xFF4A90E2), modifier = Modifier.padding(start = 4.dp))
    }
}

@Composable
fun DescriptionSection() {
    Text(
        "Lorem ipsum dolor sit amet consectetur. Lectus viverra sed aliquam quis enim leo. Turpis nec facilisis placerat dolor ac donec. Odio semper quis rutrum nisl lacus odio vivamus ultricies. Ultrices tristique platea feugiat ac velit nulla. Proin lectus commodo id nullam commodo venenatis.",
        color = Color(0xFF6B6B6B),
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(top = 16.dp)
    )

    Text(
        "Read More",
        color = Color(0xFF4A90E2),
        modifier = Modifier
            .padding(top = 8.dp)
            .clickable {}
    )
}

// 6. Instructor Profile
@Composable
fun CourseInstructor() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Image(
                painter = painterResource(id = R.drawable.istockphoto_1682296067_612x612),
                contentDescription = "Instructor",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text("Tom Makesman", style = MaterialTheme.typography.titleMedium)
                Text("Design Tutor", color = Color(0xFF6B6B6B))
            }
            Icon(Icons.Outlined.Star, "Rate", tint = Color(0xFF4A90E2))
        }
    }
}

// 7. Price & Enroll
@Composable
fun CourseActions() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                "$95",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Text("VAT included", color = Color(0xFF6B6B6B))
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2))
        ) {
            Text("GET ENROLL", color = Color.White)
        }
    }
}