package com.example.levi.alarm.view

import android.app.AlertDialog
import android.content.Context
import android.os.Handler
import android.os.Message
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_main.view.*
import java.lang.ref.WeakReference
import java.util.*

class TimerView : LinearLayout {

    private var timer: Timer? = Timer()
    private var timerTask: TimerTask? = null
    private val myHandler = MyHandler(this@TimerView)

    companion object {
        const val WHAT_TIME_END = 1
        const val WHAT_TIME_TICK = 2
        private var mAllTimerCount: Int = 0

        class MyHandler(view: View) : Handler() {
            private val mView: WeakReference<View> = WeakReference(view)
            override fun handleMessage(msg: Message?) {
                when (msg!!.what) {
                    WHAT_TIME_END -> {
                        AlertDialog.Builder(mView.get()?.context)
                                .setTitle("Time is end")
                                .setMessage("Time is end")
                                .setNegativeButton("Cancel", null)
                                .show()
                        (mView.get() as TimerView).btn_start.visibility = View.VISIBLE
                        (mView.get() as TimerView).btn_resume.visibility = View.GONE
                        (mView.get() as TimerView).btn_pause.visibility = View.GONE
                        (mView.get() as TimerView).btn_reset.visibility = View.GONE
                    }
                    WHAT_TIME_TICK -> {
                        val hour = mAllTimerCount / (60 * 60)
                        val min = (mAllTimerCount / 60) % 60
                        val sec = mAllTimerCount % 60
                        (mView.get() as TimerView).et_hour.setText(hour.toString())
                        (mView.get() as TimerView).et_min.setText(min.toString())
                        (mView.get() as TimerView).et_sec.setText(sec.toString())
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
        et_hour.setText("00")
        et_hour.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!TextUtils.isEmpty(s)) {
                    if (Integer.parseInt(s.toString()) > 59) {
                        et_hour.setText("59")
                    } else if (Integer.parseInt(s.toString()) < 0) {
                        et_hour.setText("00")
                    }
                    checkEnableBtnStart()
                }
            }
        })

        et_min.setText("00")
        et_min.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!TextUtils.isEmpty(s)) {
                    if (Integer.parseInt(s.toString()) > 59) {
                        et_min.setText("59")
                    } else if (Integer.parseInt(s.toString()) < 0) {
                        et_min.setText("00")
                    }
                    checkEnableBtnStart()
                }
            }
        })
        et_sec.setText("00")
        et_sec.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!TextUtils.isEmpty(s)) {
                    if (Integer.parseInt(s.toString()) > 59) {
                        et_sec.setText("59")
                    } else if (Integer.parseInt(s.toString()) < 0) {
                        et_sec.setText("00")
                    }
                    checkEnableBtnStart()
                }
            }
        })
        btn_start.visibility = View.VISIBLE
        btn_start.isEnabled = false
        btn_start.setOnClickListener {
            startTimer()
            btn_start.visibility = View.GONE
            btn_pause.visibility = View.VISIBLE
            btn_reset.visibility = View.VISIBLE
        }
        btn_pause.visibility = View.GONE
        btn_pause.setOnClickListener {
            stopTimer()
            btn_pause.visibility = View.GONE
            btn_resume.visibility = View.VISIBLE
        }
        btn_resume.visibility = View.GONE
        btn_resume.setOnClickListener {
            startTimer()
            btn_pause.visibility = View.VISIBLE
            btn_resume.visibility = View.GONE
        }
        btn_reset.visibility = View.GONE
        btn_reset.setOnClickListener {
            stopTimer()
            btn_resume.visibility = View.GONE
            btn_pause.visibility = View.GONE
            btn_reset.visibility = View.GONE
            btn_start.visibility = View.VISIBLE
            et_hour.setText("00")
            et_min.setText("00")
            et_sec.setText("00")
        }

    }

    private fun startTimer() {
        if (timerTask == null) {
            mAllTimerCount = et_hour.text.toString().toInt() * 60 * 60 + et_min.text.toString().toInt() * 60 + et_sec.text.toString().toInt()
            timerTask = object : TimerTask() {
                override fun run() {
                    mAllTimerCount--
                    myHandler.sendEmptyMessage(WHAT_TIME_TICK)
                    if (mAllTimerCount < 0) {
                        myHandler.sendEmptyMessage(WHAT_TIME_END)
                        stopTimer()
                    }

                }
            }
            timer?.schedule(timerTask, 1000, 1000)
        }
    }

    private fun stopTimer() {
        timer?.cancel()
        timer = null
        timerTask = null
        timer = Timer()
    }

    private fun checkEnableBtnStart() {
        btn_start.isEnabled = et_hour.text.toString().toInt() > 0
                || et_min.text.toString().toInt() > 0
                || et_sec.text.toString().toInt() > 0
    }
}