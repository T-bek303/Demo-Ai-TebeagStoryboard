package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.StoryboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScriptStudioScreen(
    viewModel: StoryboardViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToScenes: () -> Unit
) {
    val context = LocalContext.current
    val project by viewModel.activeProject.collectAsState()

    var scriptContent by remember(project?.scriptText) {
        mutableStateOf(project?.scriptText ?: "")
    }

    var showCustomScriptDialog by remember { mutableStateOf(false) }
    var customScriptPaste by remember { mutableStateOf("") }

    val wordCount = remember(scriptContent) {
        scriptContent.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }.size
    }

    val readingTimeMinutes = remember(wordCount, project?.wpm) {
        val wpm = project?.wpm ?: 150
        if (wpm > 0) (wordCount / wpm.toDouble()) else 0.0
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Script Studio & Analyzer",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = project?.name ?: "No Project Selected",
                            style = MaterialTheme.typography.bodySmall.copy(color = CyberCyan)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("back_btn_script")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showCustomScriptDialog = true },
                        modifier = Modifier.testTag("custom_script_paste_btn")
                    ) {
                        Icon(Icons.Default.PostAdd, contentDescription = "Paste Custom Script", tint = CyberCyan)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ObsidianBg,
                    titleContentColor = TextPrimary
                )
            )
        },
        containerColor = ObsidianBg
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Script Analysis Metric Card
            item {
                CyberCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = SurfaceCard
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Analisis Retensi & Struktur Naskah",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = CyberCyan
                            )
                        )
                        StatusBadge(status = if (scriptContent.isNotBlank()) "Ready" else "Draft")
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ScorePill(label = "Word Count", score = wordCount, modifier = Modifier.weight(1f))
                        ScorePill(label = "Hook Rating", score = 94, modifier = Modifier.weight(1f))
                        ScorePill(label = "Retention", score = 92, modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Estimasi Durasi: String.format(\"%.1f min\", readingTimeMinutes) pada kecepatan ${project?.wpm ?: 150} WPM.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                    if (project?.scriptAnalysis?.isNotBlank() == true) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = project?.scriptAnalysis ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextAccent)
                        )
                    }
                }
            }

            // AI Script Quick Tools
            item {
                Column {
                    Text(
                        text = "AI Quick Tools:",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            ToolChip(
                                label = "Tulis Ulang",
                                icon = Icons.Default.AutoFixHigh,
                                onClick = {
                                    project?.let {
                                        viewModel.applyScriptAction(it.id, "REWRITE")
                                        Toast.makeText(context, "AI sedang menulis ulang naskah...", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        }
                        item {
                            ToolChip(
                                label = "Perpanjang",
                                icon = Icons.Default.FormatLineSpacing,
                                onClick = {
                                    project?.let {
                                        viewModel.applyScriptAction(it.id, "EXPAND")
                                        Toast.makeText(context, "AI sedang memperluas konteks naskah...", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        }
                        item {
                            ToolChip(
                                label = "Perpendek",
                                icon = Icons.Default.Compress,
                                onClick = {
                                    project?.let {
                                        viewModel.applyScriptAction(it.id, "SHORTEN")
                                        Toast.makeText(context, "AI sedang memadatkan naskah...", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        }
                        item {
                            ToolChip(
                                label = "Perkuat Hook",
                                icon = Icons.Default.Bolt,
                                onClick = {
                                    project?.let {
                                        viewModel.applyScriptAction(it.id, "HOOK")
                                        Toast.makeText(context, "AI sedang meracik hook pembuka baru...", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        }
                        item {
                            ToolChip(
                                label = "Tingkatkan Retensi",
                                icon = Icons.Default.TrendingUp,
                                onClick = {
                                    project?.let {
                                        viewModel.applyScriptAction(it.id, "RETENTION")
                                        Toast.makeText(context, "AI mengoptimasi curiosity loops...", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // Script Text Editor Area
            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "11-Part Master Script",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Row {
                            CopyButton(textToCopy = scriptContent, testTagSuffix = "script")
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    project?.let { p ->
                                        viewModel.updateScriptTextDirect(p.id, scriptContent)
                                        Toast.makeText(context, "Naskah tersimpan!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = ObsidianBg),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("Simpan", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = scriptContent,
                        onValueChange = {
                            scriptContent = it
                            project?.let { p -> viewModel.updateScriptTextDirect(p.id, it) }
                        },
                        placeholder = { Text("Tulis atau generate naskah video Anda di sini...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 350.dp)
                            .testTag("script_editor_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = BorderGlow,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = SurfaceDark,
                            unfocusedContainerColor = SurfaceDark
                        ),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp)
                    )
                }
            }

            // Bottom Flow Button
            item {
                GradientButton(
                    text = "Konversi Naskah ke Scene",
                    icon = Icons.Default.Movie,
                    onClick = {
                        project?.let { p ->
                            viewModel.regenerateScenes(p.id)
                        }
                        onNavigateToScenes()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    // Modal Dialog: "Gunakan Naskah Saya"
    if (showCustomScriptDialog) {
        AlertDialog(
            onDismissRequest = { showCustomScriptDialog = false },
            title = { Text("Gunakan Naskah Sendiri", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Paste naskah video Anda di bawah ini. AI TebeagStoryboard akan menganalisis dan membaginya menjadi scene dan visual otomatis.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                    OutlinedTextField(
                        value = customScriptPaste,
                        onValueChange = { customScriptPaste = it },
                        placeholder = { Text("Paste naskah lengkap Anda di sini...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = BorderGlow
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customScriptPaste.isNotBlank()) {
                            project?.let { p ->
                                viewModel.analyzeCustomScriptAndBreakdown(p.id, customScriptPaste)
                                scriptContent = customScriptPaste
                                Toast.makeText(context, "Naskah berhasil diproses!", Toast.LENGTH_SHORT).show()
                            }
                            showCustomScriptDialog = false
                            customScriptPaste = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = ObsidianBg)
                ) {
                    Text("Proses & Buat Scene", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomScriptDialog = false }) {
                    Text("Batal", color = TextSecondary)
                }
            },
            containerColor = SurfaceDark
        )
    }
}

@Composable
fun ToolChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Surface(
        color = SurfaceCard,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderGlow),
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = CyberCyan, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
        }
    }
}
