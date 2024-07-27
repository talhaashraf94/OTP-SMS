package com.example.sms2

import android.app.Activity
import android.os.Bundle
import android.content.Intent

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val serviceIntent = Intent(this, MyBackgroundService::class.java)
        startService(serviceIntent)
    }
}
