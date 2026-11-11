package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserSettings(
    val isServiceEnabled: Boolean = true,
    val activeCategories: Set<String> = setOf("MATEMATIKA", "IPA", "IPS", "CUSTOM"),
    val questionsPerSession: Int = 5,
    val baseDurationMinutes: Int = 30,
    val penaltyPerMistakeMinutes: Int = 5,
    val maxMistakesAllowed: Int = 5,
    val totalQuizzesCompleted: Int = 0,
    val totalTimeEarnedMinutes: Int = 0
)

class SettingsRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("study_locker_settings", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<UserSettings> = _settings.asStateFlow()

    private fun loadSettings(): UserSettings {
        val isServiceEnabled = prefs.getBoolean("is_service_enabled", true)
        val categories = prefs.getStringSet(
            "active_categories",
            setOf("MATEMATIKA", "IPA", "IPS", "CUSTOM")
        ) ?: setOf("MATEMATIKA", "IPA", "IPS", "CUSTOM")
        val questionsPerSession = prefs.getInt("questions_per_session", 5).coerceIn(5, 20)
        val baseDurationMinutes = prefs.getInt("base_duration_minutes", 30).coerceIn(5, 180)
        val penaltyPerMistakeMinutes = prefs.getInt("penalty_per_mistake_minutes", 5)
        val maxMistakesAllowed = prefs.getInt("max_mistakes_allowed", 5)
        val totalQuizzesCompleted = prefs.getInt("total_quizzes_completed", 0)
        val totalTimeEarnedMinutes = prefs.getInt("total_time_earned_minutes", 0)

        return UserSettings(
            isServiceEnabled = isServiceEnabled,
            activeCategories = categories,
            questionsPerSession = questionsPerSession,
            baseDurationMinutes = baseDurationMinutes,
            penaltyPerMistakeMinutes = penaltyPerMistakeMinutes,
            maxMistakesAllowed = maxMistakesAllowed,
            totalQuizzesCompleted = totalQuizzesCompleted,
            totalTimeEarnedMinutes = totalTimeEarnedMinutes
        )
    }

    fun setServiceEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("is_service_enabled", enabled).apply()
        _settings.value = _settings.value.copy(isServiceEnabled = enabled)
    }

    fun setActiveCategories(categories: Set<String>) {
        val safeSet = if (categories.isEmpty()) setOf("MATEMATIKA") else categories
        prefs.edit().putStringSet("active_categories", safeSet).apply()
        _settings.value = _settings.value.copy(activeCategories = safeSet)
    }

    fun toggleCategory(category: String) {
        val current = _settings.value.activeCategories.toMutableSet()
        if (current.contains(category)) {
            // Keep at least one category selected
            if (current.size > 1) {
                current.remove(category)
            }
        } else {
            current.add(category)
        }
        setActiveCategories(current)
    }

    fun setQuestionsPerSession(count: Int) {
        val safeCount = count.coerceIn(5, 20)
        prefs.edit().putInt("questions_per_session", safeCount).apply()
        _settings.value = _settings.value.copy(questionsPerSession = safeCount)
    }

    fun setBaseDurationMinutes(minutes: Int) {
        val safeMinutes = minutes.coerceIn(5, 180)
        prefs.edit().putInt("base_duration_minutes", safeMinutes).apply()
        _settings.value = _settings.value.copy(baseDurationMinutes = safeMinutes)
    }

    fun setPenaltyPerMistakeMinutes(minutes: Int) {
        val safeMinutes = minutes.coerceIn(1, 30)
        prefs.edit().putInt("penalty_per_mistake_minutes", safeMinutes).apply()
        _settings.value = _settings.value.copy(penaltyPerMistakeMinutes = safeMinutes)
    }

    fun recordQuizCompletion(earnedMinutes: Int) {
        val newCount = _settings.value.totalQuizzesCompleted + 1
        val newEarned = _settings.value.totalTimeEarnedMinutes + earnedMinutes
        prefs.edit()
            .putInt("total_quizzes_completed", newCount)
            .putInt("total_time_earned_minutes", newEarned)
            .apply()
        _settings.value = _settings.value.copy(
            totalQuizzesCompleted = newCount,
            totalTimeEarnedMinutes = newEarned
        )
    }
}
