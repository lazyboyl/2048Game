package com.github.game

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.FrameLayout

class Game2048View : FrameLayout {

    /**
     * true表示游戏中，false表示游戏还没开始
     */
    var mStarted: Boolean = false

    private var mTouchSlop: Int = 0

    private var mColumnSize: Int

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0) {
        val vc = ViewConfiguration.get(getContext())
        mTouchSlop = vc.scaledTouchSlop
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        /**
         * 获取组件自定义的值，这个值在我们的custom:grid中设定好的值
         */
        val typeArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.Game2048View)
        mColumnSize = typeArray.getInt(R.styleable.Game2048View_grid, 4)
        typeArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        println("widthSize=>$widthSize")
        setMeasuredDimension(widthSize, widthSize)
    }

    /**
     * 当前拇指按下时候的坐标的位置
     */
    private var touchDownX: Float = 0f
    private var touchDownY: Float = 0f
    /**
     * 当前的操作行为是拇指按屏幕
     */
    private var scrolled: Boolean = false
    /**
     * 触屏事件的处理
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        println("mColumnSize=>$mColumnSize   mTouchSlop=>$mTouchSlop")
        when (event.action) {
            // 当拇指接触到触屏的时候触发的事件
            MotionEvent.ACTION_DOWN -> {
                touchDownX = event.x
                touchDownY = event.y
                scrolled = false
            }
            // 当拇指在触屏上移动的时候触发的事件
            MotionEvent.ACTION_MOVE -> {
                // 表示一次正常的操作行为
                if(mStarted && !scrolled){

                }
                println("ACTION_MOVE【$event.x $event.y】")
            }
        }
        return true
    }


}