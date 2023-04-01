package uk.ac.bournemouth.ap.battleships

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.google.android.material.snackbar.Snackbar
import uk.ac.bournemouth.ap.battleshiplib.BattleshipGrid
import uk.ac.bournemouth.ap.battleshiplib.GuessCell
import uk.ac.bournemouth.ap.battleshiplib.shipAt
import uk.ac.bournemouth.ap.battleshipslogic.MyBattleShipGame
import uk.ac.bournemouth.ap.battleshipslogic.MyGrid
import uk.ac.bournemouth.ap.battleshipslogic.MyOpponent

class GameView: View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    val game = MyBattleShipGame(10,10)


    /*private val redGame = MyBattleShipGame(10,10)

    private var redGrid = redGame.redPlayerGrid*/


    private val colCount:Int get() = game.columns
    private val rowCount:Int get() = game.rows

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
        strokeWidth = 20f
        strokeCap = Paint.Cap.ROUND
        color = Color.BLACK
    }

    private val redCirclePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply{
        style = Paint.Style.STROKE
        strokeWidth = 10f
        isAntiAlias = true
        color = Color.RED
    }

    private val blueCirclePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply{
        style = Paint.Style.STROKE
        strokeWidth = 10f
        isAntiAlias = true
        color = Color.BLUE
    }

    private val redXPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        textSize = 20f * resources.displayMetrics.density
        typeface = Typeface.SANS_SERIF
        color = Color.RED
    }

    private val blueXPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        textSize = 20f * resources.displayMetrics.density
        typeface = Typeface.SANS_SERIF
        color = Color.BLUE
    }

    private val redSunkPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.RED
    }

    private val blueSunkPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
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

        //Measure size for each grid.
        val gridWidth = (canvasWidth/2)
        val gridHeight = (canvasHeight/2)


        //Set the cell size
        val cellWidth = minOf(canvasWidth / colCount)
        val cellHeight = minOf(gridHeight / rowCount)

        //Set circle radius
        val radius: Float = minOf(cellWidth/2,cellHeight/2) -10f

        //Draw background for top grid.
        canvas.drawRect(0f,0f, canvasWidth, gridHeight, backPaint)

        /*//Draw top column headers
        for(x in 0 ..colCount){
            canvas.drawRect(x*cellWidth,0f,cellWidth, cellHeight,headerPaint)
        }

        //Draw top column numbers
        var topXCount = 0
        for(x in 1 until colCount){
            canvas.drawText("$topXCount", x*cellWidth+cellWidth/2,cellHeight/2 + textOffset, wordPaint)
            topXCount += 1
        }

        //Draw top row headers
        for(y in 0 .. rowCount){
            canvas.drawRect(0f,y*cellHeight,cellWidth, cellHeight, headerPaint)
        }

        //Draw top row numbers
        var topYCount = 0
        for(y in 1 until rowCount){
            canvas.drawText("$topYCount", cellWidth/2,y*cellHeight+cellHeight/2 + textOffset, wordPaint)
            topYCount += 1
        }*/

        //Draw top grid column lines
        for(x in 0..colCount){
            canvas.drawLine(x * cellWidth, 0f, x * cellWidth, gridHeight, linePaint)
        }

        //Draw top grid row lines
        for(y in 0..rowCount){
            canvas.drawLine(0f, y * cellHeight, canvasWidth, y * cellHeight, linePaint)
        }

        //Draw top cell tokens
        for(x in 1..colCount) {
            for (y in 1..rowCount) {

                if (game.bluePlayer.shipAt(x-1, y-1) != null) {
                    canvas.drawRect(
                        (x-1) * cellWidth,
                        (y-1) * cellHeight,
                        (x) * cellWidth,
                        (y) * cellHeight,
                        shipPaint
                    )
                    canvas.drawText(
                        game.bluePlayer.shipAt(x - 1, y - 1)!!.index.toString(),
                        x * cellWidth - cellWidth / 2,
                        y * cellHeight - cellHeight / 2 + textOffset,
                        wordPaint
                    )
                }

                if (game.redGrid.data[x - 1, y - 1] == GuessCell.MISS) {
                    canvas.drawCircle(
                        x * cellWidth - cellWidth / 2,
                        y * cellHeight - cellHeight / 2,
                        radius,
                        blueCirclePaint
                    )
                } else if (game.bluePlayer.shipAt(x - 1, y - 1) != null && game.redGrid.data[x - 1, y - 1] == GuessCell.HIT(
                        game.bluePlayer.shipAt(
                            x - 1,
                            y - 1
                        )!!.index
                    )
                ) {
                    canvas.drawText(
                        "X",
                        x * cellWidth - cellWidth / 2,
                        y * cellHeight - cellHeight / 2 + textOffset,
                        blueXPaint
                    )
                } else if (game.bluePlayer.shipAt(x - 1, y - 1) != null && game.redGrid.data[x - 1, y - 1] == GuessCell.SUNK(
                        game.bluePlayer.shipAt(
                            x - 1,
                            y - 1
                        )!!.index
                    )
                ) {
                    canvas.drawRect(
                        (x - 1) * cellWidth,
                        (y - 1) * cellHeight,
                        (x) * cellWidth,
                        (y) * cellHeight,
                        blueSunkPaint
                    )
                }
            }
        }

        //Draw background for bottom grid.
        canvas.drawRect(0f,gridHeight+10f, canvasWidth, canvasHeight, backPaint)


        /*Draw bottom column headers
        for(x in 0 ..colCount){
            canvas.drawRect(x*cellWidth,(gridHeight+10f)+cellHeight,cellWidth, cellHeight,headerPaint)
        }

        //Draw bottom column numbers
        var bottomXCount = 0
        for(x in 1 until colCount){
            canvas.drawText("$bottomXCount", x*cellWidth+cellWidth/2,(gridHeight+10f)+cellHeight/2 + textOffset, wordPaint)
            bottomXCount += 1
        }

        //Draw bottom row headers
        for(y in 0 .. rowCount){
            canvas.drawRect(0f,(gridHeight+10f)+y*cellHeight,cellWidth, cellHeight, headerPaint)
        }

        //Draw bottom row numbers
        var bottomYCount = 0
        for(y in 1 until rowCount){
            canvas.drawText("$bottomYCount", cellWidth/2,(gridHeight+10f)+y*cellHeight+cellHeight/2 + textOffset, wordPaint)
            bottomYCount += 1
        }*/

        //Draw bottom grid column lines
        for(x in 0..colCount){
            canvas.drawLine(x * cellWidth, gridHeight+10f, x * cellWidth, canvasHeight, linePaint)
        }

        //Draw bottom grid row lines
        for(y in 0..rowCount){
            canvas.drawLine(0f, (gridHeight+10f)+y * cellHeight, canvasWidth, (gridHeight+10f)+y * cellHeight, linePaint)
        }

        //Draw top cell tokens
        for(x in 1..colCount) {
            for (y in 1..rowCount) {

                if (game.redPlayer.shipAt(x - 1, y - 1) != null) {
                    canvas.drawRect(
                        (x-1) * cellWidth,
                        gridHeight+10f+(y-1) * cellHeight,
                        (x) * cellWidth,
                        gridHeight+10f+(y) * cellHeight,
                        shipPaint
                    )
                    canvas.drawText(
                        game.redPlayer.shipAt(x - 1, y - 1)!!.index.toString(),
                        x * cellWidth - cellWidth / 2,
                        (gridHeight+10f)+y * cellHeight - cellHeight / 2 + textOffset,
                        wordPaint
                    )
                }

                if (game.blueGrid.data[x - 1, y - 1] == GuessCell.MISS) {
                    canvas.drawCircle(
                        x * cellWidth - cellWidth / 2,
                        (gridHeight+10f)+y * cellHeight - cellHeight / 2,
                        radius,
                        redCirclePaint
                    )
                } else if (game.redPlayer.shipAt(
                        x - 1,
                        y - 1
                    ) != null && game.blueGrid.data[x - 1, y - 1] == GuessCell.HIT(
                        game.redPlayer.shipAt(
                            x - 1,
                            y - 1
                        )!!.index
                    )
                ) {
                    canvas.drawText(
                        "X",
                        x * cellWidth - cellWidth / 2,
                        (gridHeight+10f)+y * cellHeight - cellHeight / 2 + textOffset,
                        redXPaint
                    )
                } else if (game.redPlayer.shipAt(
                        x - 1,
                        y - 1
                    ) != null && game.blueGrid.data[x - 1, y - 1] == GuessCell.SUNK(
                        game.redPlayer.shipAt(
                            x - 1,
                            y - 1
                        )!!.index
                    )
                ) {
                    canvas.drawRect(
                        (x - 1) * cellWidth,
                        gridHeight+10f+(y-1) * cellHeight,
                        (x) * cellWidth,
                        gridHeight+10f+(y) * cellHeight,
                        redSunkPaint
                    )
                }
            }
        }





                /*
                if(redGrid[x-2][y-2] == 2 || redGrid[x-2][y-2] == 3 || redGrid[x-2][y-2] == 4 ||redGrid[x-2][y-2] == 5){
                    canvas.drawRect((x-1)*cellWidth, (y-1)*cellHeight, (x)*cellWidth, (y)*cellHeight, shipPaint)
                    canvas.drawText(redGrid[x-2][y-2].toString(), x*cellWidth-cellWidth/2,y*cellHeight-cellHeight/2 + textOffset, wordPaint)
                }
                else if(redGrid[x-2][y-2] == 1){
                    canvas.drawCircle(x*cellWidth-cellWidth/2,y*cellHeight-cellHeight/2,radius, circlePaint)
                }
                else if(redGrid[x-2][y-2] == 6){
                    canvas.drawText("X", x*cellWidth-cellWidth/2,y*cellHeight-cellHeight/2 + textOffset, xPaint)
                }
                else if(redGrid[x-2][y-2] == 7){
                    canvas.drawRect((x-1)*cellWidth, (y-1)*cellHeight, (x)*cellWidth, (y)*cellHeight, sunkPaint)
                }
                else{
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

    //Currently unsure how to switch between players. This function is for debug purposes will be removed later.
    fun play():Unit{
        var turn = 0
        while (turn < 80) {
            game.playTurn(game.columns, game.rows, game.blueGrid)
            game.playTurn(game.columns, game.rows, game.redGrid)
            invalidate()
            turn++
        }
    }

    val run = play()
}