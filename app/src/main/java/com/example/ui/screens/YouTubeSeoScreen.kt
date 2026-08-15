package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun YouTubeSeoScreen(
    viewModel: StoryboardViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val project by viewModel.activeProject.collectAsState()

    var titleInput by remember(project?.finalTitle) { mutableStateOf(project?.finalTitle ?: "") }
    var descriptionInput by remember(project?.youtubeDescription) { mutableStateOf(project?.youtubeDescription ?: "") }
    var hashtagsInput by remember(project?.hashtags) { mutableStateOf(project?.hashtags ?: "") }
    var thumbnailPromptInput by remember(project?.thumbnailPrompt) { mutableStateOf(project?.thumbnailPrompt ?: "") }
    var thumbnailTextInput by remember(project?.thumbnailText) { mutableStateOf(project?.thumbnailText ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "YouTube SEO & Thumbnail",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = project?.name ?: "No Project Selected",
                            style = MaterialTheme.typography.bodySmall.copy(color = CyberCyan)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("back_btn_seo")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            project?.let { p ->
                                viewModel.regenerateSeo(p.id)
                                Toast.makeText(context, "Regenerating SEO metadata...", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.testTag("regenerate_seo_btn")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Regenerate SEO", tint = CyberCyan)
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Final Selected Title
            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Judul Video Utama (Selected Title)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = CyberCyan
                            )
                        )
                        CopyButton(textToCopy = titleInput, testTagSuffix = "seo_title")
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = titleInput,
                        onValueChange = {
                            titleInput = it
                            project?.let { p -> viewModel.updateProjectField(p.id, finalTitle = it) }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = BorderGlow
                        )
                    )
                }
            }

            // Thumbnail Visual Prompt & Overlay Text
            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Thumbnail Concept & Text Overlay",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = AmberGlow
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Text Overlay Thumbnail (3-6 Kata Menarik):", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = thumbnailTextInput,
                        onValueChange = {
                            thumbnailTextInput = it
                            project?.let { p -> viewModel.updateProjectField(p.id, thumbnailText = it) }
                        },
                        placeholder = { Text("Contoh: THE $1M WEALTH TRAP") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AmberGlow,
                            unfocusedBorderColor = BorderGlow
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Prompt Gambar Thumbnail (16:9 High CTR):", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                        CopyButton(textToCopy = thumbnailPromptInput, testTagSuffix = "thumb_prompt")
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = thumbnailPromptInput,
                        onValueChange = {
                            thumbnailPromptInput = it
                            project?.let { p -> viewModel.updateProjectField(p.id, thumbnailPrompt = it) }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AmberGlow,
                            unfocusedBorderColor = BorderGlow
                        )
                    )
                }
            }

            // 20 Categorized Hashtags
            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "20 Viral Hashtags",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = EmeraldGreen
                            )
                        )
                        CopyButton(textToCopy = hashtagsInput, label = "Copy 20 Hashtags", testTagSuffix = "all_hashtags")
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = hashtagsInput,
                        onValueChange = {
                            hashtagsInput = it
                            project?.let { p -> viewModel.updateProjectField(p.id, hashtags = it) }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldGreen,
                            unfocusedBorderColor = BorderGlow
                        )
                    )
                }
            }

            // SEO YouTube Description
            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "SEO YouTube Description & Timestamps",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = CyberCyan
                            )
                        )
                        CopyButton(textToCopy = descriptionInput, label = "Copy Description", testTagSuffix = "desc")
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = descriptionInput,
                        onValueChange = {
                            descriptionInput = it
                            project?.let { p -> viewModel.updateProjectField(p.id, youtubeDescription = it) }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 220.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = BorderGlow
                        )
                    )
                }
            }
        }
    }
}
