package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.AiSettingsEntity
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.StoryboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiSettingsScreen(
    viewModel: StoryboardViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val currentSettings by viewModel.aiSettings.collectAsState()

    var provider by remember(currentSettings) { mutableStateOf(currentSettings?.provider ?: "Gemini API") }
    var apiKey by remember(currentSettings) { mutableStateOf(currentSettings?.customApiKey ?: "") }
    var showApiKey by remember { mutableStateOf(false) }
    var selectedModel by remember(currentSettings) { mutableStateOf(currentSettings?.model ?: "gemini-3.5-flash") }
    var temperature by remember(currentSettings) { mutableFloatStateOf(currentSettings?.temperature ?: 0.7f) }
    var maxTokens by remember(currentSettings) { mutableIntStateOf(currentSettings?.maxTokens ?: 8192) }
    var systemInstruction by remember(currentSettings) {
        mutableStateOf(
            currentSettings?.systemInstruction
                ?: "You are AI TebeagStoryboard, an elite cinematic YouTube director, scriptwriter, and visual storyboard engine."
        )
    }

    val availableModels = listOf("gemini-3.5-flash", "gemini-3.5-pro", "gemini-2.5-flash")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "AI Studio Settings",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("back_btn_settings")) {
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
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "1. AI Engine & Credentials",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = CyberCyan
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("AI Provider:", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                    Spacer(modifier = Modifier.height(4.dp))
                    DropdownSelector(
                        items = listOf("Gemini API (Google AI Studio)", "Custom Endpoint"),
                        selectedItem = provider,
                        onItemSelected = { provider = it }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Custom Gemini API Key (Optional Override):", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = apiKey,
                        onValueChange = { apiKey = it },
                        placeholder = { Text("AIzaSy...") },
                        visualTransformation = if (showApiKey) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showApiKey = !showApiKey }) {
                                Icon(
                                    imageVector = if (showApiKey) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle API Key",
                                    tint = TextMuted
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth().testTag("api_key_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = BorderGlow
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Catatan: Jika dikosongkan, studio akan menggunakan kunci BuildConfig standar atau engine studio offline secara otomatis.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp)
                    )
                }
            }

            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "2. Model & Generation Parameters",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = CyberCyan
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Pilih Model AI:", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                    Spacer(modifier = Modifier.height(4.dp))
                    DropdownSelector(
                        items = availableModels,
                        selectedItem = selectedModel,
                        onItemSelected = { selectedModel = it }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Creativity / Temperature: ${String.format("%.2f", temperature)}",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary)
                        )
                    }

                    Slider(
                        value = temperature,
                        onValueChange = { temperature = it },
                        valueRange = 0.0f..1.0f,
                        steps = 10,
                        colors = SliderDefaults.colors(
                            thumbColor = CyberCyan,
                            activeTrackColor = CyberCyan,
                            inactiveTrackColor = SurfaceCardHighlight
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Max Output Tokens: $maxTokens", style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary))
                    Slider(
                        value = maxTokens.toFloat(),
                        onValueChange = { maxTokens = it.toInt() },
                        valueRange = 2048f..16384f,
                        steps = 7,
                        colors = SliderDefaults.colors(
                            thumbColor = CyberBlue,
                            activeTrackColor = CyberBlue,
                            inactiveTrackColor = SurfaceCardHighlight
                        )
                    )
                }
            }

            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "3. Master System Instruction",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = CyberCyan
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = systemInstruction,
                        onValueChange = { systemInstruction = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 120.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = BorderGlow
                        )
                    )
                }
            }

            item {
                GradientButton(
                    text = "Simpan Pengaturan AI",
                    icon = Icons.Default.Save,
                    onClick = {
                        val newSettings = AiSettingsEntity(
                            id = 1,
                            provider = provider,
                            customApiKey = apiKey.trim(),
                            model = selectedModel,
                            temperature = temperature,
                            maxTokens = maxTokens,
                            systemInstruction = systemInstruction
                        )
                        viewModel.saveAiSettings(newSettings)
                        Toast.makeText(context, "Pengaturan AI berhasil disimpan!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth().testTag("save_ai_settings_btn")
                )
            }
        }
    }
}
