package com.example.levi.alarm.view

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import com.example.levi.alarm.receiver.AlarmReceiver
import kotlinx.android.synthetic.main.activity_main.view.*
import java.util.*

class AlarmView : LinearLayout {

    private var adapter: ArrayAdapter<AlarmData>? = null
    private val mAlarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    companion object {
        class AlarmData(var time: Long = 0,
                        var date: Calendar = Calendar.getInstance(),
                        private var timeLabel: String = "") {
            init {
                date.timeInMillis = time
                timeLabel = "${date.get(Calendar.YEAR)}.${date.get(Calendar.MONTH) + 1}" +
                        ".${date.get(Calendar.DAY_OF_MONTH)} ${date.get(Calendar.HOUR_OF_DAY)}:${date.get(Calendar.MINUTE)}"
            }

            override fun toString(): String {
                return timeLabel
            }

            fun getId(): Int {
                return (time / (1000 * 60)).toInt()
            }
        }
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) : super(context, attributeSet, defStyle)

    override fun onFinishInflate() {
        super.onFinishInflate()
        Log.i(TAG, "onFinishInflate")
        adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1)
        lv_alarm_list.adapter = adapter
        val alarmList = readAlarmList()
        if (alarmList != null && alarmList.isNotEmpty()) {
            adapter!!.addAll(alarmList)
        }
        btn_add_alarm.setOnClickListener { addAlarm() }
        lv_alarm_list.setOnItemLongClickListener { _, _, position, _ ->
            AlertDialog.Builder(context).setTitle("操作选项").setItems(listOf("删除").toTypedArray()) { dialog: DialogInterface, which: Int ->
                deleteAlarm(position)
            }.setNegativeButton("取消", null).show()
            true
        }
    }

    private val TAG = AlarmView::class.java.simpleName


    @SuppressLint("NewApi")
    private fun addAlarm() {
        val c: Calendar = Calendar.getInstance()
        TimePickerDialog(context, { view, hourOfDay, minute ->
            val calendar: Calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            val currentTime = Calendar.getInstance()
            if (calendar.timeInMillis <= currentTime.timeInMillis) {
                calendar.timeInMillis = calendar.timeInMillis + 24 * 60 * 60 * 1000
            }

            adapter!!.add(AlarmData(calendar.timeInMillis))
            when (Build.VERSION.SDK_INT) {
                in 23..Int.MAX_VALUE -> {
                    Log.i(TAG, "sdk ${Build.VERSION.SDK_INT} setExactAndAllowWhileIdle")
                    mAlarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
                            PendingIntent.getBroadcast(context, AlarmData(calendar.timeInMillis).getId(), Intent(context, AlarmReceiver::class.java), 0))
                }
                in 19..22 -> {
                    Log.i(TAG, "sdk ${Build.VERSION.SDK_INT} setExact")
                    mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
                            PendingIntent.getBroadcast(context, AlarmData(calendar.timeInMillis).getId(), Intent(context, AlarmReceiver::class.java), 0))
                }
                else -> {
                    Log.i(TAG, "sdk ${Build.VERSION.SDK_INT} setRepeating")
                    mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, 1 * 60 * 1000,
                            PendingIntent.getBroadcast(context, AlarmData(calendar.timeInMillis).getId(), Intent(context, AlarmReceiver::class.java), 0))
                }
            }
            saveAlarmList()
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show()
    }

    private fun deleteAlarm(position: Int) {
        val item = adapter!!.getItem(position)
        adapter!!.remove(adapter!!.getItem(position))
        mAlarmManager.cancel(PendingIntent.getBroadcast(context, item!!.getId(), Intent(context, AlarmReceiver::class.java), 0))
        saveAlarmList()
    }

    private val ALARM_LIST = "alarm_list"

    private fun saveAlarmList() {
        val editor = context.getSharedPreferences(AlarmView::class.java.simpleName, Context.MODE_PRIVATE).edit()
        val stringBuffer = StringBuffer()
        for (i in 0 until adapter!!.count) {
            stringBuffer.append(adapter!!.getItem(i)!!.time).append(",")
        }
        if (stringBuffer.isNotEmpty()) {
            editor.putString(ALARM_LIST, stringBuffer.toString().substring(0, stringBuffer.length - 1))
            Log.i(TAG, stringBuffer.toString().substring(0, stringBuffer.length - 1))
        } else {
            editor.remove(ALARM_LIST)
        }
        editor.apply()
    }

    private fun readAlarmList(): MutableList<AlarmData>? {
        val editor = context.getSharedPreferences(AlarmView::class.java.simpleName, Context.MODE_PRIVATE)
        val timeMillis = editor.getString(ALARM_LIST, null)
        if (timeMillis != null) {
            val timeList = timeMillis.split(",")
            val multableList = MutableList(timeList.size) {
                AlarmData(timeList[it].toLong())
            }
            return multableList
        }
        return null
    }
}