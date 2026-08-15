package com.example.data.model

data class NichePreset(
    val id: String,
    val name: String,
    val description: String,
    val iconName: String = "category"
)

data class StylePreset(
    val id: String,
    val name: String,
    val description: String,
    val promptInstruction: String
)

data class AnimationStylePreset(
    val id: String,
    val name: String,
    val description: String
)

data class TemplatePreset(
    val id: String,
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
    val language: String
)

enum class GenerationStep(val stepNumber: Int, val title: String, val description: String) {
    ANALYZING_TOPIC(1, "Analyzing Topic", "Parsing niche, audience, and intent..."),
    GENERATING_TITLES(2, "Generating Titles", "Crafting 20 high-CTR viral title options..."),
    CREATING_SCRIPT(3, "Creating Script", "Writing 11-part retention-optimized script..."),
    ANALYZING_SCRIPT(4, "Analyzing Script", "Evaluating hooks, pacing, and retention curve..."),
    CREATING_SCENES(5, "Creating Scenes", "Segmenting narration into cinematic visual scenes..."),
    CREATING_CHARACTERS(6, "Creating Characters", "Building consistent character visual profiles..."),
    CREATING_STORYBOARD(7, "Creating Storyboard", "Generating frame compositions & camera language..."),
    GENERATING_IMAGE_PROMPTS(8, "Generating Image Prompts", "Building detailed 16:9 photorealistic prompts..."),
    GENERATING_VIDEO_PROMPTS(9, "Generating Video Prompts", "Formulating motion & camera dynamic directives..."),
    GENERATING_YOUTUBE_SEO(10, "Generating YouTube SEO", "Writing SEO description and 20 targeted hashtags..."),
    CREATING_THUMBNAIL_PROMPT(11, "Creating Thumbnail Prompt", "Composing high-CTR visual prompt & overlay text..."),
    FINALIZING_PROJECT(12, "Finalizing Project", "Syncing database & preparing master studio export...")
}

object Presets {
    val DEFAULT_NICHES = listOf(
        NichePreset("niche_finance", "Finance", "Personal finance, wealth accumulation, banking systems"),
        NichePreset("niche_business", "Business", "Startup case studies, corporate battles, market disruptions"),
        NichePreset("niche_investing", "Investing", "Stock market strategies, real estate, portfolio breakdown"),
        NichePreset("niche_economics", "Economics", "Global macro trends, inflation dynamics, world monetary systems"),
        NichePreset("niche_tech", "Technology", "Future tech revolutions, gadget teardowns, computing history"),
        NichePreset("niche_ai", "AI", "Artificial intelligence breakthroughs, LLMs, automation impact"),
        NichePreset("niche_history", "History", "Deep-dive historical turning points, untold empire secrets"),
        NichePreset("niche_mystery", "Mystery", "Unsolved crimes, bizarre phenomena, high-stakes investigations"),
        NichePreset("niche_documentary", "Documentary", "Cinematic long-form investigative journalism & exposés"),
        NichePreset("niche_self_dev", "Self Development", "Peak human performance, behavioral psychology, habits")
    )

