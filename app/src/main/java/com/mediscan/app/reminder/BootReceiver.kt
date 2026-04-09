package com.mediscan.app.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mediscan.app.core.constants.ApiEndpoints
import com.mediscan.app.data.model.Reminder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val TAG = "BootReceiver"

/**
 * BroadcastReceiver that re-schedules all active reminders after device reboot.
 * AlarmManager alarms are cleared on reboot, so we need to re-register them.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        Log.d(TAG, "Boot completed — re-scheduling medicine reminders")

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            Log.w(TAG, "No logged-in user, skipping reminder reschedule")
            return
        }

        // Use a coroutine to fetch reminders from Firestore and reschedule
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val snapshot = FirebaseFirestore.getInstance()
                    .collection(ApiEndpoints.REMINDERS_COLLECTION)
                    .whereEqualTo("patientId", uid)
                    .whereEqualTo("isActive", true)
                    .get()
                    .await()

                val reminders = snapshot.toObjects(Reminder::class.java)
                val now = System.currentTimeMillis()

                reminders.forEach { reminder ->
                    val endDate = reminder.startDate + (reminder.timeDurationDays.toLong() * 24 * 60 * 60 * 1000)
                    if (endDate > now) {
                        // Re-create a temporary ViewModel-like scheduler
                        ReminderScheduler.scheduleAlarms(context, reminder)
                        Log.d(TAG, "Re-scheduled: ${reminder.medicineName}")
                    }
                }

                Log.d(TAG, "Re-scheduled ${reminders.size} active reminders")
            } catch (e: Exception) {
                Log.e(TAG, "Error re-scheduling reminders", e)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
