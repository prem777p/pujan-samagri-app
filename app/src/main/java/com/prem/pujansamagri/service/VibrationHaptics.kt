package com.prem.pujansamagri.service


import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator

class VibrationHaptics {
    companion object {
        fun vibration(context: Context) {
            context.getSystemService(Vibrator::class.java)
                .vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
        }
    }
}