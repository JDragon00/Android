package com.example.mediaplayer

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.MediaController
import com.example.mediaplayer.databinding.ActivityMainBinding

// 동영상 재생 프로그램
class MainActivity : AppCompatActivity() {

    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var mediaController: MediaController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        setContentView(binding.root)

        configureVideoView()
    }

    // 동영상 프로그램을 컨트롤 할 수 있는 기능을 제공함.
    fun configureVideoView() {
        binding.videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.movie))
        mediaController = MediaController(this)
        mediaController?.setAnchorView(binding.videoView)
        binding.videoView.setMediaController(mediaController)

        binding.videoView.setOnPreparedListener {
            it.isLooping = true // 동영상 반복
            Log.d("11111", "Duration = " + binding.videoView.duration) // 총 재생시간 출력
        }
        binding.videoView.start()
    }
}