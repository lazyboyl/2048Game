package com.github.game

import android.animation.*
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Point
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.FrameLayout
import com.github.game.GameUtil.Block
import java.util.*
import kotlin.math.abs

/**
 * 类描述：2048的主入口类
 * @author linzf
 * @since 2019-09-18
 */
class Game2048View : FrameLayout {

    lateinit var gameConfig: GameConfig

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

    /**
     * 总分
     */
    private var totalScore: Int = 0

    /**
     * 当前计算到的最大值
     */
    private var mMaxValue: Int = 0

    /**
     * 动画的移动的时间
     */
    private val mDuration: Long = 300

    /**
     * 方块的二维数组的数据
     */
    private lateinit var mBlockArray: Array<Array<Block>>

    /**
     * 滑动后剩余的空白位置
     */
    private var mEmptyPointList: ArrayList<Point> = ArrayList()

    /**
     * 滑动后需要移动 的Action
     */
    private var mActionList: ArrayList<Block> = ArrayList()
    private var mGameUtil = GameUtil()

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

    /**
     * 功能描述：开始游戏
     */
    fun start() {
        Log.d(TAG, "开始游戏")
        reset()
        doNext(true)
        mStarted = true
        doActionTest()
    }


    private fun doActionTest() {
        var animatorSet: AnimatorSet = AnimatorSet()
        var size: Int = mBlockArray.size
        for (y in (0 until size)) {
            for (x in (0 until size)) {
                var blockView = getView(x, y) ?: continue
                println("x=>" + blockView.point.x + ",y=>" + blockView.point.y)
                var scrollHorizontal: Boolean = true

                var startOffset = (width * blockView.point.x).toFloat() / mColumnSize
                var targetOffset = (width * 0).toFloat() / mColumnSize

                Log.d(TAG,  " 像素从" + startOffset + "到" + targetOffset)

                var animation: ValueAnimator = ObjectAnimator.ofFloat(blockView, (if (scrollHorizontal) "translationX" else "translationY"), startOffset, targetOffset)

                animation.duration = mDuration
                animation.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        Log.d(TAG,  " ==完成,像素:" + (if (scrollHorizontal) blockView.translationX else blockView.translationY))
                    }

                    override fun onAnimationStart(animation: Animator?) {
                        //修改View中的坐标,不等动画结束
                        blockView.point.x = 0
                        blockView.point.y = blockView.point.y
                    }
                })

