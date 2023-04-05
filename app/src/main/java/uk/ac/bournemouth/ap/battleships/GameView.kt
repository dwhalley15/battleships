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



class GameView: View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    var msg: String = ""

    var game = MyBattleShipGame(10,10)

    private var listener: MyBattleShipGame.OnGameFinishedListener? = null

    private val colCount:Int get() = game.columns
    private val rowCount:Int get() = game.rows

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

    private val textOffset = run {
        val textBounds = Rect()
        wordPaint.getTextBounds("X", 0, 1, textBounds)
        (textBounds.top + textBounds.bottom) / -2f
    }

    override fun onDraw(canvas: Canvas) {

        //Measure size of the canvas
        val canvasWidth = width.toFloat()
        val canvasHeight = height.toFloat()

        //Measure size for each grid. Maybe do something here with width and height which is greater?
        val gridWidth = (canvasWidth/2)
        val gridHeight = (canvasHeight/2)


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
        canvas.drawRect(0f,gridHeight+20f, canvasWidth, canvasHeight, backPaint)

        //Draw bottom grid column lines
        for(x in 0..colCount){
            canvas.drawLine(x * cellWidth, gridHeight+20f, x * cellWidth, canvasHeight, linePaint)
        }

        //Draw bottom grid row lines
        for(y in 0..rowCount){
            canvas.drawLine(0f, (gridHeight+20f)+y * cellHeight, canvasWidth, (gridHeight+20f)+y * cellHeight, linePaint)
        }

        //Draw bottom cell tokens
        for(x in 1..colCount) {
            for (y in 1..rowCount) {

                /*if (game.redPlayer.shipAt(x - 1, y - 1) != null) {
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
                        (gridHeight+20f)+y * cellHeight - cellHeight / 2,
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
                        (gridHeight+20f)+y * cellHeight - cellHeight / 2 + textOffset,
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
                        gridHeight+20f+(y-1) * cellHeight,
                        (x) * cellWidth,
                        gridHeight+20f+(y) * cellHeight,
                        redSunkPaint
                    )
                }
            }
        }

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


    private val gridChangeListener = BattleshipGrid.BattleshipGridListener { grid, column, row -> invalidate() }

    init{
        game.blueGrid.addOnGridChangeListener(gridChangeListener)
        game.redGrid.addOnGridChangeListener(gridChangeListener)
    }

    private val gestureDetector = GestureDetectorCompat(context, object:
    GestureDetector.SimpleOnGestureListener(){
        override fun onDown(e: MotionEvent):Boolean{
            return true
        }
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            val cellWidth = width.toFloat()/colCount
            val cellHeight = (height.toFloat()/2)/rowCount
            val cell = minOf(cellWidth, cellHeight)
            val x = e.x.toInt()
            val y = e.y.toInt()
            val column = x/cell
            val row = (y - (height.toFloat()/2)-40f) / cell
            return if(y < (height.toFloat()/2)+20f){
                false
            } else{
                game.playerGame(column.toInt(), row.toInt())
                true
            }
        }
    })

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event)
    }

    fun setOnGameFinishedListener(listener: MyBattleShipGame.OnGameFinishedListener) {
        this.listener = listener
    }

    private fun gameOver(){
        listener?.onGameFinished()
    }

}