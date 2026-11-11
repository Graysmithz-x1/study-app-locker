package com.example.ui.lock

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MainActivity
import com.example.StudyLockerApplication
import com.example.data.DefaultQuestions
import com.example.data.MotivationalQuotes
import com.example.data.QuestionEntity
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TertiaryAmber
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LockQuizActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val packageNameExtra = intent.getStringExtra(EXTRA_PACKAGE_NAME) ?: "com.example.app"
        val appNameExtra = intent.getStringExtra(EXTRA_APP_NAME) ?: "Aplikasi"

        setContent {
            MyApplicationTheme {
                LockQuizScreen(
                    targetPackageName = packageNameExtra,
                    targetAppName = appNameExtra,
                    onExitToHome = {
                        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                            addCategory(Intent.CATEGORY_HOME)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        startActivity(homeIntent)
                        finish()
                    },
                    onOpenTargetApp = {
                        val launchIntent = packageManager.getLaunchIntentForPackage(packageNameExtra)
                        if (launchIntent != null) {
                            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(launchIntent)
                        } else {
                            val mainIntent = Intent(this@LockQuizActivity, MainActivity::class.java)
                            startActivity(mainIntent)
                        }
                        finish()
                    }
                )
            }
        }
    }

    companion object {
        const val EXTRA_PACKAGE_NAME = "extra_target_package"
        const val EXTRA_APP_NAME = "extra_target_app_name"

        fun createIntent(context: Context, packageName: String, appName: String): Intent {
            return Intent(context, LockQuizActivity::class.java).apply {
                putExtra(EXTRA_PACKAGE_NAME, packageName)
                putExtra(EXTRA_APP_NAME, appName)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
        }
    }
}

enum class QuizFlowState {
    LOADING,
    IN_PROGRESS,
    FAILED,
    COMPLETED
}

