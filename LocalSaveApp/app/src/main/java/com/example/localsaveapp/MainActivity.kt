package com.example.localsaveapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.localsaveapp.databinding.ActivityMainBinding
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import kotlin.text.StringBuilder


// 안드로이드 로컬 디스크에서 파일 불러오기
class MainActivity : AppCompatActivity() {

    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    val OPEN_REQUEST_CODE = 41

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        setContentView(binding.root)

        binding.openButton.setOnClickListener {
            fileOpen()
        }
    }

    // 파일 열기 UI를 제공
    fun fileOpen() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT) // 파일을 선택 할 수 있는 피커 사용자 인터페이스를 제공
        // 열기 가능한 파일의 형식을 제한
        intent.addCategory(Intent.CATEGORY_OPENABLE) // 열기 가능한 파일 형식을 제한
        intent.type = "*/*"
        startActivityForResult(intent, OPEN_REQUEST_CODE)
    }

    // Intent 시작 -> 여기로
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var currentUri: Uri? = null

        if(requestCode == OPEN_REQUEST_CODE) {

            data?.let {
                currentUri = it.data
                try {
                    val content = readFileContent(currentUri)
                    binding.textView.setText(content) // TextView에 뿌려줌.
                } catch (e: IOException) {
                    println(" error : ${e}")
                }
            }
        }
    }

    // 텍스트 파일 읽기
    fun readFileContent(uri: Uri?) : StringBuilder {
        val inputStream = contentResolver.openInputStream(uri!!)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val currentLine = reader.readLines()
        var textData = StringBuilder() // 여러개의 문장을 저장 - 변수에 함수 할당.

        currentLine.let {
            for (line in currentLine) {
                textData.append(line + "\n") // 한 문장씩 textData에 저장
            }
        }

        inputStream!!.close()
        return textData
    }
}