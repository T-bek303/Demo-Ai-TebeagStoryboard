package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.data.local.entities.TitleIdeaEntity
import com.example.data.model.Presets
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.StoryboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViralTitleFinderScreen(
    viewModel: StoryboardViewModel,
    onNavigateBack: () -> Unit
) {
    val project by viewModel.activeProject.collectAsState()
    val titles by viewModel.activeTitles.collectAsState()
    var selectedCategory by remember { mutableStateOf("All Categories") }
    var searchKeyword by remember { mutableStateOf("") }
    var customTone by remember { mutableStateOf("") }

    var showAddCustomDialog by remember { mutableStateOf(false) }
    var customTitleInput by remember { mutableStateOf("") }

    val filteredTitles = titles.filter { item ->
        val matchesCat = if (selectedCategory == "All Categories") true else item.category.equals(selectedCategory, ignoreCase = true)
        val matchesKeyword = if (searchKeyword.isBlank()) true else item.title.contains(searchKeyword, ignoreCase = true)
        matchesCat && matchesKeyword
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Viral Title Finder",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = project?.name ?: "No Project Selected",
                            style = MaterialTheme.typography.bodySmall.copy(color = CyberCyan),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("back_btn_titles")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showAddCustomDialog = true },
                        modifier = Modifier.testTag("add_custom_title_btn")
                    ) {
                        Icon(Icons.Default.AddCircleOutline, contentDescription = "Add Custom Title", tint = CyberCyan)
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
            // Regeneration Action Bar
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
                            text = "20 Viral Title Matrix",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Algoritma skor CTR, Curiosity, SEO, dan Emotional resonance.",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                    }
                    Button(
                        onClick = {
                            project?.let { viewModel.regenerateTitles(it.id, searchKeyword, customTone) }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = ObsidianBg),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Regenerate 20", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Category Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(Presets.TITLE_CATEGORIES) { category ->
                    val isSelected = selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        label = { Text(category) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyberCyan,
                            selectedLabelColor = ObsidianBg,
                            containerColor = SurfaceCard,
                            labelColor = TextPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (filteredTitles.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Belum ada ide judul.", style = MaterialTheme.typography.titleMedium.copy(color = TextSecondary))
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { project?.let { viewModel.regenerateTitles(it.id) } },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = ObsidianBg)
                        ) {
                            Text("Generate 20 Judul Sekarang", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredTitles, key = { it.id }) { item ->
                        TitleCard(
                            item = item,
                            onSelect = {
                                project?.let { p ->
                                    viewModel.selectFinalTitle(p.id, item.id, item.title)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAddCustomDialog) {
        AlertDialog(
            onDismissRequest = { showAddCustomDialog = false },
            title = { Text("Tambah Judul Kustom", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = customTitleInput,
                    onValueChange = { customTitleInput = it },
                    label = { Text("Tulis Judul Anda Sendiri") },
                    placeholder = { Text("Contoh: The Secret That Wall Street Hides From You") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customTitleInput.isNotBlank()) {
                            project?.let { p ->
                                viewModel.addCustomTitle(p.id, customTitleInput)
                            }
                            showAddCustomDialog = false
                            customTitleInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = ObsidianBg)
                ) {
                    Text("Pilih Judul Ini", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCustomDialog = false }) {
                    Text("Batal", color = TextSecondary)
                }
            },
            containerColor = SurfaceDark
        )
    }
}

@Composable
fun TitleCard(
    item: TitleIdeaEntity,
    onSelect: () -> Unit
) {
    val borderColor = if (item.isSelected) CyberCyan else BorderGlow
    val bgColor = if (item.isSelected) SurfaceCardHighlight else SurfaceCard

    CyberCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = borderColor,
        backgroundColor = bgColor
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = CyberCyan.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "#${item.titleNumber}",
                        color = CyberCyan,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    color = NeonPurple.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = item.category,
                        color = TextAccent,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            if (item.isSelected) {
                Surface(
                    color = EmeraldGreen.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.4f))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Judul Terpilih", color = EmeraldGreen, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = item.title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                lineHeight = 22.sp
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 6 Metrics Pill Row
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            ScorePill(label = "CTR", score = item.ctrScore, modifier = Modifier.weight(1f))
            ScorePill(label = "Curiosity", score = item.curiosityScore, modifier = Modifier.weight(1f))
            ScorePill(label = "SEO", score = item.seoScore, modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            ScorePill(label = "Viral", score = item.viralScore, modifier = Modifier.weight(1f))
            ScorePill(label = "Emotional", score = item.emotionalScore, modifier = Modifier.weight(1f))
            ScorePill(label = "Evergreen", score = item.evergreenScore, modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            CopyButton(textToCopy = item.title, testTagSuffix = "title_${item.id}")
            Spacer(modifier = Modifier.width(8.dp))
            if (!item.isSelected) {
                Button(
                    onClick = onSelect,
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = ObsidianBg),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text("Gunakan Judul Ini", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}
