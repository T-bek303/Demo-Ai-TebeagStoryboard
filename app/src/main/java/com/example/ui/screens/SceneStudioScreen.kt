package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
fun SceneStudioScreen(
    viewModel: StoryboardViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToStoryboard: () -> Unit
) {
    val project by viewModel.activeProject.collectAsState()
    val scenes by viewModel.activeScenes.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }
    var sceneToEdit by remember { mutableStateOf<SceneEntity?>(null) }
    var isNewScene by remember { mutableStateOf(false) }

    // Form fields for editing/adding
    var titleInput by remember { mutableStateOf("") }
    var timeRangeInput by remember { mutableStateOf("") }
    var narrationInput by remember { mutableStateOf("") }
    var visualInput by remember { mutableStateOf("") }
    var cameraAngleInput by remember { mutableStateOf("") }
    var cameraMovementInput by remember { mutableStateOf("") }
    var characterIdInput by remember { mutableStateOf("") }
    var lightingInput by remember { mutableStateOf("") }
    var moodInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Scene Studio",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${scenes.size} Scenes • ${project?.name ?: ""}",
                            style = MaterialTheme.typography.bodySmall.copy(color = CyberCyan),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("back_btn_scenes")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            isNewScene = true
                            sceneToEdit = null
                            titleInput = "Scene ${scenes.size + 1}"
                            timeRangeInput = "00:00 - 00:30"
                            narrationInput = ""
                            visualInput = ""
                            cameraAngleInput = "Wide Angle"
                            cameraMovementInput = "Slow Push-In"
                            characterIdInput = ""
                            lightingInput = "Cinematic contrast"
                            moodInput = "Engaging"
                            showEditDialog = true
                        },
                        modifier = Modifier.testTag("add_new_scene_header_btn")
                    ) {
                        Icon(Icons.Default.AddCircleOutline, contentDescription = "Add Scene", tint = CyberCyan)
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
            // Action Bar
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
                            text = "Timeline & Visual Breakdown",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Atur narasi, timecode, sudut kamera, dan tata cahaya tiap scene.",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                    }
                    Button(
                        onClick = {
                            project?.let { viewModel.regenerateScenes(it.id) }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = ObsidianBg),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Regenerate", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (scenes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Belum ada scene untuk project ini.", style = MaterialTheme.typography.titleMedium.copy(color = TextSecondary))
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { project?.let { viewModel.regenerateScenes(it.id) } },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = ObsidianBg)
                        ) {
                            Text("Generate Scene dari Naskah", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(scenes, key = { it.id }) { scene ->
                        SceneCardItem(
                            scene = scene,
                            onEdit = {
                                isNewScene = false
                                sceneToEdit = scene
                                titleInput = scene.title
                                timeRangeInput = scene.timeRange
                                narrationInput = scene.narration
                                visualInput = scene.visualDescription
                                cameraAngleInput = scene.cameraAngle
                                cameraMovementInput = scene.cameraMovement
                                characterIdInput = scene.characterId
                                lightingInput = scene.lighting
                                moodInput = scene.mood
                                showEditDialog = true
                            },
                            onDelete = {
                                viewModel.deleteScene(scene.id)
                            }
                        )
                    }
                }
            }
        }
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text(if (isNewScene) "Tambah Scene Baru" else "Edit Scene #${sceneToEdit?.sceneNumber}", fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = titleInput,
                            onValueChange = { titleInput = it },
                            label = { Text("Judul Scene") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = timeRangeInput,
                            onValueChange = { timeRangeInput = it },
                            label = { Text("Timecode (e.g. 00:00 - 00:45)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = narrationInput,
                            onValueChange = { narrationInput = it },
                            label = { Text("Naskah / Voiceover") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = visualInput,
                            onValueChange = { visualInput = it },
                            label = { Text("Deskripsi Visual di Layar") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = cameraAngleInput,
                                onValueChange = { cameraAngleInput = it },
                                label = { Text("Camera Angle") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = cameraMovementInput,
                                onValueChange = { cameraMovementInput = it },
                                label = { Text("Movement") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = lightingInput,
                                onValueChange = { lightingInput = it },
                                label = { Text("Lighting") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = moodInput,
                                onValueChange = { moodInput = it },
                                label = { Text("Mood") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val currentProject = project ?: return@Button
                        if (isNewScene) {
                            val newScene = SceneEntity(
                                id = 0,
                                projectId = currentProject.id,
                                sceneNumber = scenes.size + 1,
                                timeRange = timeRangeInput,
                                title = titleInput,
                                narration = narrationInput,
                                visualDescription = visualInput,
                                cameraAngle = cameraAngleInput,
                                cameraMovement = cameraMovementInput,
                                characterId = characterIdInput,
                                location = "Studio Location",
                                lighting = lightingInput,
                                mood = moodInput,
                                transition = "Cross Dissolve",
                                imagePrompt = "8k cinematic shot of $visualInput, $lightingInput, 16:9",
                                videoPrompt = "$cameraMovementInput camera of $visualInput, cinematic 24fps",
                                frameOrder = scenes.size + 1
                            )
                            viewModel.addScene(newScene)
                        } else {
                            sceneToEdit?.let { current ->
                                val updated = current.copy(
                                    title = titleInput,
                                    timeRange = timeRangeInput,
                                    narration = narrationInput,
                                    visualDescription = visualInput,
                                    cameraAngle = cameraAngleInput,
                                    cameraMovement = cameraMovementInput,
                                    characterId = characterIdInput,
                                    lighting = lightingInput,
                                    mood = moodInput
                                )
                                viewModel.updateScene(updated)
                            }
                        }
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = ObsidianBg)
                ) {
                    Text("Simpan", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Batal", color = TextSecondary)
                }
            },
            containerColor = SurfaceDark
        )
    }
}

@Composable
fun SceneCardItem(
    scene: SceneEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    CyberCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = SurfaceCard
    ) {
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
                        text = "SCENE ${scene.sceneNumber}",
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

            Row {
                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = CyberCyan, modifier = Modifier.size(16.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = CrimsonRed, modifier = Modifier.size(16.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = scene.title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = Modifier.height(6.dp))

        if (scene.narration.isNotBlank()) {
            Surface(
                color = SurfaceDark,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "VOICEOVER / NARASI:",
                        style = MaterialTheme.typography.labelSmall.copy(color = CyberCyan, fontSize = 9.sp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "\"${scene.narration}\"",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        Text(
            text = "VISUAL: ${scene.visualDescription}",
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "🎥 ${scene.cameraAngle} | ${scene.cameraMovement}",
                style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
            )
        }
    }
}
