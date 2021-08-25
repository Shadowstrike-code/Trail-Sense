package com.kylecorry.trail_sense.tools.backtrack.domain

import androidx.annotation.ColorInt
import com.kylecorry.trailsensecore.domain.geo.PathPoint

class DefaultPointColoringStrategy(@ColorInt private val color: Int) : IPointColoringStrategy {
    override fun getColor(point: PathPoint): Int {
        return color
    }
}