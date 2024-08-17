package com.dhiwise.figma.utils

import android.content.Context
import android.widget.Toast
import com.google.gson.Gson

inline fun <reified T> String.fromJson(): T {
    return Gson().fromJson(this, T::class.java)
}

fun Any.toJsonString(): String {
    return Gson().toJson(this)
}

fun Context.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}