    val DEFAULT_WRITING_STYLES = listOf(
        StylePreset(
            "style_cinematic_doc",
            "Cinematic Documentary",
            "Atmospheric, immersive, grave pacing with dramatic pauses and evocative language.",
            "Use a serious, suspenseful cinematic documentary tone with deep atmosphere, immersive pacing, and vivid metaphors."
        ),
        StylePreset(
            "style_professional",
            "Professional",
            "Polished, analytical, corporate clarity with data-driven structure.",
            "Maintain a crisp, highly professional, credible tone with logical deduction and authoritative phrasing."
        ),
        StylePreset(
            "style_storytelling",
            "Storytelling",
            "Character-driven narrative arc, suspenseful cliffhangers, and emotional resonance.",
            "Employ narrative storytelling techniques with character empathy, rising tension, and emotional stakes."
        ),
        StylePreset(
            "style_emotional",
            "Emotional",
            "Heartfelt, poignant, deeply personal reflections and philosophical weight.",
            "Evoke deep emotional connection, vulnerability, and contemplative philosophical insights."
        ),
        StylePreset(
            "style_educational",
            "Educational",
            "Crystal clear breakdowns, intuitive analogies, and step-by-step insight.",
            "Explain complex mechanisms with simple analogies, structured logic, and engaging clarity."
        ),
        StylePreset(
            "style_investigative",
            "Investigative",
            "Incisive exposé questioning, forensic evidence analysis, and investigative tension.",
            "Adopt an investigative reporter tone: forensic, questioning assumptions, revealing hidden truths."
        ),
        StylePreset(
            "style_dramatic",
            "Dramatic",
            "High stakes, intense confrontations, cliffhangers, and urgent tempo.",
            "Craft dramatic tension with punchy short sentences, rising stakes, and electrifying climaxes."
        ),
        StylePreset(
            "style_conversational",
            "Conversational",
            "Relatable, humorous, engaging, talking directly to a friend over coffee.",
            "Use an energetic conversational style, breaking the fourth wall, friendly and approachable."
        ),
        StylePreset(
            "style_viral",
            "Fast-Paced Viral",
            "Rapid curiosity loops, dynamic rhythm, zero fluff, maximum retention hooks.",
            "Keep the pace rapid, hook every 15 seconds, maintain intense curiosity loops with no wasted words."
        ),
        StylePreset(
            "style_authoritative",
            "Authoritative",
            "Commanding expert voice, definitive conclusions, and executive gravitas.",
            "Speak with commanding expertise, decisive arguments, and heavyweight credibility."
        )
    )

    val DEFAULT_ANIMATION_STYLES = listOf(
        AnimationStylePreset("anim_3d", "3D Animation", "Hyper-detailed 3D cinematic CGI renders with volumetric lighting and depth."),
        AnimationStylePreset("anim_vox", "Vox Style Artifact", "Clean kinetic typography, archival paper cutouts, data graphics, and modern collage."),
        AnimationStylePreset("anim_whiteboard", "Whiteboard Animation", "Hand-drawn line illustrations, clean black ink sketching on dynamic canvas."),
        AnimationStylePreset("anim_motion_graphics", "Motion Graphic", "Sleek geometric 2D vectors, futuristic UI HUDs, smooth kinetic transitions.")
    )

    val DURATION_PRESETS = listOf(10, 15, 20, 25, 30, 35, 40)
    val SCENE_COUNT_PRESETS = listOf("Auto", "10 Scene", "20 Scene", "30 Scene", "40 Scene", "50 Scene", "60 Scene")

    val TARGET_COUNTRIES = listOf(
        "United States", "Indonesia", "United Kingdom", "Canada", "Australia",
        "Germany", "India", "Japan", "Brazil", "Global / Worldwide"
    )

    val LANGUAGES = listOf(
        "Indonesian", "English", "Spanish", "German", "French", "Japanese", "Portuguese", "Hindi"
    )

    val TITLE_CATEGORIES = listOf(
        "All Categories", "Curiosity", "Shock", "Emotional", "Educational",
        "Search Based", "Story Based", "Controversial", "Documentary", "Evergreen", "Viral"
    )

