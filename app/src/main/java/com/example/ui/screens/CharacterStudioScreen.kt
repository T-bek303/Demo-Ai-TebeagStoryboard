package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.CharacterEntity
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.StoryboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterStudioScreen(
    viewModel: StoryboardViewModel,
    onNavigateBack: () -> Unit
) {
    val project by viewModel.activeProject.collectAsState()
    val characters by viewModel.activeCharacters.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var isNewChar by remember { mutableStateOf(false) }
    var charToEdit by remember { mutableStateOf<CharacterEntity?>(null) }

    var codeInput by remember { mutableStateOf("") }
    var nameInput by remember { mutableStateOf("") }
    var ageInput by remember { mutableStateOf("30") }
    var genderInput by remember { mutableStateOf("Male") }
    var appearanceInput by remember { mutableStateOf("") }
    var clothingInput by remember { mutableStateOf("") }
    var clothingColorInput by remember { mutableStateOf("") }
    var bodyShapeInput by remember { mutableStateOf("Athletic") }
    var personalityInput by remember { mutableStateOf("") }
    var visualStyleInput by remember { mutableStateOf("Cinematic realism") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Character Studio",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${characters.size} Profiles • ${project?.name ?: ""}",
                            style = MaterialTheme.typography.bodySmall.copy(color = CyberCyan)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("back_btn_chars")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            isNewChar = true
                            charToEdit = null
                            codeInput = "CHAR-00${characters.size + 1}"
                            nameInput = ""
                            ageInput = "32"
                            genderInput = "Male"
                            appearanceInput = "Sharp focused expression, dark brown hair"
                            clothingInput = "Dark modern blazer, turtleneck"
                            clothingColorInput = "Charcoal and Cyan"
                            bodyShapeInput = "Lean Athletic"
                            personalityInput = "Analytical, calm, relentless investigator"
                            visualStyleInput = "Cinematic documentary realism with rim light"
                            showDialog = true
                        },
                        modifier = Modifier.testTag("add_character_btn")
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = "Add Character", tint = CyberCyan)
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
                            text = "Character Consistency Engine",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Profil karakter yang terkunci untuk menjaga konsistensi visual di seluruh scene.",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (characters.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(bottom = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Belum ada profil karakter tersimpan.", style = MaterialTheme.typography.titleMedium.copy(color = TextSecondary))
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                isNewChar = true
                                codeInput = "CHAR-001"
                                nameInput = "Julian Thorne"
                                appearanceInput = "Sharp focused expression, dark brown hair"
                                clothingInput = "Dark modern blazer, turtleneck"
                                clothingColorInput = "Charcoal and Cyan"
                                bodyShapeInput = "Lean Athletic"
                                personalityInput = "Analytical investigator"
                                visualStyleInput = "Cinematic realism"
                                showDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = ObsidianBg)
                        ) {
                            Text("Buat Karakter Baru", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(characters, key = { it.id }) { character ->
                        CharacterCardItem(
                            character = character,
                            onEdit = {
                                isNewChar = false
                                charToEdit = character
                                codeInput = character.characterCode
                                nameInput = character.name
                                ageInput = character.age.toString()
                                genderInput = character.gender
                                appearanceInput = character.appearance
                                clothingInput = character.clothing
                                clothingColorInput = character.clothingColor
                                bodyShapeInput = character.bodyShape
                                personalityInput = character.personality
                                visualStyleInput = character.visualStyle
                                showDialog = true
                            },
                            onDelete = {
                                viewModel.deleteCharacter(character.id)
                            }
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(if (isNewChar) "Buat Karakter Baru" else "Edit Karakter", fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 420.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = codeInput,
                                onValueChange = { codeInput = it },
                                label = { Text("Kode (e.g. CHAR-001)") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = nameInput,
                                onValueChange = { nameInput = it },
                                label = { Text("Nama Karakter") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = ageInput,
                                onValueChange = { ageInput = it },
                                label = { Text("Usia") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = genderInput,
                                onValueChange = { genderInput = it },
                                label = { Text("Gender") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    item {
                        OutlinedTextField(
                            value = appearanceInput,
                            onValueChange = { appearanceInput = it },
                            label = { Text("Deskripsi Wajah & Fisik") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = clothingInput,
                            onValueChange = { clothingInput = it },
                            label = { Text("Pakaian Utama (Signature Outfit)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = clothingColorInput,
                            onValueChange = { clothingColorInput = it },
                            label = { Text("Warna Pakaian") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = personalityInput,
                            onValueChange = { personalityInput = it },
                            label = { Text("Kepribadian & Karakteristik") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = visualStyleInput,
                            onValueChange = { visualStyleInput = it },
                            label = { Text("Visual Art Direction") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val currentProject = project ?: return@Button
                        if (isNewChar) {
                            val newChar = CharacterEntity(
                                id = 0,
                                projectId = currentProject.id,
                                characterCode = codeInput.ifBlank { "CHAR-001" },
                                name = nameInput.ifBlank { "Character" },
                                age = ageInput.toIntOrNull() ?: 30,
                                gender = genderInput,
                                appearance = appearanceInput,
                                clothing = clothingInput,
                                clothingColor = clothingColorInput,
                                bodyShape = bodyShapeInput,
                                personality = personalityInput,
                                visualStyle = visualStyleInput
                            )
                            viewModel.addCharacter(newChar)
                        } else {
                            charToEdit?.let { current ->
                                val updated = current.copy(
                                    characterCode = codeInput,
                                    name = nameInput,
                                    age = ageInput.toIntOrNull() ?: current.age,
                                    gender = genderInput,
                                    appearance = appearanceInput,
                                    clothing = clothingInput,
                                    clothingColor = clothingColorInput,
                                    bodyShape = bodyShapeInput,
                                    personality = personalityInput,
                                    visualStyle = visualStyleInput
                                )
                                viewModel.updateCharacter(updated)
                            }
                        }
                        showDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = ObsidianBg)
                ) {
                    Text("Simpan", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Batal", color = TextSecondary)
                }
            },
            containerColor = SurfaceDark
        )
    }
}

@Composable
fun CharacterCardItem(
    character: CharacterEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val charPrompt = "${character.name} (${character.characterCode}), ${character.age} year old ${character.gender.lowercase()}, ${character.appearance}, wearing ${character.clothing} (${character.clothingColor}), ${character.visualStyle}"

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
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(CyberCyan, NeonPurple))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = character.name.take(2).uppercase(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = ObsidianBg,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = character.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = CyberCyan.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = character.characterCode,
                                color = CyberCyan,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "${character.gender} • ${character.age} tahun",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
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

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Wajah & Ciri: ${character.appearance}",
            style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Pakaian: ${character.clothing} (${character.clothingColor})",
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Kepribadian: ${character.personality}",
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Art: ${character.visualStyle}",
                style = MaterialTheme.typography.labelSmall.copy(color = TextCyan),
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            CopyButton(textToCopy = charPrompt, label = "Copy Prompt", testTagSuffix = "char_${character.id}")
        }
    }
}
