package com.cognifyteam.cognifyapp.ui.learningpath.screen

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cognifyteam.cognifyapp.data.AppContainer
import com.cognifyteam.cognifyapp.data.models.LearningPathStep
import com.cognifyteam.cognifyapp.ui.FabState
import com.cognifyteam.cognifyapp.ui.TopBarState
import com.cognifyteam.cognifyapp.ui.auth.AuthViewModel
import com.cognifyteam.cognifyapp.ui.auth.rememberGoogleSignLauncher
import com.cognifyteam.cognifyapp.ui.common.UserViewModel
import com.cognifyteam.cognifyapp.ui.theme.CognifyApplicationTheme
import kotlinx.coroutines.launch

@Composable
fun AddNewLearningPathScreen(
    appContainer: AppContainer?,
    navController: NavController?,
    onFabStateChange: (FabState) -> Unit,
    onTopBarStateChange: (TopBarState) -> Unit,
    onShowSnackbar: (String) -> Unit,
) {
    // add new learning path view model
    val viewModel: AddNewLearningPathViewModel = viewModel(
        factory = AddNewLearningPathViewModel.provideFactory(
            smartRepository = appContainer!!.smartRepository
        )
    )
    val uiState by viewModel.uiState.collectAsState()

    // user view model
    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModel.provideFactory(
            authRepository = appContainer.authRepository
        )
    )

    val currentUser by userViewModel.userState.collectAsState()

    // Top Bar tetap sama untuk semua state
    LaunchedEffect(Unit) {
        viewModel.resetState()
        onTopBarStateChange(
            TopBarState(
                isVisible = true,
                title = if (uiState.screenState == LearningPathScreenState.FORM) "Buat Learning Path" else "Hasil Learning Path",
                navigationIcon = {
                    IconButton(onClick = { navController?.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                }
            )
        )
        onFabStateChange(FabState(isVisible = false))
    }

    val context = LocalContext.current

    LaunchedEffect(uiState) {
        if(uiState.screenState == LearningPathScreenState.FAILED_GENERATE || uiState.screenState == LearningPathScreenState.SAVE_RESULT){
            Toast.makeText(context, uiState.screenMessage, Toast.LENGTH_LONG).show()
            viewModel.resetState()
        }
    }

    // Menggunakan 'when' untuk menampilkan UI berdasarkan state
    when (uiState.screenState) {
        LearningPathScreenState.FORM, LearningPathScreenState.FAILED_GENERATE-> FormContent(uiState, viewModel, onShowSnackbar)
        LearningPathScreenState.LOADING -> LoadingContent()
        LearningPathScreenState.RESULT, LearningPathScreenState.SAVE_RESULT -> ResultContent(uiState, viewModel, userViewModel)
    }
}

// --- Composable untuk Setiap State Layar ---

@Composable
fun FormContent(
    uiState: AddLearningPathUiState,
    viewModel: AddNewLearningPathViewModel,
    onShowSnackbar: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                Section(title = "Pilih Topik", helperText = "Pilih dari topik yang tersedia atau masukkan topik khusus yang ingin Anda pelajari.") {
                    TopicSelectionGrid(topics = uiState.predefinedTopics, selectedTopic = uiState.selectedTopic, onTopicSelected = viewModel::onTopicSelected)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = uiState.customTopic,
                        onValueChange = viewModel::onCustomTopicChanged,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Atau masukkan topik kustom...") },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }
            }
            item {
                Section(title = "Prompt Tambahan", helperText = "Berikan konteks tambahan untuk mendapatkan learning path yang lebih personal dan sesuai kebutuhan.") {
                    OutlinedTextField(
                        value = uiState.additionalPrompt,
                        onValueChange = viewModel::onAdditionalPromptChanged,
                        modifier = Modifier.fillMaxWidth().height(150.dp),
                        shape = RoundedCornerShape(12.dp),
                        placeholder = { Text("- Tolong sertakan proyek praktis...\n- Saya sudah paham HTML/CSS...") }
                    )
                    Text(text = "${uiState.additionalPrompt.length} / 500", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.fillMaxWidth().padding(top = 4.dp), textAlign = TextAlign.End)
                }
            }
            item {
                Section(title = "Level Pembelajaran", helperText = "Pilih level yang sesuai dengan pengetahuan dan pengalaman Anda saat ini.") {
                    LevelSelector(levels = uiState.learningLevels, selectedLevel = uiState.selectedLevel, onLevelSelected = viewModel::onLevelSelected)
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }

        Button(
            onClick = { viewModel.onGenerateClicked() },
            enabled = uiState.isGenerateButtonEnabled,
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp).height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Text(text = "Generate Learning Path", fontSize = 16.sp)
        }
    }
}

