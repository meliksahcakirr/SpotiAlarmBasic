package com.meliksahcakir.spotialarm.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import com.meliksahcakir.spotialarm.R

class SimpleCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    var text: String = ""
        set(value) {
            field = value
            textView.text = value
        }

    var drawable: Drawable? = null
        set(value) {
            field = value
            imageView.setImageDrawable(value)
        }

    private var textView: TextView
    private var imageView: ImageView

    init {
        val v = View.inflate(context, R.layout.simple_card_view, this)
        textView = v.findViewById(R.id.scvTextView)
        imageView = v.findViewById(R.id.scvImageView)
        val attributes = context.obtainStyledAttributes(
            attrs,
            R.styleable.SimpleCardView,
            defStyleAttr,
            0
        )
        text = attributes.getString(R.styleable.SimpleCardView_scvText) ?: ""
        drawable = attributes.getDrawable(R.styleable.SimpleCardView_scvDrawable)
        attributes.recycle()
    }
}