@Composable
fun LockQuizScreen(
    targetPackageName: String,
    targetAppName: String,
    onExitToHome: () -> Unit,
    onOpenTargetApp: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as StudyLockerApplication
    val scope = rememberCoroutineScope()
    val settings by app.settingsRepository.settings.collectAsState()

    var flowState by remember { mutableStateOf(QuizFlowState.LOADING) }
    var questions by remember { mutableStateOf<List<QuestionEntity>>(emptyList()) }
    var currentIndex by remember { mutableIntStateOf(0) }
    var mistakeCount by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var isAnswerSubmitted by remember { mutableStateOf(false) }
    var earnedMinutes by remember { mutableIntStateOf(settings.baseDurationMinutes) }
    var randomMotivationalQuote by remember { mutableStateOf("") }

    val baseDuration = settings.baseDurationMinutes
    val penaltyPerMistake = settings.penaltyPerMistakeMinutes
    val maxMistakes = settings.maxMistakesAllowed

    // Function to initialize and shuffle questions
    fun loadQuestions() {
        flowState = QuizFlowState.LOADING
        currentIndex = 0
        mistakeCount = 0
        selectedOption = null
        isAnswerSubmitted = false

        scope.launch {
            val activeCategories = settings.activeCategories.toList()
            var fetched = app.database.questionDao().getQuestionsByCategories(activeCategories)
            if (fetched.isEmpty()) {
                fetched = DefaultQuestions.getList()
            }
            val neededCount = settings.questionsPerSession.coerceIn(5, 20)
            val selected = fetched.shuffled().take(neededCount)
            questions = if (selected.size < neededCount && fetched.isNotEmpty()) {
                // If not enough questions, repeat elements to satisfy count
                (0 until neededCount).map { fetched[it % fetched.size] }
            } else {
                selected
            }
            flowState = QuizFlowState.IN_PROGRESS
        }
    }

    LaunchedEffect(Unit) {
        loadQuestions()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    )
                )
        ) {
            when (flowState) {
                QuizFlowState.LOADING -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = PrimaryIndigo)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Menyiapkan Kuis Belajar...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                QuizFlowState.IN_PROGRESS -> {
                    if (questions.isNotEmpty() && currentIndex < questions.size) {
                        val currentQuestion = questions[currentIndex]

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Header: Locked App Badge
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                                ),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(PrimaryIndigo),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = "Lock",
                                            tint = Color.White,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Study App Locker",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryIndigo
                                        )
                                        Text(
                                            text = "Kuis untuk membuka: $targetAppName",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Lives / Mistake Counter & Realtime Duration Preview
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                elevation = CardDefaults.cardElevation(2.dp),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Toleransi Kesalahan:",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        // 5 Heart Icons
                                        Row {
                                            for (i in 1..maxMistakes) {
                                                val isUsed = i <= mistakeCount
                                                Icon(
                                                    imageVector = if (isUsed) Icons.Default.FavoriteBorder else Icons.Default.Favorite,
                                                    contentDescription = null,
                                                    tint = if (isUsed) Color.LightGray else ErrorRed,
                                                    modifier = Modifier
                                                        .size(20.dp)
                                                        .padding(horizontal = 1.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Dynamic duration calculation
                                    val currentEstimatedMinutes = maxOf(5, baseDuration - (mistakeCount * penaltyPerMistake))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Timer,
                                            contentDescription = null,
                                            tint = AccentCyan,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Durasi Saat Ini: $currentEstimatedMinutes Menit",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        if (mistakeCount > 0) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "(-${mistakeCount * penaltyPerMistake}m penalti)",
                                                fontSize = 12.sp,
                                                color = ErrorRed,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Question Progress Indicator
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Soal ${currentIndex + 1} dari ${questions.size}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = currentQuestion.category,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryIndigo,
                                    modifier = Modifier
                                        .background(PrimaryIndigo.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { (currentIndex + 1).toFloat() / questions.size },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = PrimaryIndigo,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Question Text Card
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                elevation = CardDefaults.cardElevation(3.dp),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = currentQuestion.questionText,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    lineHeight = 24.sp,
                                    modifier = Modifier.padding(18.dp),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Options A, B, C, D
                            val options = listOf(
                                "A" to currentQuestion.optionA,
                                "B" to currentQuestion.optionB,
                                "C" to currentQuestion.optionC,
                                "D" to currentQuestion.optionD
                            )

                            options.forEach { (key, optionText) ->
                                val isSelected = selectedOption == key
                                val isCorrectOption = key == currentQuestion.correctOption

                                val (bgColor, borderColor) = when {
                                    !isAnswerSubmitted -> {
                                        if (isSelected) Pair(
                                            PrimaryIndigo.copy(alpha = 0.12f),
                                            PrimaryIndigo
                                        ) else Pair(
                                            MaterialTheme.colorScheme.surface,
                                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                                        )
                                    }
                                    isCorrectOption -> Pair(
                                        SuccessGreen.copy(alpha = 0.15f),
                                        SuccessGreen
                                    )
                                    isSelected -> Pair(
                                        ErrorRed.copy(alpha = 0.15f),
                                        ErrorRed
                                    )
                                    else -> Pair(
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                                        Color.Transparent
                                    )
                                }

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 5.dp)
                                        .border(1.5.dp, borderColor, RoundedCornerShape(14.dp))
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable(enabled = !isAnswerSubmitted) {
                                            selectedOption = key
                                            isAnswerSubmitted = true

                                            if (key == currentQuestion.correctOption) {
                                                // Correct answer!
                                                scope.launch {
                                                    delay(900)
                                                    if (currentIndex + 1 < questions.size) {
                                                        currentIndex++
                                                        selectedOption = null
                                                        isAnswerSubmitted = false
                                                    } else {
                                                        // Finished all questions!
                                                        val finalDuration = maxOf(5, baseDuration - (mistakeCount * penaltyPerMistake))
                                                        earnedMinutes = finalDuration
                                                        randomMotivationalQuote = MotivationalQuotes.getRandomQuote()

                                                        // Save unlocked timestamp
                                                        val unlockExpiration = System.currentTimeMillis() + (finalDuration * 60 * 1000L)
                                                        app.database.lockedAppDao().setUnlockedUntil(targetPackageName, unlockExpiration)
                                                        app.settingsRepository.recordQuizCompletion(finalDuration)

                                                        flowState = QuizFlowState.COMPLETED
                                                    }
                                                }
                                            } else {
                                                // Incorrect answer!
                                                val updatedMistakes = mistakeCount + 1
                                                mistakeCount = updatedMistakes

                                                if (updatedMistakes > maxMistakes) {
                                                    scope.launch {
                                                        delay(1200)
                                                        flowState = QuizFlowState.FAILED
                                                    }
                                                }
                                            }
                                        }
                                        .testTag("option_$key"),
                                    colors = CardDefaults.cardColors(containerColor = bgColor)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (isAnswerSubmitted && isCorrectOption) SuccessGreen
                                                    else if (isAnswerSubmitted && isSelected) ErrorRed
                                                    else if (isSelected) PrimaryIndigo
                                                    else MaterialTheme.colorScheme.surfaceVariant
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = key,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected || (isAnswerSubmitted && isCorrectOption)) Color.White else MaterialTheme.colorScheme.onSurface
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Text(
                                            text = optionText,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.weight(1f)
                                        )

                                        if (isAnswerSubmitted) {
                                            if (isCorrectOption) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = "Benar",
                                                    tint = SuccessGreen
                                                )
                                            } else if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Salah",
                                                    tint = ErrorRed
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Explanation & Next Button when incorrect answer made but not yet reached 5 errors
                            AnimatedVisibility(visible = isAnswerSubmitted && selectedOption != currentQuestion.correctOption && mistakeCount <= maxMistakes) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 12.dp)
                                ) {
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = ErrorRed.copy(alpha = 0.08f)
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp)) {
                                            Text(
                                                text = "⚠️ Kurang Tepat! Kunci Jawaban: ${currentQuestion.correctOption}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = ErrorRed
                                            )
                                            if (currentQuestion.explanation.isNotBlank()) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = currentQuestion.explanation,
                                                    fontSize = 13.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Penalti: Durasi berkurang ${penaltyPerMistake} menit.",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = ErrorRed
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Button(
                                        onClick = {
                                            if (currentIndex + 1 < questions.size) {
                                                currentIndex++
                                                selectedOption = null
                                                isAnswerSubmitted = false
                                            } else {
                                                // Completed despite mistake
                                                val finalDuration = maxOf(5, baseDuration - (mistakeCount * penaltyPerMistake))
                                                earnedMinutes = finalDuration
                                                randomMotivationalQuote = MotivationalQuotes.getRandomQuote()

                                                val unlockExpiration = System.currentTimeMillis() + (finalDuration * 60 * 1000L)
                                                scope.launch {
                                                    app.database.lockedAppDao().setUnlockedUntil(targetPackageName, unlockExpiration)
                                                    app.settingsRepository.recordQuizCompletion(finalDuration)
                                                    flowState = QuizFlowState.COMPLETED
                                                }
                                            }
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(50.dp)
                                            .testTag("btn_next_question"),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)
                                    ) {
                                        Text("Lanjut ke Soal Berikutnya", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Exit button
                            OutlinedButton(
                                onClick = onExitToHome,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("btn_exit_to_home")
                            ) {
                                Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Batal & Kembali ke Layar Utama")
                            }
                        }
                    }
                }

                QuizFlowState.FAILED -> {
                    // Failed Screen (> 5 mistakes)
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(ErrorRed.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Gagal",
                                tint = ErrorRed,
                                modifier = Modifier.size(54.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "Kuis Gagal!",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = ErrorRed
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Kamu telah melakukan lebih dari 5 kesalahan ($mistakeCount salah).\nDurasi waktu hangus dan kamu harus mengulang kuis dari awal untuk membuka $targetAppName.",
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(28.dp))

                        Button(
                            onClick = { loadQuestions() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("btn_retry_quiz"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Ulangi Kuis Sekarang", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = onExitToHome,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("btn_failed_exit"),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.Home, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Kembali ke Beranda")
                        }
                    }
                }

                QuizFlowState.COMPLETED -> {
                    // ================= 4. LAYAR PENCAPAIAN & MOTIVASI (ACHIEVEMENT SCREEN) =================
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Celebratory Trophy Badge
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            TertiaryAmber,
                                            TertiaryAmber.copy(alpha = 0.3f)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "Piala Pencapaian",
                                tint = Color.White,
                                modifier = Modifier.size(64.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "🎉 HEBAT! KUIS TUNTAS!",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Kunci $targetAppName berhasil dibuka!",
                            fontSize = 15.sp,
                            color = SuccessGreen,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Score & Duration Summary Card
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(4.dp),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "DURASI PENGGUNAAN DIPEROLEH",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    Text(
                                        text = "$earnedMinutes",
                                        fontSize = 48.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = SuccessGreen
                                    )
                                    Text(
                                        text = " Menit",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Details breakdown
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                            RoundedCornerShape(12.dp)
                                        )
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Total Soal", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("${questions.size}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Kesalahan", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("$mistakeCount", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = if (mistakeCount > 0) ErrorRed else SuccessGreen)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Penalti", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("-${mistakeCount * penaltyPerMistake}m", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = if (mistakeCount > 0) ErrorRed else Color.Gray)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Epic Random Motivational Quote Card
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = PrimaryIndigo.copy(alpha = 0.08f)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "💡 Kutipan Motivasi Belajar:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryIndigo
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = randomMotivationalQuote.ifBlank { MotivationalQuotes.getRandomQuote() },
                                    fontSize = 14.sp,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                    lineHeight = 20.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Action Buttons
                        Button(
                            onClick = onOpenTargetApp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("btn_open_target_app"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Buka $targetAppName Sekarang", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = onExitToHome,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("btn_completed_home"),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.Home, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Kembali ke Beranda Study Lock")
                        }
                    }
                }
            }
        }
    }
}
