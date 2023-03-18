package uk.ac.bournemouth.ap.battleships

import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi

var View.defaultFocusHighlightEnabledCompat: Boolean
    get() = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        -> Api26Compat.defaultFocusHighlightEnabled(this)

        else -> false
    }
    set(value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Api26Compat.setDefaultFocusHighlightEnabled(this, value)
        }
    }

@RequiresApi(Build.VERSION_CODES.O)
private object Api26Compat {
    fun defaultFocusHighlightEnabled(view: View): Boolean {
        return view.defaultFocusHighlightEnabled
    }

    fun setDefaultFocusHighlightEnabled(view: View, value: Boolean) {
        view.defaultFocusHighlightEnabled=value
    }

}