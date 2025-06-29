package com.cognifyteam.cognifyapp.ui.learningpath.screen

import android.widget.Toast
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.filled.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.cognifyteam.cognifyapp.data.AppContainer
import com.cognifyteam.cognifyapp.data.models.LearningPathStep
import com.cognifyteam.cognifyapp.ui.FabState
import com.cognifyteam.cognifyapp.ui.TopBarState
import com.cognifyteam.cognifyapp.ui.common.UserViewModel
import com.cognifyteam.cognifyapp.ui.theme.CognifyApplicationTheme

// --- SCREEN UTAMA (DENGAN PERUBAHAN) ---
@Composable
fun MainLearningPathScreen(
    appContainer: AppContainer? = null,
    navController: NavController? = null,
    onFabStateChange: (FabState) -> Unit,
    onTopBarStateChange: (TopBarState) -> Unit,
    onShowSnackbar: (String) -> Unit,
) {
    val viewModel: LearningPathViewModel = viewModel(
        factory = LearningPathViewModel.provideFactory(
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

    LaunchedEffect(Unit) {
        viewModel.loadInitialData()
        onFabStateChange(FabState(
            isVisible = true,
            icon = Icons.Filled.Add,
            description = "Manage Paths",
            onClick = {
                navController?.navigate("add_learning_path")
            }
        ))
        onTopBarStateChange(TopBarState(isVisible = false))
    }

    val focusManager = LocalFocusManager.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        item { HeaderSection() }

        item {
            SearchBarSection(
                searchQuery = uiState.searchQuery,
                onQueryChanged = viewModel::onSearchQueryChanged, // Bisa juga ditulis seperti ini
                onSearch = { focusManager.clearFocus() },
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
            )
        }

        item {
            FilterChipSection(
                categories = uiState.allCategories,
                selectedCategory = uiState.selectedCategory,
                onCategorySelected = viewModel::onCategorySelected // Referensi fungsi
            )
        }

        item { SectionTitle(title = "Learning Paths Terbaru", onSeeAllClicked = { /*TODO*/ }) }

        items(uiState.learningPaths, key = { it.id }) { path ->
            LearningPathCard(
                path = path,
                // Tambahkan lambda untuk event klik like
                onLikeClicked = { viewModel.onLikeClicked(path.id, currentUser!!.firebaseId) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                onClicked = { navController?.navigate("learning_path_details/${path.id}") },
                userViewModel = userViewModel
            )
        }
    }
}


// --- KOMPONEN-KOMPONEN UI (SearchBarSection DIUBAH TOTAL) ---

@Composable
fun HeaderSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.secondary,
                        MaterialTheme.colorScheme.primary
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 24.dp)
        ) {
            Text(
                text = "Smart Learning",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Temukan jalur pembelajaran yang dipersonalisasi untuk kebutuhan Anda",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            )
        }
    }
}

// --- SearchBarSection DIUBAH MENJADI TEXTFIELD FUNGSIONAL ---
@Composable
fun SearchBarSection(
    searchQuery: String,
    onQueryChanged: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = searchQuery,
        onValueChange = onQueryChanged,
        modifier = modifier
            .fillMaxWidth()
            // Trik 1: Tambahkan border di sini, bukan di dalam TextField
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                shape = RoundedCornerShape(50)
            ),
        shape = RoundedCornerShape(50), // Trik 2: Bentuk harus sama dengan border
        placeholder = {
            // Placeholder dibuat sama persis dengan Text sebelumnya
            Text(
                text = "Cari learning path, skill, atau topik...",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        leadingIcon = {
            // Ikon ditempatkan di leadingIcon
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch() }),
        colors = TextFieldDefaults.colors(
            // Trik 3: Kustomisasi warna untuk mencocokkan desain Card
            // Latar belakang dibuat semi-transparan seperti sebelumnya
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),

            // Warna text dan cursor
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            cursorColor = MaterialTheme.colorScheme.primary,

            // Trik 4: Menghilangkan garis bawah (indicator) bawaan TextField
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}


