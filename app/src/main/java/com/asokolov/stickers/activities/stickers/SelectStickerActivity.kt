package com.asokolov.stickers.activities.stickers

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.MenuItem
import com.asokolov.stickers.R
import com.asokolov.stickers.activities.SpaceDividerDecorator
import kotlinx.android.synthetic.main.activity_stickers.*

class StickerObject(val assetName: String, val drawable: Drawable)

class StickerSelectActivity : AppCompatActivity() {

    val stickers by lazy(LazyThreadSafetyMode.NONE) {
        listOf(
                StickerObject("stickers/1.png", Drawable.createFromStream(assets.open("stickers/1.png"), null)),
                StickerObject("stickers/2.png", Drawable.createFromStream(assets.open("stickers/2.png"), null)),
                StickerObject("stickers/3.png", Drawable.createFromStream(assets.open("stickers/3.png"), null)),
                StickerObject("stickers/4.png", Drawable.createFromStream(assets.open("stickers/4.png"), null)),
                StickerObject("stickers/5.png", Drawable.createFromStream(assets.open("stickers/5.png"), null)),
                StickerObject("stickers/6.png", Drawable.createFromStream(assets.open("stickers/6.png"), null)),
                StickerObject("stickers/7.png", Drawable.createFromStream(assets.open("stickers/7.png"), null)),
                StickerObject("stickers/8.png", Drawable.createFromStream(assets.open("stickers/8.png"), null)),
                StickerObject("stickers/9.png", Drawable.createFromStream(assets.open("stickers/9.png"), null))
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stickers)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        initRecycler()
    }

    private fun initRecycler() {
        with(stickers_rv) {
            layoutManager
            isNestedScrollingEnabled = false
            layoutManager = GridLayoutManager(this@StickerSelectActivity, 3)
            addItemDecoration(SpaceDividerDecorator(resources.getDimensionPixelSize(R.dimen.color_item_divider)))
            adapter = StickersAdapter(this@StickerSelectActivity, stickers) { stickerAssetName ->
                val intent = Intent()
                intent.putExtra(EXTRA_STICKER_ASSET_NAME, stickerAssetName)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        val EXTRA_STICKER_ASSET_NAME = "extra_sticker_asset_name"
    }
}