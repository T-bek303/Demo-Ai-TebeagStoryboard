package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.StoryboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectWorkspaceScreen(
    projectId: String,
    viewModel: StoryboardViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(projectId) {
        viewModel.selectProject(projectId)
    }

    val project by viewModel.activeProject.collectAsState()
    val titles by viewModel.activeTitles.collectAsState()
    val scenes by viewModel.activeScenes.collectAsState()
    val characters by viewModel.activeCharacters.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        "Overview",
        "Judul (${titles.size})",
        "Naskah",
        "Karakter (${characters.size})",
        "Scene (${scenes.size})",
        "Storyboard",
        "YouTube SEO",
        "Export"
    )

    var showSaveAsTemplateDialog by remember { mutableStateOf(false) }
    var templateNameInput by remember { mutableStateOf("") }
    var templateDescInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = project?.name ?: "Studio Workspace",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${project?.id ?: ""} • ${project?.niche ?: ""}",
                                style = MaterialTheme.typography.bodySmall.copy(color = CyberCyan, fontSize = 11.sp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            project?.status?.let { StatusBadge(status = it) }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("workspace_back_btn")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            templateNameInput = "${project?.niche ?: "Video"} Template"
                            templateDescInput = "Formula berdasarkan project ${project?.name ?: ""}"
                            showSaveAsTemplateDialog = true
                        }
                    ) {
                        Icon(Icons.Default.BookmarkAdd, contentDescription = "Save as Template", tint = CyberCyan)
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
        ) {
            // Scrollable Tab Row
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = SurfaceDark,
                contentColor = CyberCyan,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = CyberCyan
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTabIndex == index) CyberCyan else TextSecondary
                                )
                            )
                        }
                    )
                }
            }

            // Tab Content Body
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ObsidianBg)
            ) {
                when (selectedTabIndex) {
                    0 -> WorkspaceOverviewTab(
                        project = project,
                        titlesCount = titles.size,
                        scenesCount = scenes.size,
                        charactersCount = characters.size,
                        onTabSelect = { selectedTabIndex = it },
                        onRegenerateAll = {
                            project?.let { p ->
                                viewModel.regenerateTitles(p.id)
                                viewModel.regenerateScript(p.id)
                                viewModel.regenerateScenes(p.id)
                                viewModel.regenerateSeo(p.id)
                                Toast.makeText(context, "Regenerating all studio modules...", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                    1 -> ViralTitleFinderScreen(viewModel = viewModel, onNavigateBack = { selectedTabIndex = 0 })
                    2 -> ScriptStudioScreen(
                        viewModel = viewModel,
                        onNavigateBack = { selectedTabIndex = 0 },
                        onNavigateToScenes = { selectedTabIndex = 4 }
                    )
                    3 -> CharacterStudioScreen(viewModel = viewModel, onNavigateBack = { selectedTabIndex = 0 })
                    4 -> SceneStudioScreen(
                        viewModel = viewModel,
                        onNavigateBack = { selectedTabIndex = 0 },
                        onNavigateToStoryboard = { selectedTabIndex = 5 }
                    )
                    5 -> StoryboardScreen(viewModel = viewModel, onNavigateBack = { selectedTabIndex = 0 })
                    6 -> YouTubeSeoScreen(viewModel = viewModel, onNavigateBack = { selectedTabIndex = 0 })
                    7 -> ExportScreen(viewModel = viewModel, onNavigateBack = { selectedTabIndex = 0 })
                }
            }
        }
    }

    if (showSaveAsTemplateDialog) {
        AlertDialog(
            onDismissRequest = { showSaveAsTemplateDialog = false },
            title = { Text("Simpan Sebagai Template", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = templateNameInput,
                        onValueChange = { templateNameInput = it },
                        label = { Text("Nama Template") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = templateDescInput,
                        onValueChange = { templateDescInput = it },
                        label = { Text("Deskripsi Template") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val currentProject = project
                        if (currentProject != null && templateNameInput.isNotBlank()) {
                            viewModel.saveAsCustomTemplate(templateNameInput, templateDescInput, currentProject)
                            Toast.makeText(context, "Template berhasil disimpan!", Toast.LENGTH_SHORT).show()
                            showSaveAsTemplateDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = ObsidianBg)
                ) {
                    Text("Simpan Template", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveAsTemplateDialog = false }) {
                    Text("Batal", color = TextSecondary)
                }
            },
            containerColor = SurfaceDark
        )
    }
}

@Composable
fun WorkspaceOverviewTab(
    project: com.example.data.local.entities.ProjectEntity?,
    titlesCount: Int,
    scenesCount: Int,
    charactersCount: Int,
    onTabSelect: (Int) -> Unit,
    onRegenerateAll: () -> Unit
) {
    if (project == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = CyberCyan)
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Master Project Header Card
        item {
            CyberCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Project Summary",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = CyberCyan
                        )
                    )
                    Button(
                        onClick = onRegenerateAll,
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceCardHighlight, contentColor = CyberCyan),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Regenerate All", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = project.name,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Topik: ${project.topic}",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                )

                if (project.finalTitle.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = SurfaceDark,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("JUDUL TERPILIH:", style = MaterialTheme.typography.labelSmall.copy(color = CyberCyan, fontSize = 9.sp))
                            Text(project.finalTitle, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
                        }
                    }
                }
            }
        }

        // 4 Fast Jump Quick Stats
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                WorkspaceQuickJumpPill(
                    title = "Judul",
                    value = "$titlesCount Pilihan",
                    icon = Icons.Default.TrendingUp,
                    color = AmberGlow,
                    onClick = { onTabSelect(1) },
                    modifier = Modifier.weight(1f)
                )
                WorkspaceQuickJumpPill(
                    title = "Naskah",
                    value = "${project.durationMinutes}m (${project.durationMinutes * project.wpm} kata)",
                    icon = Icons.Default.Article,
                    color = NeonPurple,
                    onClick = { onTabSelect(2) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                WorkspaceQuickJumpPill(
                    title = "Karakter",
                    value = "$charactersCount Profil",
                    icon = Icons.Default.PersonOutline,
                    color = Color(0xFFEC4899),
                    onClick = { onTabSelect(3) },
                    modifier = Modifier.weight(1f)
                )
                WorkspaceQuickJumpPill(
                    title = "Scene",
                    value = "$scenesCount Frames",
                    icon = Icons.Default.Movie,
                    color = CyberCyan,
                    onClick = { onTabSelect(4) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Parameter Breakdown Grid
        item {
            CyberCard(modifier = Modifier.fillMaxWidth()) {
                Text("Parameter Teknis Studio", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(10.dp))
                ParamRow("Niche / Sub-Niche", "${project.niche} (${project.subNiche})")
                ParamRow("Gaya Bahasa", project.contentStyle)
                ParamRow("Gaya Visual", project.animationStyle)
                ParamRow("Target Audience", project.targetAudience)
                ParamRow("Bahasa & Negara", "${project.language} (${project.country})")
                ParamRow("Kecepatan WPM", "${project.wpm} WPM")
            }
        }

        // Quick Export CTA
        item {
            GradientButton(
                text = "Export Master Package (TXT/PDF/JSON)",
                icon = Icons.Default.Download,
                onClick = { onTabSelect(7) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun WorkspaceQuickJumpPill(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CyberCard(
        modifier = modifier,
        onClick = onClick,
        backgroundColor = SurfaceCard
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                Text(text = value, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary), maxLines = 1)
            }
        }
    }
}

@Composable
fun ParamRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
        Text(text = value, style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary, fontWeight = FontWeight.SemiBold))
    }
}
