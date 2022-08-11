package com.example.printapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.print.PrintHelper
import com.example.printapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    val binding by lazy { ActivityMainBinding.inflate((layoutInflater))}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        setContentView(binding.root)


        var url: String = "http://58.151.46.178:48000/img_skin/doc.html?key=Winserver_And_naver_docx2021%20(18).docx&convType=img&imageConverting=true&convLocale=ko_KR&contextPath=/img_IMG_QA-380_1cha_0214444444/Winserver_And_naver_docx2021%20(18).docx"
        printWebView(url)

        binding.printBt.setOnClickListener {
            createWebPrintJob(binding.webView)
        }
    }

    fun printWebView(url: String) {

        binding.webView.webViewClient = object : WebViewClient() { //HTML 콘텐츠를 WebView 함수에 로드

            // 현재 웹뷰에 로드될 URL에 대한 컨트롤 - URL 접속할때 호출.
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                return super.shouldOverrideUrlLoading(view, request)
            }
        }

        binding.webView.settings.javaScriptEnabled = true // 웹뷰에서 자바스크립트 동자
        binding.webView.loadUrl(url)  // 해당 url을 웹뷰에서 Open

    }

    // 인쇄작업
   fun createWebPrintJob(webView: WebView?) {
        val printManager = this.getSystemService(Context.PRINT_SERVICE) as PrintManager // 인쇄 매니저 서비스 객체를 참조
        val printAdapter = webView?.createPrintDocumentAdapter("MyDocument") // 인쇄 어댑터를 생성
        val jobName = getString(R.string.app_name) + " Document" // 인쇄 작업의 이름

        printManager.print(jobName, printAdapter!!, PrintAttributes.Builder().build()) // 인쇄시작
    }
}