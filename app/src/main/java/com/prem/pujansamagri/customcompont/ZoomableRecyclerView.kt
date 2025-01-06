package com.prem.pujansamagri.customcompont

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.ScaleGestureDetector
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView


/**
 * This is not used but can be used in parchaCanvas activity for zoom effect on recyclerview
 * **/
class ZoomableRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : RecyclerView(context, attrs) {

    private var scaleFactor = 1.0f
    private val scaleGestureDetector = ScaleGestureDetector(context, ScaleListener())
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var offsetX = 0f
    private var offsetY = 0f
    private val gestureDetector = GestureDetector(context, GestureListener())

    init {
        setWillNotDraw(false)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(event)
        gestureDetector.onTouchEvent(event) // Detect double-tap here

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchX = event.x
                lastTouchY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                if (scaleFactor > 1f) {
                    val deltaX = event.x - lastTouchX
                    val deltaY = event.y - lastTouchY
                    offsetX += deltaX
                    offsetY += deltaY
                    lastTouchX = event.x
                    lastTouchY = event.y

                    // Apply translation to the RecyclerView
                    translationX = offsetX
                    translationY = offsetY
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                // Handle any reset or additional logic if needed
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: android.graphics.Canvas) {
        canvas.save()
        canvas.scale(scaleFactor, scaleFactor, width / 2f, height / 2f)
        canvas.translate(offsetX, offsetY)  // Apply translation
        super.onDraw(canvas)
        canvas.restore()
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val previousScale = scaleFactor
            scaleFactor *= detector.scaleFactor

            // Limit the scale factor to prevent excessive zoom
            scaleFactor = scaleFactor.coerceIn(0.5f, 2.0f)

            // Apply scaling
            scaleX = scaleFactor
            scaleY = scaleFactor

            // Invalidate to redraw with new scale
            invalidate()
            return true
        }
    }

    private inner class GestureListener :
        GestureDetector.SimpleOnGestureListener() {
        override fun onDoubleTap(e: MotionEvent): Boolean {
            // Reset the scale and translation when double-tapped
            resetZoomAndTranslation()
            return true
        }
    }

    // Function to reset the zoom and translation
    private fun resetZoomAndTranslation() {
        scaleFactor = 1.0f
        offsetX = 0f
        offsetY = 0f

        scaleX = scaleFactor
        scaleY = scaleFactor
        translationX = offsetX
        translationY = offsetY

        invalidate()
    }
}