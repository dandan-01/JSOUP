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

        // Enable JavaScript in WebView. (this is if you want to use the same interactivity in the website)
        webView.settings.javaScriptEnabled = true

        // Set WebView client to handle page navigation
        webView.webViewClient = WebViewClient()

        // CoroutineScope for handling asynchronous tasks
        // We're using coroutine here to fetch and parse information
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Fetch HTML content from the webpage using GET
                val document = Jsoup.connect(url).get()

                // Find all <link rel="stylesheet"> elements to fetch CSS
                val cssLinks = document.select("link[rel=stylesheet]")

                // StringBuilder to hold all CSS
                val allCss = StringBuilder()

                // Fetch each CSS linked in the HTML and append it to allCss
                cssLinks.forEach { link ->
                    val cssUrl = link.absUrl("href") // Ensure the URL is absolute
                    try {
                        val cssContent =
                            Jsoup.connect(cssUrl).ignoreContentType(true).execute().body()
                        allCss.append(cssContent).append("\n")
                    } catch (e: Exception) {
                        Log.e("CSS Fetch Error", "Could not fetch CSS from $cssUrl", e)
                    }
                }

                // Inject the fetched CSS into a <style> tag in the document's <head>
                document.head().appendElement("style").text(allCss.toString())

                // Convert the modified document to a string
                val htmlContentWithInlineCss = document.toString()

                // Load the modified HTML into the WebView on the main thread
                withContext(Dispatchers.Main) {
                    webView.loadDataWithBaseURL(
                        "https://www.iana.org/",
                        htmlContentWithInlineCss,
                        "text/html",
                        "UTF-8",
                        null
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}