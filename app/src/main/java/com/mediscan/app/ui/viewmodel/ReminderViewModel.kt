package com.mediscan.app.ui.viewmodel

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mediscan.app.core.utils.NetworkResult
import com.mediscan.app.data.model.Reminder
import com.mediscan.app.data.repository.ReminderRepository
import com.mediscan.app.reminder.ReminderAlarmReceiver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

private const val TAG = "ReminderVM"

/**
 * ReminderViewModel — manages reminder state for patients.
 * Handles saving, loading, updating, deleting reminders, and scheduling alarms.
 */
@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val auth: FirebaseAuth,
) : ViewModel() {

    private val _reminders = MutableStateFlow<NetworkResult<List<Reminder>>>(NetworkResult.Idle)
    val reminders: StateFlow<NetworkResult<List<Reminder>>> = _reminders

    private val _saveState = MutableStateFlow<NetworkResult<Reminder>>(NetworkResult.Idle)
    val saveState: StateFlow<NetworkResult<Reminder>> = _saveState

    private val _updateState = MutableStateFlow<NetworkResult<Reminder>>(NetworkResult.Idle)
    val updateState: StateFlow<NetworkResult<Reminder>> = _updateState

    private val _deleteState = MutableStateFlow<NetworkResult<Unit>>(NetworkResult.Idle)
    val deleteState: StateFlow<NetworkResult<Unit>> = _deleteState

    init {
        loadReminders()
    }

    /** Load all reminders for current patient */
    fun loadReminders() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _reminders.value = NetworkResult.Loading
            _reminders.value = reminderRepository.getReminders(uid)
        }
    }

    /** Save a new reminder and schedule alarms */
    fun saveReminder(reminder: Reminder, context: Context) {
        val uid = auth.currentUser?.uid ?: return
        val toSave = reminder.copy(patientId = uid)
        viewModelScope.launch {
            _saveState.value = NetworkResult.Loading
            val result = reminderRepository.saveReminder(toSave)
            _saveState.value = result

            if (result is NetworkResult.Success) {
                scheduleAlarms(context, result.data)
                loadReminders()
            }
        }
    }

    /** Update an existing reminder, reschedule alarms */
    fun updateReminder(reminder: Reminder, context: Context) {
        viewModelScope.launch {
            _updateState.value = NetworkResult.Loading
            cancelAlarms(context, reminder.id)
            val result = reminderRepository.updateReminder(reminder)
            _updateState.value = result
            if (result is NetworkResult.Success) {
                scheduleAlarms(context, result.data)
                loadReminders()
            }
        }
    }

    /** Delete a reminder and cancel its alarms */
    fun deleteReminder(reminderId: String, context: Context) {
        viewModelScope.launch {
            _deleteState.value = NetworkResult.Loading
            cancelAlarms(context, reminderId)
            _deleteState.value = reminderRepository.deleteReminder(reminderId)
            loadReminders()
        }
    }

    fun resetSaveState() { _saveState.value = NetworkResult.Idle }
    fun resetUpdateState() { _updateState.value = NetworkResult.Idle }
    fun resetDeleteState() { _deleteState.value = NetworkResult.Idle }

    // ═══════════════════════════════════════════════════
    // Alarm Scheduling
    // ═══════════════════════════════════════════════════

    private val dayNameToCalendarDay = mapOf(
        "Sun" to Calendar.SUNDAY, "Mon" to Calendar.MONDAY,
        "Tue" to Calendar.TUESDAY, "Wed" to Calendar.WEDNESDAY,
        "Thu" to Calendar.THURSDAY, "Fri" to Calendar.FRIDAY,
        "Sat" to Calendar.SATURDAY,
    )

    /**
     * Schedule alarms for a reminder.
     * For each day × each time, schedule alarms for the duration.
     * Schedules for TODAY if today's day matches and the time hasn't passed yet.
     */
    fun scheduleAlarms(context: Context, reminder: Reminder) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val canScheduleExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else true

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

                // Find nearest occurrence of this day (today or forward)
                if (cal.get(Calendar.DAY_OF_WEEK) != calendarDay) {
                    while (cal.get(Calendar.DAY_OF_WEEK) != calendarDay) {
                        cal.add(Calendar.DAY_OF_MONTH, 1)
                    }
                } else if (cal.timeInMillis <= now) {
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
                                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pendingIntent)
                            } else {
                                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pendingIntent)
                            }
                            Log.d(TAG, "Scheduled: ${reminder.medicineName} at $timeStr on $dayName, trigger=${cal.timeInMillis}, rc=$requestCode")
                        } catch (e: SecurityException) {
                            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pendingIntent)
                        }
                    }
                    cal.add(Calendar.WEEK_OF_YEAR, 1)
                }
            }
        }
        Log.d(TAG, "Finished scheduling alarms for: ${reminder.medicineName}")
    }

    /** Cancel all alarms for a reminder */
    private fun cancelAlarms(context: Context, reminderId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val baseCode = reminderId.hashCode() and 0x00FFFFFF
        for (i in 0 until 500) {
            val intent = Intent(context, ReminderAlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context, baseCode + i, intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()
            }
        }
        Log.d(TAG, "Cancelled alarms for reminder: $reminderId")
    }
}
