package com.trowical.arcamera.ui.custom

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Size
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.trowical.arcamera.R

class GridLinesView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    companion object {
        const val ANIM_DURATION = 250L
        const val GRID_X = 3f
        const val LINES = 2f
        const val HEIGHT_NAV_BAR = 200f
        const val OFFSET = 100f
    }

    private enum class Position {
        V_RIGHT, V_LEFT, H_TOP, H_BOTTOM
    }

    private var vRightValue = -OFFSET
    private var vLeftValue = getScreenSize().width.toFloat() + OFFSET
    private var hTopValue = -OFFSET
    private var vBottomValue = getScreenSize().height.toFloat() + OFFSET

    init {
        isClickable = false
        isFocusable = false
    }

    private val paint: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            strokeWidth = 1f
            style = Paint.Style.STROKE
            color = Color.argb(255, 255, 255, 255)
        }
    }

    private var gridIsVisible: Boolean = false
        set(value) {
            field = value
            startTranslateAnim()
        }

    /**
     * get value animator
     */
    private fun getValueAnim(
        startValue: Float,
        endValue: Float,
        position: Position
    ) = ValueAnimator.ofFloat(
        startValue,
        endValue
    ).apply {
        interpolator = DecelerateInterpolator()
        duration = ANIM_DURATION
        if (!gridIsVisible) {
            reverse()
        }
        addUpdateListener {
            val value = it.animatedValue as Float
            when (position) {
                Position.V_RIGHT -> vRightValue = value
                Position.V_LEFT -> vLeftValue = value
                Position.H_TOP -> hTopValue = value
                Position.H_BOTTOM -> vBottomValue = value
            }
            invalidate()
        }
    }

    /**
     * start animation
     */
    private fun startTranslateAnim() {
        val size = getScreenSize()
        val height = size.height.toFloat()
        val width = size.width.toFloat()
        val vRightAnim = getValueAnim(
            width + OFFSET, (width / GRID_X) * LINES, Position.V_RIGHT
        )
        if (!vRightAnim.isStarted) {
            vRightAnim.start()
        }
        ///////////////////
        val vLeftAnim = getValueAnim(
            -OFFSET, width / GRID_X, Position.V_LEFT
        )
        if (!vLeftAnim.isStarted) {
            vLeftAnim.start()
        }
        ///////////////////
        val hTopAnim = getValueAnim(
            -OFFSET, height / GRID_X, Position.H_TOP
        )
        if (!hTopAnim.isStarted) {
            hTopAnim.start()
        }
        ///////////////////
        val hBottomAnim = getValueAnim(
            height + OFFSET, (height / GRID_X) * LINES, Position.H_BOTTOM
        )
        if (!hBottomAnim.isStarted) {
            hBottomAnim.start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawGridLines(canvas)
    }

    private fun getScreenSize(): Size {
        val metrics = resources.displayMetrics
        return Size(metrics.widthPixels, metrics.heightPixels)
    }

    /**
     * draw grid lines
     */
    private fun drawGridLines(canvas: Canvas) {
        val size = getScreenSize()
        val height = size.height.toFloat()
        val width = size.width.toFloat()
        val vStopY: Float = (height + HEIGHT_NAV_BAR)
        canvas.drawLine(vRightValue, 0f, vRightValue, vStopY, paint)
        canvas.drawLine(vLeftValue, 0f, vLeftValue, vStopY, paint)
        canvas.drawLine(0f, hTopValue, width, hTopValue, paint)
        canvas.drawLine(0f, vBottomValue, width, vBottomValue, paint)
    }

    /**
     * show-hide grid lines
     */
    fun toggleGridLines(notifyIcon: (icon: Int) -> Unit) {
        gridIsVisible = !gridIsVisible
        val icon = if (gridIsVisible)
            R.drawable.ic_grid_off else
            R.drawable.ic_grid_on
        notifyIcon(icon)
    }

}