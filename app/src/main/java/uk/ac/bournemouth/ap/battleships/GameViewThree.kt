package uk.ac.bournemouth.ap.battleships

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GestureDetectorCompat
import uk.ac.bournemouth.ap.battleshiplib.BattleshipGrid
import uk.ac.bournemouth.ap.battleshiplib.GuessCell
import uk.ac.bournemouth.ap.battleshipslogic.MyBattleShipGame
import kotlin.math.sqrt

/**
 *A class that draws he UI for the two player game mode.
 *
 * @author David Whalley
 */
class GameViewThree: View {
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
    var game = MyBattleShipGame(10,10)

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

        //Measure height for each grid.
        val gridHeight = (canvasHeight/2)

        //Work out the desired margin between grids.
        val margin = (sqrt(canvasHeight * 0.01) + 15).toFloat()

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

                /*This shows the blue players ships on the grid. Uncomment for debug purposes.
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
                }*/

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
        canvas.drawRect(0f,gridHeight+margin, canvasWidth, canvasHeight, backPaint)

        //Draw bottom grid column lines
        for(x in 0..colCount){
            canvas.drawLine(x * cellWidth, gridHeight+margin, x * cellWidth, canvasHeight, linePaint)
        }

        //Draw bottom grid row lines
        for(y in 0..rowCount){
            canvas.drawLine(0f, (gridHeight+margin)+y * cellHeight, canvasWidth, (gridHeight+margin)+y * cellHeight, linePaint)
        }

        //Draw bottom cell tokens
        for(x in 1..colCount) {
            for (y in 1..rowCount) {

                /*This shows the red players ships on the grid. Uncomment for debug purposes.
                if (game.redPlayer.shipAt(x - 1, y - 1) != null) {
                    canvas.drawRect(
                        (x-1) * cellWidth,
                        gridHeight+20f+(y-1) * cellHeight,
                        (x) * cellWidth,
                        gridHeight+20f+(y) * cellHeight,
                        shipPaint
                    )
                    canvas.drawText(
                        game.redPlayer.shipAt(x - 1, y - 1)!!.index.toString(),
                        x * cellWidth - cellWidth / 2,
                        (gridHeight+20f)+y * cellHeight - cellHeight / 2 + textOffset,
                        wordPaint
                    )
                }*/

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

        //Sets msg to the winner and triggers a game over when the game is finsihed.
        if(game.blueGrid.isFinished || game.redGrid.isFinished){
            msg = if(game.blueGrid.isFinished){
                "Red"
            }
            else if(game.redGrid.isFinished){
                "Blue"
            }
            else{
                ""
            }
            gameOver()
        }
    }//End of onDraw

    /**
     * Holds the listeners for grid changes.
     */
    private val gridChangeListener = BattleshipGrid.BattleshipGridListener { _, _, _ -> invalidate() }

    /**
     * Adds the listeners for grid changes to both player grids.
     */
    init{
        game.blueGrid.addOnGridChangeListener(gridChangeListener)
        game.redGrid.addOnGridChangeListener(gridChangeListener)
    }

    /**
     * Holds the details for touch screen events for player 1.
     */
    private val gestureDetectorOne = GestureDetectorCompat(context, object:
    GestureDetector.SimpleOnGestureListener(){
        override fun onDown(e: MotionEvent):Boolean{
            return true
        }
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            val canvasHeight = height.toFloat()
            val margin = (sqrt(canvasHeight * 0.01) + 15).toFloat()
            val cellOffset = (canvasHeight+883)/75
            val cellWidth = width.toFloat()/colCount
            val cellHeight = (canvasHeight/2)/rowCount
            val cell = minOf(cellWidth, cellHeight)
            val x = e.x.toInt()
            val y = e.y.toInt()
            val column = x/cell
            val row =  (y - (canvasHeight/2)-cellOffset) / cell
            return if(y < (canvasHeight/2)+margin){
                false
            } else{
                game.twoPlayerGame(column.toInt(), row.toInt(), game.blueGrid)
                true
            }
        }
    })

    /**
     * Holds the details for touch screen events for player two.
     */
    private val gestureDetectorTwo = GestureDetectorCompat(context, object:
        GestureDetector.SimpleOnGestureListener(){
        override fun onDown(e: MotionEvent):Boolean{
            return true
        }
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            val canvasHeight = height.toFloat()
            val margin = (sqrt(canvasHeight * 0.01) + 15).toFloat()
            val cellOffset = (canvasHeight+883)/75
            val cellWidth = width.toFloat()/colCount
            val cellHeight = (canvasHeight/2)/rowCount
            val cell = minOf(cellWidth, cellHeight)
            val x = e.x.toInt()
            val y = e.y.toInt()
            val column = x/cell
            val row =  (y-cellOffset)/cell
            return if(y > (canvasHeight/2)+margin){
                false
            } else{
                game.twoPlayerGame(column.toInt(), row.toInt(), game.redGrid)
                true
            }
        }
    })

    /**
     * The function that triggers an on touch event.
     *
     * @param event The touch event triggered.
     *
     *@return True or false depending on if the result of the touch event.
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if(game.turn == 1){
            gestureDetectorOne.onTouchEvent(event) || super.onTouchEvent(event)
        } else{
            gestureDetectorTwo.onTouchEvent(event) || super.onTouchEvent(event)
        }

    }

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