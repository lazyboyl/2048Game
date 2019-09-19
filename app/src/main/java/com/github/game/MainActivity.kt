package com.github.game

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var gameController: GameController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /**
         * 初始化游戏控制类
         */
        gameController = GameController(applicationContext)

        /**
         * 获取游戏配置，并将游戏配置赋值给到游戏主页面
         */
        gameView.gameConfig = gameController.getDefaultGameConfig()

        /**
         * 功能描述：监听start的单击事件来实现游戏的启动
         */
        start.setOnClickListener{
            gameView.start()
        }

    }

    /**
     * 功能描述：设置当前的分数的值
     * @param scoreValue 当前分数的值
     */
    fun onScoreChanged(scoreValue: Int) {
        score.text = scoreValue.toString()
    }
}
