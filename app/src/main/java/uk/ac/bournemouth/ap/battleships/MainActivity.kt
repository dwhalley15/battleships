package uk.ac.bournemouth.ap.battleships

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import uk.ac.bournemouth.ap.battleshipslogic.MyBattleShipGame
/**
 *A class that creates the UI for the game menu.
 *
 * @author David Whalley
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    /**
     * The function that starts a single player game vs the computer.
     *
     * @param view Used to create the UI.
     */
    fun startSinglePlayer(view: View) {
        val intent = Intent(this, SinglePlayerActivity::class.java)
        startActivity(intent)
    }

    /**
     * The function that starts a computer vs computer game.
     *
     * @param view Used to create the UI.
     */
    fun startCompPlay(view: View) {
        val intent = Intent(this, ComputerVsActivity::class.java)
        startActivity(intent)
    }

    /**
     * The function that starts a player vs player game.
     *
     * @param view Used to create the UI.
     */
    fun startTwoPlayer(view: View) {
        val intent = Intent(this, PlayerVsPlayerActivity::class.java)
        startActivity(intent)
    }

}