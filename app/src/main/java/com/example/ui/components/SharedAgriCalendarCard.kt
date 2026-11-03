package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CalendarSyncHelper
import com.example.model.AppLanguage
import com.example.model.FarmTaskReminder
import com.example.model.TaskCategory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun SharedAgriCalendarCard(
    tasks: List<FarmTaskReminder>,
    language: AppLanguage,
    onToggleTaskCompletion: (FarmTaskReminder) -> Unit,
    onNavigateToFullSchedule: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val calendarHelper = remember { CalendarSyncHelper(context) }

    var selectedCalendar by remember { mutableStateOf(Calendar.getInstance()) }
    var selectedCategoryFilter by remember { mutableStateOf<TaskCategory?>(null) }

    // Selected date in format YYYY-MM-DD
    val sdf = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val monthYearFormat = remember {
        SimpleDateFormat(
            if (language == AppLanguage.TAMIL) "MMMM yyyy" else "MMMM yyyy",
            Locale.getDefault()
        )
    }

    var selectedDateStr by remember { mutableStateOf(sdf.format(Date())) }

    // Calculate calendar grid for selected month
    val currentMonthCal = selectedCalendar.clone() as Calendar
    currentMonthCal.set(Calendar.DAY_OF_MONTH, 1)
    val firstDayOfWeek = currentMonthCal.get(Calendar.DAY_OF_WEEK) - 1 // 0-indexed for Sunday
    val daysInMonth = currentMonthCal.getActualMaximum(Calendar.DAY_OF_MONTH)

    val currentYear = currentMonthCal.get(Calendar.YEAR)
    val currentMonth = currentMonthCal.get(Calendar.MONTH)

    // Filter tasks based on category if selected
    val filteredTasks = tasks.filter { task ->
        selectedCategoryFilter == null || task.category == selectedCategoryFilter
    }

    // Map of YYYY-MM-DD to list of tasks
    val tasksByDate = remember(filteredTasks) {
        filteredTasks.groupBy { it.scheduledDate }
    }

    val selectedDayTasks = tasksByDate[selectedDateStr] ?: emptyList()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("shared_agri_calendar_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 1. Header: Title, System Sync Button & Share
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0xFFE8F5E9), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Agri Calendar",
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (language == AppLanguage.TAMIL) "வேளாண் நாட்காட்டி" else "Agri Task Calendar",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (language == AppLanguage.TAMIL) "பாசனம், உரம் & அறுவடை அட்டவணை" else "Irrigation, Fertilizer & Harvest Sync",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = { calendarHelper.shareCalendarScheduleText(tasks, language) },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("share_calendar_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = { calendarHelper.syncAllTasksToSystemCalendar(tasks, language) },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("sync_all_calendar_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.EditCalendar,
                            contentDescription = "Sync System Calendar",
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Month Selector & Navigation Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        val newCal = selectedCalendar.clone() as Calendar
                        newCal.add(Calendar.MONTH, -1)
                        selectedCalendar = newCal
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Previous Month",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = monthYearFormat.format(selectedCalendar.time),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                IconButton(
                    onClick = {
                        val newCal = selectedCalendar.clone() as Calendar
                        newCal.add(Calendar.MONTH, 1)
                        selectedCalendar = newCal
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Next Month",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3. Category Filter Chips (Irrigation, Fertilizer, Harvest, Pest Spray)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = selectedCategoryFilter == null,
                        onClick = { selectedCategoryFilter = null },
                        label = {
                            Text(
                                text = if (language == AppLanguage.TAMIL) "அனைத்தும்" else "All Tasks",
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        modifier = Modifier.testTag("calendar_filter_all")
                    )
                }
                items(TaskCategory.values()) { cat ->
                    val isSelected = selectedCategoryFilter == cat
                    val chipIcon = when (cat) {
                        TaskCategory.IRRIGATION -> "💧"
                        TaskCategory.FERTILIZER -> "🧪"
                        TaskCategory.PEST_SPRAY -> "🛡️"
                        TaskCategory.WEEDING -> "🌿"
                        TaskCategory.HARVEST -> "🌾"
                    }
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategoryFilter = if (isSelected) null else cat },
                        leadingIcon = { Text(chipIcon, fontSize = 12.sp) },
                        label = {
                            Text(
                                text = cat.getDisplayName(language),
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        modifier = Modifier.testTag("calendar_filter_${cat.name.lowercase()}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4. Days of Week Header
            val dayHeaders = if (language == AppLanguage.TAMIL) {
                listOf("ஞா", "தி", "செ", "பு", "வி", "வெ", "ச")
            } else {
                listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                dayHeaders.forEach { header ->
                    Text(
                        text = header,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 5. Month Calendar Grid (7 columns)
            val totalGridCells = firstDayOfWeek + daysInMonth
            val rowsCount = (totalGridCells + 6) / 7

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                for (r in 0 until rowsCount) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        for (c in 0 until 7) {
                            val cellIndex = r * 7 + c
                            val dayNum = cellIndex - firstDayOfWeek + 1

                            if (cellIndex < firstDayOfWeek || dayNum > daysInMonth) {
                                Box(modifier = Modifier.weight(1f).aspectRatio(1.1f))
                            } else {
                                val cellCal = selectedCalendar.clone() as Calendar
                                cellCal.set(Calendar.DAY_OF_MONTH, dayNum)
                                val cellDateStr = sdf.format(cellCal.time)
                                val isSelectedDay = cellDateStr == selectedDateStr
                                val dayTasks = tasksByDate[cellDateStr] ?: emptyList()

                                CalendarDayCell(
                                    dayNum = dayNum,
                                    isSelected = isSelectedDay,
                                    tasks = dayTasks,
                                    onClick = { selectedDateStr = cellDateStr },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 6. Selected Date Tasks Detail Container
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (language == AppLanguage.TAMIL) "📅 $selectedDateStr பணிகள்" else "📅 Tasks for $selectedDateStr",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        TextButton(
                            onClick = onNavigateToFullSchedule,
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text(
                                text = if (language == AppLanguage.TAMIL) "அனைத்தும் காண்க >" else "View All >",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (selectedDayTasks.isEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircleOutline,
                                contentDescription = null,
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == AppLanguage.TAMIL) "இந்த நாளில் திட்டமிட்ட பாசன/உரப் பணிகள் இல்லை." else "No scheduled farming tasks on this date.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            selectedDayTasks.forEach { task ->
                                CalendarTaskItemRow(
                                    task = task,
                                    language = language,
                                    onToggleCompletion = { onToggleTaskCompletion(task) },
                                    onSyncToCalendar = { calendarHelper.syncTaskToSystemCalendar(task, language) }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 7. Quick Action Bar: Device Calendar Sync Promo Banner
            Button(
                onClick = { calendarHelper.syncAllTasksToSystemCalendar(tasks, language) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("sync_phone_calendar_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20))
            ) {
                Icon(
                    imageVector = Icons.Default.EventAvailable,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (language == AppLanguage.TAMIL) "போன் நாட்காட்டியில் (Calendar) பாசன அட்டவணையைச் சேர்" else "Sync Schedule to Phone Calendar App",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CalendarDayCell(
    dayNum: Int,
    isSelected: Boolean,
    tasks: List<FarmTaskReminder>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerBg = when {
        isSelected -> MaterialTheme.colorScheme.primary
        tasks.isNotEmpty() -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
        else -> Color.Transparent
    }

    val textColor = when {
        isSelected -> Color.White
        tasks.isNotEmpty() -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = modifier
            .aspectRatio(1.1f)
            .clip(RoundedCornerShape(10.dp))
            .background(containerBg)
            .border(
                width = if (isSelected) 2.dp else 0.5.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { onClick() }
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = dayNum.toString(),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if (isSelected || tasks.isNotEmpty()) FontWeight.Bold else FontWeight.Normal,
                color = textColor
            )

            // Category indicator dots
            if (tasks.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    tasks.take(3).forEach { t ->
                        val dotColor = when (t.category) {
                            TaskCategory.IRRIGATION -> Color(0xFF1976D2)
                            TaskCategory.FERTILIZER -> Color(0xFFF57C00)
                            TaskCategory.HARVEST -> Color(0xFF388E3C)
                            TaskCategory.PEST_SPRAY -> Color(0xFF7B1FA2)
                            TaskCategory.WEEDING -> Color(0xFF00796B)
                        }
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .background(if (isSelected) Color.White else dotColor, CircleShape)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarTaskItemRow(
    task: FarmTaskReminder,
    language: AppLanguage,
    onToggleCompletion: () -> Unit,
    onSyncToCalendar: () -> Unit
) {
    val (categoryColor, categoryIcon) = when (task.category) {
        TaskCategory.IRRIGATION -> Pair(Color(0xFF1565C0), Icons.Default.WaterDrop)
        TaskCategory.FERTILIZER -> Pair(Color(0xFFE65100), Icons.Default.Science)
        TaskCategory.HARVEST -> Pair(Color(0xFF2E7D32), Icons.Default.Agriculture)
        TaskCategory.PEST_SPRAY -> Pair(Color(0xFF6A1B9A), Icons.Default.Security)
        TaskCategory.WEEDING -> Pair(Color(0xFF00695C), Icons.Default.Grass)
    }

    val titleText = if (language == AppLanguage.TAMIL) task.titleTamil else task.titleEnglish
    val cropText = if (language == AppLanguage.TAMIL) task.cropTamilName else task.cropName
    val stageText = if (language == AppLanguage.TAMIL) task.growthStageNameTamil else task.growthStageNameEnglish

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("calendar_task_item_${task.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) MaterialTheme.colorScheme.surface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, categoryColor.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onToggleCompletion,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = "Toggle Complete",
                    tint = if (task.isCompleted) Color(0xFF2E7D32) else MaterialTheme.colorScheme.outline
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(categoryColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = categoryIcon,
                    contentDescription = null,
                    tint = categoryColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = titleText,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (task.isCompleted) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = categoryColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = cropText,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = categoryColor,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
                Text(
                    text = "${task.scheduledTime} • $stageText",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(
                onClick = onSyncToCalendar,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Event,
                    contentDescription = "Add to Calendar",
                    tint = categoryColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
