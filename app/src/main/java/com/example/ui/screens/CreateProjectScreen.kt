package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Presets
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.StoryboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProjectScreen(
    viewModel: StoryboardViewModel,
    onProjectCreated: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val customNiches by viewModel.customNiches.collectAsState()
    val customStyles by viewModel.customStyles.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val currentStep by viewModel.currentGenerationStep.collectAsState()

    var projectName by remember { mutableStateOf("") }
    var topic by remember { mutableStateOf("") }
    var targetCountry by remember { mutableStateOf("United States") }
    var targetAudience by remember { mutableStateOf("Curious thinkers and knowledge seekers") }
    var language by remember { mutableStateOf("English") }
    var selectedNiche by remember { mutableStateOf("Finance") }
    var subNiche by remember { mutableStateOf("Macroeconomics & Banking") }
    var selectedStyle by remember { mutableStateOf("Cinematic Documentary") }
    var customStylePrompt by remember { mutableStateOf("") }
    var durationMinutes by remember { mutableIntStateOf(15) }
    var isCustomDuration by remember { mutableStateOf(false) }
    var customDurationText by remember { mutableStateOf("15") }
    var wpm by remember { mutableIntStateOf(150) }
    var sceneCountSetting by remember { mutableStateOf("Auto") }
    var isCustomSceneCount by remember { mutableStateOf(false) }
    var customSceneCountText by remember { mutableStateOf("20") }
    var animationStyle by remember { mutableStateOf("3D Animation") }
    var customAnimationStyle by remember { mutableStateOf("") }
    var characterName by remember { mutableStateOf("Julian Thorne") }

    // Dialog states
    var showAddNicheDialog by remember { mutableStateOf(false) }
    var newNicheName by remember { mutableStateOf("") }
    var newNicheDesc by remember { mutableStateOf("") }

    var showAddStyleDialog by remember { mutableStateOf(false) }
    var newStyleName by remember { mutableStateOf("") }
    var newStyleInstruction by remember { mutableStateOf("") }

    if (isGenerating && currentStep != null) {
        GenerationProgressDialog(currentStep = currentStep!!) {}
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Pembuatan Project Baru",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("back_button")
                    ) {
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Parameter Studio Video",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = CyberCyan
                    )
                )
                Text(
                    text = "Konfigurasikan alur cerita, durasi naskah, dan parameter visual AI.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                )
            }

            // Nama Project & Topik
            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "1. Informasi Utama",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = CyberCyan
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = projectName,
                        onValueChange = { projectName = it },
                        label = { Text("Nama Project") },
                        placeholder = { Text("Contoh: The Secret Mechanics of Quantitative Easing") },
                        modifier = Modifier.fillMaxWidth().testTag("project_name_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = BorderGlow,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = topic,
                        onValueChange = { topic = it },
                        label = { Text("Topik / Ide Video") },
                        placeholder = { Text("Contoh: How central banks print money and trigger the Cantillon Effect") },
                        modifier = Modifier.fillMaxWidth().testTag("project_topic_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = BorderGlow,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        minLines = 2
                    )
                }
            }

            // Target Audience, Country, Language
            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "2. Target Pemirsa & Bahasa",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = CyberCyan
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = targetAudience,
                        onValueChange = { targetAudience = it },
                        label = { Text("Target Audience") },
                        modifier = Modifier.fillMaxWidth().testTag("target_audience_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = BorderGlow,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Country selector
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Negara Target", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                            Spacer(modifier = Modifier.height(4.dp))
                            DropdownSelector(
                                items = Presets.TARGET_COUNTRIES,
                                selectedItem = targetCountry,
                                onItemSelected = { targetCountry = it }
                            )
                        }

                        // Language selector
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Bahasa Naskah", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                            Spacer(modifier = Modifier.height(4.dp))
                            DropdownSelector(
                                items = Presets.LANGUAGES,
                                selectedItem = language,
                                onItemSelected = { language = it }
                            )
                        }
                    }
                }
            }

            // 10 Niches + Custom Niche
            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "3. Niche Konten",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = CyberCyan
                            )
                        )
                        TextButton(
                            onClick = { showAddNicheDialog = true },
                            modifier = Modifier.testTag("add_custom_niche_btn")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = CyberCyan)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+ Custom Niche", color = CyberCyan, style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val allNiches = Presets.DEFAULT_NICHES.map { it.name } + customNiches.map { it.name }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(allNiches) { nicheName ->
                            val isSelected = selectedNiche == nicheName
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedNiche = nicheName },
                                label = { Text(nicheName) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyberCyan,
                                    selectedLabelColor = ObsidianBg,
                                    containerColor = SurfaceCardHighlight,
                                    labelColor = TextPrimary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = subNiche,
                        onValueChange = { subNiche = it },
                        label = { Text("Sub-Niche / Spesialisasi") },
                        placeholder = { Text("Contoh: Macroeconomics, Stock Analysis, AI Ethics") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = BorderGlow,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }
            }

            // 10 Gaya Bahasa + Custom Style
            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "4. Gaya Bahasa Naskah",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = CyberCyan
                            )
                        )
                        TextButton(
                            onClick = { showAddStyleDialog = true },
                            modifier = Modifier.testTag("add_custom_style_btn")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = CyberCyan)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+ Custom Style", color = CyberCyan, style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val allStyles = Presets.DEFAULT_WRITING_STYLES.map { it.name } + customStyles.map { it.name }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(allStyles) { styleName ->
                            val isSelected = selectedStyle == styleName
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedStyle = styleName },
                                label = { Text(styleName) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NeonPurple,
                                    selectedLabelColor = ObsidianBg,
                                    containerColor = SurfaceCardHighlight,
                                    labelColor = TextPrimary
                                )
                            )
                        }
                    }

                    if (customStylePrompt.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Custom Instruction: $customStylePrompt",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextAccent)
                        )
                    }
                }
            }

            // Durasi Video & WPM Word Calculation
            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    val finalDuration = if (isCustomDuration) customDurationText.toIntOrNull() ?: 15 else durationMinutes
                    val estimatedWords = finalDuration * wpm

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "5. Durasi Video & WPM",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = CyberCyan
                            )
                        )
                        Surface(
                            color = CyberCyan.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "±$estimatedWords kata ($finalDuration m)",
                                color = CyberCyan,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(Presets.DURATION_PRESETS) { dur ->
                            val isSelected = !isCustomDuration && durationMinutes == dur
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    isCustomDuration = false
                                    durationMinutes = dur
                                },
                                label = { Text("$dur m") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyberCyan,
                                    selectedLabelColor = ObsidianBg,
                                    containerColor = SurfaceCardHighlight,
                                    labelColor = TextPrimary
                                )
                            )
                        }
                        item {
                            FilterChip(
                                selected = isCustomDuration,
                                onClick = { isCustomDuration = true },
                                label = { Text("Custom") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyberCyan,
                                    selectedLabelColor = ObsidianBg,
                                    containerColor = SurfaceCardHighlight,
                                    labelColor = TextPrimary
                                )
                            )
                        }
                    }

                    if (isCustomDuration) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = customDurationText,
                            onValueChange = { customDurationText = it },
                            label = { Text("Masukkan Menit Durasi") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = BorderGlow,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // WPM Selector
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Words Per Minute (WPM): $wpm WPM",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                        )
                    }

                    Slider(
                        value = wpm.toFloat(),
                        onValueChange = { wpm = it.toInt() },
                        valueRange = 100f..220f,
                        steps = 11,
                        colors = SliderDefaults.colors(
                            thumbColor = CyberCyan,
                            activeTrackColor = CyberCyan,
                            inactiveTrackColor = SurfaceCardHighlight
                        )
                    )
                }
            }

            // Jumlah Scene & Gaya Animasi
            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "6. Visual & Gaya Animasi",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = CyberCyan
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Jumlah Scene:", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(Presets.SCENE_COUNT_PRESETS) { sc ->
                            val isSelected = !isCustomSceneCount && sceneCountSetting == sc
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    isCustomSceneCount = false
                                    sceneCountSetting = sc
                                },
                                label = { Text(sc) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyberBlue,
                                    selectedLabelColor = ObsidianBg,
                                    containerColor = SurfaceCardHighlight,
                                    labelColor = TextPrimary
                                )
                            )
                        }
                        item {
                            FilterChip(
                                selected = isCustomSceneCount,
                                onClick = { isCustomSceneCount = true },
                                label = { Text("Custom") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyberBlue,
                                    selectedLabelColor = ObsidianBg,
                                    containerColor = SurfaceCardHighlight,
                                    labelColor = TextPrimary
                                )
                            )
                        }
                    }

                    if (isCustomSceneCount) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = customSceneCountText,
                            onValueChange = { customSceneCountText = it },
                            label = { Text("Jumlah Scene Kustom") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = BorderGlow,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Gaya Animasi:", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(Presets.DEFAULT_ANIMATION_STYLES) { anim ->
                            val isSelected = animationStyle == anim.name
                            FilterChip(
                                selected = isSelected,
                                onClick = { animationStyle = anim.name },
                                label = { Text(anim.name) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = DeepIndigo,
                                    selectedLabelColor = TextPrimary,
                                    containerColor = SurfaceCardHighlight,
                                    labelColor = TextPrimary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = characterName,
                        onValueChange = { characterName = it },
                        label = { Text("Nama Karakter Utama (Opsional)") },
                        placeholder = { Text("Contoh: Julian Thorne (CHAR-001)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = BorderGlow,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }
            }

            // Big Launch Action: GENERATE PROJECT
            item {
                Spacer(modifier = Modifier.height(8.dp))
                GradientButton(
                    text = "GENERATE PROJECT",
                    icon = Icons.Default.RocketLaunch,
                    onClick = {
                        if (topic.isBlank()) {
                            Toast.makeText(context, "Please enter a topic for the video project.", Toast.LENGTH_SHORT).show()
                            return@GradientButton
                        }
                        val finalDuration = if (isCustomDuration) customDurationText.toIntOrNull() ?: 15 else durationMinutes
                        val finalSceneCount = if (isCustomSceneCount) "$customSceneCountText Scene" else sceneCountSetting

                        viewModel.createAndGenerateProject(
                            name = projectName.ifBlank { topic.take(40) },
                            topic = topic,
                            country = targetCountry,
                            targetAudience = targetAudience,
                            language = language,
                            niche = selectedNiche,
                            subNiche = subNiche,
                            contentStyle = selectedStyle,
                            customStyleInstruction = customStylePrompt,
                            durationMinutes = finalDuration,
                            wpm = wpm,
                            sceneCountSetting = finalSceneCount,
                            animationStyle = animationStyle,
                            customAnimationStyle = customAnimationStyle,
                            customCharacterName = characterName,
                            onCompleted = onProjectCreated
                        )
                    },
                    modifier = Modifier.fillMaxWidth().testTag("generate_project_submit_btn")
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Dialog: Add Custom Niche
    if (showAddNicheDialog) {
        AlertDialog(
            onDismissRequest = { showAddNicheDialog = false },
            title = { Text("Tambah Custom Niche", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newNicheName,
                        onValueChange = { newNicheName = it },
                        label = { Text("Nama Niche") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newNicheDesc,
                        onValueChange = { newNicheDesc = it },
                        label = { Text("Deskripsi Singkat") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newNicheName.isNotBlank()) {
                            viewModel.addCustomNiche(newNicheName, newNicheDesc)
                            selectedNiche = newNicheName
                            showAddNicheDialog = false
                            newNicheName = ""
                            newNicheDesc = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = ObsidianBg)
                ) {
                    Text("Simpan", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddNicheDialog = false }) {
                    Text("Batal", color = TextSecondary)
                }
            },
            containerColor = SurfaceDark
        )
    }

    // Dialog: Add Custom Style
    if (showAddStyleDialog) {
        AlertDialog(
            onDismissRequest = { showAddStyleDialog = false },
            title = { Text("Tambah Custom Writing Style", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newStyleName,
                        onValueChange = { newStyleName = it },
                        label = { Text("Nama Gaya Bahasa") },
                        placeholder = { Text("Contoh: Dark Cinematic Mystery") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newStyleInstruction,
                        onValueChange = { newStyleInstruction = it },
                        label = { Text("Instruksi Prompt Gaya Bahasa") },
                        placeholder = { Text("Contoh: Gunakan gaya investigasi yang dingin, serius, dan atmosferik.") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newStyleName.isNotBlank()) {
                            viewModel.addCustomStyle(newStyleName, newStyleInstruction)
                            selectedStyle = newStyleName
                            customStylePrompt = newStyleInstruction
                            showAddStyleDialog = false
                            newStyleName = ""
                            newStyleInstruction = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple, contentColor = ObsidianBg)
                ) {
                    Text("Simpan", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddStyleDialog = false }) {
                    Text("Batal", color = TextSecondary)
                }
            },
            containerColor = SurfaceDark
        )
    }
}

@Composable
fun DropdownSelector(
    items: List<String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Surface(
            color = SurfaceCard,
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderGlow),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .clickable { expanded = true }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
            ) {
                Text(
                    text = selectedItem,
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary),
                    maxLines = 1
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown",
                    tint = CyberCyan
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(SurfaceDark)
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item, color = TextPrimary) },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}
