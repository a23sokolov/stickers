package com.asokolov.stickers.activities.stickers

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.asokolov.stickers.R

class StickersAdapter(
        context: Context,
        private val stickers: List<StickerObject>,
        private val onStickerClickCallback: (stickerAssetName: String) -> Unit
) : RecyclerView.Adapter<StickersAdapter.StickerViewHolder>() {
    private val layoutInflater: LayoutInflater

    init {
        this.layoutInflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = StickerViewHolder(layoutInflater.inflate(R.layout.sticker_item, parent, false))

    override fun onBindViewHolder(holder: StickerViewHolder, position: Int) {
        with(holder) {
            stickerImage.setImageDrawable(getItem(position).drawable)
            stickerImage.setOnClickListener({
                val pos = adapterPosition
                if (pos != android.support.v7.widget.RecyclerView.NO_POSITION) {
                    onStickerClickCallback.invoke(getItem(pos).assetName)
                }
            })
        }
    }

    override fun getItemCount() = stickers.size

    private fun getItem(position: Int) = stickers[position]


    class StickerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val stickerImage: ImageView = itemView.findViewById(R.id.stickerImage)
    }
}
