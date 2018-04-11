package com.asokolov.stickers.utils

import android.graphics.PointF

object MathUtils {

    fun pointInTriangle(pt: PointF, v1: PointF,
                        v2: PointF, v3: PointF): Boolean {

        val b1 = crossProduct(pt, v1, v2) < 0.0f
        val b2 = crossProduct(pt, v2, v3) < 0.0f
        val b3 = crossProduct(pt, v3, v1) < 0.0f

        return b1 == b2 && b2 == b3
    }

    private fun crossProduct(a: PointF, b: PointF, c: PointF): Float {
        return crossProduct(a.x, a.y, b.x, b.y, c.x, c.y)
    }

    private fun crossProduct(ax: Float, ay: Float, bx: Float, by: Float, cx: Float, cy: Float): Float {
        return (ax - cx) * (by - cy) - (bx - cx) * (ay - cy)
    }
}