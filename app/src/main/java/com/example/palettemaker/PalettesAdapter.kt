package com.example.palettemaker

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class PalettesAdapter(context: Context, private var palettes: ArrayList<Palette>): BaseAdapter() {
    private var context: Context? = context

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val palette = palettes[position]

        var resultingView = convertView
        if (resultingView == null) {
            val layoutInflater = LayoutInflater.from(context)
            resultingView = layoutInflater.inflate(R.layout.palette_grid_cell, null)
        }

        val colorSlots = arrayOf<View>(
            resultingView!!.findViewById<View>(R.id.color_slot_1),
            resultingView.findViewById<View>(R.id.color_slot_2),
            resultingView.findViewById<View>(R.id.color_slot_3),
            resultingView.findViewById<View>(R.id.color_slot_4)
        )
        val paletteLabel = resultingView.findViewById<TextView>(R.id.palette_label)

        var index = -1
        for (i in 0..3) {
            if (palette.getColors().size > i) {
                val color = palette.getColors()[i]
                colorSlots[i].setBackgroundColor(Color.parseColor(color))
            } else if (palette.getColors().size > 0) {
                if (palette.getColors().size - 1 > index) {
                    index++
                } else {
                    index = 0
                }
                colorSlots[i].setBackgroundColor(Color.parseColor(palette.getColors()[index]))
            }
        }

        paletteLabel.text = palette.getName()

        return resultingView
    }

    override fun getItem(index: Int): Any {
        return palettes[index]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return palettes.size
    }

    fun addPalette(palette: Palette) {
        var foundDuplicate = false
        for (i in 0..(this.palettes.size - 1)) {
            if (this.palettes[i].getName() == palette.getName()) {
                val newList = this.palettes[i].getColors()
                newList.addAll(palette.getColors())
                this.palettes[i].setColors(newList)
                foundDuplicate = true
            }
        }
        if (!foundDuplicate) { this.palettes.add(palette) }
    }

    fun removePalette(palette: Palette) {
        this.palettes.remove(palette)
    }

    fun getpalettes(): ArrayList<Palette> {
        return this.palettes
    }
}