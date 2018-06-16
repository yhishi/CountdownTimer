package com.yoshiki.hishikawa.countdowntimer

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var soundPool: SoundPool
    private var soundResId = 0

    /**
     * MyCountDownTimer
     * CountDownTimerクラスを今回のカウントダウンアプリ用に継承したクラス
     *
     * millisInFuture：タイマーの設定時間（ミリ秒）
     * countDownInterval：onTickメソッドで実行する間隔（ミリ秒）
     */
    inner class MyCountDownTimer(millisInFuture: Long, countDownInterval: Long)
        // CountDownTimerクラスを継承＆コンストラクタ
        : CountDownTimer(millisInFuture, countDownInterval) {

        // カウントダウン中かどうかのフラグ
        var isRunnning = false

        // コンストラクタで指定した間隔で呼び出される
        // millisUntilFinished：タイマーの残り時間（ミリ秒）
        override fun onTick(millisUntilFinished: Long) {
            val minute = millisUntilFinished / 1000L / 60L // 分
            val second = millisUntilFinished / 1000L % 60L // 秒

            // 画面に残り時間を指定した書式文字列（0:00）で表示
            // %1d→引数1番目(minute)を整数表示 %2$02d→引数2番目(second)を整数2桁で表示
            timerText.text = "%1d:%2$02d".format(minute, second)
        }

        // タイマー終了時に呼び出される
        override fun onFinish() {
            timerText.text = "0:00"

            // アラーム音再生
            soundPool.play(soundResId, 1.0f, 100f, 0, 0, 1.0f)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timerText.text = "3:00"

        // カウントダウンインスタンス生成
        val timer = MyCountDownTimer(3 * 60 * 1000, 100)

        // ボタンタップ時
        playStop.setOnClickListener {
            when(timer.isRunnning) {
                // カウントダウン中の場合
                true -> timer.apply {
                    isRunnning = false
                    cancel()
                    playStop.setImageResource(R.drawable.ic_play_arrow_black_24dp)
                }
                // 停止中の場合
                false -> timer.apply {
                    isRunnning = true
                    start()
                    playStop.setImageResource(R.drawable.ic_stop_black_24dp)
                }
            }

        }
    }

    override fun onResume() {
        super.onResume()

        // SoundPoolインスタンス生成 maxStreams：同時再生可能な音源数 streamType：オーディオのストリームタイプ
        soundPool =
                // APIバージョンで処理を分岐
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    @Suppress("DEPRECATION")
                    SoundPool(2, AudioManager.STREAM_ALARM, 0)
                } else {
                    val audioAttributes = AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .build()
                    SoundPool.Builder()
                            .setMaxStreams(1)
                            .setAudioAttributes(audioAttributes)
                            .build()
                }

        // サウンドリソース読み込み
        soundResId = soundPool.load(this, R.raw.bellsound, 1)
    }

    override fun onPause() {
        super.onPause()

        // メモリ解放
        soundPool.release()
    }
}
