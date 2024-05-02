package com.trowical.arcamera.ui.custom

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs
import kotlin.math.sqrt

class SliderLayoutManager(
    context: Context,
    private val notifyPosition: (position: Int) -> Unit
) : LinearLayoutManager(context) {

    private var recyclerView: RecyclerView? = null
    private var lastPositionNotified = -1

    init {
        orientation = RecyclerView.HORIZONTAL
    }

    override fun onLayoutChildren(
        recycler: RecyclerView.Recycler, state: RecyclerView.State
    ) {
        super.onLayoutChildren(recycler, state)
        scaleView()
    }

    override fun scrollHorizontallyBy(
        dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State
    ): Int {
        scaleView()
        return super.scrollHorizontallyBy(dx, recycler, state)
    }

    /**
     * scale recycler view item
     */
    private fun scaleView() {
        postOnAnimation {
            val mid = width / 2.0f
            for (i in 0 until childCount) {
                //calculating the distance of the child from the center
                getChildAt(i)?.let { child ->
                    val childMid = (getDecoratedLeft(child) + getDecoratedRight(child)) / 2.0f
                    val distanceFromCenter = abs(mid - childMid)

                    //scaling formula
                    val scale = (1 - sqrt(
                        (distanceFromCenter / width * 0.33f)
                            .toDouble()
                    )).toFloat()
                    //set new scale to view
                    child.scaleX = scale
                    child.scaleY = scale
                }
            }
        }
    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        findPosition(state)
    }

    /**
     * find position and notify
     */
    private fun findPosition(state: Int) {
        if (state == RecyclerView.SCROLL_STATE_IDLE) {
            var minDistance = recyclerView!!.width
            var position = -1
            for (i in 0 until recyclerView!!.childCount) {
                val child = recyclerView!!.getChildAt(i)
                if (child != null) {
                    val childCenterX =
                        getDecoratedLeft(child) + (getDecoratedRight(child) - getDecoratedLeft(child)) / 2
                    val newDistance = abs(childCenterX - getCenterXRView())
                    if (newDistance < minDistance) {
                        minDistance = newDistance
                        position = recyclerView!!.getChildLayoutPosition(child)
                    }
                }
            }
            notifyNewPosition(position)
        }
    }

    /**
     * notify new position with optional action
     */
    private fun notifyNewPosition(position: Int, action: (() -> Unit)? = null) {
        if (position != lastPositionNotified) {
            action?.let { it() }
            notifyPosition(position)
            lastPositionNotified = position
        }
    }

    /**
     * get center x recycler view
     */
    private fun getCenterXRView() =
        (recyclerView!!.right - recyclerView!!.left) / 2 + recyclerView!!.left

    override fun onAttachedToWindow(view: RecyclerView) {
        super.onAttachedToWindow(view)
        this.recyclerView = view
        //add smart snapping
        recyclerView?.let {
            if (it.onFlingListener == null) {
                LinearSnapHelper().attachToRecyclerView(it)
            }
        }
    }

    /**
     * scroll to position effect
     */
    fun scrollToEffect(position: Int) {
        postOnAnimation {
            notifyNewPosition(position) {
                scrollToPositionWithOffset(position, 0)
            }
        }
    }

}