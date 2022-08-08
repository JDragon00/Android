package com.example.recorderapp

import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import com.example.recorderapp.databinding.ActivityMainBinding
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission

class MainActivity : AppCompatActivity() {

    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var audioFilePath: String? = null
    private var isRecording = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        setContentView(binding.root)

        setPermission() // 권한 체크
        audioSetup()

        binding.recordBt.setOnClickListener {
            recordAudio()
        }
        binding.stopBt.setOnClickListener {
            stopAudio()
        }
        binding.playBt.setOnClickListener {
            playAudio()
        }
    }

    // 권한체크
    // gradle.properties android.enableJetifier=true 추가
    private fun setPermission() {
        val permission = object : PermissionListener {
            override fun onPermissionGranted() { // 권한이 허용되었을 경우 이곳이 수행
                Toast.makeText(this@MainActivity, "권한이 혀용 되었습니다.", Toast.LENGTH_SHORT).show() }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) { // 권한이 거부되었을 경우
                Toast.makeText(this@MainActivity, "권한이 거부 되었습니다.", Toast.LENGTH_SHORT).show() }
        }

        TedPermission.with(this)
            .setPermissionListener(permission)
            .setRationaleMessage("갤러리를 사용하시려면 권한을 허용해주세요.")
            .setDeniedMessage("읽기 권한을 거부하셨습니다. [앱 설정] -> [권한] 항목에서 허용해주세요.")
            .setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.RECORD_AUDIO) // 필요한 권한을 추가
            .check()
    }

    // 마이크 체크
    fun audioSetup() {
        if(!hasMicrophone()) { // 마이크가 없다면, 모든 기능을 비활성화
            binding.stopBt.isEnabled = false
            binding.playBt.isEnabled = false
            binding.recordBt.isEnabled = false
        } else {
            binding.playBt.isEnabled = false
            binding.stopBt.isEnabled = false
        }
        // 저장소의 위치
        audioFilePath = this.getExternalFilesDir(Environment.DIRECTORY_MUSIC)?.absolutePath + "/myaudio.3gp"
    }

    // 디바이스에 마이크 장치가 있는지 확인
    fun hasMicrophone(): Boolean {
        val pmanager = this.packageManager
        return pmanager.hasSystemFeature( PackageManager.FEATURE_MICROPHONE)
    }

    // 녹음 기능 실행
    fun recordAudio() {
        // 녹음중이면 STOP 버튼 활성황
        isRecording = true
        binding.stopBt.isEnabled = true
        binding.playBt.isEnabled = false
        binding.recordBt.isEnabled = false
        
        try {
            mediaRecorder = MediaRecorder()
            mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC) // 오디오의 입력 소스 MIC
            mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP) // 오디오나 비디오가 저장되는 형식
            mediaRecorder?.setOutputFile(audioFilePath) // 오디오나 비디오가 저장되는 파일 경로
            mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB) // 오디오 인코더를 지정
            mediaRecorder?.prepare() // 녹음을 시작하기 위해 MediaRecorder 인스턴스를 준비
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mediaRecorder?.start() // 녹음 시작
    }

    // 녹음을 중지하고 mediaRecorder이 점유한 모든 리소스를 해제한다.
    fun stopAudio() {
        binding.stopBt.isEnabled = false
        binding.playBt.isEnabled = true

        if (isRecording) {
            binding.recordBt.isEnabled = false
            mediaRecorder?.stop() // 녹음을 중지한다.
            mediaRecorder?.release() // 해당 인스턴스가 점유한 모든 리소스를 해제시킨다.
            mediaRecorder = null
            isRecording = false
        } else {
            mediaPlayer?.release()
            mediaPlayer = null
            binding.recordBt.isEnabled = true
        }
    }

    // 외부 저장소에 있는 오디오 파일을 재생
    fun playAudio() {
        binding.playBt.isEnabled = false
        binding.recordBt.isEnabled = false
        binding.stopBt.isEnabled = true

        mediaPlayer = MediaPlayer()
        mediaPlayer?.setDataSource(audioFilePath) // 재생할 오디오 소스의 위치
        mediaPlayer?.prepare() // 재생 시작을 준비
        mediaPlayer?.start() // 재생 시작
    }
}