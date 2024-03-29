package com.example.palettemaker

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import android.graphics.drawable.BitmapDrawable
import androidx.core.graphics.toColor
import com.google.android.material.button.MaterialButton
import kotlin.math.roundToInt
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import androidx.core.graphics.ColorUtils


class ColorSelectorActivity: AppCompatActivity() {
    var hexColor = "#000000"

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_selector)
        val extras = intent.extras
        val imagePath = extras?.get("image_path") as String

        val imageView = findViewById<ImageView>(R.id.color_selector_image_view)
        var bitmap = BitmapFactory.decodeFile(imagePath)
        val matrix = Matrix()
        if (bitmap.width > bitmap.height) {
            matrix.postRotate(90.toFloat())
            bitmap = Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                matrix,
                true
            )
        }
        imageView.setImageBitmap(bitmap)

        // confirm selection
        val confirmButton = findViewById<MaterialButton>(R.id.confirm_color_button)
        confirmButton.setOnClickListener { selectColor() }

        // move selector to wherever user clicked on image
        val selector = findViewById<MovableFloatingActionButton>(R.id.selector_button)

        selector.x = (imageView.drawable.intrinsicWidth.toDouble() * .37).toFloat()
        selector.y = (imageView.drawable.intrinsicHeight.toDouble() * .35).toFloat()
        imageView.setOnTouchListener { view, event -> moveButton(view, event, selector) }
//        imageView.doOnLayout { updateColor() } // wait for image view to load?

        // updates the color when dragging the button
        selector.setOnTouchListener { view, motionEvent -> updateColor(); selector.onTouch(view, motionEvent) }
    }

    private fun moveButton(view: View?, event: MotionEvent?, selector: MovableFloatingActionButton): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                selector.x = event.x
                selector.y = event.y
                updateColor() // change the color
            }
            MotionEvent.ACTION_UP -> {
                view?.performClick()
            }
        }
        return view?.onTouchEvent(event) ?: true
    }

    private fun updateColor() {
        val imageView = findViewById<ImageView>(R.id.color_selector_image_view)
        val bitmap = (imageView.drawable as BitmapDrawable).bitmap
        val selector = findViewById<MovableFloatingActionButton>(R.id.selector_button)

        // ratios to convert imageView coordinate to bitmap coordinate
        // inspired by https://stackoverflow.com/questions/11334984/correspondence-between-imageview-coordinates-and-bitmap-pixels-android
        val widthRatio = bitmap.width.toDouble() / imageView.width.toDouble()
        val heightRatio = bitmap.height.toDouble() / imageView.height.toDouble()

        // account for button size (grab color from middle, not sides)
        val xLoc = (selector.x + (selector.width / 2)) * widthRatio
        val yLoc = (selector.y + (selector.height / 2)) * heightRatio
        val pixel = bitmap.getPixel(xLoc.roundToInt(), yLoc.roundToInt())

        val selectButton = findViewById<MaterialButton>(R.id.confirm_color_button)
        hexColor = String.format("#%06X", 0xFFFFFF and pixel.toColor().toArgb())
        val backgroundColor = pixel.toColor().toArgb()
        selectButton.background.setTint(backgroundColor)

        // make sure text is readable by calculating contrast and swapping color if needed
        if (ColorUtils.calculateContrast(Color.WHITE, backgroundColor) < 4) { // 4 is arbitrary
            selectButton.setTextColor(Color.BLACK)
            selectButton.iconTint = ColorStateList.valueOf(Color.BLACK)
        } else {
            selectButton.setTextColor(Color.WHITE)
            selectButton.iconTint = ColorStateList.valueOf(Color.WHITE)
        }
        selectButton.text = hexColor
    }

    private fun selectColor() {
        val result = Intent()
        result.putExtra("color", hexColor)
        setResult(Activity.RESULT_OK, result)
        finish()
    }
}