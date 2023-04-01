package uk.ac.bournemouth.ap.battleships

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.google.android.material.snackbar.Snackbar
import uk.ac.bournemouth.ap.battleshiplib.BattleshipGrid
import uk.ac.bournemouth.ap.battleshiplib.GuessCell
import uk.ac.bournemouth.ap.battleshipslogic.MyBattleShipGame
import uk.ac.bournemouth.ap.battleshipslogic.MyOpponent
import java.util.concurrent.TimeUnit

class GameViewTwo: View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr

    )

    val game = MyBattleShipGame(10,10)

    //private val blueGame = MyBattleShipGame(10,10)

    //private var blueGrid = blueGame.bluePlayerGrid

    private val colCount:Int get() = game.columns+1
    private val rowCount:Int get() = game.rows+1

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
        textAlign = Paint.Align.CENTER
        textSize = 20f * resources.displayMetrics.density
        typeface = Typeface.SANS_SERIF
        color = Color.BLACK
    }

    private val dotPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply{
        strokeWidth = 10f
        strokeCap = Paint.Cap.ROUND
        color = Color.BLACK
    }

    private val circlePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply{
        style = Paint.Style.STROKE
        strokeWidth = 10f
        isAntiAlias = true
        color = Color.BLUE
    }

    private val xPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        textSize = 20f * resources.displayMetrics.density
        typeface = Typeface.SANS_SERIF
        color = Color.BLUE
    }

    private val sunkPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.BLUE
    }

    private val shipPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.GRAY
    }

    private val textOffset = run {
        val textBounds = Rect()
        wordPaint.getTextBounds("X", 0, 1, textBounds)
        (textBounds.top + textBounds.bottom) / -2f
    }


    override fun onDraw(canvas: Canvas) {

        //Measure size of the canvas
        val canvasWidth = width.toFloat()
        val canvasHeight = height.toFloat()

        //Set the cell size
        val cellWidth = minOf(canvasWidth / colCount)
        val cellHeight = minOf(canvasHeight / rowCount)

        //Set circle radius
        val radius: Float = minOf(cellWidth/2,cellHeight/2) -10f

        //Draw background
        canvas.drawRect(0f,0f, canvasWidth, canvasHeight, backPaint)

        //Draw column headers
        for(x in 0..colCount){
            canvas.drawRect(x*cellWidth,0f,cellWidth, cellHeight,headerPaint)
        }

        //Draw column numbers
        var xCount = 0
        for(x in 1..colCount){
            canvas.drawText("$xCount", x*cellWidth+cellWidth/2,cellHeight/2 + textOffset, wordPaint)
            xCount += 1
        }

        //Draw row headers
        for(y in 0..rowCount){
            canvas.drawRect(0f,y*cellHeight,cellWidth, cellHeight, headerPaint)
        }

        //Draw row numbers
        var yCount = 0
        for(y in 1..rowCount){
            canvas.drawText("$yCount", cellWidth/2,y*cellHeight+cellHeight/2 + textOffset, wordPaint)
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


        //Draw cell tokens
        for(x in 2..colCount){
            for(y in 2..rowCount){

                if(game.bluePlayer.shipAt(x-2,y-2) != null){
                    canvas.drawRect((x-1)*cellWidth, (y-1)*cellHeight, (x)*cellWidth, (y)*cellHeight, shipPaint)
                    canvas.drawText(game.bluePlayer.shipAt(x-2,y-2)!!.index.toString(), x*cellWidth-cellWidth/2,y*cellHeight-cellHeight/2 + textOffset, wordPaint)
                }

                if(game.redGrid.data[x - 2, y - 2] == GuessCell.MISS){
                    canvas.drawCircle(
                        x * cellWidth - cellWidth / 2,
                        y * cellHeight - cellHeight / 2,
                        radius,
                        circlePaint
                    )
                }
                else if(game.bluePlayer.shipAt(x-2,y-2) != null && game.redGrid.data[x - 2, y - 2] == GuessCell.HIT(game.bluePlayer.shipAt(x-2,y-2)!!.index)){
                    canvas.drawText(
                        "X",
                        x * cellWidth - cellWidth / 2,
                        y * cellHeight - cellHeight / 2 + textOffset,
                        xPaint
                    )
                }
                else if(game.bluePlayer.shipAt(x-2,y-2) != null && game.redGrid.data[x - 2, y - 2] == GuessCell.SUNK(game.bluePlayer.shipAt(x-2,y-2)!!.index)){
                    canvas.drawRect(
                        (x - 1) * cellWidth,
                        (y - 1) * cellHeight,
                        (x) * cellWidth,
                        (y) * cellHeight,
                        sunkPaint
                    )
                }
                /*
                else {
                    canvas.drawPoint(
                        x * cellWidth - cellWidth / 2,
                        y * cellHeight - cellHeight / 2,
                        dotPaint
                    )
                }*/

                /*

                if(blueGrid[x-2][y-2] == 2 || blueGrid[x-2][y-2] == 3 || blueGrid[x-2][y-2] == 4 ||blueGrid[x-2][y-2] == 5){//Show ships
                    canvas.drawRect((x-1)*cellWidth, (y-1)*cellHeight, (x)*cellWidth, (y)*cellHeight, shipPaint)
                    canvas.drawText(blueGrid[x-2][y-2].toString(), x*cellWidth-cellWidth/2,y*cellHeight-cellHeight/2 + textOffset, wordPaint)
                }
                else if(blueGrid[x-2][y-2] == 1){//Show misses
                    canvas.drawCircle(x*cellWidth-cellWidth/2,y*cellHeight-cellHeight/2,radius, circlePaint)
                }
                else if(blueGrid[x-2][y-2] == 6){//Show hits
                    canvas.drawText("X", x*cellWidth-cellWidth/2,y*cellHeight-cellHeight/2 + textOffset, xPaint)
                }
                else if(blueGrid[x-2][y-2] == 7){//Show sunk
                    canvas.drawRect((x-1)*cellWidth, (y-1)*cellHeight, (x)*cellWidth, (y)*cellHeight, sunkPaint)
                }
                else{//Show unsigned
                    canvas.drawPoint(x * cellWidth - cellWidth / 2, y * cellHeight - cellHeight / 2, dotPaint)
                }

                //Unsigned token
                //canvas.drawPoint(x * cellWidth - cellWidth / 2, y * cellHeight - cellHeight / 2, dotPaint)

                //Miss token
                //canvas.drawCircle(x*cellWidth-cellWidth/2,y*cellHeight-cellHeight/2,radius, circlePaint)

                //Hit token
                //canvas.drawText("X", x*cellWidth-cellWidth/2,y*cellHeight-cellHeight/2 + textOffset, xPaint)

                //Sunk token
                //canvas.drawRect(x*cellWidth, y*cellHeight, cellWidth, cellHeight, sunkPaint)

                 */

            }
        }
    }

    //Currently unsure how to switch between players. This function is for debug purposes will be removed later.
    fun play():Unit{
        var turn = 0
        while (turn < 80) {
            game.playTurn(10, 10, game.redGrid)
            invalidate()
            turn++
        }
    }
    val run = play()
}