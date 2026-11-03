package com.example.data

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.CalendarContract
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.model.AppLanguage
import com.example.model.FarmTaskReminder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class CalendarSyncHelper(private val context: Context) {

    fun hasCalendarPermission(): Boolean {
        val readPerm = ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_CALENDAR)
        val writePerm = ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_CALENDAR)
        return readPerm == PackageManager.PERMISSION_GRANTED && writePerm == PackageManager.PERMISSION_GRANTED
    }

    // Direct CalendarContract Sync (Inserts into default device calendar)
    fun syncTaskToSystemCalendar(task: FarmTaskReminder, lang: AppLanguage): Boolean {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdf.parse(task.scheduledDate) ?: return false

            val cal = Calendar.getInstance()
            cal.time = date
            cal.set(Calendar.HOUR_OF_DAY, 7) // Default 7:00 AM
            cal.set(Calendar.MINUTE, 0)
            val startTimeMs = cal.timeInMillis

            cal.add(Calendar.HOUR_OF_DAY, 1)
            val endTimeMs = cal.timeInMillis

            val title = if (lang == AppLanguage.TAMIL) {
                "🌾 [உழவன் X] ${task.cropTamilName}: ${task.titleTamil}"
            } else {
                "🌾 [Uzhav X] ${task.cropName}: ${task.titleEnglish}"
            }

            val description = if (lang == AppLanguage.TAMIL) {
                "வளர்ச்சி பருவம்: ${task.growthStageNameTamil}\nகுறிப்பு: ${task.notesTamil}"
            } else {
                "Growth Stage: ${task.growthStageNameEnglish}\nNotes: ${task.notesEnglish}"
            }

            // Insert into system calendar provider if permission granted
            if (hasCalendarPermission()) {
                val values = ContentValues().apply {
                    put(CalendarContract.Events.CALENDAR_ID, 1) // Default local calendar ID
                    put(CalendarContract.Events.TITLE, title)
                    put(CalendarContract.Events.DESCRIPTION, description)
                    put(CalendarContract.Events.EVENT_LOCATION, "Farm Field")
                    put(CalendarContract.Events.DTSTART, startTimeMs)
                    put(CalendarContract.Events.DTEND, endTimeMs)
                    put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
                    put(CalendarContract.Events.HAS_ALARM, 1)
                }

                val uri = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
                if (uri != null) {
                    val msg = if (lang == AppLanguage.TAMIL) "நாட்காட்டியில் வெற்றிகரமாக சேமிக்கப்பட்டது!" else "Successfully synced to System Calendar!"
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    return true
                }
            }

            // Fallback: Launch System Calendar Intent chooser if direct insert fails or permission missing
            launchCalendarIntent(title, description, startTimeMs, endTimeMs)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Launch Calendar Intent directly
    fun launchCalendarIntent(title: String, description: String, startMs: Long, endMs: Long) {
        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, title)
            putExtra(CalendarContract.Events.DESCRIPTION, description)
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMs)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMs)
            putExtra(CalendarContract.Events.EVENT_LOCATION, "Farm Field")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "No calendar app found", Toast.LENGTH_SHORT).show()
        }
    }

    // Batch Sync All Tasks to Calendar
    fun syncAllTasksToSystemCalendar(tasks: List<FarmTaskReminder>, lang: AppLanguage) {
        var count = 0
        for (task in tasks.take(10)) { // Sync upcoming tasks
            if (syncTaskToSystemCalendar(task, lang)) {
                count++
            }
        }
        val msg = if (lang == AppLanguage.TAMIL) {
            "$count பாசன & உரமிடும் பணிகள் நாட்காட்டியில் சேர்க்கப்பட்டது"
        } else {
            "Synced $count farming tasks to System Calendar"
        }
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }

    // Share Task Schedule Digest via WhatsApp / System Share Intent
    fun shareCalendarScheduleText(tasks: List<FarmTaskReminder>, lang: AppLanguage) {
        val titleStr = if (lang == AppLanguage.TAMIL) "🌾 உழவன் X - வேளாண் பாசனம் & உர அட்டவணை" else "🌾 Uzhav X - Farm Irrigation & Fertilizer Schedule"
        val sb = StringBuilder()
        sb.append(titleStr).append("\n\n")

        val grouped = tasks.groupBy { it.scheduledDate }
        for ((dateStr, dayTasks) in grouped.entries.take(7)) {
            sb.append("📅 ").append(dateStr).append(":\n")
            for (t in dayTasks) {
                val icon = when (t.category) {
                    com.example.model.TaskCategory.IRRIGATION -> "💧"
                    com.example.model.TaskCategory.FERTILIZER -> "🧪"
                    com.example.model.TaskCategory.HARVEST -> "🌾"
                    com.example.model.TaskCategory.PEST_SPRAY -> "🛡️"
                    else -> "🌱"
                }
                val tTitle = if (lang == AppLanguage.TAMIL) t.titleTamil else t.titleEnglish
                val crop = if (lang == AppLanguage.TAMIL) t.cropTamilName else t.cropName
                val status = if (t.isCompleted) "✅" else "⏳"
                sb.append("  $icon $status [$crop] $tTitle (${t.scheduledTime})\n")
            }
            sb.append("\n")
        }

        sb.append(if (lang == AppLanguage.TAMIL) "உழவன் X ஆப் மூலம் உருவாக்கப்பட்டது." else "Generated via Uzhav X App.")

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, sb.toString())
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val chooser = Intent.createChooser(intent, if (lang == AppLanguage.TAMIL) "அட்டவணையை பகிர்க" else "Share Schedule")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(chooser)
        } catch (_: Exception) {}
    }
}
