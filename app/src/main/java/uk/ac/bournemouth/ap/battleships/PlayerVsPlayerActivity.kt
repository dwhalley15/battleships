package uk.ac.bournemouth.ap.battleships
/**
 *A class that creates the UI for the two player game.
 *
 * @author David Whalley
 */
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import uk.ac.bournemouth.ap.battleshipslogic.MyBattleShipGame

class PlayerVsPlayerActivity : AppCompatActivity(), MyBattleShipGame.OnGameFinishedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_vs_player)
        val gameView = findViewById<GameViewThree>(R.id.gameViewThree)
        gameView.setOnGameFinishedListener(this)
    }

    /**
     * The function that starts a when a game has finished.
     * Displays a Alert with the game winner.
     * On clicking the OK button returns to game menu.
     */
    override fun onGameFinished() {
        val builder = AlertDialog.Builder(this)
        val gameView = findViewById<GameViewThree>(R.id.gameViewThree)
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