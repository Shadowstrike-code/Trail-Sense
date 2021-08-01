package com.kylecorry.trail_sense.navigation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kylecorry.qr.QRService
import com.kylecorry.trail_sense.databinding.FragmentBeaconQrShareBinding
import com.kylecorry.trailsensecore.domain.navigation.Beacon
import com.kylecorry.trailsensecore.infrastructure.system.UiUtils
import com.kylecorry.trailsensecore.infrastructure.view.BoundBottomSheetDialogFragment

class BeaconQRBottomSheet : BoundBottomSheetDialogFragment<FragmentBeaconQrShareBinding>() {

    private val qr = QRService()

    var beacon: Beacon? = null
        set(value) {
            field = value
            updateUI()
        }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUI()
    }

    private fun updateUI(){
        val beacon = this.beacon ?: return
        if (!isBound){
            return
        }
        binding.beaconName.text = beacon.name
        val encoded = "geo:${beacon.coordinate.latitude},${beacon.coordinate.longitude}"
        val size = UiUtils.dp(requireContext(), 250f).toInt()
        val bitmap = qr.encode(encoded, size, size)
        binding.beaconQr.setImageBitmap(bitmap)
    }


    override fun generateBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBeaconQrShareBinding {
        return FragmentBeaconQrShareBinding.inflate(layoutInflater, container, false)
    }

}