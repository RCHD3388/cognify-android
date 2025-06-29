package com.cognifyteam.cognifyapp.ui.course.addcourse

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog


@Composable
fun SectionsManager(
    sections: List<SectionState>,
    onAddSection: (String) -> Unit,
    onRemoveSection: (Int) -> Unit,
    onAddMaterial: (Int, MaterialState) -> Unit,
    onRemoveMaterial: (Int, Int) -> Unit,
    onUpdateMaterial: (Int, Int, MaterialState) -> Unit,
    onMaterialViewFileClick: (Uri) -> Unit
) {

    var showAddSectionDialog by remember { mutableStateOf(false) }
    var showAddMaterialDialogForSection by remember { mutableStateOf<Int?>(null) }
    var viewingMaterial by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    if (showAddSectionDialog) {
        AddSectionDialog(
            onDismiss = { showAddSectionDialog = false },
            onConfirm = { title ->
                onAddSection(title)
                showAddSectionDialog = false
            }
        )
    }

    if (showAddMaterialDialogForSection != null) {
        val sectionIndex = showAddMaterialDialogForSection!!
        AddMaterialDialog(
            onDismiss = { showAddMaterialDialogForSection = null },
            onConfirm = { material ->
                onAddMaterial(sectionIndex, material)
                showAddMaterialDialogForSection = null
            }
        )
    }
    viewingMaterial?.let { (sectionIndex, materialIndex) ->
        val material = sections[sectionIndex].materials[materialIndex]
        MaterialDetailDialog(
            initialMaterial = material,
            onDismiss = { viewingMaterial = null },
            onConfirm = { updatedMaterial ->
                onUpdateMaterial(sectionIndex, materialIndex, updatedMaterial)
                viewingMaterial = null
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Course Content", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Medium)

        sections.forEachIndexed { index, section ->
            SectionItem(
                section = section,
                onAddMaterialClick = { showAddMaterialDialogForSection = index },
                onRemoveSectionClick = { onRemoveSection(index) },
                onRemoveMaterialClick = { materialIndex -> onRemoveMaterial(index, materialIndex) },
                onMaterialClick = { materialIndex ->
                    // Set material yang akan dilihat saat diklik
                    viewingMaterial = Pair(index, materialIndex)
                },
                onMaterialViewFileClick = onMaterialViewFileClick
            )
        }

        Button(
            onClick = { showAddSectionDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8F0FE), contentColor = Color(0xFF4A90E2))
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Section")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Section")
        }
    }
}

