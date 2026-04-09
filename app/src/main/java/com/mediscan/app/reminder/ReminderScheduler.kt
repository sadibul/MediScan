package com.mediscan.app.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.mediscan.app.data.model.Reminder
import java.util.Calendar

private const val TAG = "ReminderScheduler"

/**
 * Static helper for scheduling reminder alarms.
 * Used by both ReminderViewModel and BootReceiver.
 */
object ReminderScheduler {

    private val dayNameToCalendarDay = mapOf(
        "Sun" to Calendar.SUNDAY,
        "Mon" to Calendar.MONDAY,
        "Tue" to Calendar.TUESDAY,
        "Wed" to Calendar.WEDNESDAY,
        "Thu" to Calendar.THURSDAY,
        "Fri" to Calendar.FRIDAY,
        "Sat" to Calendar.SATURDAY,
    )

    fun scheduleAlarms(context: Context, reminder: Reminder) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val canScheduleExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }

        val now = System.currentTimeMillis()
        val endDate = reminder.startDate + (reminder.timeDurationDays.toLong() * 24 * 60 * 60 * 1000)
        var requestCodeCounter = reminder.id.hashCode() and 0x00FFFFFF

        for (timeStr in reminder.medicineTimes) {
            val parts = timeStr.split(":")
            if (parts.size != 2) continue
            val hour = parts[0].toIntOrNull() ?: continue
            val minute = parts[1].toIntOrNull() ?: continue

            for (dayName in reminder.daysOfWeek) {
                val calendarDay = dayNameToCalendarDay[dayName] ?: continue

                val cal = Calendar.getInstance()
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)

                while (cal.get(Calendar.DAY_OF_WEEK) != calendarDay) {
                    cal.add(Calendar.DAY_OF_MONTH, 1)
                }
                if (cal.timeInMillis <= now) {
                    cal.add(Calendar.WEEK_OF_YEAR, 1)
                }

                while (cal.timeInMillis <= endDate) {
                    if (cal.timeInMillis > now) {
                        val requestCode = requestCodeCounter++
                        val intent = Intent(context, ReminderAlarmReceiver::class.java).apply {
                            putExtra("reminder_id", reminder.id)
                            putExtra("medicine_name", reminder.medicineName)
                            putExtra("description", reminder.description)
                            putExtra("time_str", timeStr)
                            putExtra("request_code", requestCode)
                        }
                        val pendingIntent = PendingIntent.getBroadcast(
                            context, requestCode, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                        try {
                            if (canScheduleExact) {
                                alarmManager.setExactAndAllowWhileIdle(
                                    AlarmManager.RTC_WAKEUP,
                                    cal.timeInMillis,
                                    pendingIntent
                                )
                            } else {
                                alarmManager.set(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pendingIntent)
                            }
                        } catch (e: SecurityException) {
                            alarmManager.set(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pendingIntent)
                        }
                    }
                    cal.add(Calendar.WEEK_OF_YEAR, 1)
                }
            }
        }
        Log.d(TAG, "Scheduled alarms for: ${reminder.medicineName}")
    }
}
