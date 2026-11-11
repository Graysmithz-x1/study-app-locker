package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TertiaryAmber
import com.example.util.PermissionHelper
import kotlin.math.roundToInt

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val app = context.applicationContext as StudyLockerApplication
    val settings by app.settingsRepository.settings.collectAsState()

    val hasUsageAccess = PermissionHelper.hasUsageStatsPermission(context)
    val hasOverlay = PermissionHelper.hasOverlayPermission(context)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("settings_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section: Active Subject Categories
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(PrimaryIndigo.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Category, contentDescription = null, tint = PrimaryIndigo, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Kategori Soal Aktif", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Pilih mata pelajaran yang akan muncul di kuis", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))

                    val categoriesList = listOf(
                        "MATEMATIKA" to "Matematika (Aljabar, Geometri, Aritmetika)",
                        "IPA" to "IPA (Fisika, Biologi, Kimia, Tata Surya)",
                        "IPS" to "IPS (Sejarah, Geografi, Ekonomi, Sosial)",
                        "CUSTOM" to "Soal Saya (Pertanyaan Kustom Buatanmu)"
                    )

                    categoriesList.forEach { (catKey, catLabel) ->
                        val isChecked = settings.activeCategories.contains(catKey)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { app.settingsRepository.toggleCategory(catKey) }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { app.settingsRepository.toggleCategory(catKey) },
                                colors = CheckboxDefaults.colors(checkedColor = PrimaryIndigo),
                                modifier = Modifier.testTag("checkbox_category_$catKey")
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = catLabel,
                                fontSize = 14.sp,
                                fontWeight = if (isChecked) FontWeight.SemiBold else FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Section: Question Count Per Quiz (5 to 20)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(AccentCyan.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.FormatListNumbered, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Jumlah Soal per Kuis", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Rentang 5 hingga 20 soal setiap membuka kunci", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(
                            text = "${settings.questionsPerSession} Soal",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = PrimaryIndigo
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Slider(
                        value = settings.questionsPerSession.toFloat(),
                        onValueChange = { app.settingsRepository.setQuestionsPerSession(it.roundToInt()) },
                        valueRange = 5f..20f,
                        steps = 14,
                        colors = SliderDefaults.colors(
                            thumbColor = PrimaryIndigo,
                            activeTrackColor = PrimaryIndigo
                        ),
                        modifier = Modifier.testTag("slider_question_count")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("5 Soal (Cepat)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("10 Soal (Disarankan)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("20 Soal (Intensif)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // Section: Unlock Duration & Penalty Rules
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SuccessGreen.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Timer, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Durasi Normal Penggunaan", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Waktu yang didapat jika menjawab benar semua (0 salah)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(
                            text = "${settings.baseDurationMinutes}m",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = SuccessGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Slider(
                        value = settings.baseDurationMinutes.toFloat(),
                        onValueChange = { app.settingsRepository.setBaseDurationMinutes(it.roundToInt()) },
                        valueRange = 10f..120f,
                        steps = 10,
                        colors = SliderDefaults.colors(
                            thumbColor = SuccessGreen,
                            activeTrackColor = SuccessGreen
                        ),
                        modifier = Modifier.testTag("slider_base_duration")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("10 Menit", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("30 Menit (Standar)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("120 Menit", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Penalty Information Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Aturan Penalti Kesalahan:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ErrorRed)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("• 0 Salah: Durasi penuh diperoleh (${settings.baseDurationMinutes} Menit).", fontSize = 12.sp)
                            Text("• Setiap Salah: Durasi otomatis dipotong ${settings.penaltyPerMistakeMinutes} Menit per kesalahan.", fontSize = 12.sp)
                            Text("• Lebih dari 5 Salah: KUIS GAGAL! Durasi hangus dan harus mengulang dari awal.", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = ErrorRed)
                            Text("• Setelah durasi habis, aplikasi otomatis terkunci kembali.", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Section: System Permissions Status
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(PrimaryIndigo.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = PrimaryIndigo, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Status Izin Sistem", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Permission 1: Usage Access
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Akses Penggunaan (Usage Access)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text(
                                if (hasUsageAccess) "Izin diberikan ✓" else "Belum diizinkan - Diperlukan untuk mendeteksi app",
                                fontSize = 12.sp,
                                color = if (hasUsageAccess) SuccessGreen else ErrorRed
                            )
                        }
                        if (!hasUsageAccess) {
                            OutlinedButton(onClick = { PermissionHelper.openUsageAccessSettings(context) }) {
                                Text("Buka Izin")
                            }
                        } else {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Permission 2: Overlay
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Tampilkan di Atas Aplikasi Lain (Overlay)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text(
                                if (hasOverlay) "Izin diberikan ✓" else "Belum diizinkan - Diperlukan untuk layar kuis",
                                fontSize = 12.sp,
                                color = if (hasOverlay) SuccessGreen else ErrorRed
                            )
                        }
                        if (!hasOverlay) {
                            OutlinedButton(onClick = { PermissionHelper.openOverlaySettings(context) }) {
                                Text("Buka Izin")
                            }
                        } else {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen)
                        }
                    }
                }
            }
        }
    }
}
