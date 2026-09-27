package de.fitapp.app.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import de.fitapp.app.data.entity.*
import de.fitapp.app.ui.AppViewModel

private val stepTitles = listOf(
    "Willkommen", "Ziel", "Körperdaten", "Aktivität", "Ernährungsziele", "Training"
)

@Composable
fun OnboardingScreen(viewModel: AppViewModel) {
    var step by remember { mutableIntStateOf(0) }

    var goal by remember { mutableStateOf(Goal.STAY) }
    var age by remember { mutableStateOf("30") }
    var heightCm by remember { mutableStateOf("175") }
    var weightKg by remember { mutableStateOf("75") }
    var targetWeightKg by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf(Gender.DIVERS) }
    var activity by remember { mutableStateOf(ActivityLevel.MODERATE) }
    var experience by remember { mutableStateOf(TrainingExperience.BEGINNER) }

    var calorieGoal by remember { mutableStateOf(2200) }
    var proteinGoal by remember { mutableStateOf(120) }
    var carbsGoal by remember { mutableStateOf(250) }
    var fatGoal by remember { mutableStateOf(70) }

    fun applyEstimateIfPossible() {
        val a = age.toIntOrNull() ?: return
        val h = heightCm.toIntOrNull() ?: return
        val w = weightKg.toFloatOrNull() ?: return
        val estimate = viewModel.estimateCalories(a, h, w, gender, activity, goal)
        calorieGoal = estimate.goalKcal
        proteinGoal = estimate.proteinG
        carbsGoal = estimate.carbsG
        fatGoal = estimate.fatG
    }

    fun finish(skippedRest: Boolean = false) {
        val profile = UserProfile(
            goal = goal,
            age = age.toIntOrNull() ?: 30,
            heightCm = heightCm.toIntOrNull() ?: 175,
            weightKg = weightKg.toFloatOrNull() ?: 75f,
            targetWeightKg = targetWeightKg.toFloatOrNull(),
            gender = gender,
            activityLevel = activity,
            trainingExperience = experience,
            dailyCalorieGoal = calorieGoal,
            dailyProteinGoalG = proteinGoal,
            dailyCarbsGoalG = carbsGoal,
            dailyFatGoalG = fatGoal,
            onboardingCompleted = true
        )
        viewModel.saveProfile(profile)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Einrichtung – ${stepTitles[step]}") }) },
        bottomBar = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = { finish(skippedRest = true) }) {
                    Text(if (step == stepTitles.lastIndex) "Fertig" else "Überspringen")
                }
                Button(onClick = {
                    if (step == 1) applyEstimateIfPossible()
                    if (step < stepTitles.lastIndex) step++ else finish()
                }) {
                    Text(if (step == stepTitles.lastIndex) "Los geht's" else "Weiter")
                }
            }
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (step) {
                0 -> {
                    Text(
                        "Willkommen bei FitApp!",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        "In wenigen Schritten richten wir deine App ein. Wir fragen nach deinem " +
                            "Ziel, ein paar Körperdaten und deinem Trainingsstand. Damit können wir " +
                            "passende Kalorien- und Trainingsvorschläge berechnen. Du kannst jeden " +
                            "Schritt überspringen und später im Profil ändern."
                    )
                }
                1 -> {
                    Text("Was ist dein Hauptziel?", style = MaterialTheme.typography.titleMedium)
                    GoalOption("Abnehmen", goal == Goal.LOSE_WEIGHT) { goal = Goal.LOSE_WEIGHT }
                    GoalOption("Gewicht halten", goal == Goal.STAY) { goal = Goal.STAY }
                    GoalOption("Muskeln aufbauen", goal == Goal.GAIN_MUSCLE) { goal = Goal.GAIN_MUSCLE }
                    GoalOption("Allgemein fitter werden", goal == Goal.GENERAL_FITNESS) { goal = Goal.GENERAL_FITNESS }
                }
                2 -> {
                    Text("Deine Körperdaten", style = MaterialTheme.typography.titleMedium)
                    Text("Diese Angaben werden nur für die Berechnung deines Kalorienbedarfs verwendet.")
                    OutlinedTextField(age, { age = it }, label = { Text("Alter (Jahre)") }, keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(heightCm, { heightCm = it }, label = { Text("Größe (cm)") }, keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(weightKg, { weightKg = it }, label = { Text("Aktuelles Gewicht (kg)") }, keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal))
                    if (goal == Goal.LOSE_WEIGHT || goal == Goal.GAIN_MUSCLE) {
                        OutlinedTextField(targetWeightKg, { targetWeightKg = it }, label = { Text("Zielgewicht (kg, optional)") }, keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal))
                    }
                    Text("Geschlecht (für die Berechnung des Grundumsatzes):")
                    GoalOption("Männlich", gender == Gender.MALE) { gender = Gender.MALE }
                    GoalOption("Weiblich", gender == Gender.FEMALE) { gender = Gender.FEMALE }
                    GoalOption("Divers", gender == Gender.DIVERS) { gender = Gender.DIVERS }
                }
                3 -> {
                    Text("Wie aktiv bist du im Alltag?", style = MaterialTheme.typography.titleMedium)
                    GoalOption("Überwiegend sitzend", activity == ActivityLevel.SEDENTARY) { activity = ActivityLevel.SEDENTARY }
                    GoalOption("Leicht aktiv (wenig Bewegung)", activity == ActivityLevel.LIGHT) { activity = ActivityLevel.LIGHT }
                    GoalOption("Moderat aktiv", activity == ActivityLevel.MODERATE) { activity = ActivityLevel.MODERATE }
                    GoalOption("Sehr aktiv", activity == ActivityLevel.ACTIVE) { activity = ActivityLevel.ACTIVE }
                    GoalOption("Extrem aktiv (körperliche Arbeit/Leistungssport)", activity == ActivityLevel.VERY_ACTIVE) { activity = ActivityLevel.VERY_ACTIVE }
                }
                4 -> {
                    LaunchedEffect(Unit) { applyEstimateIfPossible() }
                    Text("Deine Tagesziele", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Basierend auf deinen Angaben schlagen wir folgende Werte vor – ein " +
                            "Näherungswert, den du jederzeit anpassen kannst."
                    )
                    NumberField("Kalorien (kcal)", calorieGoal) { calorieGoal = it }
                    NumberField("Eiweiß (g)", proteinGoal) { proteinGoal = it }
                    NumberField("Kohlenhydrate (g)", carbsGoal) { carbsGoal = it }
                    NumberField("Fett (g)", fatGoal) { fatGoal = it }
                }
                5 -> {
                    Text("Trainingserfahrung", style = MaterialTheme.typography.titleMedium)
                    GoalOption("Anfänger:in", experience == TrainingExperience.BEGINNER) { experience = TrainingExperience.BEGINNER }
                    GoalOption("Fortgeschritten", experience == TrainingExperience.INTERMEDIATE) { experience = TrainingExperience.INTERMEDIATE }
                    GoalOption("Erfahren", experience == TrainingExperience.ADVANCED) { experience = TrainingExperience.ADVANCED }
                    Text(
                        "Deine aktuellen Gewichte/Wiederholungen bei Grundübungen kannst du direkt " +
                            "im Trainingsbereich eintragen, sobald die Einrichtung abgeschlossen ist."
                    )
                }
            }
        }
    }
}

@Composable
private fun GoalOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Text(label, modifier = Modifier.padding(start = 8.dp))
    }
}

@Composable
private fun NumberField(label: String, value: Int, onChange: (Int) -> Unit) {
    OutlinedTextField(
        value = value.toString(),
        onValueChange = { it.toIntOrNull()?.let(onChange) },
        label = { Text(label) },
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth()
    )
}
