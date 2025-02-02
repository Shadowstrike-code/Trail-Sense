package com.kylecorry.trail_sense.astronomy.ui.items

import android.content.Context
import com.kylecorry.andromeda.core.coroutines.onDefault
import com.kylecorry.ceres.list.ListItem
import com.kylecorry.ceres.list.ResourceListIcon
import com.kylecorry.sol.units.Coordinate
import com.kylecorry.trail_sense.R
import com.kylecorry.trail_sense.astronomy.ui.format.EclipseFormatter
import java.time.LocalDate

class SolarEclipseListItemProducer(context: Context) : BaseAstroListItemProducer(context) {

    override suspend fun getListItem(
        date: LocalDate,
        location: Coordinate
    ): ListItem? = onDefault {
        val eclipse = astronomyService.getSolarEclipse(location, date) ?: return@onDefault null

        // Advanced
        val peakAltitude = astronomyService.getSunAltitude(location, eclipse.peak)
        val peakAzimuth = astronomyService.getSunAzimuth(location, eclipse.peak)

        list(
            5,
            context.getString(R.string.solar_eclipse),
            EclipseFormatter.type(context, eclipse),
            ResourceListIcon(
                if (eclipse.isTotal) {
                    R.drawable.ic_total_solar_eclipse
                } else {
                    R.drawable.ic_partial_solar_eclipse
                }
            ),
            data = times(eclipse.start, eclipse.peak, eclipse.end, date)
        ) {
            val advancedData = listOf(
                context.getString(R.string.times) to times(eclipse.start, eclipse.peak, eclipse.end, date),
                context.getString(R.string.duration) to duration(eclipse.duration),
                context.getString(R.string.obscuration) to data(EclipseFormatter.type(context, eclipse)),
                context.getString(R.string.magnitude) to decimal(eclipse.magnitude, 2),
                context.getString(R.string.astronomy_altitude_peak) to degrees(peakAltitude),
                context.getString(R.string.astronomy_direction_peak) to direction(peakAzimuth)
            )

            showAdvancedData(context.getString(R.string.solar_eclipse), advancedData)
        }
    }


}