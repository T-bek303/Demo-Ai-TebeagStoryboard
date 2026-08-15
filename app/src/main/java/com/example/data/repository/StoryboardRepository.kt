package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.entities.*
import com.example.data.service.AIProductionEngine
import kotlinx.coroutines.flow.Flow

class StoryboardRepository(context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val projectDao = db.projectDao()
    private val titleIdeaDao = db.titleIdeaDao()
    private val sceneDao = db.sceneDao()
    private val characterDao = db.characterDao()
    private val customNicheDao = db.customNicheDao()
    private val customStyleDao = db.customStyleDao()
    private val templateDao = db.templateDao()
    private val aiSettingsDao = db.aiSettingsDao()

    val aiEngine = AIProductionEngine()

    // Projects
    fun getAllProjects(): Flow<List<ProjectEntity>> = projectDao.getAllProjects()
    fun getProject(id: String): Flow<ProjectEntity?> = projectDao.getProjectById(id)
    suspend fun getProjectDirect(id: String): ProjectEntity? = projectDao.getProjectByIdDirect(id)
    fun getProjectCount(): Flow<Int> = projectDao.getProjectCount()
    fun getTotalSceneCount(): Flow<Int> = sceneDao.getTotalSceneCount()

    suspend fun saveProject(project: ProjectEntity) {
        projectDao.insertOrUpdate(project.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteProject(id: String) {
        projectDao.deleteById(id)
        titleIdeaDao.deleteForProject(id)
        sceneDao.deleteForProject(id)
        characterDao.deleteForProject(id)
    }

    suspend fun duplicateProject(sourceId: String): String {
        val source = projectDao.getProjectByIdDirect(sourceId) ?: return ""
        val newId = "PROJECT-${System.currentTimeMillis() % 100000}"
        val newProject = source.copy(
            id = newId,
            name = "${source.name} (Copy)",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        projectDao.insertOrUpdate(newProject)

        val titles = titleIdeaDao.getTitlesForProjectDirect(sourceId)
        titleIdeaDao.insertAll(titles.map { it.copy(id = 0, projectId = newId) })

        val characters = characterDao.getCharactersForProjectDirect(sourceId)
        characterDao.insertAll(characters.map { it.copy(id = 0, projectId = newId) })

        val scenes = sceneDao.getScenesForProjectDirect(sourceId)
        sceneDao.insertAll(scenes.map { it.copy(id = 0, projectId = newId) })

        return newId
    }

    // Titles
    fun getTitles(projectId: String): Flow<List<TitleIdeaEntity>> = titleIdeaDao.getTitlesForProject(projectId)
    suspend fun getTitlesDirect(projectId: String): List<TitleIdeaEntity> = titleIdeaDao.getTitlesForProjectDirect(projectId)
    suspend fun saveTitles(titles: List<TitleIdeaEntity>) = titleIdeaDao.insertAll(titles)
    suspend fun addTitle(title: TitleIdeaEntity) = titleIdeaDao.insert(title)
    suspend fun updateTitle(title: TitleIdeaEntity) = titleIdeaDao.update(title)
    suspend fun selectTitle(projectId: String, titleId: Long, selectedTitleText: String) {
        titleIdeaDao.clearSelection(projectId)
        titleIdeaDao.setSelected(titleId)
        val project = projectDao.getProjectByIdDirect(projectId)
        if (project != null) {
            projectDao.insertOrUpdate(project.copy(finalTitle = selectedTitleText, updatedAt = System.currentTimeMillis()))
        }
    }

    // Scenes
    fun getScenes(projectId: String): Flow<List<SceneEntity>> = sceneDao.getScenesForProject(projectId)
    suspend fun getScenesDirect(projectId: String): List<SceneEntity> = sceneDao.getScenesForProjectDirect(projectId)
    suspend fun saveScenes(scenes: List<SceneEntity>) = sceneDao.insertAll(scenes)
    suspend fun addScene(scene: SceneEntity): Long = sceneDao.insert(scene)
    suspend fun updateScene(scene: SceneEntity) = sceneDao.update(scene)
    suspend fun deleteScene(id: Long) = sceneDao.deleteById(id)

    // Characters
    fun getCharacters(projectId: String): Flow<List<CharacterEntity>> = characterDao.getCharactersForProject(projectId)
    suspend fun getCharactersDirect(projectId: String): List<CharacterEntity> = characterDao.getCharactersForProjectDirect(projectId)
    suspend fun saveCharacters(characters: List<CharacterEntity>) = characterDao.insertAll(characters)
    suspend fun addCharacter(character: CharacterEntity): Long = characterDao.insert(character)
    suspend fun updateCharacter(character: CharacterEntity) = characterDao.update(character)
    suspend fun deleteCharacter(id: Long) = characterDao.deleteById(id)

    // Custom Niches & Styles
    fun getCustomNiches(): Flow<List<CustomNicheEntity>> = customNicheDao.getAllCustomNiches()
    suspend fun addCustomNiche(name: String, description: String): Long =
        customNicheDao.insert(CustomNicheEntity(name = name, description = description))
    suspend fun deleteCustomNiche(id: Long) = customNicheDao.deleteById(id)

    fun getCustomStyles(): Flow<List<CustomStyleEntity>> = customStyleDao.getAllCustomStyles()
    suspend fun addCustomStyle(name: String, promptInstruction: String): Long =
        customStyleDao.insert(CustomStyleEntity(name = name, promptInstruction = promptInstruction))
    suspend fun deleteCustomStyle(id: Long) = customStyleDao.deleteById(id)

    // Templates
    fun getTemplates(): Flow<List<TemplateEntity>> = templateDao.getAllTemplates()
    suspend fun saveTemplate(template: TemplateEntity) = templateDao.insert(template)
    suspend fun deleteTemplate(id: String) = templateDao.deleteById(id)

    // AI Settings
    fun getAiSettings(): Flow<AiSettingsEntity?> = aiSettingsDao.getSettings()
    suspend fun getAiSettingsDirect(): AiSettingsEntity = aiSettingsDao.getSettingsDirect() ?: AiSettingsEntity()
    suspend fun saveAiSettings(settings: AiSettingsEntity) = aiSettingsDao.insertOrUpdate(settings)
}
