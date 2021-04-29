package com.droid.customlogger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.droid.customlogger.customlog.AndroidLogAdapter
import com.droid.customlogger.customlog.Logger

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Logger.addLogAdapter(AndroidLogAdapter())

        Logger.d("HELOOOOOO")
        Logger.e("Error")

    }
}