package com.example.filemanager

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class StorageCircleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    var progress = 0.8f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val size = minOf(width, height).toFloat()
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 14f
        paint.color = 0xffe5e7eb.toInt()
        canvas.drawCircle(width / 2f, height / 2f, size / 2f - 14f, paint)
        paint.color = 0xff2563eb.toInt()
        canvas.drawArc(
            14f, 14f, width - 14f, height - 14f,
            -90f, progress * 360f, false, paint
        )
    }
}
