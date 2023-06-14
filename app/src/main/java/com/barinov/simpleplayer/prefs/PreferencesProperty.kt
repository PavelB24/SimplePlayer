package com.barinov.simpleplayer.prefs

import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class PreferencesProperty<T>(
    private val sharedPreferences: SharedPreferences,
    private val key: String,
    private val default : T,
    private val getter : SharedPreferences.(String, T) -> T,
    private val setter : SharedPreferences.Editor.(String, T) -> SharedPreferences.Editor
) : ReadWriteProperty<Any, T> {

    override fun getValue(thisRef: Any, property: KProperty<*>): T =
        sharedPreferences.getter(key, default)

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) =
        sharedPreferences.edit()
            .setter(key, value)
            .apply()
}


fun SharedPreferences.string(key : String, def : String?) = PreferencesProperty<String?>(
    this,
    key,
    def,
    SharedPreferences::getString,
    SharedPreferences.Editor::putString
)

fun SharedPreferences.float(key : String, def : Float) = PreferencesProperty<Float>(
    this,
    key,
    def,
    SharedPreferences::getFloat,
    SharedPreferences.Editor::putFloat
)

fun SharedPreferences.int(key : String, def : Int) = PreferencesProperty<Int>(
    this,
    key,
    def,
    SharedPreferences::getInt,
    SharedPreferences.Editor::putInt
)

fun SharedPreferences.long(key : String, def : Long) = PreferencesProperty<Long>(
    this,
    key,
    def,
    SharedPreferences::getLong,
    SharedPreferences.Editor::putLong
)

fun SharedPreferences.boolean(key : String, def : Boolean) = PreferencesProperty<Boolean>(
    this,
    key,
    def,
    SharedPreferences::getBoolean,
    SharedPreferences.Editor::putBoolean
)

