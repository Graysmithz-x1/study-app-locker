package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.StudyLockerApplication
import com.example.data.QuestionEntity
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SecondaryViolet
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TertiaryAmber
import kotlinx.coroutines.launch

@Composable
fun QuestionBankScreen() {
    val context = LocalContext.current
    val app = context.applicationContext as StudyLockerApplication
    val scope = rememberCoroutineScope()

    val allQuestions by app.database.questionDao().getAllQuestions().collectAsState(initial = emptyList())
    var selectedCategoryFilter by remember { mutableStateOf("SEMUA") }

    var showAddEditDialog by remember { mutableStateOf(false) }
    var questionToEdit by remember { mutableStateOf<QuestionEntity?>(null) }
    var questionToDelete by remember { mutableStateOf<QuestionEntity?>(null) }

    val filteredQuestions = remember(allQuestions, selectedCategoryFilter) {
        when (selectedCategoryFilter) {
            "MATEMATIKA" -> allQuestions.filter { it.category == "MATEMATIKA" }
            "IPA" -> allQuestions.filter { it.category == "IPA" }
            "IPS" -> allQuestions.filter { it.category == "IPS" }
            "CUSTOM" -> allQuestions.filter { it.isCustom || it.category == "CUSTOM" }
            else -> allQuestions
        }
    }

    val mathCount = allQuestions.count { it.category == "MATEMATIKA" }
    val scienceCount = allQuestions.count { it.category == "IPA" }
    val socialCount = allQuestions.count { it.category == "IPS" }
    val customCount = allQuestions.count { it.isCustom || it.category == "CUSTOM" }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    questionToEdit = null
                    showAddEditDialog = true
                },
                containerColor = PrimaryIndigo,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("fab_add_question")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Soal")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Category Filter Bar
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategoryFilter == "SEMUA",
                        onClick = { selectedCategoryFilter = "SEMUA" },
                        label = { Text("Semua (${allQuestions.size})") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedCategoryFilter == "MATEMATIKA",
                        onClick = { selectedCategoryFilter = "MATEMATIKA" },
                        label = { Text("Matematika ($mathCount)") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedCategoryFilter == "IPA",
                        onClick = { selectedCategoryFilter = "IPA" },
                        label = { Text("IPA ($scienceCount)") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedCategoryFilter == "IPS",
                        onClick = { selectedCategoryFilter = "IPS" },
                        label = { Text("IPS ($socialCount)") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedCategoryFilter == "CUSTOM",
                        onClick = { selectedCategoryFilter = "CUSTOM" },
                        label = { Text("Soal Saya ($customCount)") }
                    )
                }
            }

            // Questions List
            if (filteredQuestions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (selectedCategoryFilter == "CUSTOM")
                                "Belum ada soal custom buatanmu.\nTekan tombol (+) di bawah untuk membuat soal baru!"
                            else
                                "Tidak ada soal dalam kategori ini.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("questions_list"),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 88.dp, top = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredQuestions, key = { it.id }) { question ->
                        QuestionCardItem(
                            question = question,
                            onEdit = {
                                questionToEdit = question
                                showAddEditDialog = true
                            },
                            onDelete = {
                                questionToDelete = question
                            }
                        )
                    }
                }
            }
        }
    }

    // Add / Edit Dialog
    if (showAddEditDialog) {
        QuestionAddEditDialog(
            initialQuestion = questionToEdit,
            onDismiss = { showAddEditDialog = false },
            onSave = { updated ->
                scope.launch {
                    if (updated.id == 0L) {
                        app.database.questionDao().insertQuestion(updated)
                    } else {
                        app.database.questionDao().updateQuestion(updated)
                    }
                    showAddEditDialog = false
                }
            }
        )
    }

    // Delete Confirmation Dialog
    if (questionToDelete != null) {
        AlertDialog(
            onDismissRequest = { questionToDelete = null },
            title = { Text("Hapus Soal?") },
            text = { Text("Apakah kamu yakin ingin menghapus soal ini dari Bank Soal?") },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            questionToDelete?.let { app.database.questionDao().deleteQuestion(it) }
                            questionToDelete = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { questionToDelete = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun QuestionCardItem(
    question: QuestionEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val categoryColor = when (question.category) {
        "MATEMATIKA" -> PrimaryIndigo
        "IPA" -> SuccessGreen
        "IPS" -> TertiaryAmber
        else -> SecondaryViolet
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Category Badge & Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(categoryColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = question.category,
                            color = categoryColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    if (question.isCustom) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(AccentCyan.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Soal Saya",
                                color = AccentCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Row {
                    if (question.isCustom) {
                        IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = PrimaryIndigo, modifier = Modifier.size(18.dp))
                        }
                        IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = ErrorRed, modifier = Modifier.size(18.dp))
                        }
                    }
                    IconButton(onClick = { expanded = !expanded }, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (expanded) "Sembunyikan" else "Buka"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Question Text
            Text(
                text = question.questionText,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Options Preview
            Spacer(modifier = Modifier.height(10.dp))

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    val opts = listOf(
                        "A" to question.optionA,
                        "B" to question.optionB,
                        "C" to question.optionC,
                        "D" to question.optionD
                    )

                    opts.forEach { (letter, text) ->
                        val isCorrect = letter == question.correctOption
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .background(
                                    if (isCorrect) SuccessGreen.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$letter. ",
                                fontWeight = FontWeight.Bold,
                                color = if (isCorrect) SuccessGreen else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = text,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            if (isCorrect) {
                                Text(
                                    text = "Kunci Jawaban ✓",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SuccessGreen
                                )
                            }
                        }
                    }

                    if (question.explanation.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "💡 Pembahasan: ${question.explanation}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuestionAddEditDialog(
    initialQuestion: QuestionEntity?,
    onDismiss: () -> Unit,
    onSave: (QuestionEntity) -> Unit
) {
    var category by remember { mutableStateOf(initialQuestion?.category ?: "CUSTOM") }
    var questionText by remember { mutableStateOf(initialQuestion?.questionText ?: "") }
    var optionA by remember { mutableStateOf(initialQuestion?.optionA ?: "") }
    var optionB by remember { mutableStateOf(initialQuestion?.optionB ?: "") }
    var optionC by remember { mutableStateOf(initialQuestion?.optionC ?: "") }
    var optionD by remember { mutableStateOf(initialQuestion?.optionD ?: "") }
    var correctOption by remember { mutableStateOf(initialQuestion?.correctOption ?: "A") }
    var explanation by remember { mutableStateOf(initialQuestion?.explanation ?: "") }
    var errorMessage by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (initialQuestion == null) "Tambah Soal Baru" else "Edit Soal")
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Category Selector
                Text("Kategori Soal:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("CUSTOM", "MATEMATIKA", "IPA", "IPS").forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(if (cat == "CUSTOM") "Custom" else cat, fontSize = 11.sp) }
                        )
                    }
                }

                // Question Input
                OutlinedTextField(
                    value = questionText,
                    onValueChange = { questionText = it },
                    label = { Text("Teks Pertanyaan") },
                    placeholder = { Text("Contoh: Berapakah 12 + 18?") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_question_text"),
                    shape = RoundedCornerShape(12.dp)
                )

                // 4 Options
                OutlinedTextField(
                    value = optionA,
                    onValueChange = { optionA = it },
                    label = { Text("Pilihan A") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = optionB,
                    onValueChange = { optionB = it },
                    label = { Text("Pilihan B") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = optionC,
                    onValueChange = { optionC = it },
                    label = { Text("Pilihan C") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = optionD,
                    onValueChange = { optionD = it },
                    label = { Text("Pilihan D") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // Correct Answer Selector
                Text("Kunci Jawaban Benar:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    listOf("A", "B", "C", "D").forEach { opt ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { correctOption = opt }
                        ) {
                            RadioButton(
                                selected = correctOption == opt,
                                onClick = { correctOption = opt },
                                colors = RadioButtonDefaults.colors(selectedColor = PrimaryIndigo)
                            )
                            Text(opt, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Explanation Input
                OutlinedTextField(
                    value = explanation,
                    onValueChange = { explanation = it },
                    label = { Text("Pembahasan / Penjelasan (Opsional)") },
                    placeholder = { Text("Penjelasan kenapa jawaban tersebut benar") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                if (errorMessage.isNotEmpty()) {
                    Text(errorMessage, color = ErrorRed, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (questionText.isBlank() || optionA.isBlank() || optionB.isBlank() || optionC.isBlank() || optionD.isBlank()) {
                        errorMessage = "Mohon lengkapi pertanyaan dan seluruh pilihan jawaban A, B, C, D!"
                    } else {
                        val newEntity = QuestionEntity(
                            id = initialQuestion?.id ?: 0L,
                            category = category,
                            questionText = questionText.trim(),
                            optionA = optionA.trim(),
                            optionB = optionB.trim(),
                            optionC = optionC.trim(),
                            optionD = optionD.trim(),
                            correctOption = correctOption,
                            explanation = explanation.trim(),
                            isCustom = true
                        )
                        onSave(newEntity)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                modifier = Modifier.testTag("btn_save_question")
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
