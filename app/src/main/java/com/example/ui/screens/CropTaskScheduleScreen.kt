package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.viewmodel.UzhavViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropTaskScheduleScreen(
    viewModel: UzhavViewModel,
    state: com.example.viewmodel.UzhavUiState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lang = state.language
    val primaryColor = MaterialTheme.colorScheme.primary

    var selectedCropName by remember { mutableStateOf("Samba Paddy") }
    var selectedCropTamil by remember { mutableStateOf("சம்பா நெல்") }
    var sowingDateText by remember {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -42) // 42 days ago default so stage 2 tillering is active!
        mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time))
    }

    var selectedCategoryFilter by remember { mutableStateOf<TaskCategory?>(null) }
    var showAddTaskDialog by remember { mutableStateOf(false) }

    val tasks = state.taskReminders
    val growthProgress = remember(selectedCropName, sowingDateText) {
        viewModel.calculateCropGrowthProgress(selectedCropName, selectedCropTamil, sowingDateText)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("crop_task_schedule_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header Title & Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF00695C), // Deep Forest Teal
                                    Color(0xFF00897B),
                                    Color(0xFF26A69A)
                                )
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(Color.White.copy(alpha = 0.25f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EventRepeat,
                                    contentDescription = "Task Scheduler",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (lang == AppLanguage.TAMIL) "பயிர் வளர்ச்சி & பாசன/உர நினைவூட்டல்" else "Crop Stage & Task Scheduler",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = if (lang == AppLanguage.TAMIL) "விதைத்த நாள் அடிப்படையில் தொடர் பாசனம், உரமிடல் மற்றும் பூச்சி தெளிப்பு அட்டவணை" else "Recurring irrigation and fertigation reminders matched to crop stage",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Crop & Sowing Date Configurator Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (lang == AppLanguage.TAMIL) "பயிர் & விதைத்த தேதி தேர்ந்தெடுங்கள்:" else "Select Crop & Sowing Date:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )

                    // Crop Selection Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "Samba Paddy" to "சம்பா நெல்",
                            "Turmeric" to "மஞ்சள்",
                            "Cotton" to "பருத்தி",
                            "Sugarcane" to "கரும்பு"
                        ).forEach { (eng, tam) ->
                            val isSelected = selectedCropName == eng
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedCropName = eng
                                    selectedCropTamil = tam
                                },
                                label = { Text(if (lang == AppLanguage.TAMIL) tam else eng, fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Sowing Date Input & Auto-Generate Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = sowingDateText,
                            onValueChange = { sowingDateText = it },
                            label = { Text(if (lang == AppLanguage.TAMIL) "விதைத்த தேதி (YYYY-MM-DD)" else "Sowing Date (YYYY-MM-DD)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )

                        Button(
                            onClick = {
                                viewModel.generateTaskScheduleForCrop(selectedCropName, selectedCropTamil, sowingDateText)
                                Toast.makeText(context, if (lang == AppLanguage.TAMIL) "பயிர் அட்டவணை உருவாக்கப்பட்டது!" else "Tasks generated for $selectedCropName!", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("generate_crop_tasks_btn")
                        ) {
                            Icon(Icons.Default.AutoMode, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (lang == AppLanguage.TAMIL) "அட்டவணை ஆக்குக" else "Auto-Schedule")
                        }
                    }
                }
            }
        }

        // 3. Current Crop Growth Stage Progress Tracker Card
        growthProgress?.let { progress ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🌱", fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${if (lang == AppLanguage.TAMIL) progress.cropTamilName else progress.cropName} - ${if (lang == AppLanguage.TAMIL) "வளர்ச்சி நிலை" else "Growth Tracker"}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = primaryColor
                            ) {
                                Text(
                                    text = "${progress.daysAfterSowing} ${if (lang == AppLanguage.TAMIL) "நாட்கள்" else "Days"}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // Progress Bar
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (lang == AppLanguage.TAMIL) progress.currentStage.stageNameTamil else progress.currentStage.stageNameEnglish,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00695C)
                                )
                                Text(
                                    text = "${(progress.overallProgressPercent * 100).toInt()}%",
                                    fontWeight = FontWeight.Bold,
                                    color = primaryColor
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { progress.overallProgressPercent },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(5.dp)),
                                color = Color(0xFF00897B),
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }

                        Text(
                            text = if (lang == AppLanguage.TAMIL) progress.currentStage.descriptionTamil else progress.currentStage.descriptionEnglish,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // 4. Tasks Filter Bar & Add Custom Task Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (lang == AppLanguage.TAMIL) "நினைவூட்டல் பணிகள் (${tasks.size})" else "Task Reminders (${tasks.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = { showAddTaskDialog = true },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("add_custom_task_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Task", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (lang == AppLanguage.TAMIL) "புதிய பணி" else "Add Task", fontSize = 12.sp)
                }
            }
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = selectedCategoryFilter == null,
                        onClick = { selectedCategoryFilter = null },
                        label = { Text(if (lang == AppLanguage.TAMIL) "அனைத்தும்" else "All") }
                    )
                }
                items(TaskCategory.values()) { cat ->
                    FilterChip(
                        selected = selectedCategoryFilter == cat,
                        onClick = { selectedCategoryFilter = cat },
                        label = { Text(cat.getDisplayName(lang)) }
                    )
                }
            }
        }

        // 5. Tasks List
        val filteredTasks = tasks.filter { selectedCategoryFilter == null || it.category == selectedCategoryFilter }

        if (filteredTasks.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.CheckCircleOutline, contentDescription = null, tint = primaryColor, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (lang == AppLanguage.TAMIL) "அனைத்து பாசன & உரப்பணிகளும் நிறைவுற்றன!" else "All crop tasks up to date!",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (lang == AppLanguage.TAMIL) "மேலே உள்ள 'அட்டவணை ஆக்குக' பட்டனை அழுத்தி புதிய பணிகளை உருவாக்குங்கள்." else "Tap 'Auto-Schedule' above to populate recurring stage reminders.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        } else {
            items(filteredTasks, key = { it.id }) { task ->
                TaskReminderItemCard(
                    task = task,
                    language = lang,
                    onToggleCompleted = { viewModel.toggleTaskCompleted(task) },
                    onDelete = { viewModel.deleteTaskReminder(task.id) }
                )
            }
        }
    }

    // Add Custom Task Dialog
    if (showAddTaskDialog) {
        var customTitle by remember { mutableStateOf("") }
        var customCategory by remember { mutableStateOf(TaskCategory.IRRIGATION) }
        var customRecurrence by remember { mutableStateOf(RecurrencePattern.EVERY_3_DAYS) }
        var customNotes by remember { mutableStateOf("Morning 06:00 AM") }

        AlertDialog(
            onDismissRequest = { showAddTaskDialog = false },
            title = { Text(if (lang == AppLanguage.TAMIL) "புதிய தொடர் பாசன/உரப்பணி சேர்க்க" else "Add Custom Recurring Task") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = customTitle,
                        onValueChange = { customTitle = it },
                        label = { Text(if (lang == AppLanguage.TAMIL) "பணி தலைப்பு (எ.கா. சொட்டுநீர் பாசனம்)" else "Task Title") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = if (lang == AppLanguage.TAMIL) "பணி வகை:" else "Category:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        TaskCategory.values().take(3).forEach { cat ->
                            FilterChip(
                                selected = customCategory == cat,
                                onClick = { customCategory = cat },
                                label = { Text(cat.getDisplayName(lang), fontSize = 10.sp) }
                            )
                        }
                    }

                    Text(
                        text = if (lang == AppLanguage.TAMIL) "சுழற்சி முறை (Recurrence):" else "Recurrence Pattern:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(RecurrencePattern.DAILY, RecurrencePattern.EVERY_3_DAYS, RecurrencePattern.WEEKLY).forEach { rec ->
                            FilterChip(
                                selected = customRecurrence == rec,
                                onClick = { customRecurrence = rec },
                                label = { Text(rec.getDisplayName(lang), fontSize = 10.sp) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = customNotes,
                        onValueChange = { customNotes = it },
                        label = { Text(if (lang == AppLanguage.TAMIL) "குறிப்பு / நேரம்" else "Notes / Time") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (customTitle.isNotBlank()) {
                        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                        val newTask = FarmTaskReminder(
                            id = "task_custom_${System.currentTimeMillis()}",
                            cropName = selectedCropName,
                            cropTamilName = selectedCropTamil,
                            titleTamil = customTitle,
                            titleEnglish = customTitle,
                            category = customCategory,
                            recurrence = customRecurrence,
                            scheduledDate = todayStr,
                            scheduledTime = "06:00 AM",
                            isCompleted = false,
                            growthStageNameTamil = "வளர்ச்சி நிலை (Active Stage)",
                            growthStageNameEnglish = "Active Stage Care",
                            notesTamil = customNotes,
                            notesEnglish = customNotes
                        )
                        viewModel.addCustomTaskReminder(newTask)
                        showAddTaskDialog = false
                    }
                }) {
                    Text(if (lang == AppLanguage.TAMIL) "சேர்" else "Add Reminder")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTaskDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun TaskReminderItemCard(
    task: FarmTaskReminder,
    language: AppLanguage,
    onToggleCompleted: () -> Unit,
    onDelete: () -> Unit
) {
    val titleText = if (language == AppLanguage.TAMIL) task.titleTamil else task.titleEnglish
    val stageText = if (language == AppLanguage.TAMIL) task.growthStageNameTamil else task.growthStageNameEnglish
    val categoryText = task.category.getDisplayName(language)
    val recurrenceText = task.recurrence.getDisplayName(language)
    val notesText = if (language == AppLanguage.TAMIL && task.notesTamil.isNotBlank()) task.notesTamil else task.notesEnglish

    val categoryColor = when (task.category) {
        TaskCategory.IRRIGATION -> Color(0xFF0288D1) // Blue
        TaskCategory.FERTILIZER -> Color(0xFF2E7D32) // Green
        TaskCategory.PEST_SPRAY -> Color(0xFFD84315) // Orange/Red
        TaskCategory.WEEDING -> Color(0xFF7CB342)    // Light Green
        TaskCategory.HARVEST -> Color(0xFFF57F17)    // Gold
    }

    val categoryIcon = when (task.category) {
        TaskCategory.IRRIGATION -> Icons.Default.WaterDrop
        TaskCategory.FERTILIZER -> Icons.Default.Science
        TaskCategory.PEST_SPRAY -> Icons.Default.BugReport
        TaskCategory.WEEDING -> Icons.Default.ContentCut
        TaskCategory.HARVEST -> Icons.Default.Agriculture
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (task.isCompleted) 0.dp else 2.dp),
        border = if (!task.isCompleted) androidx.compose.foundation.BorderStroke(1.dp, categoryColor.copy(alpha = 0.5f)) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Task Category Icon
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(categoryColor.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = categoryIcon,
                    contentDescription = categoryText,
                    tint = categoryColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = categoryColor.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = categoryText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = categoryColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = recurrenceText,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                Text(
                    text = titleText,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (task.isCompleted) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "🗓️ ${task.scheduledDate} (${task.scheduledTime}) • $stageText",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline
                )

                if (notesText.isNotBlank()) {
                    Text(
                        text = notesText,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(onClick = onToggleCompleted) {
                Icon(
                    imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = "Toggle Complete",
                    tint = if (task.isCompleted) Color(0xFF2E7D32) else MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}
