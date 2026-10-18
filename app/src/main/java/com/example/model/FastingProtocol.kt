package com.example.model

/**
 * Fasting protocols with clinical and physiological context.
 */
enum class FastingProtocol(
    val id: String,
    val title: String,
    val ratio: String,
    val fastingHours: Float,
    val eatingHours: Float,
    val subtitle: String,
    val description: String,
    val insulinReduction: String,
    val autophagyLevel: String,
    val difficulty: String
) {
    CIRCADIAN_12_12(
        id = "12_12",
        title = "Circadian Fast",
        ratio = "12:12",
        fastingHours = 12f,
        eatingHours = 12f,
        subtitle = "Baseline Digestive Rest",
        description = "Aligns metabolic rhythms with daylight cycles. Minimizes late-night insulin spikes and rests the gastrointestinal tract.",
        insulinReduction = "Mild",
        autophagyLevel = "Dormant to Baseline",
        difficulty = "Beginner"
    ),
    LEANGAINS_16_8(
        id = "16_8",
        title = "Leangains",
        ratio = "16:8",
        fastingHours = 16f,
        eatingHours = 8f,
        subtitle = "Standard Fat Loss & Insulin Drop",
        description = "The gold standard protocol. Glycogen reserves deplete, insulin levels decline to baseline, and the body triggers mild ketosis and begins early cellular cleanup.",
        insulinReduction = "High",
        autophagyLevel = "Initiating (16h threshold)",
        difficulty = "Moderate"
    ),
    WARRIOR_18_6(
        id = "18_6",
        title = "Deep Burn",
        ratio = "18:6",
        fastingHours = 18f,
        eatingHours = 6f,
        subtitle = "Accelerated Ketosis & Active Autophagy",
        description = "Extends the fast 2 hours into the autophagy zone. Accelerates hepatic ketone production, enhances mental clarity, and boosts insulin sensitivity.",
        insulinReduction = "Very High",
        autophagyLevel = "Active (2+ hours in autophagy zone)",
        difficulty = "Intermediate"
    ),
    WARRIOR_20_4(
        id = "20_4",
        title = "Warrior Fast",
        ratio = "20:4",
        fastingHours = 20f,
        eatingHours = 4f,
        subtitle = "Deep Cellular Rejuvenation",
        description = "Four hours spent deep within the cellular recycling window. Dramatically decreases circulating insulin and promotes mitochondrial biogenesis.",
        insulinReduction = "Peak Daily",
        autophagyLevel = "Intense (4 hours in autophagy zone)",
        difficulty = "Advanced"
    ),
    OMAD_23_1(
        id = "23_1",
        title = "OMAD (One Meal A Day)",
        ratio = "23:1",
        fastingHours = 23f,
        eatingHours = 1f,
        subtitle = "Maximum Daily Autophagy",
        description = "Dedicated single-meal protocol tuned for maximum daily autophagy (7 hours past the 16h mark) and near-zero daily insulin excursion.",
        insulinReduction = "Maximal",
        autophagyLevel = "Peak Daily Autophagy (7h)",
        difficulty = "Expert"
    ),
    CUSTOM(
        id = "custom",
        title = "Custom Fast",
        ratio = "Custom",
        fastingHours = 16f,
        eatingHours = 8f,
        subtitle = "User Defined Window",
        description = "Customizable fasting duration tailored to personal schedules, prolonged multi-day fasts, or specific clinical guidance.",
        insulinReduction = "Variable",
        autophagyLevel = "Dependent on length",
        difficulty = "Custom"
    );

    companion object {
        fun fromId(id: String): FastingProtocol {
            return entries.firstOrNull { it.id == id } ?: LEANGAINS_16_8
        }
    }
}
