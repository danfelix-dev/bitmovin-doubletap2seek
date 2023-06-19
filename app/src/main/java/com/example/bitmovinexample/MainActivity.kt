package com.example.bitmovinexample

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.GestureDetector
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GestureDetectorCompat
import com.bitmovin.player.api.Player
import com.bitmovin.player.api.source.SourceConfig
import com.example.bitmovinexample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var player: Player

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setupPlayer()
        setupViews()
        setupSource()

        setContentView(binding.root)
    }

    fun setupPlayer() {
//        binding.playerView.player = player
        player = binding.playerView.player!!
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setupViews() {
        val listener = object : GestureDetector.SimpleOnGestureListener() {
            private var lastDownTapTime: Long = 0
            private var isDoubleDownTap: Boolean = false

            override fun onDoubleTap(e: MotionEvent): Boolean {
                val width = binding.playerView.width
                if (e.x > width / 2) {
                    player.seek(player.currentTime + 10)
                } else {
                    player.seek(player.currentTime - 10)
                }
                return true
            }

            override fun onDown(e: MotionEvent): Boolean {
                val currentTime = System.currentTimeMillis()
                val timeSinceLastTap = currentTime - lastDownTapTime
                lastDownTapTime = currentTime

                isDoubleDownTap = timeSinceLastTap <= 200


                return binding.playerView.dispatchTouchEvent(e)
            }

            override fun onSingleTapUp(e: MotionEvent): Boolean {
                Handler(Looper.getMainLooper()).postDelayed({
                    if (!isDoubleDownTap) {
                        binding.playerView.dispatchTouchEvent(e)
                    }
                }, 200)

                return true
            }
        }
        val detector = GestureDetectorCompat(this, listener)

        binding.controlsLayout.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                p1?.let {
                    return detector.onTouchEvent(it)
                }
                return false
            }
        })
    }

    fun setupSource() {
        val sourceItem = SourceConfig.fromUrl("https://bitdash-a.akamaihd.net/content/sintel/sintel.mpd")
        player.load(sourceItem)
    }

}