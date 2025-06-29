package com.cognifyteam.cognifyapp.ui.course

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.cognifyteam.cognifyapp.R
import com.cognifyteam.cognifyapp.data.AppContainer
import com.cognifyteam.cognifyapp.data.models.Course
import com.cognifyteam.cognifyapp.data.models.Discussion
import com.cognifyteam.cognifyapp.data.models.Rating
import com.cognifyteam.cognifyapp.ui.FabState
import com.cognifyteam.cognifyapp.ui.TopBarState
import com.cognifyteam.cognifyapp.ui.common.UserViewModel

// Enum untuk merepresentasikan Tab yang aktif
enum class CourseTab {
    Overview, Discussion, Lessons, Reviews
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseScreen(
    navController: NavController,
    appContainer: AppContainer,
    courseId: String,
    onFabStateChange: (FabState) -> Unit,
    onTopBarStateChange: (TopBarState) -> Unit,
) {
    // --- Inisialisasi ViewModel ---
    val courseViewModel: CourseViewModel = viewModel(factory = CourseViewModel.provideFactory(appContainer.courseRepository))
    val discussionViewModel: DiscussionViewModel = viewModel(factory = DiscussionViewModel.provideFactory(appContainer.discussionRepository))
    val ratingViewModel: RatingViewModel = viewModel(factory = RatingViewModel.provideFactory(appContainer.ratingRepository))
    val userViewModel: UserViewModel = viewModel(factory = UserViewModel.provideFactory(appContainer.authRepository))

    // --- Amati State ---
    val discussionUiState by discussionViewModel.uiState.collectAsState()
    val courseDetailState by courseViewModel.courseDetailState.collectAsState()
    val ratingsListState by ratingViewModel.ratingsListState.collectAsState()
    val currentUser by userViewModel.userState.collectAsState()
    val context = LocalContext.current

    // --- Efek Samping ---
    LaunchedEffect(key1 = courseId) {
        discussionViewModel.loadDiscussions(courseId)
        courseViewModel.loadCourseDetails(courseId)
        ratingViewModel.loadRatings(courseId)
        onFabStateChange(FabState(isVisible = false))
        onTopBarStateChange(
            TopBarState(isVisible = true, title = "Course Details",
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        )
    }

    // --- Tampilan Utama ---
    when (val state = courseDetailState) {
        is CourseDetailUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        }
        is CourseDetailUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(text = state.message, color = MaterialTheme.colorScheme.error) }
        }
        is CourseDetailUiState.Success -> {
            val course = state.course
            var selectedTab by remember { mutableStateOf(CourseTab.Overview) }

            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                CourseBanner(course = course)
                CourseTabs(selectedTab = selectedTab, onTabSelected = { selectedTab = it })

                Box(modifier = Modifier.animateContentSize()) {
                    when (selectedTab) {
                        CourseTab.Overview -> {
                            CourseOverviewContent(course = course)
                        }
                        CourseTab.Discussion -> {
                            DiscussionSection(
                                uiState = discussionUiState,
                                onAddDiscussion = { newDiscussionText ->
                                    currentUser?.firebaseId?.let { id ->
                                        discussionViewModel.addDiscussion(courseId, id, newDiscussionText)
                                    }
                                },
                                onAddReply = { discussionId, replyText ->
                                    currentUser?.firebaseId?.let { id ->
                                        discussionViewModel.addReply(discussionId, id, replyText)
                                    }
                                }
                            )
                        }
                        CourseTab.Lessons -> ComingSoonSection(tabName = "Lessons")
                        CourseTab.Reviews -> {
                            RatingSection(
                                uiState = ratingsListState,
                                onSubmitRating = { rating, comment ->
                                    currentUser?.firebaseId?.let { id ->
                                        ratingViewModel.submitRating(courseId, id, rating, comment)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// Composable baru untuk konten tab Overview
@Composable
fun CourseOverviewContent(course: Course) {
    Column {
        CourseDetails(course = course)
        CourseInstructor(course = course)
        CourseActions(course = course)
    }
}

// Composable placeholder untuk tab yang belum diimplementasikan
@Composable
fun ComingSoonSection(tabName: String) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("$tabName content is coming soon!", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun RatingSection(
    uiState: RatingsListUiState, // Menerima state daftar semua rating
    onSubmitRating: (rating: Int, comment: String) -> Unit
) {
    var selectedRating by remember { mutableIntStateOf(0) }
    var comment by remember { mutableStateOf("") }
    // `isSubmitting` bisa kita infer dari state yang lain nanti jika perlu

    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        // Form untuk memberi/mengupdate rating
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Write Your Review", /*...*/)
                // Star Rating
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    // --- GUNAKAN `repeat()` INI ---
                    repeat(5) { index ->
                        val i = index + 1 // `index` dimulai dari 0, jadi kita tambahkan 1
                        Icon(
                            imageVector = if (i <= selectedRating) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = "Star $i",
                            tint = if (i <= selectedRating) Color(0xFFFFC107) else MaterialTheme.colorScheme.outline,
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { selectedRating = if (selectedRating == i) i - 1 else i } // Logika toggle yang lebih baik
                                .padding(horizontal = 4.dp)
                        )
                    }
                    // --- AKHIR PERBAIKAN ---
                }
                // Comment TextField
                OutlinedTextField(value = comment, onValueChange = { comment = it }, /*...*/)
                // Submit Button
                Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.End) {
                    Button(
                        onClick = {
                            if (selectedRating > 0) {
                                onSubmitRating(selectedRating, comment)
                                // Reset form setelah submit
                                selectedRating = 0
                                comment = ""
                            } else {
                                Toast.makeText(context, "Please select a star rating", Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = selectedRating > 0
                    ) {
                        Text("Submit Review")
                    }
                }
            }
        }

        // Judul untuk daftar semua review
        Text(
            text = "All Reviews",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Tampilkan daftar review berdasarkan state
        when (uiState) {
            is RatingsListUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            is RatingsListUiState.Error -> {
                Text(uiState.message, color = MaterialTheme.colorScheme.error)
            }
            is RatingsListUiState.Success -> {
                if (uiState.ratings.isEmpty()) {
                    Text("No reviews yet. Be the first!", color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        uiState.ratings.forEach { rating ->
                            RatingItem(rating = rating)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RatingItem(rating: Rating) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(rating.authorName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                // Tampilkan bintang rating yang sudah ada
                Row {
                    Text("${rating.rating} ⭐", style = MaterialTheme.typography.bodyMedium)
                }
            }
            if (!rating.comment.isNullOrBlank()) {
                Text(rating.comment, modifier = Modifier.padding(top = 8.dp), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

// --- Komponen UI Lainnya ---

@Composable
fun CourseTabs(selectedTab: CourseTab, onTabSelected: (CourseTab) -> Unit) {
    TabRow(
        selectedTabIndex = selectedTab.ordinal,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        CourseTab.values().forEach { tab ->
            Tab(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                text = { Text(text = tab.name) }
            )
        }
    }
}

@Composable
fun CourseBanner(course: Course) {
    Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
        AsyncImage(
            model = "http://10.0.2.2:3000${course.thumbnail}",
            contentDescription = course.name,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.robot),
            error = painterResource(id = R.drawable.robot)
        )
    }
}

@Composable
fun CourseDetails(course: Course) {
    Column(modifier = Modifier.padding(16.dp)) {
        CourseTitleAndRating(course = course)
        InstructorInfo(course = course)
        DescriptionSection(course = course)
    }
}

@Composable
fun CourseTitleAndRating(course: Course) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = course.name,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.weight(1f)
        )
        RatingWithCount(rating = course.rating.toFloatOrNull() ?: 0.0f, count = 375)
    }
}

@Composable
fun InstructorInfo(course: Course) {
    Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Person, "Instructor", tint = MaterialTheme.colorScheme.primary)
        Text(course.course_owner_name, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(start = 4.dp))
        Spacer(Modifier.width(16.dp))
        Icon(Icons.Outlined.CheckCircle, "Lessons", tint = MaterialTheme.colorScheme.primary)
        Text("32 Lessons", color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(start = 4.dp))
    }
}

@Composable
fun DescriptionSection(course: Course) {
    var isExpanded by remember { mutableStateOf(false) }
    Column(modifier = Modifier.padding(top = 16.dp).animateContentSize()) {
        Text(
            text = course.description,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = if (isExpanded) Int.MAX_VALUE else 4,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = if (isExpanded) "Read Less" else "Read More",
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(top = 8.dp)
                .clickable { isExpanded = !isExpanded }
        )
    }
}

@Composable
fun CourseInstructor(course: Course) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = "",
                contentDescription = "Instructor",
                modifier = Modifier.size(64.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant),
                placeholder = painterResource(id = R.drawable.robot),
                error = painterResource(id = R.drawable.robot)
            )
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(course.course_owner_name, style = MaterialTheme.typography.titleMedium)
                Text("Design Tutor", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun CourseActions(course: Course) {
    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Column {
            Text(text = "Rp${course.price}", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            Text("VAT included", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("GET ENROLL", color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}

@Composable
fun DiscussionSection(
    uiState: DiscussionUiState,
    onAddDiscussion: (String) -> Unit,
    onAddReply: (discussionId: Int, replyText: String) -> Unit
) {
    var newDiscussionText by remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(
            text = "Discussion",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = newDiscussionText,
                    onValueChange = { newDiscussionText = it },
                    placeholder = { Text("Start a discussion...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.End) {
                    Button(
                        onClick = {
                            if (newDiscussionText.isNotBlank()) {
                                onAddDiscussion(newDiscussionText)
                                newDiscussionText = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Post", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Post", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        when (uiState) {
            is DiscussionUiState.Loading -> {
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            }
            is DiscussionUiState.Error -> {
                Text(text = uiState.message, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 16.dp))
            }
            is DiscussionUiState.Success -> {
                if (uiState.discussions.isEmpty()) {
                    Text("Be the first to start a discussion!", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 16.dp))
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        uiState.discussions.forEach { discussion ->
                            DiscussionItem(discussion = discussion, onAddReply = { replyText -> onAddReply(discussion.id, replyText) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DiscussionItem(discussion: Discussion, onAddReply: (String) -> Unit) {
    var showReplies by remember { mutableStateOf(false) }
    var showReplyInput by remember { mutableStateOf(false) }
    var replyText by remember { mutableStateOf("") }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                // Placeholder untuk gambar profil penulis diskusi
                Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant))
                Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                    Text(text = discussion.authorName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                    Text(text = discussion.createdAt, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Text(text = discussion.content, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 12.dp))
            Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                TextButton(onClick = { showReplyInput = !showReplyInput }) {
                    Icon(Icons.Default.Send, contentDescription = "Reply", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reply", color = MaterialTheme.colorScheme.primary)
                }
                if (discussion.replies.isNotEmpty()) {
                    TextButton(onClick = { showReplies = !showReplies }) {
                        Text("${if (showReplies) "Hide" else "See"} ${discussion.replies.size} ${if (discussion.replies.size == 1) "reply" else "replies"}", color = MaterialTheme.colorScheme.primary)
                        Icon(if (showReplies) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
            if (showReplyInput) {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
                    OutlinedTextField(value = replyText, onValueChange = { replyText = it }, placeholder = { Text("Write a reply...") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                    Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showReplyInput = false; replyText = "" }) { Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        Button(onClick = {
                            if (replyText.isNotBlank()) {
                                onAddReply(replyText)
                                replyText = ""
                                showReplyInput = false
                                showReplies = true
                            }
                        }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
                            Text("Reply", color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }
            }
            if (showReplies && discussion.replies.isNotEmpty()) {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 12.dp, start = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    discussion.replies.forEach { reply -> ReplyItem(reply = reply) }
                }
            }
        }
    }
}

@Composable
fun ReplyItem(reply: Discussion) {
    Row(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(8.dp)).padding(12.dp)) {
        Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant))
        Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = reply.authorName, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                Text(text = reply.createdAt, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(text = reply.content, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp))
        }
    }
}


@Composable
fun RatingWithCount(rating: Float, count: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Outlined.Star, contentDescription = "Rating", tint = Color(0xFFFFC107), modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = "$rating ($count Reviews)", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
    }
}