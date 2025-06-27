package com.cognifyteam.cognifyapp.ui.learningpath.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.contentValuesOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLearningPathScreen() {
    val topicOptions = listOf("Machine Learning", "DevOps", "Mobile Development", "Web Development", "Other")
    var selectedTopic by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var isOtherSelected by remember { mutableStateOf(false) }
    var otherTopic by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var generatedPath by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 8.dp, end = 8.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Smart Learning Path", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
                    Text(
                        "Generate personalized learning paths with AI",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text("Your Generated Learning Path", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Spacer(modifier = Modifier.height(8.dp))
                        if (!generatedPath.isNullOrEmpty()) {
                            Text(generatedPath ?: "")
                        } else {
                            Text("The generated path will appear here after you generate it.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    if (!generatedPath.isNullOrEmpty()) {
                        Button(
                            onClick = { /* TODO: Save Path */ },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text("Save Path")
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            TextField(
                                value = if (isOtherSelected) "Other" else selectedTopic,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Select Topic") },
                                trailingIcon = {
                                    Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                                },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                colors = TextFieldDefaults.textFieldColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                topicOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            selectedTopic = option
                                            isOtherSelected = option == "Other"
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }

                        if (isOtherSelected) {
                            TextField(
                                value = otherTopic,
                                onValueChange = { otherTopic = it },
                                label = { Text("Enter Custom Topic") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                colors = TextFieldDefaults.textFieldColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                        }

                        TextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Short Description") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = false,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),

                            shape = RoundedCornerShape(16.dp)
                        )

                        Button(
                            onClick = {
                                generatedPath = "1. Understand basics of ${if (isOtherSelected) otherTopic else selectedTopic}\n2. Follow curated tutorials\n3. Build a mini project\n4. Join community & contribute\n" +
                                        "2. Follow curated tutorials\n" +
                                        "3. Build a mini project\n" +
                                        "4. Join community & contribute\n" +
                                        "2. Follow curated tutorials\n" +
                                        "3. Build a mini project\n" +
                                        "4. Join community & contribute\n" +
                                        "2. Follow curated tutorials\n" +
                                        "3. Build a mini project\n" +
                                        "4. Join community & contribute\n" +
                                        "2. Follow curated tutorials\n" +
                                        "3. Build a mini project\n" +
                                        "4. Join community & contribute"
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Generate Learning Path")
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Explore Learning Paths", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { /* TODO: Navigate to community learning paths */ },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                Icons.Filled.Home,
                                contentDescription = "Others' Paths",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Others' Paths", fontSize = 13.sp)
                        }

                        OutlinedButton(
                            onClick = { /* TODO: Navigate to user created paths */ },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.secondary),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                Icons.Filled.Person,
                                contentDescription = "My Paths",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("My Paths", fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}



@Preview
@Composable
fun MainLearningPathScreenPreview() {
    MainLearningPathScreen()
}

