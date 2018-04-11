package com.asokolov.stickers.uicomponents

import android.support.annotation.FloatRange

interface Limits {
    companion object {
        val MIN_SCALE = 0.06f
        val MAX_SCALE = 4.0f
        val INITIAL_STICKER_SCALE = 0.4f
    }
}

class Layer {

    @FloatRange(from = 0.0, to = 360.0)
    var rotationInDegrees: Float = 0.toFloat()

    var scale: Float = 0.toFloat()

    var x: Float = 0.toFloat()
    var y: Float = 0.toFloat()

    var isFlipped: Boolean = false

    protected val maxScale: Float
        get() = Limits.MAX_SCALE

    protected val minScale: Float
        get() = Limits.MIN_SCALE

    init {
        reset()
    }

    protected fun reset() {
        this.rotationInDegrees = 0.0f
        this.scale = 1.0f
        this.isFlipped = false
        this.x = 0.0f
        this.y = 0.0f
    }

    fun postScale(scaleDiff: Float) {
        val newVal = scale + scaleDiff
        if (newVal >= minScale && newVal <= maxScale) {
            scale = newVal
        }
    }

    fun postRotate(rotationInDegreesDiff: Float) {
        this.rotationInDegrees += rotationInDegreesDiff
        this.rotationInDegrees %= 360.0f
    }

    fun postTranslate(dx: Float, dy: Float) {
        this.x += dx
        this.y += dy
    }

    fun initialScale(): Float {
        return Limits.INITIAL_STICKER_SCALE
    }
}