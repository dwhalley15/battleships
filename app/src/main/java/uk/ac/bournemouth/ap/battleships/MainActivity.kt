package uk.ac.bournemouth.ap.battleships

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import uk.ac.bournemouth.ap.battleshipslogic.MyBattleShipGame

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    fun startSinglePlayer(view: View) {
        val intent = Intent(this, SinglePlayerActivity::class.java)
        startActivity(intent)
    }
    fun startCompPlay(view: View) {}

}