    val TEMPLATES = listOf(
        TemplatePreset(
            id = "tpl_finance_doc",
            name = "Finance Documentary",
            description = "High-stakes monetary collapse, banking scandals, and wealth psychology.",
            niche = "Finance",
            subNiche = "Monetary History & Banking",
            style = "Cinematic Documentary",
            durationMinutes = 20,
            sceneCount = "20 Scene",
            animationStyle = "Vox Style Artifact",
            targetAudience = "Aspiring investors and curiosity seekers aged 22-45",
            country = "United States",
            language = "English"
        ),
        TemplatePreset(
            id = "tpl_business_war",
            name = "Business Documentary",
            description = "Rise and fall corporate war stories and disruptive tech founders.",
            niche = "Business",
            subNiche = "Corporate Strategy & Market Wars",
            style = "Investigative",
            durationMinutes = 15,
            sceneCount = "20 Scene",
            animationStyle = "Motion Graphic",
            targetAudience = "Entrepreneurs and business strategists",
            country = "United States",
            language = "English"
        ),
        TemplatePreset(
            id = "tpl_ai_revolution",
            name = "AI Documentary",
            description = "The philosophical and societal transformation powered by Artificial Intelligence.",
            niche = "AI",
            subNiche = "AGI & Future Automation",
            style = "Cinematic Documentary",
            durationMinutes = 25,
            sceneCount = "30 Scene",
            animationStyle = "3D Animation",
            targetAudience = "Tech enthusiasts and futurists",
            country = "Global / Worldwide",
            language = "English"
        ),
        TemplatePreset(
            id = "tpl_history_unsolved",
            name = "History Documentary",
            description = "Untold chronicles of ancient empires and decisive secret treaties.",
            niche = "History",
            subNiche = "Ancient Empires & Lost Civilizations",
            style = "Storytelling",
            durationMinutes = 30,
            sceneCount = "30 Scene",
            animationStyle = "3D Animation",
            targetAudience = "History buffs and documentary fans",
            country = "United States",
            language = "English"
        ),
        TemplatePreset(
            id = "tpl_mystery_case",
            name = "Mystery Documentary",
            description = "Cold cases, unsolved disappearances, and high-tension investigations.",
            niche = "Mystery",
            subNiche = "Unsolved Enigmas & Forensic Files",
            style = "Investigative",
            durationMinutes = 20,
            sceneCount = "20 Scene",
            animationStyle = "Cinematic Documentary",
            targetAudience = "True crime and mystery enthusiasts",
            country = "United States",
            language = "English"
        ),
        TemplatePreset(
            id = "tpl_explainer_viral",
            name = "Explainer",
            description = "Deep explanatory breakdown of complex concepts made intuitive.",
            niche = "Economics",
            subNiche = "Global Market Dynamics",
            style = "Educational",
            durationMinutes = 15,
            sceneCount = "20 Scene",
            animationStyle = "Vox Style Artifact",
            targetAudience = "General public seeking high-yield knowledge",
            country = "Indonesia",
            language = "Indonesian"
        ),
        TemplatePreset(
            id = "tpl_viral_story",
            name = "Viral Story",
            description = "Fast-paced psychological story with constant curiosity loops.",
            niche = "Self Development",
            subNiche = "Habits & Peak Performance",
            style = "Fast-Paced Viral",
            durationMinutes = 10,
            sceneCount = "20 Scene",
            animationStyle = "Motion Graphic",
            targetAudience = "Young adults seeking personal growth",
            country = "Indonesia",
            language = "Indonesian"
        ),
        TemplatePreset(
            id = "tpl_educational_tech",
            name = "Educational",
            description = "Step-by-step technological breakdown with clear visual metaphors.",
            niche = "Technology",
            subNiche = "Semiconductors & Quantum Computing",
            style = "Educational",
            durationMinutes = 15,
            sceneCount = "20 Scene",
            animationStyle = "Motion Graphic",
            targetAudience = "Engineering students and tech hobbyists",
            country = "Global / Worldwide",
            language = "English"
        ),
        TemplatePreset(
            id = "tpl_whiteboard_finance",
            name = "Whiteboard",
            description = "Simple hand-drawn visual sketches simplifying investing and compounding.",
            niche = "Investing",
            subNiche = "Index Funds & Compounding",
            style = "Conversational",
            durationMinutes = 10,
            sceneCount = "10 Scene",
            animationStyle = "Whiteboard Animation",
            targetAudience = "Beginner investors",
            country = "Indonesia",
            language = "Indonesian"
        ),
        TemplatePreset(
            id = "tpl_3d_doc_cinema",
            name = "3D Documentary",
            description = "High production 3D CGI visuals exploring grand scientific breakthroughs.",
            niche = "Technology",
            subNiche = "Space Exploration & Astrophysics",
            style = "Cinematic Documentary",
            durationMinutes = 35,
            sceneCount = "40 Scene",
            animationStyle = "3D Animation",
            targetAudience = "Science and astronomy enthusiasts",
            country = "Global / Worldwide",
            language = "English"
        )
    )
}
