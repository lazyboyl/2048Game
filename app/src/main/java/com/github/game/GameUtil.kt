package com.github.game

import android.graphics.Point
import java.util.ArrayList

/**
 * 类描述： 游戏工具类
 * @author linzf
 * @since 2019-09-18
 */
class GameUtil {

    /**
     * 类描述： 定义一个枚举类型，分别代表着手指移动的方向我左右上下
     */
    enum class Direction {
        Left, Right, Top, Bottom
    }

    /**
     * 功能描述：移动过程实现计算
     * @param array 二维数组
     * @param direction 当前滑动方向
     * @param listPoint 当前为空的节点的数据
     * @param listAction 当前需要移动的节点
     * 以下为二维数组的坐标体系
     * 0,1,2,3
     * 1,2,3,4
     * 2,3,4,5
     * 3,4,5,6
     */
    fun scroll(
        array: Array<Array<Block>>,
        direction: Direction,
        listPoint: ArrayList<Point>,
        listAction: ArrayList<Block>
    ) {
        onStartScroll(array)
        dumpArray(array, "original")
        if (direction != Direction.Right) {
            convert(array, direction, false)
            dumpArray(array, "after convert")
        }

    }

    /**
     * 功能描述： 实现滑块的移动和相加
     */
    fun scroll(array: Array<Array<Block>>, direction: Direction) {
        var size: Int = array.size
        var largeIndexValue = 0
        var lastLargeIndex = -1
        when (direction) {
            // 向左移动滑块
            Direction.Left -> {

            }
            // 向上移动滑块
            Direction.Top -> {
                largeIndexValue = 0
                lastLargeIndex = -1
                dumpArray(array, "original")
                for (x in (size - 1 downTo 1)) {
                    for (y in (0 until size)) {
                        if (array[x - 1][y].value != 0 && lastLargeIndex === -1) {
                            largeIndexValue = array[x - 1][y].value
                            lastLargeIndex = x - 1
                        }
                        // 表示当前节点的值为0
                        if (array[x][y].value == 0) {
                            continue
                        } else if (largeIndexValue == 0) {
                            continue
                        }
                        if (array[x][y].value == largeIndexValue && x != lastLargeIndex) {
                            array[x - 1][y].value = array[x][y].value * 2
                            array[x][y].value = 0
                            largeIndexValue = 0
                            lastLargeIndex = -1
                        }
                    }
                }
                dumpArray(array, "after convert")

            }
            // 向右移动滑块
            Direction.Right -> {

            }
            // 向下移动滑块
            Direction.Bottom -> {

            }
        }
    }

    private fun convert(array: Array<Array<Block>>, direction: Direction, convertChangedSize: Boolean) {
        when (direction) {
            Direction.Right -> return
            Direction.Left -> realConvert(
                array,
                { x: Int, y: Int, size: Int -> size - 1 - x },
                { x: Int, y: Int, size: Int -> y },
                convertChangedSize
            )
            Direction.Bottom -> realConvert(
                array,
                { x: Int, y: Int, size: Int -> y },
                { x: Int, y: Int, size: Int -> size - 1 - x },
                convertChangedSize
            )
            Direction.Top -> realConvert(
                array,
                { x: Int, y: Int, size: Int -> size - 1 - y },
                { x: Int, y: Int, size: Int -> x },
                convertChangedSize
            )
        }
    }

    /**
     * 功能描述：实现翻转二维数组
     * @param array 需要翻转的数据
     * @param convertX x坐标翻转的实现
     * @param convertY y坐标翻转的实现
     * @param convertChangedSize
     */
    private fun realConvert(
        array: Array<Array<Block>>,
        convertX: (x: Int, y: Int, size: Int) -> Int,
        convertY: (x: Int, y: Int, size: Int) -> Int,
        convertChangedSize: Boolean
    ) {
        var size: Int = array.size
        var changedArray: Array<Array<Int>> = Array(size) { Array(size) { 0 } }

        for (y in (0 until size)) {
            for (x in (0 until size)) {
                if ((changedArray[y])[x] != 0) {
                    //已经转换过
                    continue
                }
                if (convertChangedSize) {
                    var block1: Block = (array[y])[x]
                    var pX = block1.changeX
                    var pY = block1.changeY
                    if (pX >= 0 && pY >= 0) {
                        block1.changeX = convertX(pX, pY, size)
                        block1.changeY = convertY(pX, pY, size)
                    }
                } else {
                    var tmp = Block(0, 0, 0)
                    tmp = (array[y])[x]
                    (array[y])[x] = (array[convertY(x, y, size)])[convertX(x, y, size)]
                    (array[convertY(x, y, size)])[convertX(x, y, size)] = tmp
                    (changedArray[convertY(x, y, size)])[convertX(x, y, size)] = 1
                }
            }
        }
    }

    /**
     * 功能描述：
     */
    private fun onStartScroll(array: Array<Array<Block>>) {
        array.flatten().forEach(Block::reset)
    }

    /**
     * 最终的位置：x,y,value
     * 临时变量:px,py,pValue,changeX,changeY,merged
     * x,y 与 px,py不相等表示有移动
     * value与pValue不相等表示有合并，value>pValue表示合并，value＝0表示被合并，要删除View
     * changeX>=0 && changeY>=0表示改变值的坐标
     */
    data class Block(
        var x: Int, var y: Int, var value: Int,
        var pX: Int = x,
        var pY: Int = y,
        var pValue: Int = value,
        var changeX: Int = -1,
        var changeY: Int = -1,
        var merged: Boolean = false
    ) {
        fun reset() {
            pX = x
            pY = y
            pValue = value
            merged = false
            changeX = -1
            changeY = -1
        }

        fun getActionStr(): String {
            var block: Block = this
            var direct =
                if (block.pX == block.x) (if (block.y > block.pY) "向下" else "向上") else (if (block.x > block.pX) "向右" else "向左")
            return "方块从 px:" + block.pX + " pY:" + block.pY + " " + direct + "滑到:" + block.x + "," + block.y + (if (block.changeX < 0) "" else (",并移除(" + block.changeX + "," + block.changeY + ")"))
        }
    }

    private fun dumpArray(array: Array<Array<Block>>, tag: String) {
        println("$tag dumpArray========")
        var size: Int = array.size;
        for (y in (0 until size)) {
            var logStr: StringBuilder = StringBuilder()
            for (x in (0 until size)) {
                var strPXPY = "(" + (array[y])[x].pX + "," + (array[y])[x].pY + "," + (array[y])[x].merged + ")"
                logStr.append(" " + (array[y])[x].value + strPXPY + " ")
            }
            println(logStr.toString())
        }
    }

    /**
     * 功能描述： 获取移动以后的偏移方向
     * @param direction 当前移动的方向
     */
    private fun getBackDirection(direction: Direction): Direction {
        return when (direction) {
            Direction.Left -> Direction.Left
            Direction.Top -> Direction.Bottom
            Direction.Right -> Direction.Left
            Direction.Bottom -> Direction.Top
        }
    }


}