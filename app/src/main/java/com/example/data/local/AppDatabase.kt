package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.daos.*
import com.example.data.local.entities.*
import com.example.data.model.Presets
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ProjectEntity::class,
        TitleIdeaEntity::class,
        SceneEntity::class,
        CharacterEntity::class,
        CustomNicheEntity::class,
        CustomStyleEntity::class,
        TemplateEntity::class,
        AiSettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun titleIdeaDao(): TitleIdeaDao
    abstract fun sceneDao(): SceneDao
    abstract fun characterDao(): CharacterDao
    abstract fun customNicheDao(): CustomNicheDao
    abstract fun customStyleDao(): CustomStyleDao
    abstract fun templateDao(): TemplateDao
    abstract fun aiSettingsDao(): AiSettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tebeag_storyboard_db"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            val database = INSTANCE ?: getDatabase(context)
                            // Populate default templates
                            val defaultTemplates = Presets.TEMPLATES.map { tpl ->
                                TemplateEntity(
                                    id = tpl.id,
                                    name = tpl.name,
                                    description = tpl.description,
                                    niche = tpl.niche,
                                    subNiche = tpl.subNiche,
                                    style = tpl.style,
                                    durationMinutes = tpl.durationMinutes,
                                    sceneCount = tpl.sceneCount,
                                    animationStyle = tpl.animationStyle,
                                    targetAudience = tpl.targetAudience,
                                    country = tpl.country,
                                    language = tpl.language,
                                    isCustom = false
                                )
                            }
                            database.templateDao().insertAll(defaultTemplates)

                            // Populate initial settings
                            database.aiSettingsDao().insertOrUpdate(AiSettingsEntity())

                            // Populate a starter showcase project
                            val sampleProjectId = "PROJECT-2026-0001"
                            val sampleProject = ProjectEntity(
                                id = sampleProjectId,
                                name = "The Secret Mechanics of Quantitative Easing",
                                topic = "How central banks create money and the hidden impact on the middle class",
                                country = "United States",
                                targetAudience = "Finance enthusiasts, investors, and middle class savers",
                                language = "English",
                                niche = "Finance",
                                subNiche = "Macroeconomics & Banking",
                                contentStyle = "Cinematic Documentary",
                                durationMinutes = 15,
                                wpm = 150,
                                sceneCountSetting = "20 Scene",
                                animationStyle = "Vox Style Artifact",
                                finalTitle = "Why Most Americans Will Never Escape the Hidden Inflation Trap",
                                scriptText = """[HOOK]
In 1971, the definition of money quietly changed forever. But nobody warned you what it would cost your life savings.

[OPENING]
Right now, in a secure boardroom on Constitution Avenue, decisions are made that dictate the value of every single hour you work. Most people think prices rise because stores get greedy. The reality is far more calculated.

[CONTEXT]
For hundreds of years, money had physical anchor lines. A dollar was backed by gold. You couldn't just conjure ten trillion dollars out of thin air without mining the metal.

[PROBLEM]
When that peg was severed, central banks unlocked the ultimate financial cheat code: Quantitative Easing. An innocuous bureaucratic term for creating digital liquidity at lightning scale.

[STORY DEVELOPMENT]
Meet Marcus, a 38-year-old software analyst who saved diligently for a decade. Despite doubling his wage, he found houses drifting twice as fast out of his reach. Marcus wasn't failing; the measuring tape was shrinking.

[MAIN EXPLANATION]
When new currency enters the financial ecosystem through asset purchases, it doesn't distribute evenly. It flows directly into real estate, equities, and institutional balance sheets first—the Cantillon Effect in full motion.

[EVIDENCE / EXAMPLES]
From 2008 to 2024, the M2 money supply surged by unprecedented multiples. The S&P 500 followed in near lockstep, while median real purchasing power remained stagnant.

[TURNING POINT]
Those who owned hard assets saw their net worths explode without producing additional goods. Those relying on wage income quietly suffered a 40% loss in purchasing power over a single decade.

[KEY REVELATION]
Inflation is not an unpredictable natural storm. It is a mathematical wealth transfer from cash holders to debt-leveraged asset owners.

[CONCLUSION]
Understanding this single economic principle separates those who run on the endless treadmill from those who build generational security.

[CALL TO ACTION]
If you want to understand how the new monetary system works before the next cycle, subscribe and inspect our full deep dive breakdown in the description below.""",
                                scriptAnalysis = "Hook Strength: 94/100 (Exceptional curiosity loop). Pacing: High tension with data evidence. Estimated Reading Time: 14m 45s. Retention Score: 92/100.",
                                youtubeDescription = "Why is the middle class quietly losing purchasing power despite working harder? In this cinematic documentary breakdown, we uncover the hidden mechanics of Quantitative Easing, the Cantillon Effect, and what happened after the gold peg was broken.\n\nTIMESTAMPS:\n00:00 - The Silent Monetary Shift\n02:15 - The Boardroom on Constitution Ave\n05:40 - The Cantillon Effect Explained\n09:30 - Asset Inflation vs Wage Reality\n13:10 - How to Protect Generational Wealth\n\n📌 Subscribe for deep-dive economic & financial documentaries.",
                                hashtags = "#Economics #FinanceDocumentary #Inflation #QuantitativeEasing #WealthBuilding #CantillonEffect #MoneySupply #FederalReserve #Investing #PersonalFinance #FinancialFreedom #Macroeconomics #CentralBanks #MiddleClassEconomy #StockMarket #AssetInflation #FinancialLiteracy #EconomicHistory #WealthTransfer #MoneySecrets",
                                thumbnailPrompt = "Close-up dramatic portrait of a stressed middle-aged worker Marcus looking at a holographic shrinking dollar bill dissolving into golden dust, ultra-cinematic 8k lighting, dark blue obsidian background with glowing golden market charts, high CTR contrast, photorealistic, 16:9",
                                thumbnailText = "THE $1M WEALTH TRAP",
                                status = "COMPLETED",
                                currentStep = 12
                            )
                            database.projectDao().insertOrUpdate(sampleProject)

                            // Populate sample titles
                            val sampleTitles = listOf(
                                TitleIdeaEntity(0, sampleProjectId, 1, "Why Most Americans Will Never Escape the Hidden Inflation Trap", "Curiosity", 94, 96, 91, 95, 88, 92, true),
                                TitleIdeaEntity(0, sampleProjectId, 2, "The $10 Trillion Heist Nobody Talked About", "Shock", 92, 94, 85, 93, 90, 86, false),
                                TitleIdeaEntity(0, sampleProjectId, 3, "How Central Banks Quietly Cut Your Salary in Half", "Emotional", 90, 92, 89, 91, 94, 89, false),
                                TitleIdeaEntity(0, sampleProjectId, 4, "The Cantillon Effect: Why the Rich Get Richer Automatically", "Educational", 88, 89, 95, 87, 82, 96, false),
                                TitleIdeaEntity(0, sampleProjectId, 5, "What Really Happened When the Gold Standard Ended in 1971", "Documentary", 87, 88, 93, 86, 80, 97, false),
                                TitleIdeaEntity(0, sampleProjectId, 6, "The Mathematical Illusion of Modern Wealth", "Curiosity", 89, 91, 86, 89, 85, 90, false)
                            )
                            database.titleIdeaDao().insertAll(sampleTitles)

                            // Populate sample character
                            val sampleCharacter = CharacterEntity(
                                id = 0,
                                projectId = sampleProjectId,
                                characterCode = "CHAR-001",
                                name = "Marcus Vance",
                                age = 38,
                                gender = "Male",
                                appearance = "Sharp analytical gaze, short dark brown hair, slight stubble, observant expression",
                                clothing = "Tailored navy blue turtleneck, charcoal slim blazer",
                                clothingColor = "Navy and Charcoal",
                                bodyShape = "Athletic build, 180cm",
                                personality = "Disciplined, cautious, data-driven researcher seeking economic truth",
                                visualStyle = "Cinematic realistic documentary lighting with subtle rim lights"
                            )
                            database.characterDao().insert(sampleCharacter)

                            // Populate sample scenes
                            val sampleScenes = listOf(
                                SceneEntity(
                                    id = 0,
                                    projectId = sampleProjectId,
                                    sceneNumber = 1,
                                    timeRange = "00:00 - 00:45",
                                    title = "The 1971 Turning Point",
                                    narration = "In 1971, the definition of money quietly changed forever. But nobody warned you what it would cost your life savings.",
                                    visualDescription = "Macro shot of a vintage 1970s gold sovereign coin dissolving into microscopic glowing blue binary code particles on an obsidian table.",
                                    cameraAngle = "Extreme Close-Up (ECU)",
                                    cameraMovement = "Slow Push-In",
                                    characterId = "",
                                    location = "Federal Reserve Vault Room, dim moody lighting",
                                    lighting = "Chiaroscuro golden rim light contrasting with cool cyan shadows",
                                    mood = "Mysterious & Grave",
                                    transition = "Cross Dissolve to dark boardroom",
                                    imagePrompt = "Cinematic 8k extreme macro photograph of a vintage 1970s US dollar bill burning into golden mathematical glowing dust, dark obsidian studio background, high contrast, anamorphic lens 85mm f/1.4, volumetric smoke, photorealistic, 16:9",
                                    videoPrompt = "Slow push in on a burning vintage banknote on a dark granite reflective table, embers drifting upward, camera gliding at 24fps with cinematic depth of field, dramatic moody lighting",
                                    frameOrder = 1
                                ),
                                SceneEntity(
                                    id = 0,
                                    projectId = sampleProjectId,
                                    sceneNumber = 2,
                                    timeRange = "00:45 - 02:15",
                                    title = "The Constitution Ave Boardroom",
                                    narration = "Right now, in a secure boardroom on Constitution Avenue, decisions are made that dictate the value of every single hour you work.",
                                    visualDescription = "Silhouetted financial council seated around an expansive mahogany table overlooking Washington DC rain-streaked night windows.",
                                    cameraAngle = "Wide Angle Shot",
                                    cameraMovement = "Slow Dolly Left",
                                    characterId = "",
                                    location = "High-rise executive boardroom at dusk",
                                    lighting = "Cool blue ambient dusk with warm desk lamp spotlights",
                                    mood = "Authoritative & Secretive",
                                    transition = "Cut to Marcus working at workstation",
                                    imagePrompt = "Cinematic wide shot of an executive financial boardroom at night, silhouetted leaders around a polished dark wood table with glowing holographic stock charts, rain streaked glass windows overlooking cityscape, 35mm film grain, 16:9",
                                    videoPrompt = "Slow lateral tracking dolly shot moving past silhouetted figures around a boardroom table, city lights flickering through rain on glass in background, 4k cinematic grading",
                                    frameOrder = 2
                                ),
                                SceneEntity(
                                    id = 0,
                                    projectId = sampleProjectId,
                                    sceneNumber = 3,
                                    timeRange = "02:15 - 04:30",
                                    title = "Marcus and the Shrinking Tape",
                                    narration = "Meet Marcus, a 38-year-old software analyst who saved diligently for a decade. Despite doubling his wage, he found houses drifting twice as fast out of his reach.",
                                    visualDescription = "Marcus (CHAR-001) standing in front of a glass analytics board examining escalating housing price graphs compared to salary bars.",
                                    cameraAngle = "Medium Close-Up",
                                    cameraMovement = "Subtle Handheld Drift",
                                    characterId = "CHAR-001",
                                    location = "Modern minimalist home office with night city view",
                                    lighting = "Soft screen glow highlighting Marcus's face with deep indigo shadows",
                                    mood = "Contemplative & Tense",
                                    transition = "Whip pan to Cantillon Effect graphics",
                                    imagePrompt = "Marcus Vance (CHAR-001), 38-year-old analytical man in navy turtleneck and charcoal blazer, looking stressed at glowing holographic financial charts in modern dimly lit room, photorealistic, sharp focus, 50mm f/1.8, 16:9",
                                    videoPrompt = "Medium shot of Marcus looking thoughtfully up at floating data visual graphs, subtle camera breath motion, light reflections dancing in his eyes, cinematic 24fps",
                                    frameOrder = 3
                                ),
                                SceneEntity(
                                    id = 0,
                                    projectId = sampleProjectId,
                                    sceneNumber = 4,
                                    timeRange = "04:30 - 07:00",
                                    title = "The Cantillon Funnel",
                                    narration = "When new currency enters the financial ecosystem through asset purchases, it flows directly into real estate and equities first.",
                                    visualDescription = "Dynamic 3D kinetic animation of a golden digital waterfall cascading down into skyscrapers while leaving suburban houses below in arid drought.",
                                    cameraAngle = "High Angle Isometric",
                                    cameraMovement = "Crane Downward Sweep",
                                    characterId = "",
                                    location = "Abstract 3D architectural city simulation",
                                    lighting = "Neon cyber cyan and amber golden beams illuminating glass skyscrapers",
                                    mood = "Explanatory & Shocking",
                                    transition = "Fade to Master Summary",
                                    imagePrompt = "Vox style 3D kinetic diagram of financial flow, golden glowing liquid money pouring from central bank emblem into towering luxury real estate towers, leaving baseline landscape dry, stylized graphic, 16:9",
                                    videoPrompt = "Isometric camera cranes downwards along a glowing skyscraper as liquid gold data streams cascade down corporate spires, smooth 60fps kinetic motion graphic",
                                    frameOrder = 4
                                )
                            )
                            database.sceneDao().insertAll(sampleScenes)
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
