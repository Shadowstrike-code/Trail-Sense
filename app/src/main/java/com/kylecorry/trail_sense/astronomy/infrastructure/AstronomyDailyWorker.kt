package com.kylecorry.trail_sense.astronomy.infrastructure

import android.content.Context
import androidx.work.WorkerParameters
import com.kylecorry.andromeda.background.DailyWorker
import com.kylecorry.andromeda.background.IOneTimeTaskScheduler
import com.kylecorry.andromeda.background.OneTimeTaskSchedulerFactory
import com.kylecorry.trail_sense.astronomy.infrastructure.commands.AstronomyAlertCommand
import com.kylecorry.trail_sense.shared.UserPreferences
import com.kylecorry.trail_sense.shared.preferences.PreferencesSubsystem
import java.time.Duration
import java.time.LocalTime

class AstronomyDailyWorker(context: Context, params: WorkerParameters) : DailyWorker(
    context,
    params,
    wakelockDuration = Duration.ofSeconds(15),
    getPreferences = { PreferencesSubsystem.getInstance(context).preferences },
) {

    private val prefs: UserPreferences by lazy {
        UserPreferences(context)
    }

    override fun isEnabled(context: Context): Boolean {
        return prefs.astronomy.sendAstronomyAlerts
    }

    override fun getScheduledTime(context: Context): LocalTime {
        return prefs.astronomy.astronomyAlertTime
    }

    override suspend fun execute(context: Context) {
        AstronomyAlertCommand(context).execute()
    }

    companion object {
        private const val UNIQUE_ID = 72394823

        private fun getScheduler(context: Context): IOneTimeTaskScheduler {
            return OneTimeTaskSchedulerFactory(context).deferrable(
                AstronomyDailyWorker::class.java,
                UNIQUE_ID
            )
        }

        fun start(context: Context) {
            getScheduler(context).start()
        }

        fun stop(context: Context) {
            getScheduler(context).cancel()
        }
    }
}