@Composable
fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Sedang membuat learning path...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun ResultContent(
    uiState: AddLearningPathUiState,
    viewModel: AddNewLearningPathViewModel,
    userViewModel: UserViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentUser by userViewModel.userState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Kotak pesan sukses
        SuccessMessageCard()

        // Judul Learning Path
        OutlinedTextField(
            value = uiState.learningPathTitle,
            onValueChange = { viewModel.onLearningPathTitleChanged(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp),
            label = { Text("Judul Learning Path") },
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        // Daftar Learning Path yang bisa di-scroll
        LazyColumn(
            modifier = Modifier.weight(1f) // PENTING: Membuat list ini bisa scroll & mengisi ruang
        ) {
            item {
                // Judul
                Text(
                    text = "Learning Path Anda",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
                )
            }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(uiState.generatedPath!!.tags) { tag ->
                        SkillTag(text = tag, MaterialTheme.typography.bodyLarge)
                    }
                }
            }
            item {
                // Main Description
                Text(
                    text = "This learning path is designed for intermediate learners who want to solidify their web development skills and build more complex web applications. You'll delve deeper into front-end frameworks, back-end development, and database integration. The goal is to equip you with the knowledge and practical experience to tackle real-world web development projects.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp, top = 8.dp)
                )
            }
            itemsIndexed(uiState.generatedPath!!.paths) { index, step ->
                LearningPathStepItem(
                    step = step,
                    isLastItem = index == uiState.generatedPath!!.paths.lastIndex
                )
            }
        }

        // Tombol Aksi di Bawah
        BottomActionButtons(
            onRegenerate = { viewModel.onRegenerateClicked() },
            onSave = { viewModel.onSaveClicked(currentUser!!.firebaseId ?: "") },
            canSave = uiState.isSaveButtonEnabled
        )
    }
}

// --- Composable Pendukung untuk Tampilan Hasil ---

@Composable
fun SuccessMessageCard() {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            // Warna hijau dari gambar bisa dipetakan ke 'tertiary' di theme kita
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Success",
                tint = MaterialTheme.colorScheme.onTertiaryContainer
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Learning path berhasil dibuat!",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Text(
                    text = "Silakan review dan simpan jika sudah sesuai.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
    }
}

@Composable
fun LearningPathStepItem(step: LearningPathStep, isLastItem: Boolean) {
    Row(
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(end = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = step.stepNumber.toString(),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
            if (!isLastItem) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(110.dp)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        }

        Column(
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Text(text = step.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = step.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.DateRange, // SEBELUMNYA: HourglassTop
                    contentDescription = "Estimasi",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Estimasi: ${step.estimatedTime}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun BottomActionButtons(onRegenerate: () -> Unit, onSave: () -> Unit, canSave: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedButton(
            onClick = onRegenerate,
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text("Regenerate Path")
        }
        Button(
            onClick = onSave,
            enabled = canSave,
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize)) // SEBELUMNYA: Save
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text("Save Path")
        }
    }
}

// --- TIDAK ADA PERUBAHAN PADA COMPOSABLE LAINNYA DI BAWAH INI ---

@Composable
fun LevelCard(
    modifier: Modifier = Modifier,
    level: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline

    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(2.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = level, fontWeight = FontWeight.Bold, color = contentColor)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = description, style = MaterialTheme.typography.bodySmall, color = contentColor)
        }
    }
}


@Composable
fun TopicSelectionGrid(
    topics: List<String>,
    selectedTopic: String?,
    onTopicSelected: (String) -> Unit
) {
    LazyHorizontalGrid(
        rows = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(topics) { topic ->
            FilterChip(
                selected = topic == selectedTopic,
                onClick = { onTopicSelected(topic) },
                label = { Text(topic) },
                leadingIcon = if (topic == selectedTopic) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Selected",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else {
                    null
                }
            )
        }
    }
}

@Composable
fun Section(
    title: String,
    helperText: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        content()
        Text(
            text = helperText,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun LevelSelector(
    levels: List<String>,
    selectedLevel: String,
    onLevelSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        levels.forEach { level ->
            val isSelected = level == selectedLevel
            val descriptions = mapOf("Pemula" to "Mulai dari dasar", "Menengah" to "Sudah ada basic", "Lanjutan" to "Tingkat expert")

            LevelCard(
                modifier = Modifier.weight(1f),
                level = level,
                description = descriptions[level] ?: "",
                isSelected = isSelected,
                onClick = { onLevelSelected(level) }
            )
        }
    }
}

@Preview(showBackground = true, name = "Add New Learning Path Screen (Light)")
@Composable
fun AddNewLearningPathScreenPreview() {
    CognifyApplicationTheme {
        AddNewLearningPathScreen(
            appContainer = null,
            navController = null,
            onFabStateChange = {},
            onTopBarStateChange = {},
            onShowSnackbar = {}
        )
    }
}

@Preview(showBackground = true, name = "Add New Learning Path Screen (Dark)")
@Composable
fun AddNewLearningPathScreenDarkPreview() {
    CognifyApplicationTheme(darkTheme = true) {
        AddNewLearningPathScreen(
            appContainer = null,
            navController = null,
            onFabStateChange = {},
            onTopBarStateChange = {},
            onShowSnackbar = {}
        )
    }
}