package com.asokolov.stickers.activities

import android.graphics.Rect
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View

class SpaceDividerDecorator(private val sizePx: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        val manager = parent.layoutManager
        when (manager) {
            is GridLayoutManager -> manager.applyOffsets(outRect, view, parent)
            is LinearLayoutManager -> manager.applyOffsets(outRect, view, parent)
        }
    }

    private fun GridLayoutManager.applyOffsets(outRect: Rect, view: View, parent: RecyclerView) {
        val halfSize = sizePx / 2
        val leftOffset = if (isFirstItemInRow(view, parent, this)) 0 else halfSize
        val rightOffset = if (isLastItemInRow(view, parent, this)) 0 else halfSize
        outRect.set(leftOffset, 0, rightOffset, sizePx)
    }

    private fun isFirstItemInRow(view: View, parent: RecyclerView, manager: GridLayoutManager): Boolean {
        val naturalPosition = parent.getChildAdapterPosition(view) + 1
        return naturalPosition % manager.spanCount == 1
    }

    private fun isLastItemInRow(view: View, parent: RecyclerView, manager: GridLayoutManager): Boolean {
        val naturalPosition = parent.getChildAdapterPosition(view) + 1
        return naturalPosition % manager.spanCount == 0
    }

    private fun LinearLayoutManager.applyOffsets(outRect: Rect, view: View, parent: RecyclerView) {
        if (isLastItem(view, parent)) {
            return
        }
        when (orientation) {
            LinearLayoutManager.VERTICAL -> outRect.set(0, 0, 0, sizePx)
            LinearLayoutManager.HORIZONTAL -> outRect.set(0, 0, sizePx, 0)
            else -> throw IllegalStateException("Invalid layout orientation")
        }
    }

    private fun isLastItem(view: View, parent: RecyclerView): Boolean {
        return parent.getChildAdapterPosition(view) == parent.adapter.itemCount - 1
    }
}