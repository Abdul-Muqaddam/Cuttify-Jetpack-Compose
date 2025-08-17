package com.example.videotoaudioconverter.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object AppPreference {

    private const val PREFERENCES_NAME="app_preferences"
    private const val KEY_LANGUAGE_CODE="selected_language"

    private lateinit var preferences: SharedPreferences

    fun init(context: Context){
        preferences =context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    }

    fun setLanguageCode(code:String){
        preferences.edit { putString(KEY_LANGUAGE_CODE, code) }
    }

    fun getLanguageCode():String{
        return preferences.getString(KEY_LANGUAGE_CODE,"en")?:"en"
    }
}