package com.github.game

import android.content.Context
import android.media.SoundPool

/**
 * 类描述：声音的静态类
 * @author linzf
 * @since 2019-09-18
 */
object SoundPoolManager {

    /**
     * 用于存放音频的【id,type】键值对
     */
    private var soundMap: HashMap<Int, String> = HashMap()

    /**
     * 用于交叉存放音频的【type,id】键值对
     */
    private var soundPreparedMap: HashMap<String, Int> = HashMap()
    /**
     * 初始化音频池
     */
    private var soundPool: SoundPool = SoundPool.Builder().build();

    var TYPE_GOOD: String = "GOOD"
    var TYPE_MERGE: String = "MERGE"
    var TYPE_MOVE: String = "MOVE"

    init {
        soundPool.setOnLoadCompleteListener { _, sampleId, status ->
            /**
             * 音频加载完成以后将音频的type作为key，id作为值保存到集合中
             */
            if (status == 0) {
                soundMap[sampleId]?.let {
                    soundPreparedMap.put(it, sampleId)
                }
            }
        }
    }

    /**
     * 功能描述： 将相关音频文件加载到音频池中
     * @param context 上下文对象
     * @param type 音频的类型【GOOD：挑战成功，MERGE：合并成功，MOVE：移动】
     * @param rawId 音频的R上的唯一标识
     */
    fun init(context: Context, type: String, rawId: Int) {
        /**
         * 将相关的音频加载到音频池中
         */
        var id = soundPool.load(context, rawId, 1)
        /**
         * 音频加载完成以后将音频的id作为key，type作为value保存到集合中
         */
        soundMap[id] = type
    }

    /**
     * 功能描述：播放音频
     * @param type 当前播放的音频的类型【GOOD：挑战成功，MERGE：合并成功，MOVE：移动】
     */
    fun play(type: String) {
        var id = soundPreparedMap[type];
        //第一个参数soundID
        //第二个参数leftVolume为左侧音量值（范围= 0.0到1.0）
        //第三个参数rightVolume为右的音量值（范围= 0.0到1.0）
        //第四个参数priority 为流的优先级，值越大优先级高，影响当同时播放数量超出了最大支持数时SoundPool对该流的处理
        //第五个参数loop 为音频重复播放次数，0为值播放一次，-1为无限循环，其他值为播放loop+1次
        //第六个参数 rate为播放的速率，范围0.5-2.0(0.5为一半速率，1.0为正常速率，2.0为两倍速率
        id?.let { soundPool.play(id, 1F, 1F, 1, 0, 1F) }

    }


}