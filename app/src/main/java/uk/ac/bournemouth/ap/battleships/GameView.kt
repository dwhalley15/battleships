package uk.ac.bournemouth.ap.battleships

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View

class GameView: View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val colCount get() = 11
    private val rowCount get() = 11

    private val backPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.rgb(0,157,196)
    }

    private val headerPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.WHITE
    }

    private val linePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.BLACK
    }

    private val wordPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        setTextAlign(Paint.Align.CENTER)
        setTextSize(20f * resources.displayMetrics.density)
        setTypeface(Typeface.SANS_SERIF)
        color = Color.BLACK
    }



    override fun onDraw(canvas: Canvas) {

        //Measure size of the canvas
        val canvasWidth = width.toFloat()
        val canvasHeight = height.toFloat()

        //Set the cell size
        val cellWidth = minOf(canvasWidth / colCount)
        val cellHeight = minOf(canvasHeight / rowCount)

        //Draw background
        canvas.drawRect(0f,0f, canvasWidth, canvasHeight, backPaint)

        //Draw column headers
        for(x in 0..colCount){
            canvas.drawRect(x*cellWidth,0f,cellWidth, cellHeight,headerPaint)
        }

        //Draw column numbers
        var xCount = 0
        for(x in 1..colCount){
            canvas.drawText("$xCount", x*cellWidth+cellWidth/2,cellHeight/2,wordPaint)
            xCount += 1
        }

        //Draw row headers
        for(y in 0..rowCount){
            canvas.drawRect(0f,y*cellHeight,cellWidth, cellHeight,headerPaint)
        }

        //Draw row numbers
        var yCount = 0
        for(y in 1..rowCount){
            canvas.drawText("$yCount", cellWidth/2,y*cellHeight+cellHeight/2,wordPaint)
            yCount += 1
        }

        //Draw grid column lines
        for(x in 0..colCount){
            canvas.drawLine(x * cellWidth, 0f, x * cellWidth, canvasHeight, linePaint)
        }

        //Draw grid row lines
        for(y in 0..rowCount){
            canvas.drawLine(0f, y * cellHeight, canvasWidth, y * cellHeight, linePaint)
        }

    }


}