package com.cognifyteam.cognifyapp.ui.course

import android.app.Activity
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.cognifyteam.cognifyapp.R
import com.cognifyteam.cognifyapp.data.AppContainer
import com.cognifyteam.cognifyapp.data.models.Course
import com.cognifyteam.cognifyapp.data.models.Discussion
import com.cognifyteam.cognifyapp.ui.FabState
import com.cognifyteam.cognifyapp.ui.TopBarState
import com.cognifyteam.cognifyapp.ui.common.UserViewModel

@Composable
fun getActivity(): Activity {
    val context = LocalContext.current
    return remember {
        generateSequence(context) {
            when (it) {
                is android.content.ContextWrapper -> it.baseContext
                else -> null
            }
        }.filterIsInstance<Activity>().first()
    }
}
@Composable
fun CourseScreen(
    navController: NavController,
    appContainer: AppContainer,
    courseId: String,
    onFabStateChange: (FabState) -> Unit,
    onTopBarStateChange: (TopBarState) -> Unit,
    onShowSnackbar: (String) -> Unit,
) {
    val courseViewModel: CourseViewModel = viewModel(
        factory = CourseViewModel.provideFactory(
            courseRepository = appContainer.courseRepository
        )
    )
    val discussionViewModel: DiscussionViewModel = viewModel(
        factory = DiscussionViewModel.provideFactory(
            discussionRepository = appContainer.discussionRepository
        )
    )
    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModel.provideFactory(
            authRepository = appContainer.authRepository
        )
    )

    val loggedInUser by userViewModel.userState.collectAsState()
    val discussionUiState by discussionViewModel.uiState.collectAsState()
    val courseDetailState by courseViewModel.courseDetailState.collectAsState()
    val currentUser by userViewModel.userState.collectAsState()
    var showPaymentWebView by remember { mutableStateOf(false) }
    var paymentSnapToken by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    // --- LaunchedEffect yang sudah ada ---
    LaunchedEffect(key1 = courseId) {
        discussionViewModel.loadDiscussions(courseId)
        courseViewModel.loadCourseDetails(courseId)

        onFabStateChange(FabState(isVisible = false))
        onTopBarStateChange(
            TopBarState(
                isVisible = true,
                title = "Course Details",
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Handle menu click */ }) {
                        Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )
        )
    }

    // --- UI UTAMA (TIDAK BERUBAH BANYAK) ---
    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = courseDetailState) {
            is CourseDetailUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is CourseDetailUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.message, color = Color.Red)
                }
            }

            is CourseDetailUiState.Success -> {
                val course = state.course
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    CourseBanner(course = course)
                    CourseTabs()
                    CourseDetails(course = course)
                    // CourseInstructor(course = course) // Dikomentari sesuai kode Anda
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
                    // --- Teruskan ViewModel ke CourseActions ---
                    CourseActions(
                        course = course,
                        viewModel = courseViewModel,
                        firebaseId = loggedInUser!!.firebaseId,
                        onEnrollClick = { token ->
                            // Callback ini akan dipanggil dari CourseActions
                            paymentSnapToken = token
                            showPaymentWebView = true
                        }
                    )
                }
            }
        }
    }
    if (showPaymentWebView && paymentSnapToken != null) {
        PaymentWebView(snapToken = paymentSnapToken!!) {
            // Callback saat pembayaran selesai
            showPaymentWebView = false
            paymentSnapToken = null
            Toast.makeText(context, "Pembayaran selesai atau ditutup.", Toast.LENGTH_LONG).show()
            // Mungkin refresh halaman atau navigasi ke halaman "My Courses"
            navController.popBackStack()
        }
    }
}


// --- SEMUA KOMPONEN UI DI BAWAH INI (TIDAK BERUBAH, KECUALI CourseActions) ---

@Composable
fun CourseBanner(course: Course) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        AsyncImage(
            model = course.thumbnail,
            contentDescription = course.name,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.logo_google),
            error = painterResource(id = R.drawable.logo_google)
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
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = course.name,
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.weight(1f))
        RatingWithCount(rating = course.rating.toFloatOrNull() ?: 0.0f, count = 375)
    }
}

@Composable
fun InstructorInfo(course: Course) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Person, "Instructor", tint = Color(0xFF4A90E2))
        Text(course.course_owner_name, color = Color(0xFF4A90E2), modifier = Modifier.padding(start = 4.dp))
        Spacer(Modifier.width(16.dp))
        Icon(Icons.Outlined.CheckCircle, "Lessons", tint = Color(0xFF4A90E2))
        Text("32 Lessons", color = Color(0xFF4A90E2), modifier = Modifier.padding(start = 4.dp))
    }
}

