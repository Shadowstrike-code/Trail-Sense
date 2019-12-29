package com.kylecorry.survival_aid.altimeter

import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.anychart.AnyChartView
import com.kylecorry.survival_aid.R
import com.kylecorry.survival_aid.navigator.gps.GPS
import java.util.*
import com.anychart.AnyChart.area
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Cartesian
import com.anychart.enums.ScaleTypes
import com.kylecorry.survival_aid.navigator.gps.LocationMath
import com.kylecorry.survival_aid.toZonedDateTime
import com.kylecorry.survival_aid.weather.Barometer
import com.kylecorry.survival_aid.weather.BarometerAlarmReceiver
import com.kylecorry.survival_aid.weather.PressureHistory
import java.time.Duration
import java.time.Instant
import kotlin.math.roundToInt




class AltimeterFragment : Fragment(), Observer {

    private lateinit var barometer: Barometer
    private lateinit var gps: GPS

    private var lastGpsAltitude = 0.0
    private var lastGpsPressure = 0.0f
    private var gotGpsReading = false
    private var gotBarometerReading = false
    private var units = "meters"

    private var lastAltitude = 0.0
    private val ALTITUDE_SMOOTHING = 0.6

    private val CHART_DURATION = Duration.ofHours(12)

    private lateinit var altitudeTxt: TextView
    private lateinit var chart: AnyChartView

    private lateinit var areaChart: Cartesian

    private var chartInitialized = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_altimeter, container, false)

        barometer = Barometer(context!!)
        gps = GPS(context!!)

        altitudeTxt = view.findViewById(R.id.altitude)
        chart = view.findViewById(R.id.altitude_chart)

        return view
    }

    override fun onResume() {
        super.onResume()
        PressureHistory.addObserver(this)
        barometer.addObserver(this)
        barometer.start()

        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        units = prefs.getString(getString(R.string.pref_distance_units), "meters") ?: "meters"

        if (PressureHistory.readings.isEmpty()){
            BarometerAlarmReceiver.loadFromFile(context!!)
        }

        if (PressureHistory.readings.isNotEmpty()){
            lastGpsAltitude = PressureHistory.readings.last().altitude
            lastGpsPressure = PressureHistory.readings.last().reading
            lastAltitude = lastGpsAltitude
            gotGpsReading = true
            gotBarometerReading = true
            updateAltitude()
            createAltitudeChart()
        } else {
            gps.updateLocation {
                gps.updateLocation {
                    if (context != null) {
                        gotGpsReading = true
                        lastGpsAltitude = gps.altitude
                        lastAltitude = lastGpsAltitude
                        updateAltitude()
                        createAltitudeChart()
                    }
                }
            }
        }

    }

    override fun onPause() {
        super.onPause()
        barometer.stop()
        PressureHistory.deleteObserver(this)
        barometer.deleteObserver(this)
    }

    override fun update(o: Observable?, arg: Any?) {
        if (o == barometer){
            if (!gotBarometerReading) {
                lastGpsPressure = barometer.pressure
                gotBarometerReading = true
            }
            updateAltitude()
        }
        if (o == PressureHistory) {
            if (!chartInitialized) {
                createAltitudeChart()
            } else {
                updateAltimeterChartData()
            }
        }
    }

    private fun updateAltitude() {
        if (!gotGpsReading) return
        if (!gotBarometerReading) return
        if (barometer.pressure == 0.0f) return

        val altitude = getCalibratedAltitude(lastGpsAltitude.toFloat(), lastGpsPressure, barometer.pressure).toDouble()

        lastAltitude = (1 - ALTITUDE_SMOOTHING) * altitude + ALTITUDE_SMOOTHING * lastAltitude

        altitudeTxt.text = "${LocationMath.convertToBaseUnit(lastAltitude.toFloat(), units).roundToInt()} ${if (units == "meters") "m" else "ft"}"
    }

    private fun getCalibratedAltitude(gpsAltitude: Float, pressureAtGpsAltitude: Float, currentPressure: Float): Float {
        val gpsBarometricAltitude = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressureAtGpsAltitude)
        val currentBarometricAltitude = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, currentPressure)
        val change = currentBarometricAltitude - gpsBarometricAltitude
        return gpsAltitude + change
    }

    private fun createAltitudeChart(){
        areaChart = area()
        areaChart.credits().enabled(false)
        areaChart.animation(true)
        areaChart.title("Altitude History")
        updateAltimeterChartData()
        areaChart.yAxis(0).title(false)
        areaChart.yScale().ticks().interval(10)

//        areaChart.yScale().softMinimum(0)
        areaChart.xScale(ScaleTypes.DATE_TIME)

        val readings = PressureHistory.readings.filter { Duration.between(it.time, Instant.now()) < CHART_DURATION }

        if (readings.size >= 2){
            val totalTime = Duration.between(readings.first().time, readings.last().time)
            var hours = totalTime.toHours()
            val minutes = totalTime.toMinutes() - hours * 60

            when (hours) {
                0L -> areaChart.xAxis(0  ).title("$minutes minute${if (minutes == 1L) "" else "s"}")
                else -> {
                    if (minutes >= 30) hours++
                    areaChart.xAxis(0  ).title("$hours hour${if (hours == 1L) "" else "s"}")
                }
            }

        }

        areaChart.xAxis(0).labels().enabled(false)
        areaChart.getSeriesAt(0).color(String.format("#%06X", 0xFFFFFF and resources.getColor(R.color.colorPrimary, null)))
        chart.setChart(areaChart)
        chartInitialized = true
    }

    private fun updateAltimeterChartData(){
        val seriesData = mutableListOf<DataEntry>()

        if (PressureHistory.readings.isEmpty()){
            BarometerAlarmReceiver.loadFromFile(context!!)
        }

        val readings = PressureHistory.readings.filter { Duration.between(it.time, Instant.now()) < CHART_DURATION }

        var referenceReading = readings.first()

        readings.forEach {
            val date = it.time.toZonedDateTime()
            if (Duration.between(referenceReading.time, it.time) >= Duration.ofMinutes(31)){
                referenceReading = it
            }

            seriesData.add(
                PressureDataEntry(
                    (date.toEpochSecond() + date.offset.totalSeconds) * 1000,
                    LocationMath.convertToBaseUnit(getCalibratedAltitude(referenceReading.altitude.toFloat(), referenceReading.reading, it.reading), units)
                )
            )
        }
        areaChart.data(seriesData)
    }

    private inner class PressureDataEntry internal constructor(
        x: Number,
        value: Number
    ) : ValueDataEntry(x, value)
}
