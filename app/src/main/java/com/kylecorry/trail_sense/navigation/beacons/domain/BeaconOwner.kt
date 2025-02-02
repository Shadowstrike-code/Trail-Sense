package com.kylecorry.trail_sense.navigation.beacons.domain

enum class BeaconOwner(val id: Int) {
    User(0),
    Path(1),
    CellSignal(2),
    Maps(3),
    Triangulate(4)
}