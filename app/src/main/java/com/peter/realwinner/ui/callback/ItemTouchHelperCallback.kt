package com.peter.realwinner.ui.callback

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.max
import kotlin.math.min

class ItemTouchHelperCallback(private val listener: ItemTouchHelperListener): ItemTouchHelper.Callback() {

    sealed class ButtonState {
        object GONE : ButtonState()
        object LEFT : ButtonState()
        object RIGHT : ButtonState()
    }

    companion object {
        private const val BUTTON_WIDTH = 200f
    }

    private var swipeBack = false
    private var buttonState: ButtonState = ButtonState.GONE
    private var buttonInstance: RectF? = null
    private var currentItemViewHolder: RecyclerView.ViewHolder? = null

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun isLongPressDragEnabled(): Boolean {
        return false
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//        if (direction == ItemTouchHelper.LEFT) {
//            listener.onItemDismiss(viewHolder.adapterPosition)
//        }
    }

    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
        if (swipeBack) {
            swipeBack = buttonState != ButtonState.GONE;
            return 0
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection)
    }

    fun onDraw(c: Canvas) {
        currentItemViewHolder?.let {
            drawButtons(c, it)
        }
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        if (actionState == ACTION_STATE_SWIPE) {
            if (buttonState != ButtonState.GONE) {
                val newDx = when (buttonState) {
                    ButtonState.RIGHT -> min(dX, -BUTTON_WIDTH)
                    ButtonState.LEFT -> max(dX, BUTTON_WIDTH)
                    else -> dX
                }
                super.onChildDraw(c, recyclerView, viewHolder, newDx, dY, actionState, isCurrentlyActive)
            } else {
                setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
        if (buttonState == ButtonState.GONE) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }

        currentItemViewHolder = viewHolder
    }

    private fun drawButtons(c: Canvas, viewHolder: RecyclerView.ViewHolder) {
        val buttonWidthWithoutPadding = BUTTON_WIDTH - 20f
        val corners = 16f
        val itemView = viewHolder.itemView
        val p = Paint()
        val leftButton = RectF(itemView.left.toFloat(), itemView.top.toFloat(), itemView.left.toFloat() + buttonWidthWithoutPadding, itemView.bottom.toFloat())
        p.color = Color.BLUE
        c.drawRoundRect(leftButton, corners, corners, p)
        drawText("EDIT", c, leftButton, p)

        val rightButton = RectF(itemView.right.toFloat() - buttonWidthWithoutPadding, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
        p.color = Color.RED
        c.drawRoundRect(rightButton, corners, corners, p)
        drawText("DELETE", c, rightButton, p)

        buttonInstance = when (buttonState) {
            ButtonState.RIGHT -> {
                rightButton
            }
            ButtonState.LEFT -> {
                leftButton
            }
            else -> {
                null
            }
        }
    }

    private fun drawText(text: String, c: Canvas, button: RectF, p: Paint) {
        val textSize = 32f
        p.color = Color.WHITE
        p.isAntiAlias = true
        p.textSize = textSize
        val textWidth = p.measureText(text)
        c.drawText(text, button.centerX() - (textWidth / 2), button.centerY() + (textSize / 2), p)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchListener(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dx: Float, dy: Float, actionState: Int, isCurrentlyActive: Boolean) {
        recyclerView.setOnTouchListener { v, event ->
            swipeBack = event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP
            if (swipeBack) {
                buttonState = when {
                    dx < -BUTTON_WIDTH -> {
                        ButtonState.RIGHT
                    }
                    dx > BUTTON_WIDTH -> {
                        ButtonState.LEFT
                    }
                    else -> {
                        ButtonState.GONE
                    }
                }
                if (buttonState != ButtonState.GONE) {
                    setTouchDownListener(c, recyclerView, viewHolder, dx, dy, actionState, isCurrentlyActive)
                    setItemsClickable(recyclerView, false)
                }
            }
            false
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchDownListener(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dx: Float, dy: Float, actionState: Int, isCurrentlyActive: Boolean) {
        recyclerView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                setTouchUpListener(c, recyclerView, viewHolder, dx, dy, actionState, isCurrentlyActive)
            }
            false
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchUpListener(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dx: Float, dy: Float, actionState: Int, isCurrentlyActive: Boolean) {
        recyclerView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                super.onChildDraw(c, recyclerView, viewHolder, 0f, dy, actionState, isCurrentlyActive)
                recyclerView.setOnTouchListener { _, _ ->
                    false
                }
                setItemsClickable(recyclerView, true)
                swipeBack = false

                buttonState = ButtonState.GONE
                currentItemViewHolder = null
            }
            false
        }
    }

    private fun setItemsClickable(recyclerView: RecyclerView, isClickable: Boolean) {
        for (i in 0 until recyclerView.childCount) {
            recyclerView.getChildAt(i).isClickable = isClickable
        }
    }
}