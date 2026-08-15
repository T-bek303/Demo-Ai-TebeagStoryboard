package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.SceneEntity
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.StoryboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryboardScreen(
    viewModel: StoryboardViewModel,
    onNavigateBack: () -> Unit
) {
    val project by viewModel.activeProject.collectAsState()
    val scenes by viewModel.activeScenes.collectAsState()

    var showPromptEditorDialog by remember { mutableStateOf(false) }
    var selectedSceneForPrompt by remember { mutableStateOf<SceneEntity?>(null) }
    var imagePromptInput by remember { mutableStateOf("") }
    var videoPromptInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Visual Storyboard & Prompt Hub",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${scenes.size} Frames • ${project?.name ?: ""}",
                            style = MaterialTheme.typography.bodySmall.copy(color = CyberCyan),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("back_btn_storyboard")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            // Master Overview Header Card
            CyberCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = SurfaceCard
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Frame Directives & AI Prompts",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Prompt Midjourney / Stable Diffusion (16:9) & Runway / Luma camera motion.",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                    }
                    StatusBadge(status = if (scenes.isNotEmpty()) "Ready" else "Draft")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (scenes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(bottom = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Belum ada frame storyboard.", style = MaterialTheme.typography.titleMedium.copy(color = TextSecondary))
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { project?.let { viewModel.regenerateScenes(it.id) } },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = ObsidianBg)
                        ) {
                            Text("Generate Storyboard Sekarang", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(scenes, key = { it.id }) { scene ->
                        StoryboardFrameCard(
                            scene = scene,
                            onEditPrompts = {
                                selectedSceneForPrompt = scene
                                imagePromptInput = scene.imagePrompt
                                videoPromptInput = scene.videoPrompt
                                showPromptEditorDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    if (showPromptEditorDialog && selectedSceneForPrompt != null) {
        AlertDialog(
            onDismissRequest = { showPromptEditorDialog = false },
            title = { Text("Edit AI Prompts (Frame #${selectedSceneForPrompt?.sceneNumber})", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = imagePromptInput,
                        onValueChange = { imagePromptInput = it },
                        label = { Text("Image Prompt (16:9 Midjourney / SD)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                    OutlinedTextField(
                        value = videoPromptInput,
                        onValueChange = { videoPromptInput = it },
                        label = { Text("Video Prompt (Motion Directives)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedSceneForPrompt?.let { current ->
                            viewModel.updateScene(
                                current.copy(
                                    imagePrompt = imagePromptInput,
                                    videoPrompt = videoPromptInput
                                )
                            )
                        }
                        showPromptEditorDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = ObsidianBg)
                ) {
                    Text("Simpan Prompts", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPromptEditorDialog = false }) {
                    Text("Batal", color = TextSecondary)
                }
            },
            containerColor = SurfaceDark
        )
    }
}

@Composable
fun StoryboardFrameCard(
    scene: SceneEntity,
    onEditPrompts: () -> Unit
) {
    CyberCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = SurfaceCard
    ) {
        // Frame Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = CyberCyan.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "FRAME ${scene.sceneNumber}",
                        color = CyberCyan,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    color = DeepIndigo.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = scene.timeRange,
                        color = Color(0xFFBCE0FD),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
                if (scene.characterId.isNotBlank()) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = Color(0xFFEC4899).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = scene.characterId,
                            color = Color(0xFFF472B6),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            IconButton(onClick = onEditPrompts, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Prompts", tint = CyberCyan, modifier = Modifier.size(16.dp))
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Visual Preview Frame Aspect Box (16:9 placeholder mockup)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceDark)
                .border(1.dp, BorderGlow, RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Surface(
                        color = ObsidianBg.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "16:9 CINEMATIC",
                            color = CyberCyan,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = "${scene.cameraAngle} • ${scene.cameraMovement}",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 10.sp)
                    )
                }

                Text(
                    text = scene.visualDescription,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Mood: ${scene.mood} | Light: ${scene.lighting}",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 10.sp)
                    )
                    Text(
                        text = "Transition: ${scene.transition}",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextAccent, fontSize = 10.sp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Image Prompt Section
        Surface(
            color = SurfaceDark,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Image, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "IMAGE PROMPT (Midjourney / SD)",
                            style = MaterialTheme.typography.labelSmall.copy(color = CyberCyan, fontWeight = FontWeight.Bold)
                        )
                    }
                    CopyButton(textToCopy = scene.imagePrompt, label = "Copy Prompt", testTagSuffix = "img_${scene.id}")
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = scene.imagePrompt,
                    style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary, lineHeight = 18.sp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Video Prompt Section
        Surface(
            color = SurfaceDark,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Videocam, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "VIDEO PROMPT (Camera & Motion)",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextAccent, fontWeight = FontWeight.Bold)
                        )
                    }
                    CopyButton(textToCopy = scene.videoPrompt, label = "Copy Motion", testTagSuffix = "vid_${scene.id}")
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = scene.videoPrompt,
                    style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary, lineHeight = 18.sp)
                )
            }
        }
    }
}
