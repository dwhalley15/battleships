package uk.ac.bournemouth.ap.battleships

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import uk.ac.bournemouth.ap.battleshiplib.GuessCell
import uk.ac.bournemouth.ap.battleshipslogic.MyBattleShipGame
import java.util.concurrent.TimeUnit

/**
 *A class that draws he UI for the computer vs computer game mode.
 *
 * @author David Whalley
 */
class GameViewTwo: View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    /**
     * Holds the created game of type MyBattleShipGame.
     */
    private var game = MyBattleShipGame(10,10)

    /**
     * Holds the eventual winner of the game.
     */
    var msg: String = ""

    /**
     * Hold the listener for when the game has finished.
     */
    private var listener: MyBattleShipGame.OnGameFinishedListener? = null

    /**
     * Holds the maximum columns and rows used to draw the grid.
     */
    private val colCount:Int get() = game.columns
    private val rowCount:Int get() = game.rows

    /**
     * Initilise all paints used for UI.
     */
    private val backPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.rgb(0,157,196)
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

    /**
     * Offsets the text to keep it central.
     */
    private val textOffset = run {
        val textBounds = Rect()
        wordPaint.getTextBounds("X", 0, 1, textBounds)
        (textBounds.top + textBounds.bottom) / -2f
    }

    /**
     * The main function that draws the UI.
     */
    override fun onDraw(canvas: Canvas) {

        //Measure size of the canvas
        val canvasWidth = width.toFloat()
        val canvasHeight = height.toFloat()

        //Work out the desired margin between grids.
        val margin = ((canvasHeight*0.94)/100).toFloat()

        //Measure height for each grid.
        val gridHeight = (canvasHeight/2)-margin


        //Set the cell size
        val cellWidth = minOf(canvasWidth / colCount)
        val cellHeight = minOf(gridHeight / rowCount)

        //Set circle radius
        val radius: Float = minOf(cellWidth/2,cellHeight/2) -10f

        //Draw background for top grid.
        canvas.drawRect(0f,0f, canvasWidth, gridHeight, backPaint)

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
        canvas.drawRect(0f,gridHeight+margin, canvasWidth, canvasHeight-margin, backPaint)

        //Draw bottom grid column lines
        for(x in 0..colCount){
            canvas.drawLine(x * cellWidth, gridHeight+margin, x * cellWidth, canvasHeight-margin, linePaint)
        }

        //Draw bottom grid row lines
        for(y in 0..rowCount){
            canvas.drawLine(0f, (gridHeight+margin)+y * cellHeight, canvasWidth, (gridHeight+margin)+y * cellHeight, linePaint)
        }

        //Draw bottom cell tokens
        for(x in 1..colCount) {
            for (y in 1..rowCount) {

                if (game.redPlayer.shipAt(x - 1, y - 1) != null) {
                    canvas.drawRect(
                        (x-1) * cellWidth,
                        gridHeight+margin+(y-1) * cellHeight,
                        (x) * cellWidth,
                        gridHeight+margin+(y) * cellHeight,
                        shipPaint
                    )
                    canvas.drawText(
                        game.redPlayer.shipAt(x - 1, y - 1)!!.index.toString(),
                        x * cellWidth - cellWidth / 2,
                        (gridHeight+margin)+y * cellHeight - cellHeight / 2 + textOffset,
                        wordPaint
                    )
                }

                if (game.blueGrid.data[x - 1, y - 1] == GuessCell.MISS) {
                    canvas.drawCircle(
                        x * cellWidth - cellWidth / 2,
                        (gridHeight+margin)+y * cellHeight - cellHeight / 2,
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
                        (gridHeight+margin)+y * cellHeight - cellHeight / 2 + textOffset,
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
                        gridHeight+margin+(y-1) * cellHeight,
                        (x) * cellWidth,
                        gridHeight+margin+(y) * cellHeight,
                        redSunkPaint
                    )
                }
            }
        }

        //Triggers the computer vs computer game to play.
        //Also sets msg as the winner and triggers gameOver() when the game is finished.
        if(!game.blueGrid.isFinished && !game.redGrid.isFinished){
            game.playGame()
            TimeUnit.SECONDS.sleep(1L)
            invalidate()
        }
        else{
            msg = when (game.turn) {
                1 -> {
                    "Blue"
                }
                2 -> {
                    "Red"
                }
                else -> {
                    ""
                }
            }
            gameOver()
        }
    }//End of onDraw

    /**
     * The function sets the on game finished listener.
     *
     * @param listener Of type OnGameFinishedListener
     */
    fun setOnGameFinishedListener(listener: MyBattleShipGame.OnGameFinishedListener) {
        this.listener = listener
    }

    /**
     * The function that triggers the on game finished listener when the game is over.
     */
    private fun gameOver(){
        listener?.onGameFinished()
    }

}