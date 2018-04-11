package com.asokolov.stickers.activities.main

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.asokolov.stickers.uicomponents.Layer
import com.asokolov.stickers.R
import com.asokolov.stickers.activities.SpaceDividerDecorator
import com.asokolov.stickers.activities.stickers.StickerSelectActivity
import com.asokolov.stickers.uicomponents.StickerEntity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.backround_picker.*

class GradientColor(val baseColor: String,
                    val gradientColor: String,
                    var isPressed: Boolean = false,
                    val dimenPressed: Int,
                    val dimenNotPressed: Int)

class MainActivity : AppCompatActivity() {


    val pressedDimen by lazy(LazyThreadSafetyMode.NONE) {
        resources.getDimensionPixelSize(R.dimen.color_item_pressed)
    }
    val notPressedDimen by lazy(LazyThreadSafetyMode.NONE) {
        resources.getDimensionPixelSize(R.dimen.color_item_not_pressed)
    }
    val stickers by lazy(LazyThreadSafetyMode.NONE) {
        listOf(
                GradientColor("#be1313", "#c75000", false, pressedDimen, notPressedDimen),
                GradientColor("#3d60eb", "#2baeff", false, pressedDimen, notPressedDimen),
                GradientColor("#04a3c9", "#00deaf", false, pressedDimen, notPressedDimen),
                GradientColor("#d51082", "#ff4e3b", false, pressedDimen, notPressedDimen),
                GradientColor("#be1313", "#c75000", false, pressedDimen, notPressedDimen),
                GradientColor("#3d60eb", "#2baeff", false, pressedDimen, notPressedDimen),
                GradientColor("#04a3c9", "#00deaf", false, pressedDimen, notPressedDimen),
                GradientColor("#d51082", "#ff4e3b", false, pressedDimen, notPressedDimen),
                GradientColor("#be1313", "#c75000", false, pressedDimen, notPressedDimen),
                GradientColor("#3d60eb", "#2baeff", false, pressedDimen, notPressedDimen),
                GradientColor("#04a3c9", "#00deaf", false, pressedDimen, notPressedDimen),
                GradientColor("#d51082", "#ff4e3b", false, pressedDimen, notPressedDimen)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        with(backgroundItems) {
            layoutManager
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(SpaceDividerDecorator(resources.getDimensionPixelSize(R.dimen.color_item_divider)))
            adapter = ColorPickerAdapter(
                    stickers, { color ->
                motionView.setBackgroundColor(Color.parseColor(color.baseColor))
            }, {
                // fixme: add permission access to gallery, and take picture
                Toast.makeText(this@MainActivity, "Logic not added yet", Toast.LENGTH_SHORT).show()
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.main_add_sticker) {
            //todo: add bottomsheet fragment with custom height
            val intent = Intent(this, StickerSelectActivity::class.java)
            startActivityForResult(intent, SELECT_STICKER_REQUEST_CODE)
            return true
        } else if (item.itemId == R.id.main_add_text) {
            //todo: add logic to change text font
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_STICKER_REQUEST_CODE) {
                if (data != null) {
                    val stickerId = data.getStringExtra(StickerSelectActivity.EXTRA_STICKER_ASSET_NAME)
                    if (!stickerId.isNullOrBlank()) {
                        addSticker(stickerId)
                    }
                }
            }
        }
    }

    private fun addSticker(stickerPath: String) {
        motionView.post({
            val layer = Layer()
            val pica = BitmapFactory.decodeStream(assets.open(stickerPath))
            val stickerEntity = StickerEntity(layer, pica, motionView.getWidth(), motionView.getHeight())
            motionView.addStickerAndSetPosition(stickerEntity)
        })
    }

    companion object {
        const val SELECT_STICKER_REQUEST_CODE = 10
    }
}