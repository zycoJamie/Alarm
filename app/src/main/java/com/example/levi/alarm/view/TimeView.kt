package com.example.levi.alarm.view

import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.example.levi.alarm.R
import kotlinx.android.synthetic.main.activity_main.view.*
import java.lang.ref.WeakReference
import java.util.*

class TimeView : LinearLayout {

    private val mTimeTv: TextView? = null
    private val mTimeHandler: TimeView.Companion.TimeHandler = TimeHandler(this@TimeView)


    companion object {
        class TimeHandler(timeView: TimeView) : Handler() {
            private val view: WeakReference<TimeView> = WeakReference(timeView)
            override fun handleMessage(msg: Message?) {
                view.get()!!.refreshTime()
                if (view.get()!!.visibility == View.VISIBLE) {
                    view.get()!!.mTimeHandler.sendEmptyMessageDelayed(0, 1000)
                }
            }
        }
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) : super(context, attributeSet, defStyle)


    @SuppressWarnings("all")
    private fun refreshTime() {
        Log.i(TAG,"refresh time")
        val c:Calendar= Calendar.getInstance()
        tv_time.text="${c.get(Calendar.HOUR_OF_DAY)}:${c.get(Calendar.MINUTE)}:${c.get(Calendar.SECOND)}"
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        tv_time.text = ""
    }

    private val TAG=TimeView::class.java.simpleName

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        Log.i(TAG,"onVisibilityChanged")
        Log.i(TAG,"visibility : $visibility")
        if(visibility==View.VISIBLE){
            mTimeHandler.sendEmptyMessage(0)
        }else{
            mTimeHandler.removeMessages(0)
        }
    }
}