@Composable
fun SectionItem(
    section: SectionState,
    onAddMaterialClick: () -> Unit,
    onRemoveSectionClick: () -> Unit,
    onRemoveMaterialClick: (Int) -> Unit,
    onMaterialClick: (Int) -> Unit, // <-- Tambahkan parameter ini
    onMaterialViewFileClick: (Uri) -> Unit // <-- TAMBAHKAN INI
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(section.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            IconButton(onClick = onRemoveSectionClick) {
                Icon(Icons.Default.Delete, contentDescription = "Remove Section", tint = Color.Gray)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        section.materials.forEachIndexed { index, material ->
            MaterialItem(
                material = material,
                onRemoveClick = { onRemoveMaterialClick(index) },
                onItemClick = { onMaterialClick(index) }, // <-- Panggil lambda di sini
                onViewFileClick = onMaterialViewFileClick
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        TextButton(onClick = onAddMaterialClick, modifier = Modifier.align(Alignment.End)) {
            Text("+ Add Material")
        }
    }
}

@Composable
fun MaterialItem(
    material: MaterialState,
    onRemoveClick: () -> Unit,
    onItemClick: () -> Unit ,
    onViewFileClick: (Uri) -> Unit // <-- TAMBAHKAN INI
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFFF8F9FA))
            .clickable { onItemClick() } // <-- Tambahkan modifier ini
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = when (material.material_type) {
                "video" -> Icons.Default.PlayArrow
                "document" -> Icons.Default.Edit
                else -> Icons.Default.List
            },
            contentDescription = "Material Type",
            tint = Color(0xFF4A90E2)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(material.title, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        material.fileUri?.let { uri ->
            IconButton(onClick = { onViewFileClick(uri) }, modifier = Modifier.size(20.dp)) {
                // Anda bisa menggunakan ikon lain jika mau, misal Icons.Default.Visibility
                Icon(
                    imageVector = Icons.Default.List, // Contoh ikon, ganti sesuai selera
                    contentDescription = "View File",
                    tint = Color.Gray
                )
            }
        }
        IconButton(onClick = onRemoveClick, modifier = Modifier.size(20.dp)) {
            Icon(Icons.Default.Delete, contentDescription = "Remove Material", tint = Color.LightGray)
        }
    }
}

@Composable
fun AddSectionDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(12.dp), color = Color.White) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Add New Section", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Section Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { if (title.isNotBlank()) onConfirm(title) }) { Text("Add") }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMaterialDialog(onDismiss: () -> Unit, onConfirm: (MaterialState) -> Unit) {
    var materialState by remember { mutableStateOf(MaterialState()) }
    var expanded by remember { mutableStateOf(false) }
    val materialTypes = listOf("document", "video", "Other")
    val context = LocalContext.current

    // Launcher untuk memilih file
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            materialState = materialState.copy(fileUri = it)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(12.dp), color = Color.White) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Add New Material", style = MaterialTheme.typography.titleLarge)

                OutlinedTextField(
                    value = materialState.title,
                    onValueChange = { materialState = materialState.copy(title = it) },
                    label = { Text("Material Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = materialState.description,
                    onValueChange = { materialState = materialState.copy(description = it) },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Dropdown untuk memilih tipe material
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(
                        value = materialState.material_type,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Material Type") },
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, "Dropdown") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        materialTypes.forEach { type ->
                            DropdownMenuItem(text = { Text(type) }, onClick = {
                                // Reset fileUri saat tipe berubah
                                materialState = materialState.copy(material_type = type, fileUri = null)
                                expanded = false
                            })
                        }
                    }
                }

                // --- BAGIAN BARU UNTUK UPLOAD FILE ---
                // Tampilkan tombol upload hanya jika tipe "video" atau "document"
                if (materialState.material_type == "video" || materialState.material_type == "document") {
                    val mimeType = if (materialState.material_type == "video") "video/*" else "application/pdf"

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { filePickerLauncher.launch(mimeType) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Upload File")
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Select ${materialState.material_type.capitalize()}",
                                color = Color.Black
                            )
                        }
                        // Tampilkan nama file yang dipilih
                        materialState.fileUri?.let { uri ->
                            val fileName = getFileNameFromUri(context, uri)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "File: $fileName",
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
                // --- AKHIR BAGIAN BARU ---

                Row(modifier = Modifier.align(Alignment.End)) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        // Tambahkan validasi: pastikan file sudah dipilih jika tipe materialnya video/document
                        val isFileRequired = materialState.material_type == "video" || materialState.material_type == "document"
                        if (materialState.title.isNotBlank() && (!isFileRequired || materialState.fileUri != null)) {
                            onConfirm(materialState)
                        }
                    }) {
                        Text("Add")
                    }
                }
            }
        }
    }
}
fun getFileNameFromUri(context: Context, uri: Uri): String {
    var fileName: String? = null
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1) {
                fileName = it.getString(nameIndex)
            }
        }
    }
    return fileName ?: uri.lastPathSegment ?: "Unknown file"
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialDetailDialog(
    initialMaterial: MaterialState,
    onDismiss: () -> Unit,
    onConfirm: (MaterialState) -> Unit
) {
    // State untuk menampung perubahan pada material
    var materialState by remember { mutableStateOf(initialMaterial) }
    var expanded by remember { mutableStateOf(false) }
    val materialTypes = listOf("document", "video", "Other")
    val context = LocalContext.current

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            materialState = materialState.copy(fileUri = it)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(12.dp), color = Color.White) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Material Details", style = MaterialTheme.typography.titleLarge)

                OutlinedTextField(
                    value = materialState.title,
                    onValueChange = { materialState = materialState.copy(title = it) },
                    label = { Text("Material Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = materialState.description,
                    onValueChange = { materialState = materialState.copy(description = it) },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Dropdown untuk tipe material
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(
                        value = materialState.material_type,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Material Type") },
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, "Dropdown") },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        materialTypes.forEach { type ->
                            DropdownMenuItem(text = { Text(type) }, onClick = {
                                materialState = materialState.copy(material_type = type, fileUri = null)
                                expanded = false
                            })
                        }
                    }
                }

                // Bagian upload file
                if (materialState.material_type == "video" || materialState.material_type == "document") {
                    val mimeType = if (materialState.material_type == "video") "video/*" else "application/pdf"
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { filePickerLauncher.launch(mimeType) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Upload File")
                            Spacer(Modifier.width(8.dp))
                            Text("Change ${materialState.material_type.capitalize()}", color = Color.Black)
                        }
                        materialState.fileUri?.let { uri ->
                            val fileName = getFileNameFromUri(context, uri)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "File: $fileName",
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Tombol aksi
                Row(modifier = Modifier.align(Alignment.End)) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { onConfirm(materialState) }) {
                        Text("Save Changes")
                    }
                }
            }
        }
    }
}