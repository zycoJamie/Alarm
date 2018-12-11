package com.example.levi.alarm.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.levi.alarm.view.PlayAlarmActivity

class AlarmReceiver:BroadcastReceiver() {
    private val TAG=AlarmReceiver::class.java.simpleName

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i(TAG,"闹钟执行了")
        val alarmManager=context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(PendingIntent.getBroadcast(context,resultCode,Intent(context,AlarmReceiver::class.java),0))
        val i=Intent(context,PlayAlarmActivity::class.java)
        i.flags=Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(i)
    }
}