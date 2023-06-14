package com.barinov.simpleplayer.prefs

import android.content.SharedPreferences

class PreferencesManager(sharedPreferences: SharedPreferences) {

    private companion object{
        const val REPEAT_TYPE = "repeatType"
    }


    var repeatTypeOrdinal by sharedPreferences.int(REPEAT_TYPE, 0)


}