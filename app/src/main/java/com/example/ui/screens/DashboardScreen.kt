package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.entities.ProjectEntity
import com.example.data.model.Presets
import com.example.data.model.TemplatePreset
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.StoryboardViewModel

@Composable
fun DashboardScreen(
    viewModel: StoryboardViewModel,
    onNavigateToCreate: () -> Unit,
    onNavigateToProjects: () -> Unit,
    onNavigateToWorkspace: (String) -> Unit,
    onNavigateToTitles: () -> Unit,
    onNavigateToScript: () -> Unit,
    onNavigateToScenes: () -> Unit,
    onNavigateToStoryboard: () -> Unit,
    onNavigateToCharacters: () -> Unit,
    onNavigateToSeo: () -> Unit,
    onNavigateToTemplates: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val stats by viewModel.stats.collectAsState()
    val projects by viewModel.allProjects.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val currentStep by viewModel.currentGenerationStep.collectAsState()

    if (isGenerating && currentStep != null) {
        GenerationProgressDialog(currentStep = currentStep!!) {}
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBg),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Hero Studio Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_studio_hero),
                    contentDescription = "Studio Hero",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    ObsidianBg.copy(alpha = 0.85f),
                                    ObsidianBg
                                )
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = CyberCyan.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "STUDIO PRO v2.6",
                                color = CyberCyan,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AI Video Engine",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "AI TEBEAGSTORYBOARD",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "From Idea to Complete Video Storyboard with AI.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextCyan)
                    )
                }
            }
        }

        // Primary Action: Project Baru
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                GradientButton(
                    text = "Project Baru",
                    icon = Icons.Default.Add,
                    onClick = onNavigateToCreate,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // 4 Studio Stats Grid
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(
                        title = "Total Projects",
                        value = "${stats.totalProjects}",
                        icon = Icons.Default.FolderOpen,
                        iconColor = CyberCyan,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Total Scripts",
                        value = "${stats.totalScripts}",
                        icon = Icons.Default.Article,
                        iconColor = NeonPurple,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(
                        title = "Total Scenes",
                        value = "${stats.totalScenes}",
                        icon = Icons.Default.Movie,
                        iconColor = CyberBlue,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Storyboards",
                        value = "${stats.totalStoryboards}",
                        icon = Icons.Default.Dashboard,
                        iconColor = EmeraldGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Quick AI Studio Modules
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                SectionHeader(
                    title = "Production Modules",
                    subtitle = "Modular AI video creation tools",
                    icon = Icons.Default.AutoAwesome
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ModulePill(
                        title = "Viral Titles",
                        icon = Icons.Default.TrendingUp,
                        color = AmberGlow,
                        onClick = onNavigateToTitles,
                        modifier = Modifier.weight(1f)
                    )
                    ModulePill(
                        title = "Script Studio",
                        icon = Icons.Default.EditNote,
                        color = NeonPurple,
                        onClick = onNavigateToScript,
                        modifier = Modifier.weight(1f)
                    )
                    ModulePill(
                        title = "Storyboard",
                        icon = Icons.Default.GridView,
                        color = CyberCyan,
                        onClick = onNavigateToStoryboard,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ModulePill(
                        title = "Characters",
                        icon = Icons.Default.PersonOutline,
                        color = Color(0xFFEC4899),
                        onClick = onNavigateToCharacters,
                        modifier = Modifier.weight(1f)
                    )
                    ModulePill(
                        title = "YouTube SEO",
                        icon = Icons.Default.Search,
                        color = EmeraldGreen,
                        onClick = onNavigateToSeo,
                        modifier = Modifier.weight(1f)
                    )
                    ModulePill(
                        title = "Templates",
                        icon = Icons.Default.Style,
                        color = DeepIndigo,
                        onClick = onNavigateToTemplates,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Recent Projects
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                SectionHeader(
                    title = "Project Terakhir",
                    subtitle = "Lanjutkan pengerjaan storyboard",
                    icon = Icons.Default.History,
                    trailing = {
                        TextButton(
                            onClick = onNavigateToProjects,
                            modifier = Modifier.testTag("see_all_projects_btn")
                        ) {
                            Text("Lihat Semua", color = CyberCyan)
                        }
                    }
                )

                if (projects.isEmpty()) {
                    CyberCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = SurfaceCard
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.FolderOpen,
                                contentDescription = "Empty",
                                tint = TextMuted,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Belum ada project",
                                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                            )
                            Text(
                                text = "Mulai buat project video baru pertama Anda.",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                            )
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        projects.take(3).forEach { project ->
                            RecentProjectCard(
                                project = project,
                                onClick = {
                                    viewModel.selectProject(project.id)
                                    onNavigateToWorkspace(project.id)
                                }
                            )
                        }
                    }
                }
            }
        }

        // Starter Templates Showcase
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                SectionHeader(
                    title = "Template Populer",
                    subtitle = "Mulai instan dengan formula teruji",
                    icon = Icons.Default.ViewCarousel,
                    trailing = {
                        TextButton(onClick = onNavigateToTemplates) {
                            Text("Semua", color = CyberCyan)
                        }
                    }
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    items(Presets.TEMPLATES.take(4)) { template ->
                        StarterTemplateCard(
                            template = template,
                            onClick = {
                                onNavigateToCreate()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ModulePill(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = SurfaceCard,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .minimumInteractiveComponentSize()
            .testTag("module_pill_${title.replace(" ", "_").lowercase()}")
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    fontSize = 11.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun RecentProjectCard(
    project: ProjectEntity,
    onClick: () -> Unit
) {
    CyberCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("project_item_${project.id}"),
        onClick = onClick,
        backgroundColor = SurfaceCard
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = project.id,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CyberCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    StatusBadge(status = project.status)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = project.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${project.niche} • ${project.durationMinutes}m • ${project.contentStyle}",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Open",
                tint = TextMuted,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun StarterTemplateCard(
    template: TemplatePreset,
    onClick: () -> Unit
) {
    Surface(
        color = SurfaceCard,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderGlow),
        modifier = Modifier
            .width(220.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Surface(
                color = CyberCyan.copy(alpha = 0.15f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = template.niche,
                    color = CyberCyan,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = template.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = template.description,
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${template.durationMinutes} min • ${template.animationStyle}",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextMuted),
                    maxLines = 1
                )
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Use",
                    tint = CyberCyan,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
