package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.StoryboardViewModel

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Studio", Icons.Default.Dashboard)
    object CreateProject : Screen("create_project", "Baru", Icons.Default.AddCircle)
    object Projects : Screen("projects_list", "Project", Icons.Default.Folder)
    object Templates : Screen("templates", "Template", Icons.Default.Style)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {
    private val viewModel: StoryboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val bottomNavItems = listOf(
                    Screen.Dashboard,
                    Screen.Projects,
                    Screen.CreateProject,
                    Screen.Templates,
                    Screen.Settings
                )

                val showBottomBar = currentRoute in listOf(
                    Screen.Dashboard.route,
                    Screen.Projects.route,
                    Screen.Templates.route,
                    Screen.Settings.route
                )

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            NavigationBar(
                                containerColor = SurfaceDark,
                                contentColor = CyberCyan,
                                tonalElevation = 8.dp,
                                modifier = Modifier.testTag("bottom_nav_bar")
                            ) {
                                bottomNavItems.forEach { screen ->
                                    val isSelected = currentRoute == screen.route
                                    NavigationBarItem(
                                        icon = {
                                            Icon(
                                                imageVector = screen.icon,
                                                contentDescription = screen.title,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = screen.title,
                                                fontSize = 11.sp,
                                                color = if (isSelected) CyberCyan else TextSecondary
                                            )
                                        },
                                        selected = isSelected,
                                        onClick = {
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = CyberCyan,
                                            selectedTextColor = CyberCyan,
                                            indicatorColor = CyberCyan.copy(alpha = 0.15f),
                                            unselectedIconColor = TextSecondary,
                                            unselectedTextColor = TextSecondary
                                        ),
                                        modifier = Modifier.testTag("nav_item_${screen.route}")
                                    )
                                }
                            }
                        }
                    },
                    containerColor = ObsidianBg
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Dashboard.route,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(ObsidianBg)
                    ) {
                        composable(Screen.Dashboard.route) {
                            DashboardScreen(
                                viewModel = viewModel,
                                onNavigateToCreate = { navController.navigate(Screen.CreateProject.route) },
                                onNavigateToProjects = { navController.navigate(Screen.Projects.route) },
                                onNavigateToWorkspace = { pid -> navController.navigate("workspace/$pid") },
                                onNavigateToTitles = { navController.navigate("titles") },
                                onNavigateToScript = { navController.navigate("script") },
                                onNavigateToScenes = { navController.navigate("scenes") },
                                onNavigateToStoryboard = { navController.navigate("storyboard") },
                                onNavigateToCharacters = { navController.navigate("characters") },
                                onNavigateToSeo = { navController.navigate("seo") },
                                onNavigateToTemplates = { navController.navigate(Screen.Templates.route) },
                                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                            )
                        }

                        composable(Screen.CreateProject.route) {
                            CreateProjectScreen(
                                viewModel = viewModel,
                                onProjectCreated = { pid ->
                                    navController.navigate("workspace/$pid") {
                                        popUpTo(Screen.Dashboard.route)
                                    }
                                },
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable(Screen.Projects.route) {
                            ProjectsListScreen(
                                viewModel = viewModel,
                                onNavigateToCreate = { navController.navigate(Screen.CreateProject.route) },
                                onNavigateToWorkspace = { pid -> navController.navigate("workspace/$pid") }
                            )
                        }

                        composable(
                            route = "workspace/{projectId}",
                            arguments = listOf(navArgument("projectId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val pid = backStackEntry.arguments?.getString("projectId") ?: ""
                            ProjectWorkspaceScreen(
                                projectId = pid,
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("titles") {
                            ViralTitleFinderScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("script") {
                            ScriptStudioScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToScenes = { navController.navigate("scenes") }
                            )
                        }

                        composable("scenes") {
                            SceneStudioScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToStoryboard = { navController.navigate("storyboard") }
                            )
                        }

                        composable("storyboard") {
                            StoryboardScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("characters") {
                            CharacterStudioScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("seo") {
                            YouTubeSeoScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable(Screen.Templates.route) {
                            TemplateScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onUseTemplate = { tpl ->
                                    navController.navigate(Screen.CreateProject.route)
                                }
                            )
                        }

                        composable(Screen.Settings.route) {
                            AiSettingsScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("export") {
                            ExportScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