@Composable
fun LearningPathCard(
    path: com.cognifyteam.cognifyapp.data.models.LearningPath,
    onLikeClicked: () -> Unit, // Tambahkan parameter untuk event klik
    onClicked: () -> Unit, // Tambahkan parameter untuk event klik
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel
) {
    val currentUser by userViewModel.userState.collectAsState()
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClicked
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // ... Row Avatar, Title, Description, Tags tidak berubah ...
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Avatar(initials = path.getAuthorInitial())
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = path.author_name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = path.createdAt,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                LevelTag(text = path.level)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = path.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = path.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(path.tags) { tag ->
                    SkillTag(text = tag)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            // --- Row Statistik (DIPERBARUI) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gunakan StatItem yang sudah diperbarui
                StatItem(
                    iconFilled = Icons.Filled.Favorite,
                    iconOutlined = Icons.Outlined.Favorite, // Ikon baru
                    count = path.likes.size.toString(),
                    isFilled = path.likes.any { it.userId == currentUser!!.firebaseId }, // Status dari data
                    // Gunakan warna `error` dari tema untuk like
                    color = MaterialTheme.colorScheme.error,
                    onClick = onLikeClicked // Teruskan event klik
                )
                Spacer(modifier = Modifier.width(16.dp))
                // Item komentar tidak interaktif (tidak ada onClick)
                StatItem(
                    iconFilled = Icons.Default.Send, // Tetap gunakan satu ikon
                    count = path.comments.size.toString()
                )
                Spacer(modifier = Modifier.weight(1f))

                // --- StatItem untuk "enrolled" DIHAPUS ---
            }
        }
    }
}

// --- KOMPONEN LAINNYA (TIDAK ADA PERUBAHAN) ---
@Composable
fun FilterChipSection(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            val isSelected = category == selectedCategory
            FilterChip(
                text = category,
                isSelected = isSelected,
                onClick = { onCategorySelected(category) }
            )
        }
    }
}

@Composable
fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    val border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

    Button(
        onClick = onClick,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        border = border,
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun SectionTitle(title: String, onSeeAllClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        TextButton(onClick = onSeeAllClicked) {
            Text(
                text = "Lihat Semua",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun Avatar(initials: String) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
fun LevelTag(text: String) {
    val backgroundColor = when (text.lowercase()) {
        "pemula" -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.7f)
        "menengah" -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
        else -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
    }
    val contentColor = when (text.lowercase()) {
        "pemula" -> MaterialTheme.colorScheme.onTertiaryContainer
        "menengah" -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSecondaryContainer
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = backgroundColor
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = contentColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}


@Composable
fun SkillTag(text: String, textStyle: TextStyle = MaterialTheme.typography.bodySmall) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
    ) {
        Text(
            text = text,
            style = textStyle,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun StatItem(
    iconFilled: ImageVector,
    iconOutlined: ImageVector? = null, // Ikon outline opsional
    count: String,
    isFilled: Boolean = true, // Default terisi (untuk item non-interaktif)
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onClick: (() -> Unit)? = null // Event klik opsional
) {
    // Pilih ikon berdasarkan state `isFilled`
    val icon = if (isFilled) iconFilled else (iconOutlined ?: iconFilled)

    // Buat Row bisa diklik jika ada fungsi onClick
    val rowModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }

    Row(
        modifier = rowModifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null, // Akan lebih baik jika ada deskripsi yang tepat
            modifier = Modifier.size(18.dp),
            // Warna ikon sekarang mengikuti `color` jika terisi, atau default jika outline
            tint = if (isFilled) color else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = count,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            // Warna teks juga mengikuti warna ikon agar konsisten
            color = if (isFilled) color else MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
        )
    }
}

@Preview(showBackground = true, name = "Main Learning Path Screen - Light")
@Composable
fun MainLearningPathScreenPreview() {
    CognifyApplicationTheme {
        MainLearningPathScreen(
            appContainer = null,
            navController = rememberNavController(),
            onFabStateChange = { /* Do nothing in preview */ },
            onTopBarStateChange = { /* Do nothing in preview */ },
            onShowSnackbar = { /* Do nothing in preview */ }
        )
    }
}

@Preview(showBackground = true, name = "Main Learning Path Screen - Dark")
@Composable
fun MainLearningPathScreenDarkPreview() {
    CognifyApplicationTheme(darkTheme = true) {
        MainLearningPathScreen(
            appContainer = null,
            navController = rememberNavController(),
            onFabStateChange = { /* Do nothing in preview */ },
            onTopBarStateChange = { /* Do nothing in preview */ },
            onShowSnackbar = { /* Do nothing in preview */ }
        )
    }
}