@Composable
fun DescriptionSection(course: Course) {
    Text(
        course.description,
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


@Composable
fun CourseActions(
    course: Course,
    viewModel: CourseViewModel,
    firebaseId: String,
    onEnrollClick: (snapToken: String) -> Unit // Menggunakan callback
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                "Rp${course.price}",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Text("VAT included", color = Color(0xFF6B6B6B))
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                // Panggil fungsi createPayment di ViewModel
                viewModel.createPayment(course.courseId, firebaseId = firebaseId) { token ->
                    // Panggil callback untuk memberitahu parent Composable agar menampilkan WebView
                    onEnrollClick(token)
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2))
        ) {
            Text("GET ENROLL", color = Color.White)
        }
    }
}


@Composable
fun PaymentWebView(snapToken: String, onFinished: () -> Unit) {
    val url = "https://app.sandbox.midtrans.com/snap/v2/vtweb/$snapToken"
    Log.d("PaymentWebView", "Loading URL: $url")

    AndroidView(
        modifier = Modifier.fillMaxSize(), // Ini akan mengisi seluruh Box parent
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true // PENTING!
                webViewClient = object : WebViewClient() {
                    // Cek URL untuk menentukan kapan pembayaran selesai/dibatalkan
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        Log.d("PaymentWebView", "Page finished loading: $url")
                        // Midtrans akan me-redirect ke URL yang mengandung kata "finish"
                        // setelah transaksi selesai (sukses, pending, atau gagal).
                        if (url != null && (url.contains("transaction_status") || url.contains("finish"))) {
                            onFinished()
                        }
                    }
                }
                loadUrl(url)
            }
        }
    )
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = newDiscussionText,
                    onValueChange = { newDiscussionText = it },
                    placeholder = { Text("Start a discussion...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            if (newDiscussionText.isNotBlank()) {
                                onAddDiscussion(newDiscussionText)
                                newDiscussionText = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2))
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Post", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Post")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (uiState) {
            is DiscussionUiState.Loading -> {
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is DiscussionUiState.Error -> {
                Text(text = uiState.message, color = Color.Red, modifier = Modifier.padding(vertical = 16.dp))
            }
            is DiscussionUiState.Success -> {
                if (uiState.discussions.isEmpty()) {
                    Text("Be the first to start a discussion!", modifier = Modifier.padding(vertical = 16.dp))
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        uiState.discussions.forEach { discussion ->
                            DiscussionItem(
                                discussion = discussion,
                                onAddReply = { replyText -> onAddReply(discussion.id, replyText) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DiscussionItem(
    discussion: Discussion,
    onAddReply: (String) -> Unit
) {
    var showReplies by remember { mutableStateOf(false) }
    var showReplyInput by remember { mutableStateOf(false) }
    var replyText by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = "User", /*...*/)
                Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                    Text(text = discussion.authorName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                    Text(text = discussion.createdAt, style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B6B6B))
                }
            }
            Text(text = discussion.content, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 12.dp))
            Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                TextButton(onClick = { showReplyInput = !showReplyInput }) { /*...*/ }
                if (discussion.replies.isNotEmpty()) {
                    TextButton(onClick = { showReplies = !showReplies }) {
                        Text("${if (showReplies) "Hide" else "See"} ${discussion.replies.size} ${if (discussion.replies.size == 1) "reply" else "replies"}")
                        Icon(if (showReplies) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = null)
                    }
                }
            }
            if (showReplyInput) {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
                    OutlinedTextField(value = replyText, onValueChange = { replyText = it }, /*...*/)
                    Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showReplyInput = false; replyText = "" }) { Text("Cancel") }
                        Button(onClick = {
                            if (replyText.isNotBlank()) {
                                onAddReply(replyText)
                                replyText = ""
                                showReplyInput = false
                                showReplies = true
                            }
                        }) { Text("Reply") }
                    }
                }
            }
            if (showReplies && discussion.replies.isNotEmpty()) {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 12.dp, start = 16.dp)) {
                    discussion.replies.forEach { reply ->
                        ReplyItem(reply = reply)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ReplyItem(reply: Discussion) {
    Row(modifier = Modifier.fillMaxWidth().background(Color(0xFFF8F9FA), RoundedCornerShape(8.dp)).padding(12.dp)) {
        Icon(Icons.Default.Person, contentDescription = "User", /*...*/)
        Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = reply.authorName, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                Text(text = reply.createdAt, style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B6B6B))
            }
            Text(text = reply.content, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp))
        }
    }
}
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
        TabItem(text = "Discussion", isSelected = false)
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