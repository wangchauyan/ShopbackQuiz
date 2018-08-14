package idv.chauyan.shopbackquiz.views

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import com.squareup.picasso.Picasso
import idv.chauyan.shopbackquiz.R


class CircleImageView : android.support.v7.widget.AppCompatImageView {


    var drawRes: Drawable? = null
    var bitmap: Bitmap? = null

    var paint = Paint().apply {
        isAntiAlias = true
    }



    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    fun setImageURL(url:String) {
        Picasso.get()
                .load(url)
                .placeholder(R.drawable.place_holder)
                .error(R.drawable.place_holder)
                .into(this)
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        //super.onDraw(canvas)
        if (drawable == null) return

        loadBitmap()

        if (bitmap == null) return

        val circleCenter = Math.min(canvas!!.width, canvas.height) / 2.0f
        canvas.drawCircle(circleCenter, circleCenter, circleCenter, paint)
    }

    fun loadBitmap() {
        if (drawRes == drawable)
            return
        drawRes = drawable
        bitmap = drawableToBitmap(drawRes)
        updateShader()
    }

    fun drawableToBitmap(drawable: Drawable?): Bitmap? {
        if (drawable == null) return null
        if (drawable is BitmapDrawable) drawable.bitmap

        try {
            val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)

            return bitmap
        }
        catch (e :Exception) {
            e.printStackTrace()
        }

        return null
    }

    fun updateShader() {
        if (bitmap == null) return

        val shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        var scale = Math.min(width, height) * 1.0f / Math.min(bitmap!!.width, bitmap!!.height) * 1.0f

        val matrix = Matrix()
        matrix.setScale(scale, scale)
        shader.setLocalMatrix(matrix)

        paint.setShader(shader)
    }
}
