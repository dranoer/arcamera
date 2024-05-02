package com.trowical.arcamera.ui.custom

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.LinearInterpolator
import androidx.appcompat.widget.AppCompatImageView
import com.trowical.arcamera.utils.px

class ActionButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs) {

    var listener: Listener? = null
    private var actionType = ActionType.NONE

    private enum class ActionType {
        NONE, CLICK, LONG_CLICK
    }

    interface Listener {
        fun onTakePhoto()
        fun onStartRecording()
        fun onRecordingFinished()
    }

    init {
        //set on click listener
        setOnClickListener {
            actionType = ActionType.CLICK
            listener?.onTakePhoto()
        }

        //set on long click listener
        setOnLongClickListener {
            actionType = ActionType.LONG_CLICK
            listener?.onStartRecording()
            strokeColorAnim(false)
            scaleAnim(false)
            true
        }
    }

    /**
     * animate stroke color
     */
    private fun strokeColorAnim(startRecording: Boolean = true) {
        postOnAnimation {
            val shape = this.background as GradientDrawable

            val start = if (startRecording) Color.RED else Color.WHITE
            val end = if (startRecording) Color.WHITE else Color.RED

            val animator = ValueAnimator.ofObject(ArgbEvaluator(), start, end).apply {
                duration = 200
                interpolator = LinearInterpolator()
                addUpdateListener { animation ->
                    val color = animation.animatedValue as Int
                    shape.setStroke(4.px, color)
                }
            }

            if (!animator.isStarted) {
                animator.start()
            }
        }
    }

    /**
     * animate scale
     */
    private fun scaleAnim(startRecording: Boolean = true) {
        postOnAnimation {
            val start = if (startRecording) 1.5f else 1.0f
            val end = if (startRecording) 1.0f else 1.5f

            val animator = ValueAnimator.ofFloat(start, end).apply {
                duration = 200
                interpolator = LinearInterpolator()
                addUpdateListener { animation ->
                    val scale = animation.animatedValue as Float
                    this@ActionButton.scaleX = scale
                    this@ActionButton.scaleY = scale
                }
            }

            if (!animator.isStarted) {
                animator.start()
            }
        }
    }

    /**
     * touch listener
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP &&
            actionType == ActionType.LONG_CLICK
        ) {
            actionType = ActionType.NONE
            listener?.onRecordingFinished()
            strokeColorAnim()
            scaleAnim()
        }
        return super.onTouchEvent(event)
    }

}