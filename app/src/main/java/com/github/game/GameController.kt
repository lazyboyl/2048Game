package com.github.game

import android.content.Context

/**
 * 类描述：游戏控制器
 * @author linzf
 * @since 2019-09-19
 */
class GameController (var context: Context) {

    init {
        /**
         * 初始化游戏的声音
         */
        SoundPoolManager.init(context, SoundPoolManager.TYPE_GOOD, R.raw.good)
        SoundPoolManager.init(context, SoundPoolManager.TYPE_MERGE, R.raw.merge)
        SoundPoolManager.init(context, SoundPoolManager.TYPE_MOVE, R.raw.move)
    }

    /**
     * 功能描述： 获取游戏配置
     */
    fun getDefaultGameConfig(): GameConfig {
        return GameConfig(context)
    }

}