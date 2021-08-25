package com.kylecorry.trail_sense.shared.paths

import com.kylecorry.trailsensecore.domain.pixels.PixelLineStyle

class PathLineDrawerFactory {

    fun create(style: PixelLineStyle): IPathLineDrawerStrategy {
        return when (style) {
            PixelLineStyle.Solid -> SolidPathLineDrawerStrategy()
            PixelLineStyle.Dotted -> DottedPathLineDrawerStrategy()
            PixelLineStyle.Arrow -> ArrowPathLineDrawerStrategy()
        }
    }

}