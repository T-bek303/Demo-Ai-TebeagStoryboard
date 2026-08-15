package com.example.data.local.daos

import androidx.room.*
import com.example.data.local.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY updatedAt DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :id")
    fun getProjectById(id: String): Flow<ProjectEntity?>

    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun getProjectByIdDirect(id: String): ProjectEntity?

    @Query("SELECT COUNT(*) FROM projects")
    fun getProjectCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(project: ProjectEntity)

    @Delete
    suspend fun delete(project: ProjectEntity)

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun deleteById(id: String)
}

@Dao
interface TitleIdeaDao {
    @Query("SELECT * FROM title_ideas WHERE projectId = :projectId ORDER BY titleNumber ASC")
    fun getTitlesForProject(projectId: String): Flow<List<TitleIdeaEntity>>

    @Query("SELECT * FROM title_ideas WHERE projectId = :projectId ORDER BY titleNumber ASC")
    suspend fun getTitlesForProjectDirect(projectId: String): List<TitleIdeaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(titles: List<TitleIdeaEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(title: TitleIdeaEntity)

    @Update
    suspend fun update(title: TitleIdeaEntity)

    @Query("UPDATE title_ideas SET isSelected = 0 WHERE projectId = :projectId")
    suspend fun clearSelection(projectId: String)

    @Query("UPDATE title_ideas SET isSelected = 1 WHERE id = :id")
    suspend fun setSelected(id: Long)

    @Query("DELETE FROM title_ideas WHERE projectId = :projectId")
    suspend fun deleteForProject(projectId: String)
}

@Dao
interface SceneDao {
    @Query("SELECT * FROM scenes WHERE projectId = :projectId ORDER BY frameOrder ASC")
    fun getScenesForProject(projectId: String): Flow<List<SceneEntity>>

    @Query("SELECT * FROM scenes WHERE projectId = :projectId ORDER BY frameOrder ASC")
    suspend fun getScenesForProjectDirect(projectId: String): List<SceneEntity>

    @Query("SELECT COUNT(*) FROM scenes")
    fun getTotalSceneCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(scenes: List<SceneEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(scene: SceneEntity): Long

    @Update
    suspend fun update(scene: SceneEntity)

    @Delete
    suspend fun delete(scene: SceneEntity)

    @Query("DELETE FROM scenes WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM scenes WHERE projectId = :projectId")
    suspend fun deleteForProject(projectId: String)
}

@Dao
interface CharacterDao {
    @Query("SELECT * FROM characters WHERE projectId = :projectId ORDER BY characterCode ASC")
    fun getCharactersForProject(projectId: String): Flow<List<CharacterEntity>>

    @Query("SELECT * FROM characters WHERE projectId = :projectId ORDER BY characterCode ASC")
    suspend fun getCharactersForProjectDirect(projectId: String): List<CharacterEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(characters: List<CharacterEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(character: CharacterEntity): Long

    @Update
    suspend fun update(character: CharacterEntity)

    @Delete
    suspend fun delete(character: CharacterEntity)

    @Query("DELETE FROM characters WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM characters WHERE projectId = :projectId")
    suspend fun deleteForProject(projectId: String)
}

@Dao
interface CustomNicheDao {
    @Query("SELECT * FROM custom_niches ORDER BY createdAt DESC")
    fun getAllCustomNiches(): Flow<List<CustomNicheEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(niche: CustomNicheEntity): Long

    @Update
    suspend fun update(niche: CustomNicheEntity)

    @Query("DELETE FROM custom_niches WHERE id = :id")
    suspend fun deleteById(id: Long)
}

@Dao
interface CustomStyleDao {
    @Query("SELECT * FROM custom_styles ORDER BY createdAt DESC")
    fun getAllCustomStyles(): Flow<List<CustomStyleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(style: CustomStyleEntity): Long

    @Update
    suspend fun update(style: CustomStyleEntity)

    @Query("DELETE FROM custom_styles WHERE id = :id")
    suspend fun deleteById(id: Long)
}

@Dao
interface TemplateDao {
    @Query("SELECT * FROM templates ORDER BY createdAt ASC")
    fun getAllTemplates(): Flow<List<TemplateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(template: TemplateEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(templates: List<TemplateEntity>)

    @Query("DELETE FROM templates WHERE id = :id")
    suspend fun deleteById(id: String)
}

@Dao
interface AiSettingsDao {
    @Query("SELECT * FROM ai_settings WHERE id = 1")
    fun getSettings(): Flow<AiSettingsEntity?>

    @Query("SELECT * FROM ai_settings WHERE id = 1")
    suspend fun getSettingsDirect(): AiSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(settings: AiSettingsEntity)
}
