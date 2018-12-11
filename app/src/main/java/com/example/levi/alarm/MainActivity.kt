package com.example.levi.alarm

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tabhost.setup()
        tabhost.addTab(tabhost.newTabSpec("tabTime").setIndicator("时钟").setContent(R.id.tabTime))
        tabhost.addTab(tabhost.newTabSpec("tabAlarm").setIndicator("闹钟").setContent(R.id.tabAlarm))
        tabhost.addTab(tabhost.newTabSpec("tabTimer").setIndicator("计时器").setContent(R.id.tabTimer))
        tabhost.addTab(tabhost.newTabSpec("tabStopWatch").setIndicator("秒表").setContent(R.id.tabStopWatch))
    }

    override fun onDestroy() {
        tabStopWatch.onDestroy()
        super.onDestroy()
    }
}
