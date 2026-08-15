package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val id: String, // e.g. PROJECT-2026-0001
    val name: String,
    val topic: String,
    val country: String,
    val targetAudience: String,
    val language: String,
    val niche: String,
    val subNiche: String,
    val contentStyle: String,
    val customStyleInstruction: String = "",
    val durationMinutes: Int,
    val wpm: Int = 150,
    val sceneCountSetting: String = "Auto",
    val animationStyle: String,
    val customAnimationStyle: String = "",
    val finalTitle: String = "",
    val scriptText: String = "",
    val scriptAnalysis: String = "",
    val youtubeDescription: String = "",
    val hashtags: String = "",
    val thumbnailPrompt: String = "",
    val thumbnailText: String = "",
    val status: String = "DRAFT", // DRAFT, GENERATING, READY, COMPLETED
    val currentStep: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "title_ideas")
data class TitleIdeaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: String,
    val titleNumber: Int,
    val title: String,
    val category: String, // Curiosity, Shock, Emotional, etc.
    val ctrScore: Int,
    val curiosityScore: Int,
    val seoScore: Int,
    val viralScore: Int,
    val emotionalScore: Int,
    val evergreenScore: Int,
    val isSelected: Boolean = false
)

@Entity(tableName = "scenes")
data class SceneEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: String,
    val sceneNumber: Int,
    val timeRange: String, // e.g. "00:00 - 00:12"
    val title: String,
    val narration: String,
    val visualDescription: String,
    val cameraAngle: String,
    val cameraMovement: String,
    val characterId: String = "", // e.g. "CHAR-001"
    val location: String,
    val lighting: String,
    val mood: String,
    val transition: String,
    val imagePrompt: String,
    val videoPrompt: String,
    val frameOrder: Int
)

@Entity(tableName = "characters")
data class CharacterEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: String,
    val characterCode: String, // e.g. "CHAR-001"
    val name: String,
    val age: Int,
    val gender: String,
    val appearance: String,
    val clothing: String,
    val clothingColor: String,
    val bodyShape: String,
    val personality: String,
    val visualStyle: String,
    val referenceImageUri: String = ""
)

@Entity(tableName = "custom_niches")
data class CustomNicheEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "custom_styles")
data class CustomStyleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val promptInstruction: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "templates")
data class TemplateEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val niche: String,
    val subNiche: String,
    val style: String,
    val durationMinutes: Int,
    val sceneCount: String,
    val animationStyle: String,
    val targetAudience: String,
    val country: String,
    val language: String,
    val isCustom: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "ai_settings")
data class AiSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val provider: String = "Gemini API",
    val customApiKey: String = "",
    val model: String = "gemini-3.5-flash",
    val temperature: Float = 0.7f,
    val maxTokens: Int = 8192,
    val language: String = "English",
    val systemInstruction: String = "You are AI TebeagStoryboard, an elite cinematic YouTube director, scriptwriter, and visual storyboard engine."
)
