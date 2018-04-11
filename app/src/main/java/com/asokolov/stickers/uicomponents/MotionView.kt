package com.asokolov.stickers.uicomponents

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.support.v4.view.GestureDetectorCompat
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View.OnTouchListener
import android.widget.FrameLayout
import com.almeros.android.multitouch.MoveGestureDetector
import com.almeros.android.multitouch.RotateGestureDetector
import java.util.*

interface MotionViewCallback {
    fun onStickerSelected(stickerEntity: StickerEntity)
    fun onStickerDoubleTap(stickerEntity: StickerEntity)
}

class MotionView : FrameLayout {

    // layers
    private val stickersEntities = ArrayList<StickerEntity>()
    var selectedSticker: StickerEntity? = null
        private set

    private var selectedLayerPaint: Paint? = null

    // callback
    private var motionViewCallback: MotionViewCallback? = null

    // gesture detection
    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private lateinit var rotateGestureDetector: RotateGestureDetector
    private lateinit var moveGestureDetector: MoveGestureDetector
    private lateinit var gestureDetectorCompat: GestureDetectorCompat

    private val onTouchListener = OnTouchListener { _, event ->

        scaleGestureDetector.onTouchEvent(event)
        rotateGestureDetector.onTouchEvent(event)
        moveGestureDetector.onTouchEvent(event)
        gestureDetectorCompat.onTouchEvent(event)
        true
    }

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        // hack
        setWillNotDraw(false)

        selectedLayerPaint = Paint()
        selectedLayerPaint!!.alpha = (255 * SELECTED_LAYER_ALPHA).toInt()
        selectedLayerPaint!!.isAntiAlias = true

        // init listeners
        with(this) {
            scaleGestureDetector = ScaleGestureDetector(context, ScaleListener())
            rotateGestureDetector = RotateGestureDetector(context, RotateListener())
            moveGestureDetector = MoveGestureDetector(context, MoveListener())
            gestureDetectorCompat = GestureDetectorCompat(context, TapsListener())
        }

        setOnTouchListener(onTouchListener)

        updateUI()
    }

    fun addStickerAndSetPosition(stickerEntity: StickerEntity) {
        initialTranslateAndScale(stickerEntity)
        stickersEntities.add(stickerEntity)
        selectSticker(stickerEntity, true)
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        if (selectedSticker != null) {
            selectedSticker!!.draw(canvas, selectedLayerPaint)
        }
    }

    override fun onDraw(canvas: Canvas) {
        drawAllEntities(canvas)
        super.onDraw(canvas)
    }

    private fun drawAllEntities(canvas: Canvas) {
        for (i in stickersEntities.indices) {
            stickersEntities[i].draw(canvas, null)
        }
    }

    private fun updateUI() {
        invalidate()
    }

    private fun handleTranslate(delta: PointF) {
        if (selectedSticker != null) {
            val newCenterX = selectedSticker!!.absoluteCenterX() + delta.x
            val newCenterY = selectedSticker!!.absoluteCenterY() + delta.y
            var needUpdateUI = false
            if (newCenterX >= 0 && newCenterX <= width) {
                selectedSticker!!.layer.postTranslate(delta.x / width, 0.0f)
                needUpdateUI = true
            }
            if (newCenterY >= 0 && newCenterY <= height) {
                selectedSticker!!.layer.postTranslate(0.0f, delta.y / height)
                needUpdateUI = true
            }
            if (needUpdateUI) {
                updateUI()
            }
        }
    }

    private fun initialTranslateAndScale(stickerEntity: StickerEntity) {
        stickerEntity.moveToCanvasCenter()
        stickerEntity.layer.scale = stickerEntity.layer.initialScale()
    }

    private fun selectSticker(newSelectedSticker: StickerEntity, updateCallback: Boolean) {
        if (selectedSticker != null) {
            selectedSticker!!.isSelected = false
        }
        newSelectedSticker.isSelected = true
        selectedSticker = newSelectedSticker
        invalidate()
        if (updateCallback && motionViewCallback != null) {
            motionViewCallback!!.onStickerSelected(newSelectedSticker)
        }
    }

    private fun findStickerAtPoint(x: Float, y: Float): StickerEntity? {
        var selectedSticker: StickerEntity? = null
        val p = PointF(x, y)
        for (i in stickersEntities.indices.reversed()) {
            if (stickersEntities[i].pointInLayerRect(p)) {
                selectedSticker = stickersEntities[i]
                break
            }
        }
        return selectedSticker
    }

    private fun updateSelectionOnTap(e: MotionEvent) {
        findStickerAtPoint(e.x, e.y)?.let {
            selectSticker(it, true)
        }
    }

    private fun updateOnLongPress(e: MotionEvent) {
        if (selectedSticker != null) {
            val p = PointF(e.x, e.y)
            if (selectedSticker!!.pointInLayerRect(p)) {
                bringLayerToFront(selectedSticker!!)
            }
        }
    }

    private fun bringLayerToFront(sticker: StickerEntity) {
        if (stickersEntities.remove(sticker)) {
            stickersEntities.add(sticker)
            invalidate()
        }
    }

    // overridden listeners
    private inner class TapsListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDoubleTap(e: MotionEvent): Boolean {
            if (motionViewCallback != null && selectedSticker != null) {
                motionViewCallback!!.onStickerDoubleTap(selectedSticker!!)
            }
            return true
        }

        override fun onLongPress(e: MotionEvent) {
            updateOnLongPress(e)
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            updateSelectionOnTap(e)
            return true
        }
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            if (selectedSticker != null) {
                val scaleFactorDiff = detector.scaleFactor
                selectedSticker!!.layer.postScale(scaleFactorDiff - 1.0f)
                updateUI()
            }
            return true
        }
    }

    private inner class RotateListener : RotateGestureDetector.SimpleOnRotateGestureListener() {
        override fun onRotate(detector: RotateGestureDetector): Boolean {
            if (selectedSticker != null) {
                selectedSticker!!.layer.postRotate(-detector.getRotationDegreesDelta())
                updateUI()
            }
            return true
        }
    }

    private inner class MoveListener : MoveGestureDetector.SimpleOnMoveGestureListener() {
        override fun onMove(detector: MoveGestureDetector): Boolean {
            handleTranslate(detector.getFocusDelta())
            return true
        }
    }

    companion object {
        val SELECTED_LAYER_ALPHA = 0.15f
    }
}
