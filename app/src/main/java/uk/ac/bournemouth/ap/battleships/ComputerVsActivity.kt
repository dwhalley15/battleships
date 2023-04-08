package uk.ac.bournemouth.ap.battleships

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import uk.ac.bournemouth.ap.battleshipslogic.MyBattleShipGame
/**
 *A class that creates the UI for the computer vs computer game.
 *
 * @author David Whalley
 */
class ComputerVsActivity : AppCompatActivity(), MyBattleShipGame.OnGameFinishedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_computer_vs)
        val gameView = findViewById<GameViewTwo>(R.id.gameViewTwo)
        gameView.setOnGameFinishedListener(this)
    }

    /**
     * The function that starts a when a game has finished.
     * Displays a Alert with the game winner.
     * On clicking the OK button returns to game menu.
     */
    override fun onGameFinished() {
        val builder = AlertDialog.Builder(this)
        val gameView = findViewById<GameViewTwo>(R.id.gameViewTwo)
        builder.setMessage(gameView.msg + " Wins!")
            .setCancelable(false)
            .setPositiveButton("OK") { _, _ ->
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        val alert = builder.create()
        alert.show()
    }
}