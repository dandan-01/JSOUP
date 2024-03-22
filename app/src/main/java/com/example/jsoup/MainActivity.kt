package com.example.jsoup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // defining webview using id from layout
        val webView: WebView = findViewById(R.id.webView)

        // URL of the webpage to parse
        val url = "https://www.iana.org/"

        // Enable JavaScript in WebView. (this is if you want to use the same interactivity as the url defined)
        webView.settings.javaScriptEnabled = true

        // Set WebView client to handle page navigation
        webView.webViewClient = WebViewClient()

        // CoroutineScope for handling asynchronous tasks
        // We're using coroutine here to fetch and parse information
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Fetch HTML content from the webpage using GET
                val document = Jsoup.connect(url).get()

                // htmlContent representing parsed HTML content, converted to String
                val htmlContent = document.toString()

                // Update UI on the main thread
                withContext(Dispatchers.Main) {
                    // Load HTML content into WebView
                    webView.loadData(htmlContent, "text/html", "UTF-8")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}