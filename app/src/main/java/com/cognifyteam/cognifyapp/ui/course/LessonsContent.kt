package com.cognifyteam.cognifyapp.ui.course

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit

import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cognifyteam.cognifyapp.data.models.Material
import com.cognifyteam.cognifyapp.data.models.Section
import com.squareup.moshi.Moshi
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

// DIUBAH: Fungsi ini sekarang menerima lebih banyak parameter
@Composable
fun LessonsContent(
    sectionUiState: SectionUiState,
    materialUiStateMap: Map<String, MaterialUiState>,
    onSectionClick: (sectionId: String) -> Unit,
    navController: NavController,
    isOwner: Boolean, // <-- Parameter baru
    enrollmentState: EnrollmentCheckState // <-- Parameter baru
) {
    val hasAccess = when (enrollmentState) {
        is EnrollmentCheckState.Checked -> isOwner || enrollmentState.isEnrolled
        else -> isOwner // Jika belum dicek, asumsikan hanya owner yang bisa lihat
    }

    if (hasAccess) {
        when (sectionUiState) {
            is SectionUiState.Loading -> { /* ... (tidak berubah) */
            }

            is SectionUiState.Error -> { /* ... (tidak berubah) */
            }

            is SectionUiState.Success -> {
                if (sectionUiState.sections.isEmpty()) {
                    ComingSoonSection("Lessons")
                } else {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 1000.dp), // Cegah nested scrolling
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(sectionUiState.sections) { index, section ->
                            SectionItem(
                                section = section,
                                sectionNumber = index + 1,
                                materialsState = materialUiStateMap[section.id], // Ambil state materi untuk section ini
                                onSectionClick = { onSectionClick(section.id) },
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }
    else {
        // Tampilkan pesan "terkunci" jika tidak punya akses
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Locked",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Access Denied",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "You must enroll in this course to access the lessons.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

// DIUBAH: SectionItem dibuat lebih interaktif
@Composable
fun SectionItem(
    section: Section,
    sectionNumber: Int,
    materialsState: MaterialUiState?, // State bisa null jika belum pernah diklik
    onSectionClick: () -> Unit,
    navController: NavController
) {
    var isExpanded by remember { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            // Baris header section yang bisa diklik
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        isExpanded = !isExpanded
                        // Panggil onSectionClick hanya saat membuka
                        if (isExpanded) {
                            onSectionClick()
                        }
                    }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Section $sectionNumber: ${section.title}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                // Ikon panah yang berputar
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expand",
                    modifier = Modifier.rotate(rotationAngle)
                )
            }

            // Konten materi yang bisa muncul/hilang
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)) {
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    when (materialsState) {
                        is MaterialUiState.Loading -> {
                            Box(modifier = Modifier.fillMaxWidth().padding(8.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            }
                        }
                        is MaterialUiState.Error -> {
                            Text(materialsState.message, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(8.dp))
                        }
                        is MaterialUiState.Success -> {
                            if(materialsState.materials.isEmpty()){
                                Text("No materials in this section.", modifier = Modifier.padding(8.dp))
                            } else {
                                materialsState.materials.forEach { material ->
                                    MaterialItem(material = material, navController = navController)
                                }
                            }
                        }
                        null -> {
                            // State awal sebelum section diklik
                            // Bisa juga diisi loading indicator
                        }
                    }
                }
            }
        }
    }
}

// DIUBAH: MaterialItem dibuat lebih informatif
@Composable
fun MaterialItem(
    material: Material,
    navController: NavController // <-- TAMBAHKAN INI
) {
    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    val jsonAdapter = moshi.adapter(Material::class.java)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                // 1. Ubah objek Material menjadi string JSON
                val materialJson = jsonAdapter.toJson(material)
                // 2. Encode URL agar aman dikirim sebagai argumen
                val encodedJson = URLEncoder.encode(materialJson, StandardCharsets.UTF_8.toString())
                // 3. Navigasi ke layar material
                navController.navigate("material_screen/$encodedJson")
            }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = when (material.material_type) {
                "video" -> Icons.Default.PlayArrow
                "document" -> Icons.Default.Edit
                else -> Icons.Default.Menu
            },
            contentDescription = "Material Type",
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = material.title, style = MaterialTheme.typography.bodyLarge)
    }
}