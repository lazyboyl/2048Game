package com.github.game

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.block_view_layout.view.*

/**
 * 类描述： 方块类
 * @author linzf
 * @since 2019-09-18
 */
class BlockView(context: Context, attributes: AttributeSet, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    /**
     * 方块中显示的值
     */
    private lateinit var mBlockText: TextView
    /**
     * 方块的布局
     */
    private lateinit var mCenterBlock: FrameLayout
    /**
     * 游戏的配置
     */
    private lateinit var gameConfig: GameConfig

    /**
     * 方块当前的值
     */
    private var value: Int = 0

    /**
     * 方块所在的坐标
     */
    var point: Point = Point()

    /**
     * 当我们的XML布局被加载完后，就会回调onFinshInfalte这个方法
     */
    override fun onFinishInflate() {
        super.onFinishInflate()
        init()
    }

    fun getValue(): Int {
        return this.value
    }

    /**
     * 初始化方块的时候获取到方块里的相应的组件对象
     */
    private fun init() {
        mBlockText = blockText
        mCenterBlock = centerBlock
    }

    /**
     * 功能描述：设置当前方块的值
     * @param value 当前需要设置的值
     */
    fun setNumber(value: Int) {
        this.value = value
        mBlockText.text = "$value"
        var color = 0
        /**
         * 获取当前数字所对应的文字的颜色
         */
        if (Build.VERSION.SDK_INT >= 23) {
            color = context.getColor(gameConfig.getBlockTextColor(value))
        } else {
            context.resources.getColor(gameConfig.getBlockTextColor(value))
        }
        /**
         * 设置当前方块的文字的颜色
         */
        mBlockText.setTextColor(color)

        /**
         * 设置当前方块的背景的颜色
         */
        mCenterBlock.setBackgroundResource(gameConfig.getBlockBg(value))
    }

    /**
     * 静态的创建方块的方法
     */
    companion object {
        fun create(context: Context, point: Point): BlockView {
            var blockView = LayoutInflater.from(context).inflate(R.layout.block_view_layout, null, false) as BlockView
            blockView.point = point
            return blockView
        }
    }


}