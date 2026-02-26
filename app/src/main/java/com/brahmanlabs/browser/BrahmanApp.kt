package com.brahmanlabs.browser

import android.app.Application

/**
 * Brahman Browser — Application class
 * Phase 2: Initialises app-wide singletons.
 * Phase 3+ will add: WebView engine, AdBlock engine, Download manager.
 */
class BrahmanApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // Phase 3: init ad-block engine, cache, etc.
    }

    companion object {
        const val TAG = "Brahman"
    }
}
