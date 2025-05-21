package com.cognifyteam.cognifyapp.ui.detail_course.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.cognifyteam.cognifyapp.R
import com.cognifyteam.cognifyapp.ui.detail_course.component.ChipSkill
import com.cognifyteam.cognifyapp.ui.detail_course.component.CourseHeader
import com.cognifyteam.cognifyapp.ui.detail_course.component.EnrollButton
import com.cognifyteam.cognifyapp.ui.detail_course.component.HorizontalScrollContainer
import com.cognifyteam.cognifyapp.ui.detail_course.component.RatingBar

@Composable
fun CourseOverviewScreen(navController: NavHostController) {
    val scrollState = rememberScrollState()

    Column(modifier = Modifier.fillMaxSize()) {

        // Header - Banner + TabBar (Fix)
        CourseHeader()

        // Scrollable Content
        Surface (modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                CourseContent()
            }
        }

        // Footer - Fixed Button (Fix)
        EnrollButton(95.toString())
    }
}

@Composable
fun CourseContent() {
    val fullDescription = """
        Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
        
        Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem.
    """.trimIndent()

    var isExpanded by remember { mutableStateOf(false) }

    Column (modifier = Modifier.padding(16.dp)){

        // Judul Kursus
        Text(
            text = "Mobile App UI UX",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        // Rating & Reviews
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            RatingBar(rating = 4.5f)
            Text(text = "(273 Reviews)", fontSize = 12.sp, color = Color.Gray)
        }

        // Deskripsi Singkat atau Penuh
        if (isExpanded) {
            Text(
                text = fullDescription,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 16.dp)
            )
        } else {
            val shortDescription = fullDescription.take(200) + "..."
            Text(
                text = shortDescription,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        // Tombol Read More / Read Less
        TextButton(
            onClick = { isExpanded = !isExpanded },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(
                text = if (isExpanded) "Read Less" else "Read More",
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Tutor Info
        Row(
            modifier = Modifier.padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.robot),
                contentDescription = "Tutor",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Tom Makesman",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Design Tutor",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                imageVector = Icons.Default.Face,
                contentDescription = "Chat with tutor",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        // Skill Chips
        Text(
            text = "Skills",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp)
        )

        HorizontalScrollContainer {
            Row {
                listOf("UI/UX", "Website Design", "Figma", "XD", "Animation", "User Persona").forEach {
                    ChipSkill(skill = it)
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    }
}