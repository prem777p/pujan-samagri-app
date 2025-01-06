package com.prem.pujansamagri.customcompont

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout
import kotlin.math.abs

class SynchronizedScrollBehavior(context: Context, attrs: AttributeSet):
    CoordinatorLayout.Behavior<View>(context, attrs) {

    private var appBarLayout: AppBarLayout? = null

    override fun layoutDependsOn( parent: CoordinatorLayout, child: View, dependency: View ): Boolean {
        // Make this behavior dependent on AppBarLayout
        if (dependency is AppBarLayout) {
            appBarLayout = dependency
            return true
        }
        return false
    }

    override fun onDependentViewChanged( parent: CoordinatorLayout, child: View, dependency: View ): Boolean {
        if (dependency is AppBarLayout) {
            val appBarHeight = dependency.height.toFloat()
            val totalScrollRange = dependency.totalScrollRange.toFloat()
            val currentScroll = abs(dependency.y) // Current scroll position of the AppBarLayout

            // Calculate the translation for the bottom navigation bar
            val progress = currentScroll / totalScrollRange
            child.translationY = appBarHeight * progress
            return true
        }
        return false
    }
}
