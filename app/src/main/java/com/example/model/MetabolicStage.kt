package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.StageAnabolicColor
import com.example.ui.theme.StageAutophagyColor
import com.example.ui.theme.StageCatabolicColor
import com.example.ui.theme.StageDeepRenewalColor
import com.example.ui.theme.StageFatBurnColor

/**
 * Physiological stages experienced during intermittent fasting.
 */
enum class MetabolicStage(
    val stageNumber: Int,
    val shortName: String,
    val title: String,
    val hoursRange: String,
    val startHour: Float,
    val endHour: Float,
    val summary: String,
    val cellularDetail: String,
    val insulinStatus: String,
    val autophagyStatus: String,
    val accentColor: Color
) {
    ANABOLIC(
        stageNumber = 1,
        shortName = "Anabolic",
        title = "Anabolic & Digestion",
        hoursRange = "0 – 4 Hours",
        startHour = 0f,
        endHour = 4f,
        summary = "Blood sugar rises as food digests; insulin is secreted to store nutrients.",
        cellularDetail = "mTOR is actively triggered by circulating amino acids and glucose. Cells absorb energy into glycogen and fat reserves. Cellular autophagy is suppressed.",
        insulinStatus = "Elevated & Storing Energy",
        autophagyStatus = "Inhibited (mTOR Active)",
        accentColor = StageAnabolicColor
    ),
    CATABOLIC(
        stageNumber = 2,
        shortName = "Catabolic",
        title = "Catabolic & Glycogen Depletion",
        hoursRange = "4 – 12 Hours",
        startHour = 4f,
        endHour = 12f,
        summary = "Blood sugar and insulin levels drop; liver glycogen begins depleting.",
        cellularDetail = "Digestive system completes absorption. Glucagon is released to liberate stored liver glycogen for brain and organ fuel. Fat oxidation begins to ramp up.",
        insulinStatus = "Steeply Falling to Baseline",
        autophagyStatus = "Dormant (Priming AMPK)",
        accentColor = StageCatabolicColor
    ),
    FAT_BURNING(
        stageNumber = 3,
        shortName = "Fat Burning",
        title = "Ketosis & Fat Oxidation",
        hoursRange = "12 – 16 Hours",
        startHour = 12f,
        endHour = 16f,
        summary = "Liver glycogen is nearly exhausted; body switches directly to fat for fuel.",
        cellularDetail = "Beta-hydroxybutyrate (BHB) ketones rise in blood. Fatty acids are oxidized for cellular energy. Hunger hormone (ghrelin) stabilizes. Early cellular stress responses initiate.",
        insulinStatus = "Baseline Low",
        autophagyStatus = "Initiating Early Stage",
        accentColor = StageFatBurnColor
    ),
    AUTOPHAGY(
        stageNumber = 4,
        shortName = "Autophagy",
        title = "Cellular Autophagy Peak",
        hoursRange = "16 – 24 Hours",
        startHour = 16f,
        endHour = 24f,
        summary = "Cells enter deep cleanup mode: damaged proteins and mitochondria are recycled.",
        cellularDetail = "AMPK reaches peak activation. Intracellular lysosomes consume dysfunctional proteins, misfolded organelles, and senescent waste, rejuvenating cellular function.",
        insulinStatus = "Baseline Minimal / High Sensitivity",
        autophagyStatus = "Active Peak Cleanup",
        accentColor = StageAutophagyColor
    ),
    DEEP_RENEWAL(
        stageNumber = 5,
        shortName = "Deep Renewal",
        title = "Stem Cell & Immune Reset",
        hoursRange = "24+ Hours",
        startHour = 24f,
        endHour = 72f,
        summary = "Prolonged fast: deep autophagy, systemic inflammation drops, stem cell regeneration.",
        cellularDetail = "White blood cell turnover accelerates and hematopoietic stem cells activate to renew immune components. Peak anti-inflammatory ketone signaling.",
        insulinStatus = "Minimum Sustained Levels",
        autophagyStatus = "Deep Systemic Autophagy",
        accentColor = StageDeepRenewalColor
    );

    companion object {
        fun fromElapsedMillis(elapsedMillis: Long): MetabolicStage {
            val hours = (elapsedMillis.coerceAtLeast(0L) / (1000.0 * 60.0 * 60.0)).toFloat()
            return when {
                hours < 4f -> ANABOLIC
                hours < 12f -> CATABOLIC
                hours < 16f -> FAT_BURNING
                hours < 24f -> AUTOPHAGY
                else -> DEEP_RENEWAL
            }
        }

        fun getStageProgress(elapsedMillis: Long, stage: MetabolicStage): Float {
            val hours = (elapsedMillis.coerceAtLeast(0L) / (1000.0 * 60.0 * 60.0)).toFloat()
            if (hours <= stage.startHour) return 0f
            if (hours >= stage.endHour) return 1f
            val span = stage.endHour - stage.startHour
            return ((hours - stage.startHour) / span).coerceIn(0f, 1f)
        }

        fun getMillisToNextStage(elapsedMillis: Long): Long? {
            val currentStage = fromElapsedMillis(elapsedMillis)
            if (currentStage == DEEP_RENEWAL) return null
            val nextStageStartMillis = (currentStage.endHour * 3600 * 1000).toLong()
            return (nextStageStartMillis - elapsedMillis).coerceAtLeast(0L)
        }
    }
}
