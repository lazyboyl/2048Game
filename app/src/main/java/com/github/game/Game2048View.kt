package com.github.game

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.FrameLayout

/**
 * 类描述：2048的主入口类
 * @author linzf
 * @since 2019-09-18
 */
class Game2048View : FrameLayout {

    companion object {

        var TAG = "Game2048View"

    }

    /**
     * true表示游戏中，false表示游戏还没开始
     */
    private var mStarted: Boolean = false

    /**
     * 触发移动事件的最小距离
     */
    private var mTouchSlop: Int = 0

    /**
     * 有多少行列
     */
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

    fun scroll(direction: GameUtil.Direction) {
        Log.d(TAG, "往" + direction + "滑动")

    }

        /**
     * 当前拇指按下时候的坐标的位置
     */
    private var touchDownX: Float = 0f
    private var touchDownY: Float = 0f
    /**
     * 当前的操作行为是拇指按屏幕,主要是为了防止拇指按住屏幕一直移动导致多次重复操作，因此在按下拇指的时候设置为false，在移动的时候设置为true。
     */
    private var scrolled: Boolean = false

    /**
     * 当前拇指移动的所在位置的坐标
     */
    var tmpX = 0
    var tmpY = 0

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
                if (mStarted && !scrolled) {
                    var tmpX = event.x
                    var tmpY = event.y
                    var deltaX = Math.abs(tmpX - touchDownX)
                    var deltaY = Math.abs(tmpY - touchDownY)
                    if (deltaX > deltaY && deltaX > mTouchSlop) {
                        scroll(if (tmpX > touchDownX) GameUtil.Direction.Right else GameUtil.Direction.Left)
                        scrolled = true
                    } else if (deltaY > deltaX && deltaY > mTouchSlop) {
                        scroll(if (tmpY > touchDownY) GameUtil.Direction.Bottom else GameUtil.Direction.Top)
                        scrolled = true
                    }
                }
            }
        }
        return true
    }


}