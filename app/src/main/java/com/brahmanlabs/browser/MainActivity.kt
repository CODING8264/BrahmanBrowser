package com.brahmanlabs.browser

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.webkit.*
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var urlEditText: EditText
    private lateinit var goButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        urlEditText = findViewById(R.id.urlEditText)
        goButton = findViewById(R.id.goButton)

        setupWebView()
        setupButton()
        requestStoragePermission()
    }

    private fun setupWebView() {
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                urlEditText.setText(url)
            }
        }

        webView.webChromeClient = WebChromeClient()

        // Handle downloads
        webView.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
            if (hasStoragePermission()) {
                val request = DownloadManager.Request(Uri.parse(url))
                request.setMimeType(mimetype)
                request.addRequestHeader("User-Agent", userAgent)
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                request.setDestinationInExternalPublicDir("/Download", URLUtil.guessFileName(url, contentDisposition, mimetype))
                val dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                dm.enqueue(request)
                Toast.makeText(this, "Download started...", Toast.LENGTH_SHORT).show()
            } else {
                requestStoragePermission()
            }
        }

        webView.loadUrl("https://www.google.com")
    }

    private fun setupButton() {
        goButton.setOnClickListener {
            var url = urlEditText.text.toString()
            if (!url.startsWith("http")) url = "https://$url"
            webView.loadUrl(url)
        }
    }

    private fun hasStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        } else true
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(this, "Storage permission denied. Downloads won't work.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestStoragePermission() {
        if (!hasStoragePermission() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}