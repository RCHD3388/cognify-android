package com.cognifyteam.cognifyapp.ui.learningpath.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.cognifyteam.cognifyapp.data.AppContainer
import com.cognifyteam.cognifyapp.data.models.LearningPath
import com.cognifyteam.cognifyapp.data.models.SmartComment
import com.cognifyteam.cognifyapp.ui.FabState
import com.cognifyteam.cognifyapp.ui.TopBarState
import com.cognifyteam.cognifyapp.ui.common.UserViewModel
import com.cognifyteam.cognifyapp.ui.learningpath.viewmodel.Comment
import com.cognifyteam.cognifyapp.ui.learningpath.viewmodel.LearningPathDetailViewModel
import com.cognifyteam.cognifyapp.ui.theme.CognifyApplicationTheme

// --- UI SCREEN & COMPOSABLES ---

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LearningPathDetailScreen(
    appContainer: AppContainer?,
    navController: NavController,
    onFabStateChange: (FabState) -> Unit,
    onTopBarStateChange: (TopBarState) -> Unit,
    onShowSnackbar: (String) -> Unit,
    learningPathId: Int
) {
    val viewModel: LearningPathDetailViewModel = viewModel(
        factory = LearningPathDetailViewModel.provideFactory(
            smartRepository = appContainer!!.smartRepository
        )
    )
    val uiState by viewModel.uiState.collectAsState()

    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModel.provideFactory(
            authRepository = appContainer.authRepository
        )
    )
    val currentUser by userViewModel.userState.collectAsState()

    LaunchedEffect(learningPathId) {
        onFabStateChange(FabState(isVisible = false))
        onTopBarStateChange(TopBarState(isVisible = true,
            title = "Detail Learning Path",
            navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                }
            },
        ))
        viewModel.loadLearningPath(learningPathId)
    }

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (uiState.learningPath != null) {
        val path = uiState.learningPath!!
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item { PathDetailHeader(path) }
            item { PathDescription(path) }
            item { ActionButtons(userViewModel, path, onLikeClicked = { viewModel.toggleLike(currentUser!!.firebaseId) }) }
            item {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                StepsHeader()
            }
            itemsIndexed(path.steps) { index, step ->
                LearningPathStepItem(
                    step = step,
                    isLastItem = index == path.steps.lastIndex
                )
            }
            // --- BAGIAN KOMENTAR BARU DITAMBAHKAN DI SINI ---
            item { Divider(modifier = Modifier.padding(vertical = 8.dp)) }
            item {
                CommentHeader(commentCount = path.comments.size)
            }
            item {
                CommentInput(
                    commentInput = uiState.commentInput,
                    onCommentInputChange = { viewModel.onCommentInputChange(it) },
                    onPostComment = { viewModel.postComment(currentUser!!.firebaseId) }
                )
            }
            items(path.comments, key = { it.id }) { comment ->
                CommentItem(comment = comment)
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Learning Path tidak ditemukan.")
        }
    }
}

@Composable
fun CommentHeader(commentCount: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.MailOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Comments ($commentCount)",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CommentInput(
    commentInput: String,
    onCommentInputChange: (String) -> Unit,
    onPostComment: () -> Unit
) {
    val isButtonEnabled = commentInput.isNotBlank()

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = commentInput,
            onValueChange = onCommentInputChange,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp),
            placeholder = { Text("Bagikan pengalaman atau pertanyaan Anda tentang learning path ini...") },
            shape = RoundedCornerShape(12.dp),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${commentInput.length} / 500",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(
                onClick = onPostComment,
                enabled = isButtonEnabled,
                shape = RoundedCornerShape(50)
                // Warna akan otomatis diatur oleh MaterialTheme berdasarkan status enabled/disabled
            ) {
                Text("Sent")
            }
        }
    }
}

@Composable
fun CommentItem(comment: SmartComment) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = comment.getAuthorInitial(),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
        // Konten Komentar
        Column(modifier = Modifier.weight(1f)) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = comment.author_name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "• ${comment.createdAt}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun PathDetailHeader(path: com.cognifyteam.cognifyapp.data.models.LearningPath) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = path.getAuthorInitial(),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = path.author_name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = path.createdAt, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = " • ", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.tertiaryContainer
        ) {
            Text(
                text = path.level,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PathDescription(path: com.cognifyteam.cognifyapp.data.models.LearningPath) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = path.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = path.description,
            style = MaterialTheme.typography.bodyLarge,
            lineHeight = 24.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            path.tags.forEach { tag ->
                SuggestionChip(
                    onClick = { },
                    label = { Text(tag) }
                )
            }
        }
    }
}

@Composable
fun ActionButtons(viewModel: UserViewModel, path: LearningPath, onLikeClicked: () -> Unit) {
    val currentUser = viewModel.userState.collectAsState().value
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onLikeClicked,
            shape = RoundedCornerShape(50)
        ) {
            Icon(
                imageVector = if (path.likes.any{ it.userId == currentUser!!.firebaseId}) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = "Like",
                tint = if (path.likes.any{ it.userId == currentUser!!.firebaseId}) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = path.likes.size.toString())
        }
        Button(
            onClick = { /* Navigasi ke halaman belajar */ },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text("Mulai Belajar", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun StepsHeader() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Langkah-langkah Pembelajaran",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

// --- PREVIEW ---
@Preview(showBackground = true, name = "Detail Screen Light Mode")
@Composable
fun LearningPathDetailScreenPreview() {
    CognifyApplicationTheme {
        LearningPathDetailScreen(
            appContainer = null,
            navController = rememberNavController(),
            onFabStateChange = { /* Do nothing in preview */ },
            onTopBarStateChange = { /* Do nothing in preview */ },
            onShowSnackbar = { /* Do nothing in preview */ },
            learningPathId = 1
        )
    }
}

@Preview(showBackground = true, name = "Detail Screen Dark Mode")
@Composable
fun LearningPathDetailScreenDarkPreview() {
    CognifyApplicationTheme(darkTheme = true) {
        LearningPathDetailScreen(
            appContainer = null,
            navController = rememberNavController(),
            onFabStateChange = { /* Do nothing in preview */ },
            onTopBarStateChange = { /* Do nothing in preview */ },
            onShowSnackbar = { /* Do nothing in preview */ },
            learningPathId = 1
        )
    }
}