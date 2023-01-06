package com.iodroid.ar_nav

import android.content.res.Configuration
import androidx.constraintlayout.widget.Guideline

object BindingAdapter {
    @JvmStatic
    @androidx.databinding.BindingAdapter("setOrientationBasedPercent")
    fun Guideline.setOrientationBasedPercent(orientation: Int) {
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            setGuidelinePercent(0.20f)
        } else {
            setGuidelinePercent(0.40f)
        }
    }
}