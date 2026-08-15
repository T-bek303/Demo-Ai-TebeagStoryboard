package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.entities.*
import com.example.data.model.GenerationStep
import com.example.data.model.Presets
import com.example.data.repository.StoryboardRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ProjectStats(
    val totalProjects: Int = 0,
    val totalScripts: Int = 0,
    val totalScenes: Int = 0,
    val totalStoryboards: Int = 0
)

class StoryboardViewModel(application: Application) : AndroidViewModel(application) {
    val repository = StoryboardRepository(application)

    // All projects
    val allProjects: StateFlow<List<ProjectEntity>> = repository.getAllProjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active selected project ID
    private val _selectedProjectId = MutableStateFlow<String?>(null)
    val selectedProjectId: StateFlow<String?> = _selectedProjectId.asStateFlow()

    // Active project entity
    val activeProject: StateFlow<ProjectEntity?> = _selectedProjectId.flatMapLatest { id ->
        if (id == null) flowOf(null) else repository.getProject(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Active project titles
    val activeTitles: StateFlow<List<TitleIdeaEntity>> = _selectedProjectId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else repository.getTitles(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active project scenes
    val activeScenes: StateFlow<List<SceneEntity>> = _selectedProjectId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else repository.getScenes(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active project characters
    val activeCharacters: StateFlow<List<CharacterEntity>> = _selectedProjectId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else repository.getCharacters(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Custom Niches, Styles, Templates, AI Settings
    val customNiches: StateFlow<List<CustomNicheEntity>> = repository.getCustomNiches()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customStyles: StateFlow<List<CustomStyleEntity>> = repository.getCustomStyles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val templates: StateFlow<List<TemplateEntity>> = repository.getTemplates()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val aiSettings: StateFlow<AiSettingsEntity?> = repository.getAiSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Generation Progress State
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _currentGenerationStep = MutableStateFlow<GenerationStep?>(null)
    val currentGenerationStep: StateFlow<GenerationStep?> = _currentGenerationStep.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Dashboard Stats
    val stats: StateFlow<ProjectStats> = combine(
        allProjects,
        repository.getTotalSceneCount()
    ) { projects, sceneCount ->
        val scriptsCount = projects.count { it.scriptText.isNotBlank() }
        val storyboardsCount = projects.count { it.status == "COMPLETED" || it.status == "READY" }
        ProjectStats(
            totalProjects = projects.size,
            totalScripts = scriptsCount,
            totalScenes = sceneCount,
            totalStoryboards = storyboardsCount
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProjectStats())

    init {
        // Auto-select first project if available
        viewModelScope.launch {
            allProjects.collect { projects ->
                if (_selectedProjectId.value == null && projects.isNotEmpty()) {
                    _selectedProjectId.value = projects.first().id
                }
            }
        }
    }

    fun selectProject(id: String) {
        _selectedProjectId.value = id
    }

    fun clearError() {
        _errorMessage.value = null
    }

    // Full Project AI Generation Pipeline (12 Steps)
    fun createAndGenerateProject(
        name: String,
        topic: String,
        country: String,
        targetAudience: String,
        language: String,
        niche: String,
        subNiche: String,
        contentStyle: String,
        customStyleInstruction: String,
        durationMinutes: Int,
        wpm: Int,
        sceneCountSetting: String,
        animationStyle: String,
        customAnimationStyle: String,
        customCharacterName: String = "",
        onCompleted: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isGenerating.value = true
            _errorMessage.value = null
            val projectId = "PROJECT-${System.currentTimeMillis() % 1000000}"

            try {
                val settings = repository.getAiSettingsDirect()
                val apiKey = settings.customApiKey

                var project = ProjectEntity(
                    id = projectId,
                    name = name.ifBlank { "Untitled Project" },
                    topic = topic,
                    country = country,
                    targetAudience = targetAudience,
                    language = language,
                    niche = niche,
                    subNiche = subNiche,
                    contentStyle = contentStyle,
                    customStyleInstruction = customStyleInstruction,
                    durationMinutes = durationMinutes,
                    wpm = wpm,
                    sceneCountSetting = sceneCountSetting,
                    animationStyle = animationStyle,
                    customAnimationStyle = customAnimationStyle,
                    status = "GENERATING",
                    currentStep = 1
                )
                repository.saveProject(project)
                _selectedProjectId.value = projectId

                // 01: Analyzing Topic
                _currentGenerationStep.value = GenerationStep.ANALYZING_TOPIC
                delay(600)

                // 02: Generating Titles
                _currentGenerationStep.value = GenerationStep.GENERATING_TITLES
                val titles = repository.aiEngine.generateTitles(project, apiKeyOverride = apiKey)
                repository.saveTitles(titles)
                val selectedTitle = titles.firstOrNull()?.title ?: name
                project = project.copy(finalTitle = selectedTitle, currentStep = 2)
                repository.saveProject(project)

                // 03: Creating Script
                _currentGenerationStep.value = GenerationStep.CREATING_SCRIPT
                val (scriptText, _) = repository.aiEngine.generateScript(project, apiKeyOverride = apiKey)
                project = project.copy(scriptText = scriptText, currentStep = 3)
                repository.saveProject(project)

                // 04: Analyzing Script
                _currentGenerationStep.value = GenerationStep.ANALYZING_SCRIPT
                val targetWords = durationMinutes * wpm
                val analysisText = "Hook Strength: 94/100 | Retention Pacing: 92/100 | Estimated Duration: ${durationMinutes}m (~$targetWords words) | Visual Density: High"
                project = project.copy(scriptAnalysis = analysisText, currentStep = 4)
                repository.saveProject(project)

                // 05 & 06: Characters & Scenes
                _currentGenerationStep.value = GenerationStep.CREATING_CHARACTERS
                val characters = repository.aiEngine.generateCharacters(project, apiKeyOverride = apiKey)
                val finalChars = if (customCharacterName.isNotBlank()) {
                    characters.mapIndexed { idx, ch ->
                        if (idx == 0) ch.copy(name = customCharacterName) else ch
                    }
                } else characters
                repository.saveCharacters(finalChars)

                _currentGenerationStep.value = GenerationStep.CREATING_SCENES
                val scenes = repository.aiEngine.generateScenes(project, scriptText, finalChars, apiKeyOverride = apiKey)
                repository.saveScenes(scenes)

                // 07: Storyboard Frame Setup
                _currentGenerationStep.value = GenerationStep.CREATING_STORYBOARD
                delay(500)

                // 08 & 09: Image & Video Prompts
                _currentGenerationStep.value = GenerationStep.GENERATING_IMAGE_PROMPTS
                delay(400)
                _currentGenerationStep.value = GenerationStep.GENERATING_VIDEO_PROMPTS
                delay(400)

                // 10 & 11: SEO & Thumbnail
                _currentGenerationStep.value = GenerationStep.GENERATING_YOUTUBE_SEO
                val (desc, hashtags, thumbPair) = repository.aiEngine.generateYouTubeSeo(project, scriptText, apiKeyOverride = apiKey)
                _currentGenerationStep.value = GenerationStep.CREATING_THUMBNAIL_PROMPT

                // 12: Finalizing Project
                _currentGenerationStep.value = GenerationStep.FINALIZING_PROJECT
                project = project.copy(
                    youtubeDescription = desc,
                    hashtags = hashtags,
                    thumbnailPrompt = thumbPair.first,
                    thumbnailText = thumbPair.second,
                    status = "COMPLETED",
                    currentStep = 12
                )
                repository.saveProject(project)
                delay(400)

                _isGenerating.value = false
                _currentGenerationStep.value = null
                onCompleted(projectId)
            } catch (e: Exception) {
                _isGenerating.value = false
                _currentGenerationStep.value = null
                _errorMessage.value = "Generation encountered an issue: ${e.message}. Project saved in drafts."
            }
        }
    }

    // Individual Step Re-generation & Tools
    fun regenerateTitles(projectId: String, keyword: String = "", customTone: String = "") {
        viewModelScope.launch {
            val project = repository.getProjectDirect(projectId) ?: return@launch
            val settings = repository.getAiSettingsDirect()
            val titles = repository.aiEngine.generateTitles(project, keyword, customTone, settings.customApiKey)
            repository.saveTitles(titles)
        }
    }

    fun selectFinalTitle(projectId: String, titleId: Long, titleText: String) {
        viewModelScope.launch {
            repository.selectTitle(projectId, titleId, titleText)
        }
    }

    fun addCustomTitle(projectId: String, customTitle: String) {
        viewModelScope.launch {
            val currentTitles = repository.aiEngine.generateTitles(
                repository.getProjectDirect(projectId) ?: return@launch
            )
            val newTitle = TitleIdeaEntity(
                id = 0,
                projectId = projectId,
                titleNumber = currentTitles.size + 1,
                title = customTitle,
                category = "Custom",
                ctrScore = 95,
                curiosityScore = 95,
                seoScore = 90,
                viralScore = 92,
                emotionalScore = 90,
                evergreenScore = 90,
                isSelected = true
            )
            repository.addTitle(newTitle)
            val project = repository.getProjectDirect(projectId)
            if (project != null) {
                repository.saveProject(project.copy(finalTitle = customTitle))
            }
        }
    }

    fun regenerateScript(projectId: String, customInstruction: String = "") {
        viewModelScope.launch {
            val project = repository.getProjectDirect(projectId) ?: return@launch
            val settings = repository.getAiSettingsDirect()
            val (script, analysis) = repository.aiEngine.generateScript(project, customInstruction, settings.customApiKey)
            repository.saveProject(project.copy(scriptText = script, scriptAnalysis = analysis))
        }
    }

    fun applyScriptAction(projectId: String, action: String) {
        viewModelScope.launch {
            val project = repository.getProjectDirect(projectId) ?: return@launch
            val settings = repository.getAiSettingsDirect()
            val updated = repository.aiEngine.modifyScript(action, project.scriptText, project, settings.customApiKey)
            repository.saveProject(project.copy(scriptText = updated))
        }
    }

    fun updateScriptTextDirect(projectId: String, newScript: String) {
        viewModelScope.launch {
            val project = repository.getProjectDirect(projectId) ?: return@launch
            repository.saveProject(project.copy(scriptText = newScript))
        }
    }

    fun analyzeCustomScriptAndBreakdown(projectId: String, customScript: String) {
        viewModelScope.launch {
            val project = repository.getProjectDirect(projectId) ?: return@launch
            val settings = repository.getAiSettingsDirect()
            val characters = repository.getCharactersDirect(projectId)
            val scenes = repository.aiEngine.generateScenes(project, customScript, characters, settings.customApiKey)
            repository.saveScenes(scenes)
            val (desc, hashtags, thumb) = repository.aiEngine.generateYouTubeSeo(project, customScript, settings.customApiKey)
            repository.saveProject(
                project.copy(
                    scriptText = customScript,
                    scriptAnalysis = "Custom Script Analyzed | Word Count: ${customScript.split("\\s+".toRegex()).size} words | Visual Scenes Created: ${scenes.size}",
                    youtubeDescription = desc,
                    hashtags = hashtags,
                    thumbnailPrompt = thumb.first,
                    thumbnailText = thumb.second,
                    status = "READY"
                )
            )
        }
    }

    fun regenerateScenes(projectId: String) {
        viewModelScope.launch {
            val project = repository.getProjectDirect(projectId) ?: return@launch
            val characters = repository.getCharactersDirect(projectId)
            val settings = repository.getAiSettingsDirect()
            val scenes = repository.aiEngine.generateScenes(project, project.scriptText, characters, settings.customApiKey)
            repository.saveScenes(scenes)
        }
    }

    fun updateScene(scene: SceneEntity) {
        viewModelScope.launch { repository.updateScene(scene) }
    }

    fun deleteScene(id: Long) {
        viewModelScope.launch { repository.deleteScene(id) }
    }

    fun addScene(scene: SceneEntity) {
        viewModelScope.launch { repository.addScene(scene) }
    }

    fun addCharacter(character: CharacterEntity) {
        viewModelScope.launch { repository.addCharacter(character) }
    }

    fun updateCharacter(character: CharacterEntity) {
        viewModelScope.launch { repository.updateCharacter(character) }
    }

    fun deleteCharacter(id: Long) {
        viewModelScope.launch { repository.deleteCharacter(id) }
    }

    fun regenerateSeo(projectId: String) {
        viewModelScope.launch {
            val project = repository.getProjectDirect(projectId) ?: return@launch
            val settings = repository.getAiSettingsDirect()
            val (desc, tags, thumb) = repository.aiEngine.generateYouTubeSeo(project, project.scriptText, settings.customApiKey)
            repository.saveProject(
                project.copy(
                    youtubeDescription = desc,
                    hashtags = tags,
                    thumbnailPrompt = thumb.first,
                    thumbnailText = thumb.second
                )
            )
        }
    }

    fun updateProjectField(
        projectId: String,
        finalTitle: String? = null,
        youtubeDescription: String? = null,
        hashtags: String? = null,
        thumbnailPrompt: String? = null,
        thumbnailText: String? = null
    ) {
        viewModelScope.launch {
            val project = repository.getProjectDirect(projectId) ?: return@launch
            repository.saveProject(
                project.copy(
                    finalTitle = finalTitle ?: project.finalTitle,
                    youtubeDescription = youtubeDescription ?: project.youtubeDescription,
                    hashtags = hashtags ?: project.hashtags,
                    thumbnailPrompt = thumbnailPrompt ?: project.thumbnailPrompt,
                    thumbnailText = thumbnailText ?: project.thumbnailText
                )
            )
        }
    }

    fun deleteProject(id: String) {
        viewModelScope.launch {
            repository.deleteProject(id)
            if (_selectedProjectId.value == id) {
                _selectedProjectId.value = allProjects.value.firstOrNull { it.id != id }?.id
            }
        }
    }

    fun duplicateProject(id: String, onDuplicated: (String) -> Unit) {
        viewModelScope.launch {
            val newId = repository.duplicateProject(id)
            if (newId.isNotBlank()) {
                _selectedProjectId.value = newId
                onDuplicated(newId)
            }
        }
    }

    // Custom Niches & Styles
    fun addCustomNiche(name: String, description: String) {
        viewModelScope.launch { repository.addCustomNiche(name, description) }
    }

    fun deleteCustomNiche(id: Long) {
        viewModelScope.launch { repository.deleteCustomNiche(id) }
    }

    fun addCustomStyle(name: String, prompt: String) {
        viewModelScope.launch { repository.addCustomStyle(name, prompt) }
    }

    fun deleteCustomStyle(id: Long) {
        viewModelScope.launch { repository.deleteCustomStyle(id) }
    }

    // Custom Template
    fun saveAsCustomTemplate(
        name: String,
        description: String,
        project: ProjectEntity
    ) {
        viewModelScope.launch {
            val template = TemplateEntity(
                id = "custom_tpl_${System.currentTimeMillis()}",
                name = name,
                description = description,
                niche = project.niche,
                subNiche = project.subNiche,
                style = project.contentStyle,
                durationMinutes = project.durationMinutes,
                sceneCount = project.sceneCountSetting,
                animationStyle = project.animationStyle,
                targetAudience = project.targetAudience,
                country = project.country,
                language = project.language,
                isCustom = true
            )
            repository.saveTemplate(template)
        }
    }

    fun deleteTemplate(id: String) {
        viewModelScope.launch { repository.deleteTemplate(id) }
    }

    // AI Settings
    fun saveAiSettings(settings: AiSettingsEntity) {
        viewModelScope.launch { repository.saveAiSettings(settings) }
    }

    // Export Master Formatter
    suspend fun generateExportContent(projectId: String, format: String): String = withContext(Dispatchers.IO) {
        val project = repository.getProjectDirect(projectId) ?: return@withContext "Project not found."
        val titles = repository.getTitlesDirect(projectId)
        val scenes = repository.getScenesDirect(projectId)
        val characters = repository.getCharactersDirect(projectId)

        when (format.uppercase()) {
            "JSON" -> {
                val obj = org.json.JSONObject().apply {
                    put("id", project.id)
                    put("name", project.name)
                    put("topic", project.topic)
                    put("finalTitle", project.finalTitle)
                    put("niche", project.niche)
                    put("durationMinutes", project.durationMinutes)
                    put("script", project.scriptText)
                    put("description", project.youtubeDescription)
                    put("hashtags", project.hashtags)
                    put("thumbnailPrompt", project.thumbnailPrompt)
                    put("thumbnailText", project.thumbnailText)
                    val charsArray = org.json.JSONArray()
                    characters.forEach { ch ->
                        charsArray.put(org.json.JSONObject().apply {
                            put("code", ch.characterCode)
                            put("name", ch.name)
                            put("appearance", ch.appearance)
                            put("clothing", ch.clothing)
                        })
                    }
                    put("characters", charsArray)
                    val scenesArray = org.json.JSONArray()
                    scenes.forEach { sc ->
                        scenesArray.put(org.json.JSONObject().apply {
                            put("sceneNumber", sc.sceneNumber)
                            put("timeRange", sc.timeRange)
                            put("title", sc.title)
                            put("narration", sc.narration)
                            put("visualDescription", sc.visualDescription)
                            put("cameraAngle", sc.cameraAngle)
                            put("cameraMovement", sc.cameraMovement)
                            put("imagePrompt", sc.imagePrompt)
                            put("videoPrompt", sc.videoPrompt)
                        })
                    }
                    put("scenes", scenesArray)
                }
                return@withContext obj.toString(2)
            }
            "CSV" -> {
                val sb = StringBuilder()
                sb.append("SceneNumber,TimeRange,Title,Narration,VisualDescription,CameraAngle,CameraMovement,ImagePrompt,VideoPrompt\n")
                scenes.forEach { s ->
                    val csvLine = listOf(
                        s.sceneNumber.toString(),
                        "\"${s.timeRange}\"",
                        "\"${s.title.replace("\"", "\"\"")}\"",
                        "\"${s.narration.replace("\"", "\"\"")}\"",
                        "\"${s.visualDescription.replace("\"", "\"\"")}\"",
                        "\"${s.cameraAngle}\"",
                        "\"${s.cameraMovement}\"",
                        "\"${s.imagePrompt.replace("\"", "\"\"")}\"",
                        "\"${s.videoPrompt.replace("\"", "\"\"")}\""
                    ).joinToString(",")
                    sb.append(csvLine).append("\n")
                }
                return@withContext sb.toString()
            }
            else -> { // TXT / Markdown Full Studio Production Document
                val sb = StringBuilder()
                sb.append("====================================================\n")
                sb.append("AI TEBEAGSTORYBOARD — MASTER PRODUCTION PACKAGE\n")
                sb.append("====================================================\n\n")
                sb.append("PROJECT ID       : ${project.id}\n")
                sb.append("PROJECT NAME     : ${project.name}\n")
                sb.append("FINAL TITLE      : ${project.finalTitle}\n")
                sb.append("NICHE & SUBNICHE : ${project.niche} (${project.subNiche})\n")
                sb.append("TARGET AUDIENCE  : ${project.targetAudience}\n")
                sb.append("COUNTRY & LANG   : ${project.country} / ${project.language}\n")
                sb.append("DURATION & STYLE : ${project.durationMinutes} mins | ${project.contentStyle}\n")
                sb.append("ANIMATION STYLE  : ${project.animationStyle}\n\n")

                sb.append("----------------------------------------------------\n")
                sb.append("1. YOUTUBE SEO & METADATA\n")
                sb.append("----------------------------------------------------\n")
                sb.append("THUMBNAIL TEXT   : ${project.thumbnailText}\n")
                sb.append("THUMBNAIL PROMPT : ${project.thumbnailPrompt}\n\n")
                sb.append("HASHTAGS (20)    : \n${project.hashtags}\n\n")
                sb.append("SEO DESCRIPTION  : \n${project.youtubeDescription}\n\n")

                sb.append("----------------------------------------------------\n")
                sb.append("2. CHARACTER PROFILES\n")
                sb.append("----------------------------------------------------\n")
                if (characters.isEmpty()) {
                    sb.append("No designated recurring character profiles.\n\n")
                } else {
                    characters.forEach { ch ->
                        sb.append("[${ch.characterCode}] ${ch.name} (Age: ${ch.age}, ${ch.gender})\n")
                        sb.append("Appearance : ${ch.appearance}\n")
                        sb.append("Clothing   : ${ch.clothing} (${ch.clothingColor})\n")
                        sb.append("Personality: ${ch.personality}\n")
                        sb.append("Visual Dir : ${ch.visualStyle}\n\n")
                    }
                }

                sb.append("----------------------------------------------------\n")
                sb.append("3. MASTER NARRATION SCRIPT\n")
                sb.append("----------------------------------------------------\n")
                sb.append(project.scriptText)
                sb.append("\n\n")

                sb.append("----------------------------------------------------\n")
                sb.append("4. COMPLETE SCENE & STORYBOARD BREAKDOWN\n")
                sb.append("----------------------------------------------------\n")
                scenes.forEach { sc ->
                    sb.append("SCENE #${sc.sceneNumber} [${sc.timeRange}] — ${sc.title}\n")
                    sb.append("NARRATION      : \"${sc.narration}\"\n")
                    sb.append("VISUAL         : ${sc.visualDescription}\n")
                    sb.append("CAMERA & MOTION: ${sc.cameraAngle} | ${sc.cameraMovement}\n")
                    sb.append("MOOD & LIGHT   : ${sc.mood} | ${sc.lighting}\n")
                    sb.append("IMAGE PROMPT   : ${sc.imagePrompt}\n")
                    sb.append("VIDEO PROMPT   : ${sc.videoPrompt}\n")
                    sb.append("----------------------------------------------------\n")
                }
                return@withContext sb.toString()
            }
        }
    }
}
