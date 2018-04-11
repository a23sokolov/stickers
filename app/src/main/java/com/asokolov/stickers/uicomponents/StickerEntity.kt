package com.asokolov.stickers.uicomponents

import android.graphics.*
import android.support.annotation.IntRange
import com.asokolov.stickers.utils.MathUtils

// todo: add possibility to rotate text too, isolate the same logic
class StickerEntity(val layer: Layer,
                    private val bitmap: Bitmap,
                    @field:IntRange(from = 0)
                    protected var canvasWidth: Int,
                    @param:IntRange(from = 1)
                    @field:IntRange(from = 0)
                    protected var canvasHeight: Int,
                    var isSelected: Boolean = false,

                    private val matrix: Matrix = Matrix(),
                    private val width: Int = bitmap.width,
                    private val height: Int = bitmap.height,
                    private var holyScale: Float = 0.toFloat(),
                    private val destPoints: FloatArray = FloatArray(10),
                    private val srcPoints: FloatArray = FloatArray(10),

                    private var borderPaint: Paint = Paint(),
                    private val pA: PointF = PointF(),
                    private val pB: PointF = PointF(),
                    private val pC: PointF = PointF(),
                    private val pD: PointF = PointF()
) {

    init {
        val widthAspect = 1.0f * canvasWidth / width
        val heightAspect = 1.0f * canvasHeight / height
        // fit the smallest size
        holyScale = Math.min(widthAspect, heightAspect)

        // initial position of the sticker
        srcPoints[0] = 0F
        srcPoints[1] = 0F
        srcPoints[2] = width.toFloat()
        srcPoints[3] = 0F
        srcPoints[4] = width.toFloat()
        srcPoints[5] = height.toFloat()
        srcPoints[6] = 0F
        srcPoints[7] = height.toFloat()
        srcPoints[8] = 0F
        srcPoints[8] = 0F
    }

    fun drawContent(canvas: Canvas, drawingPaint: Paint?) {
        canvas.drawBitmap(bitmap, matrix, drawingPaint)
    }

    fun release() {
        if (!bitmap.isRecycled) {
            bitmap.recycle()
        }
    }

    //    http://gamedev.stackexchange.com/questions/29260/transform-matrix-multiplication-order
    protected fun updateMatrix() {
        matrix.reset()

        val topLeftX = layer.x * canvasWidth
        val topLeftY = layer.y * canvasHeight

        val centerX = topLeftX + width.toFloat() * holyScale * 0.5f
        val centerY = topLeftY + height.toFloat() * holyScale * 0.5f

        var rotationInDegree = layer.rotationInDegrees
        var scaleX = layer.scale
        val scaleY = layer.scale
        if (layer.isFlipped) {
            rotationInDegree *= -1.0f
            scaleX *= -1.0f
        }

        matrix.preScale(scaleX, scaleY, centerX, centerY)
        matrix.preRotate(rotationInDegree, centerX, centerY)
        matrix.preTranslate(topLeftX, topLeftY)
        matrix.preScale(holyScale, holyScale)
    }

    fun absoluteCenterX(): Float {
        val topLeftX = layer.x * canvasWidth
        return topLeftX + width.toFloat() * holyScale * 0.5f
    }

    fun absoluteCenterY(): Float {
        val topLeftY = layer.y * canvasHeight

        return topLeftY + height.toFloat() * holyScale * 0.5f
    }

    fun absoluteCenter(): PointF {
        val topLeftX = layer.y * canvasWidth
        val topLeftY = layer.y * canvasHeight

        val centerX = topLeftX + width.toFloat() * holyScale * 0.5f
        val centerY = topLeftY + height.toFloat() * holyScale * 0.5f

        return PointF(centerX, centerY)
    }

    fun moveToCanvasCenter() {
        moveCenterTo(PointF(canvasWidth * 0.5f, canvasHeight * 0.5f))
    }

    fun moveCenterTo(moveToCenter: PointF) {
        val currentCenter = absoluteCenter()
        layer.postTranslate(1.0f * (moveToCenter.x - currentCenter.x) / canvasWidth,
                1.0f * (moveToCenter.y - currentCenter.y) / canvasHeight)
    }

    fun pointInLayerRect(point: PointF): Boolean {

        updateMatrix()
        // map rect vertices
        matrix.mapPoints(destPoints, srcPoints)

        pA.x = destPoints[0]
        pA.y = destPoints[1]
        pB.x = destPoints[2]
        pB.y = destPoints[3]
        pC.x = destPoints[4]
        pC.y = destPoints[5]
        pD.x = destPoints[6]
        pD.y = destPoints[7]

        return MathUtils.pointInTriangle(point, pA, pB, pC) || MathUtils.pointInTriangle(point, pA, pD, pC)
    }

    fun draw(canvas: Canvas, drawingPaint: Paint?) {

        updateMatrix()

        canvas.save()

        drawContent(canvas, drawingPaint)

        if (isSelected) {
            val storedAlpha = borderPaint.alpha
            if (drawingPaint != null) {
                borderPaint.alpha = drawingPaint.alpha
            }
            drawSelectedBg(canvas)
            borderPaint.alpha = storedAlpha
        }

        canvas.restore()
    }

    private fun drawSelectedBg(canvas: Canvas) {
        matrix.mapPoints(destPoints, srcPoints)

        canvas.drawLines(destPoints, 0, 8, borderPaint)

        canvas.drawLines(destPoints, 2, 8, borderPaint)
    }

    @Throws(Throwable::class)
    protected fun finalize() {
        try {
            release()
        } finally {
            //ignore
        }
    }
}