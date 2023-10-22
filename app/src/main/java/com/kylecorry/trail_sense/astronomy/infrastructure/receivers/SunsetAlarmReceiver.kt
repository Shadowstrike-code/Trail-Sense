package com.kylecorry.trail_sense.astronomy.infrastructure.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.kylecorry.andromeda.background.IOneTimeTaskScheduler
import com.kylecorry.andromeda.background.OneTimeTaskSchedulerFactory
import com.kylecorry.andromeda.fragments.IPermissionRequester
import com.kylecorry.trail_sense.astronomy.infrastructure.commands.SunsetAlarmCommand
import com.kylecorry.trail_sense.shared.UserPreferences
import com.kylecorry.trail_sense.shared.permissions.RequestBackgroundLocationCommand
import com.kylecorry.trail_sense.shared.permissions.requestScheduleExactAlarms
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SunsetAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return
        val shouldSend = UserPreferences(context).astronomy.sendSunsetAlerts
        if (!shouldSend) {
            return
        }

        val pendingResult = goAsync()

        val command = SunsetAlarmCommand(context.applicationContext)
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            try {
                command.execute()
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {

        private const val PI_ID = 8309

        fun scheduler(context: Context): IOneTimeTaskScheduler {
            return OneTimeTaskSchedulerFactory(context).exact(
                SunsetAlarmReceiver::class.java,
                PI_ID
            )
        }

        fun start(context: Context) {
            context.sendBroadcast(Intent(context, SunsetAlarmReceiver::class.java))
        }

        fun enable(
            fragment: Fragment,
            shouldRequestPermissions: Boolean
        ) {
            val preferences = UserPreferences(fragment.requireContext())
            preferences.astronomy.sendSunsetAlerts = true

            preferences.astronomy.isSunsetAlertEnabled = true

            if (shouldRequestPermissions) {
                fragment.requestScheduleExactAlarms {
                    start(fragment.requireContext())
                    RequestBackgroundLocationCommand(fragment).execute()
                }
            } else {
                start(fragment.requireContext())
            }
        }

        fun disable(
            fragment: Fragment
        ) {
            val preferences = UserPreferences(fragment.requireContext())
            preferences.astronomy.sendSunsetAlerts = false

            preferences.astronomy.isSunsetAlertEnabled = false

            val scheduler = scheduler(fragment.requireContext())
            scheduler.cancel()
        }

        fun isSunsetAlertEnabled(context: Context): Boolean {
            val preferences = UserPreferences(context)
            return preferences.astronomy.sendSunsetAlerts && preferences.astronomy.isSunsetAlertEnabled
        }
    }
}
