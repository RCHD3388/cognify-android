package com.cognifyteam.cognifyapp.ui.learningpath.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cognifyteam.cognifyapp.ui.theme.CognifyApplicationTheme

/**
 * Data class untuk merepresentasikan sebuah postingan Learning Path
 */
data class PublicLearningPathPost(
    val id: Int,
    val title: String,
    val authorEmail: String,
    val likes: Int,
    val comments: Int,
    val category: String = "General"
)

/**
 * Sample data untuk ditampilkan di UI
 */
private val samplePosts = listOf(
    PublicLearningPathPost(
        id = 1,
        title = "Dasar-dasar Machine Learning untuk Pemula",
        authorEmail = "user1@example.com",
        likes = 128,
        comments = 16,
        category = "AI/ML"
    ),
    PublicLearningPathPost(
        id = 2,
        title = "Menjadi DevOps Engineer dalam 6 Bulan",
        authorEmail = "devops.guru@example.com",
        likes = 256,
        comments = 32,
        category = "DevOps"
    ),
    PublicLearningPathPost(
        id = 3,
        title = "Mastering Jetpack Compose for Android",
        authorEmail = "android.dev@example.com",
        likes = 512,
        comments = 64,
        category = "Mobile Dev"
    ),
    PublicLearningPathPost(
        id = 4,
        title = "Full-Stack Web Development with React & Node.js",
        authorEmail = "web.master@example.com",
        likes = 340,
        comments = 48,
        category = "Web Dev"
    ),
    PublicLearningPathPost(
        id = 5,
        title = "Pengenalan Data Science dengan Python",
        authorEmail = "datascientist@example.com",
        likes = 480,
        comments = 55,
        category = "Data Science"
    ),
)

/**
 * Main screen untuk menampilkan daftar Learning Path publik
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLearningPathScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header Section dengan gradient background
        HeaderSection()

        // Content Section
        ContentSection()
    }
}

/**
 * Header section dengan intro dan statistik
 */
@Composable
private fun HeaderSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Send,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Learning Paths",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Komunitas",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Jelajahi jalur pembelajaran yang telah dibuat dan dibagikan oleh komunitas developer. Temukan inspirasi dan pelajari hal baru dari pengalaman orang lain.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f),
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(
                        value = "${samplePosts.size}",
                        label = "Learning Paths"
                    )
                    StatItem(
                        value = "${samplePosts.sumOf { it.likes }}",
                        label = "Total Likes"
                    )
                    StatItem(
                        value = "${samplePosts.sumOf { it.comments }}",
                        label = "Diskusi"
                    )
                }
            }
        }
    }
}

/**
 * Komponen untuk menampilkan statistik
 */
@Composable
private fun StatItem(
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

/**
 * Content section dengan daftar postingan
 */
@Composable
private fun ContentSection() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
    ) {
        items(
            items = samplePosts,
            key = { it.id }
        ) { post ->
            LearningPathPostItem(
                post = post,
                onLikeClick = { /* TODO: Handle like */ },
                onCommentClick = { /* TODO: Handle comment */ },
                onDetailClick = { /* TODO: Navigate to detail */ }
            )
        }

        // Bottom spacing
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Komponen untuk menampilkan setiap item postingan Learning Path
 */
@Composable
private fun LearningPathPostItem(
    post: PublicLearningPathPost,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onDetailClick: () -> Unit
) {
    var isLiked by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header dengan kategori dan author
            PostHeader(
                category = post.category,
                authorEmail = post.authorEmail
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Title
            Text(
                text = post.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Divider
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Action Row
            PostActionRow(
                likes = post.likes,
                comments = post.comments,
                isLiked = isLiked,
                onLikeClick = {
                    isLiked = !isLiked
                    onLikeClick()
                },
                onCommentClick = onCommentClick,
                onDetailClick = onDetailClick
            )
        }
    }
}

/**
 * Header komponen untuk setiap post
 */
@Composable
private fun PostHeader(
    category: String,
    authorEmail: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Category Badge
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.clip(RoundedCornerShape(12.dp))
        ) {
            Text(
                text = category,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }

        // Author Info
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = authorEmail.substringBefore("@"),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Action row dengan tombol like, comment, dan detail
 */
@Composable
private fun PostActionRow(
    likes: Int,
    comments: Int,
    isLiked: Boolean,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onDetailClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left side - Like and Comment buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Like Button
            FilledTonalButton(
                onClick = onLikeClick,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = if (isLiked)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (isLiked)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.FavoriteBorder,
                    contentDescription = "Like",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = likes.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
            }

            // Comment Button
            FilledTonalButton(
                onClick = onCommentClick,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Send,
                    contentDescription = "Comment",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = comments.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Right side - Detail Button
        Button(
            onClick = onDetailClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Lihat Detail",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Preview Composables
@Preview(showBackground = true, name = "Light Mode")
@Composable
private fun MainLearningPathScreenPreview() {
    CognifyApplicationTheme(darkTheme = false) {
        MainLearningPathScreen()
    }
}

@Preview(showBackground = true, name = "Dark Mode")
@Composable
private fun MainLearningPathScreenDarkPreview() {
    CognifyApplicationTheme(darkTheme = true) {
        MainLearningPathScreen()
    }
}

@Preview(showBackground = true, name = "Post Item Preview")
@Composable
private fun PostItemPreview() {
    CognifyApplicationTheme {
        LearningPathPostItem(
            post = samplePosts.first(),
            onLikeClick = {},
            onCommentClick = {},
            onDetailClick = {}
        )
    }
}
