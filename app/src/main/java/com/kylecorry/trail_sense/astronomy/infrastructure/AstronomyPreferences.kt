package com.kylecorry.trail_sense.astronomy.infrastructure

import android.content.Context
import com.kylecorry.andromeda.core.toIntCompat
import com.kylecorry.andromeda.preferences.BooleanPreference
import com.kylecorry.sol.science.astronomy.SunTimesMode
import com.kylecorry.trail_sense.R
import com.kylecorry.trail_sense.shared.QuickActionType
import com.kylecorry.trail_sense.shared.preferences.PreferencesSubsystem
import java.time.LocalDate
import java.time.LocalTime

class AstronomyPreferences(private val context: Context) {

    private val cache by lazy { PreferencesSubsystem.getInstance(context).preferences }

    val sunTimesMode: SunTimesMode
        get() {
            return when (val mode = cache.getString(R.string.pref_sun_time_mode.toString())) {
                "civil" -> SunTimesMode.Civil
                "nautical" -> SunTimesMode.Nautical
                "astronomical" -> SunTimesMode.Astronomical
                else -> SunTimesMode.Actual
            }
        }

    val centerSunAndMoon: Boolean
        get() {
            return cache.getBoolean(R.string.pref_center_sun_and_moon.toString()) ?: false
        }

    val showOnCompass: Boolean
        get() {
            val raw = cache.getString(R.string.pref_show_sun_moon_compass.toString()) ?: "never"
            return raw != "never"
        }

    val showOnCompassWhenDown: Boolean
        get() {
            val raw = cache.getString(R.string.pref_show_sun_moon_compass.toString()) ?: "never"
            return raw == "always"
        }

    var sendSunsetAlerts by BooleanPreference(
        cache,
        R.string.pref_sunset_alerts.toString(),
        false
    )

    val sendAstronomyAlerts: Boolean
        get() {
            return sendLunarEclipseAlerts || sendMeteorShowerAlerts || sendSolarEclipseAlerts
        }

    // TODO: Let the user set this
    var astronomyAlertTime: LocalTime = LocalTime.of(10, 0)

    val sendLunarEclipseAlerts by BooleanPreference(
        cache,
        R.string.pref_send_lunar_eclipse_alerts.toString(),
        false
    )

    val sendSolarEclipseAlerts by BooleanPreference(
        cache,
        R.string.pref_send_solar_eclipse_alerts.toString(),
        false
    )

    val sendMeteorShowerAlerts by BooleanPreference(
        cache,
        R.string.pref_send_meteor_shower_alerts.toString(),
        false
    )

    val sunsetAlertMinutesBefore: Long
        get() {
            return (cache.getString(R.string.pref_sunset_alert_time.toString()) ?: "60").toLong()
        }

    val sunsetAlertLastSent: LocalDate
        get() {
            val raw = (cache.getString("sunset_alert_last_sent_date") ?: LocalDate.MIN.toString())
            return LocalDate.parse(raw)
        }

    fun setSunsetAlertLastSentDate(date: LocalDate) {
        cache.putString("sunset_alert_last_sent_date", date.toString())
    }

    val leftButton: QuickActionType
        get() {
            val id = cache.getString(R.string.pref_astronomy_quick_action_left.toString())?.toIntCompat()
            return QuickActionType.values().firstOrNull { it.id == id } ?: QuickActionType.Flashlight
        }

    val rightButton: QuickActionType
        get() {
            val id = cache.getString(R.string.pref_astronomy_quick_action_right.toString())?.toIntCompat()
            return QuickActionType.values().firstOrNull { it.id == id } ?: QuickActionType.SunsetAlert
        }
}
