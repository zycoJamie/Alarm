package com.example.levi.alarm.view

import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_main.view.*
import java.lang.ref.WeakReference
import java.util.*

class StopWatchView : LinearLayout {

    private val adapter: ArrayAdapter<String> by lazy {
        ArrayAdapter<String>(context, android.R.layout.simple_list_item_1)
    }

    private var timer: Timer? = Timer()
    private var timerTask: TimerTask? = null
    private var showTimeTask: TimerTask? = null
    private val myHandler: MyHandler = MyHandler(this@StopWatchView)

    companion object {
        const val WHAT_SHOW_TIME = 1
        private var tenMillis: Int = 0

        class MyHandler(view: View) : Handler() {
            private val mView: WeakReference<View> = WeakReference(view)
            override fun handleMessage(msg: Message?) {
                when (msg?.what) {
                    WHAT_SHOW_TIME -> {
                        (mView.get() as StopWatchView).tv_hour.text = (tenMillis / 100 / 60 / 60).toString()
                        (mView.get() as StopWatchView).tv_min.text = (tenMillis / 100 / 60 % 60).toString()
                        (mView.get() as StopWatchView).tv_second.text = (tenMillis / 100 % 60).toString()
                        (mView.get() as StopWatchView).tv_millis.text = (tenMillis % 100).toString()
                    }
                }
            }
        }
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) : super(context, attributeSet, defStyle)

    override fun onFinishInflate() {
        super.onFinishInflate()
        lv_watch_time.adapter = adapter
        tv_hour.text = "0"
        tv_min.text = "0"
        tv_second.text = "0"
        tv_millis.text = "0"
        btn_pause_watch.visibility = View.GONE
        btn_pause_watch.setOnClickListener {
            stopTimer()
            btn_pause_watch.visibility = View.GONE
            btn_reset_watch.visibility = View.VISIBLE
            btn_pull_watch.visibility = View.VISIBLE
            btn_resume_watch.visibility = View.VISIBLE
        }
        btn_pull_watch.visibility = View.GONE
        btn_pull_watch.setOnClickListener {
            adapter.insert("${tenMillis / 100 / 60 / 60}:${tenMillis / 100 / 60 % 60}:${tenMillis / 100 % 60}:${tenMillis % 100}", 0)
        }
        btn_reset_watch.visibility = View.GONE
        btn_reset_watch.setOnClickListener {
            stopTimer()
            tenMillis = 0
            adapter.clear()
            btn_pause_watch.visibility = View.GONE
            btn_reset_watch.visibility = View.GONE
            btn_pull_watch.visibility = View.GONE
            btn_resume_watch.visibility = View.GONE
            btn_start_watch.visibility = View.VISIBLE
        }
        btn_resume_watch.visibility = View.GONE
        btn_resume_watch.setOnClickListener {
            startTimer()
            btn_pause_watch.visibility = View.VISIBLE
            btn_reset_watch.visibility = View.VISIBLE
            btn_pull_watch.visibility = View.VISIBLE
            btn_resume_watch.visibility = View.GONE
        }
        btn_start_watch.setOnClickListener {
            startTimer()
            btn_start_watch.visibility = View.GONE
            btn_pause_watch.visibility = View.VISIBLE
            btn_reset_watch.visibility = View.VISIBLE
            btn_pull_watch.visibility = View.VISIBLE
        }
        showTimeTask = object : TimerTask() {
            override fun run() {
                myHandler.sendEmptyMessage(WHAT_SHOW_TIME)
            }
        }
        timer?.schedule(showTimeTask, 200, 200)
    }

    private val TAG = StopWatchView::class.java.simpleName

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (getVisibility() != View.VISIBLE) {
            Log.i(TAG, "visibility :${getVisibility()}")
            showTimeTask?.cancel()
        } else {
            showTimeTask = object : TimerTask() {
                override fun run() {
                    myHandler.sendEmptyMessage(WHAT_SHOW_TIME)
                }
            }
            timer?.schedule(showTimeTask, 200, 200)
        }
    }

    private fun startTimer() {
        if (timerTask == null) {
            timerTask = object : TimerTask() {
                override fun run() {
                    tenMillis++
                }
            }
        }
        timer?.schedule(timerTask, 10, 10)
    }

    private fun stopTimer() {
        timerTask?.cancel()
        timerTask = null
    }

    fun onDestroy() {
        timer?.cancel()
    }

}