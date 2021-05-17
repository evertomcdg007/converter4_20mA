package com.saga.converter_4_20ma.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.saga.converter_4_20ma.R

class Splash :  Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        startThreadLaunch()
    }

    private fun startThreadLaunch() {
        val threadSplash: Thread = object : Thread() {
            override fun run() {
                try {
                    sleep(2000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } finally {
                    val intent = Intent(this@Splash, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        threadSplash.name = "ThreadSplash"
        threadSplash.start()
    }
}