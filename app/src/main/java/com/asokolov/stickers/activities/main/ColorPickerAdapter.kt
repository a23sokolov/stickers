package com.asokolov.stickers.activities.main

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.asokolov.stickers.R

class ColorPickerAdapter(
        private val items: List<GradientColor>,
        private val colorClicked: (color: GradientColor) -> Unit,
        private val anotherBackground: () -> Unit,
        var previewClickedPosition: Int = -1
) : RecyclerView.Adapter<ColorPickerAdapter.ChooseBackground>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        0 -> ChooseBackground.ColorView(
                LayoutInflater.from(parent.context).inflate(R.layout.color_item, parent, false)
        ).apply {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (previewClickedPosition != -1 && items[previewClickedPosition].isPressed && previewClickedPosition != position) {
                    items[previewClickedPosition].isPressed = false
                    notifyItemChanged(previewClickedPosition)
                }
                if (position != RecyclerView.NO_POSITION) {
                    previewClickedPosition = position
                    items[position].let { it.isPressed = !it.isPressed }
                    notifyItemChanged(position)
                    colorClicked.invoke(items[position])
                }
            }
        }
        else ->
            ChooseBackground.PickFromPhoto(
                    LayoutInflater.from(parent.context).inflate(R.layout.another_item, parent, false)
            ).apply {
                itemView.setOnClickListener {
                    previewClickedPosition = -1
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        anotherBackground.invoke()
                    }
                }
            }
    }

    override fun getItemCount() = items.size + 1;

    override fun onBindViewHolder(holder: ChooseBackground, position: Int) = when (getItemViewType(position)) {
        0 -> {
            with(holder as ChooseBackground.ColorView) {
                val item = items[position]
                if (item.isPressed) setState(item.dimenPressed, View.VISIBLE)
                else setState(item.dimenNotPressed, View.INVISIBLE)
                setBackground(Color.parseColor(item.baseColor), Color.parseColor(item.gradientColor))
            }
        }
        else -> Unit
    }

    override fun getItemViewType(position: Int) = when {
        position == itemCount - 1 -> 1
        else -> 0
    }

    sealed class ChooseBackground(view: View) : RecyclerView.ViewHolder(view) {

        class ColorView(view: View) : ChooseBackground(view) {
            val enabledView: View = view.findViewById(R.id.enabledView)
            val borderView: View = view.findViewById(R.id.borderView)

            fun setBackground(startColor: Int, endColor: Int) {
                val background = enabledView.background as GradientDrawable
                background.mutate()
                background.colors = IntArray(2).apply {
                    this[0] = startColor
                    this[1] = endColor
                }
            }

            fun setState(dimen: Int, visibility: Int) {
                borderView.visibility = visibility
                enabledView.layoutParams = enabledView.layoutParams.also {
                    it.height = dimen
                    it.width = dimen
                }
            }
        }

        class PickFromPhoto(view: View) : ChooseBackground(view)
    }
}
