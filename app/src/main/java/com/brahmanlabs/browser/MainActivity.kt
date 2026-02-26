package com.brahmanlabs.browser

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.brahman.browser.databinding.ActivityMainBinding
import com.brahman.browser.ui.tabs.TabManager
import com.brahman.browser.ui.tabs.TabPanelBottomSheet
import kotlinx.coroutines.launch

/**
 * Brahman Browser — MainActivity
 *
 * The single Activity that hosts the entire browser UI.
 * Phase 2 wires together:
 *   1. AddressBarView      — top chrome bar (56 dp)
 *   2. NewTabPage          — placeholder content area
 *   3. TabPanelBottomSheet — swipeable tab switcher
 *   4. DownloadOverlayView — download progress glass panel
 *   5. FloatingPlayerView  — draggable media player glass card
 *
 * Phase 3 will add a real WebView beneath the address bar.
 */
class MainActivity : AppCompatActivity() {

    /* ── ViewBinding ────────────────────────────────────── */

    private lateinit var binding: ActivityMainBinding

    /* ── Tab state ──────────────────────────────────────── */

    private val tabManager = TabManager()

    /* ── Lifecycle ──────────────────────────────────────── */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Edge-to-edge + status bar transparent
        makeFullscreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAddressBar()
        setupTabObserver()
        setupDownloadOverlay()
        setupFloatingPlayer()
    }

    /* ── 1. Address bar ─────────────────────────────────── */

    private fun setupAddressBar() {
        with(binding.addressBar) {

            // Back — Phase 2: no navigation, just callback placeholder
            onBackClick = {
                // Phase 3: webView.goBack()
            }

            // Forward — Phase 2: no navigation
            onForwardClick = {
                // Phase 3: webView.goForward()
            }

            // Refresh — Phase 2: no navigation
            onRefreshClick = {
                // Phase 3: webView.reload()
            }

            // Tab switcher pill
            onTabsClick = {
                openTabPanel()
            }

            // URL submit — Phase 2: update tab model only
            onUrlSubmit = { query ->
                val url = resolveUrl(query)
                tabManager.updateActiveTab(url = url, title = query)
                // Phase 3: webView.loadUrl(url)
            }

            // Init nav state
            setCanGoBack(false)
            setCanGoForward(false)
        }
    }

    /* ── 2. Tab state observer ──────────────────────────── */

    private fun setupTabObserver() {
        lifecycleScope.launch {
            tabManager.tabs.collect { tabs ->
                // Update address bar pill count
                binding.addressBar.setTabCount(tabs.size)

                // Update active tab URL display
                val active = tabs.firstOrNull { it.isActive }
                binding.addressBar.setUrl(active?.url ?: "")

                // Update new-tab page visibility
                binding.layoutNewTabPage.visibility =
                    if (active?.hasPage == true) View.GONE else View.VISIBLE
            }
        }
    }

    /* ── 3. Tab panel ───────────────────────────────────── */

    private fun openTabPanel() {
        val sheet = TabPanelBottomSheet()

        sheet.onTabSelected = { tab ->
            tabManager.switchToTab(tab.id)
        }

        sheet.onTabClosed = { tab ->
            tabManager.closeTab(tab.id)
            // Re-submit updated list into the sheet if still open
            sheet.submitTabs(tabManager.tabs.value)
        }

        sheet.onNewTab = {
            tabManager.openNewTab()
        }

        sheet.show(supportFragmentManager, TabPanelBottomSheet.TAG)

        // Push current tabs into the sheet once it's attached
        supportFragmentManager.executePendingTransactions()
        sheet.submitTabs(tabManager.tabs.value)

        // Keep the sheet updated as tabs change
        lifecycleScope.launch {
            tabManager.tabs.collect { tabs ->
                if (sheet.isAdded) sheet.submitTabs(tabs)
            }
        }
    }

    /* ── 4. Download overlay ────────────────────────────── */

    private fun setupDownloadOverlay() {
        with(binding.downloadOverlay) {
            setFilename("video_4k_sample.mp4")
            setProgress(0)
            setStats(speedMbs = 0f, etaSeconds = 0, downloadedMb = 0f, totalMb = 21.3f)

            onPauseResume = { paused ->
                // Phase 3: pause / resume actual download
            }

            onCancel = {
                // Phase 3: cancel actual download
            }

            onDismiss = {
                // overlay hidden
            }
        }

        // Demo button: show/hide download overlay
        binding.btnDemoDownload.setOnClickListener {
            if (binding.downloadOverlay.visibility == View.VISIBLE) {
                binding.downloadOverlay.dismissWithAnimation()
            } else {
                binding.downloadOverlay.setProgress(0)
                binding.downloadOverlay.showWithAnimation()
                simulateDownload()
            }
        }
    }

    /** Simulates a download progress for Phase 2 demo purposes */
    private fun simulateDownload() {
        var progress = 0
        val handler  = android.os.Handler(mainLooper)
        val runnable = object : Runnable {
            override fun run() {
                progress += (2..6).random()
                if (progress > 100) progress = 100

                val downloaded = 21.3f * (progress / 100f)
                binding.downloadOverlay.setProgress(progress)
                binding.downloadOverlay.setStats(
                    speedMbs     = (1.5f..3.5f).random(),
                    etaSeconds   = ((100 - progress) / 3).coerceAtLeast(0),
                    downloadedMb = downloaded,
                    totalMb      = 21.3f
                )

                if (progress < 100) {
                    handler.postDelayed(this, 150)
                } else {
                    binding.downloadOverlay.setComplete()
                }
            }
        }
        handler.postDelayed(runnable, 200)
    }

    /* ── 5. Floating player ─────────────────────────────── */

    private fun setupFloatingPlayer() {
        with(binding.floatingPlayer) {
            onPlayPause = { isPlaying ->
                // Phase 3: ExoPlayer.play() / pause()
            }
            onDismiss = {
                // player hidden
            }
        }

        // Demo button: show/hide floating player
        binding.btnDemoPlayer.setOnClickListener {
            if (binding.floatingPlayer.visibility == View.VISIBLE) {
                binding.floatingPlayer.dismissWithAnimation()
            } else {
                // Position bottom-right before showing
                binding.floatingPlayer.post {
                    binding.floatingPlayer.resetPosition(
                        parentWidth  = binding.contentFrame.width,
                        parentHeight = binding.contentFrame.height
                    )
                }
                binding.floatingPlayer.showWithAnimation()
            }
        }
    }

    /* ── Helpers ─────────────────────────────────────────── */

    /**
     * Converts a raw user query into a proper URL.
     * Phase 3: expand with search engine preference (Google / DuckDuckGo / etc.)
     */
    private fun resolveUrl(query: String): String {
        return when {
            query.startsWith("http://")  -> query
            query.startsWith("https://") -> query
            query.contains(".")          -> "https://$query"
            else                         -> "https://www.google.com/search?q=${query.trim()}"
        }
    }

    private fun makeFullscreen() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }

    /** Extension to get a random Float in a range */
    private fun ClosedFloatingPointRange<Float>.random(): Float =
        start + Math.random().toFloat() * (endInclusive - start)
}