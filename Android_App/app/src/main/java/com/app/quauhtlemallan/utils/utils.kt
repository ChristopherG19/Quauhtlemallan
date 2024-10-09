package com.app.quauhtlemallan.utils

import android.app.AlertDialog
import android.content.Context

fun showAlert(context: Context, title: String, message: String) {
    AlertDialog.Builder(context)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton("Aceptar", null)
        .show()
}