                animation.addUpdateListener { animator ->
                    var currentTranslation = (animator!!.animatedValue as Float)

                    if (targetOffset > startOffset) {

                    }
                }
                animatorSet.playSequentially(animation)
            }
            animatorSet.start()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        println("widthSize=>$widthSize")
        setMeasuredDimension(widthSize, widthSize)
    }

    /**
     * 功能描述： 实现拇指移动的时候重新计算方块
     * @param
     */
    private fun scroll(direction: GameUtil.Direction) {
        Log.d(TAG, "往" + direction + "滑动")
        mEmptyPointList.clear()
        mActionList.clear()

        mGameUtil.scroll(mBlockArray, direction, mEmptyPointList, mActionList)

        doActionAnimation(direction)

    }

    private fun doActionAnimation(direction: GameUtil.Direction) {
        var animatorSet: AnimatorSet = AnimatorSet()
        animatorSet.duration = mDuration
        mActionList.forEach {
            val needRemoveView = it.remove
            var removeTranslation: Float = 0F
            var targetRemoveView: BlockView? = null
            when (direction) {
                // 向左移动滑块
                GameUtil.Direction.Left -> {

                }
                // 向上移动滑块
                GameUtil.Direction.Top -> {
                    if (needRemoveView) {
                        removeTranslation = abs((it.changeX * width).toFloat() / mColumnSize)
                        targetRemoveView = getView(it.changeX, it.changeY)
                        removeView(targetRemoveView)
                    } else {
                        // 表示当前的动画是移动，播放移动的声音
                        gameConfig.onMove()
                    }


                    var startOffset = (width * it.pY).toFloat() / mColumnSize
                    var targetOffset = (width * it.y).toFloat() / mColumnSize

                    Log.d(TAG, it.getActionStr() + " 像素从" + startOffset + "到" + targetOffset)
                    if (it.merged) {
                        var view: BlockView? = getView(it.x, it.y)
                        // 设置距离X的0点的偏移量
                        view!!.translationX = (it.x * width.toFloat()) / mColumnSize
                        // 设置距离Y的0点的偏移量
                        view!!.translationY = (it.y * height.toFloat()) / mColumnSize
                    }
                    invalidate()

                }
                // 向右移动滑块
                GameUtil.Direction.Right -> {

                }
                // 向下移动滑块
                GameUtil.Direction.Bottom -> {

                }
            }

        }

    }

    private fun getView(x: Int, y: Int): BlockView? {
        for (index in 0 until childCount) {
            var child = (getChildAt(index) as BlockView)
            if (child.point.x == x && child.point.y == y) {
                return child
            }
        }
        return null
    }

    /**
     * 功能描述：生成方块页
     * @param first true表示初始化生成，反之则不是
     */
    private fun doNext(first: Boolean) {
        var gameStatus = checkGameOver()
        if (gameStatus > 0) {
            Log.d(TAG, "游戏结束")
        }
        // 生成方块的个数，true表示初始化生成2个，反之则生成1个
        var randomCount = if (first) gameConfig.getFirstRandomCount() else gameConfig.getNormalRandomCount()
        for (i in 0 until randomCount) {
            if (mEmptyPointList.size == 0) {
                break
            }
            // 从当前还没有被填充的数组中随机获取一个位置作为下次填充
            var randomIndex = Random().nextInt(mEmptyPointList.size)
            // 获取该位置的point的坐标体系，同时将该坐标移除空白列表
            var point = mEmptyPointList.removeAt(randomIndex)
            // 获取一个随机数
            var randomValue = gameConfig.randomValue()
            // 将生成的随机数赋给我们的方块二维数组
            mBlockArray[point.x][point.y].value = randomValue
            // 创建一个方块布局
            var view = BlockView.create(context, point)
            view.gameConfig = gameConfig
            view.setNumber(randomValue)
            // 将方块添加到当前的游戏组件中，且设置好了方块的大小
            addView(view, LayoutParams(width / mColumnSize, width / mColumnSize))
            // 设置距离X的0点的偏移量
            view.translationX = (point.y * width.toFloat()) / mColumnSize
            // 设置距离Y的0点的偏移量
            view.translationY = (point.x * height.toFloat()) / mColumnSize
            // 刷新界面
            invalidate()
            Log.d(TAG, "生成一个方块(" + point.x + "," + point.y + "),大小:" + randomValue)
        }
    }

    /**
     * 功能描述： 重置整个2048游戏的页面
     */
    private fun reset() {
        Log.d(TAG, "初始化")
        mStarted = false
        totalScore = 0
        mMaxValue = 0
        onScoreChanged()
        /**
         * 从当前视图中移除所有子视图
         */
        removeAllViews()
        mEmptyPointList.clear()
        /**
         * 创建一个二维数组，同时存放block数据
         */
        mBlockArray = Array(mColumnSize) { x -> Array(mColumnSize) { y -> Block(x, y, 0) } }
        /**
         * 加载当前所有的位置为空的点的二维坐标的数据
         */
        for (y in 0 until mColumnSize) {
            for (x in 0 until mColumnSize) {
                mEmptyPointList.add(Point(x, y))
            }
        }
    }

    /**
     * 功能描述： 判断游戏是否结束【1：赢得游戏；2：游戏结束；3：正常进行】
     */
    private fun checkGameOver(): Int {
        if (gameConfig.win(mMaxValue)) {
            return 1
        } else if (mEmptyPointList.size == 0) {
            return 2
        }
        return 0
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
    var tmpX = 0f
    var tmpY = 0f

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
                    tmpX = event.x
                    tmpY = event.y
                    var deltaX = abs(tmpX - touchDownX)
                    var deltaY = abs(tmpY - touchDownY)
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

    /**
     * 当分数发生变化的时候，需要通知父组件去更新分数的值
     */
    private fun onScoreChanged() {
        (context as MainActivity).onScoreChanged(totalScore)
    }


}