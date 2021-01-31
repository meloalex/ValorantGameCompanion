package com.alexespada.valorantgamecompanion.activity

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.Button
import com.alexespada.valorantgamecompanion.R

class CustomDialog(context: Context, val buttonOnClick: () -> Unit) : Dialog(context) {
    private lateinit var okButton:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog)

        okButton = findViewById(R.id.okButton)
        okButton.setOnClickListener {
            buttonOnClick()
        }
    }


}