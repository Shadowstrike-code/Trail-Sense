package com.kylecorry.trail_sense.weather.infrastructure.commands

import android.content.Context
import com.kylecorry.trail_sense.R
import com.kylecorry.trail_sense.shared.UserPreferences
import com.kylecorry.trail_sense.shared.alerts.IDismissibleAlerter
import com.kylecorry.trail_sense.shared.commands.generic.Command
import com.kylecorry.trail_sense.shared.preferences.Flag
import com.kylecorry.trail_sense.shared.preferences.PreferencesFlag
import com.kylecorry.trail_sense.shared.preferences.PreferencesSubsystem
import com.kylecorry.trail_sense.weather.domain.CurrentWeather
import com.kylecorry.trail_sense.weather.domain.WeatherAlert
import com.kylecorry.trail_sense.weather.infrastructure.IWeatherPreferences
import com.kylecorry.trail_sense.weather.infrastructure.alerts.StormAlerter

class StormAlertCommand(
    private val justShownFlag: Flag,
    private val prefs: IWeatherPreferences,
    private val alerter: IDismissibleAlerter
) : Command<CurrentWeather> {

    override fun execute(weather: CurrentWeather) {
        val sentAlert = justShownFlag.get()

        if (weather.prediction.alerts.contains(WeatherAlert.Storm)) {
            val shouldSend = prefs.sendStormAlerts && prefs.shouldMonitorWeather
            if (shouldSend && !sentAlert) {
                alerter.alert()
                justShownFlag.set(true)
            }
        } else {
            alerter.dismiss()
            justShownFlag.set(false)
        }
    }

    companion object {
        fun create(context: Context): StormAlertCommand {
            val prefs = PreferencesSubsystem.getInstance(context).preferences
            return StormAlertCommand(
                PreferencesFlag(prefs, context.getString(R.string.pref_just_sent_alert)),
                UserPreferences(context).weather,
                StormAlerter(context)
            )
        }

    }

}