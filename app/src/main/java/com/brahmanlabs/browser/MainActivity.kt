class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Phase 2 layout

        // --- Top Bar Buttons (placeholders) ---
        val backButton = findViewById<ImageButton>(R.id.btn_back)
        val forwardButton = findViewById<ImageButton>(R.id.btn_forward)
        val refreshButton = findViewById<ImageButton>(R.id.btn_refresh)
        val urlInput = findViewById<EditText>(R.id.url_input)

        backButton.setOnClickListener {
            // placeholder click effect
        }
        forwardButton.setOnClickListener {
            // placeholder click effect
        }
        refreshButton.setOnClickListener {
            // placeholder click effect
        }

        // --- Placeholder: WebView ---
        // we add the WebView programmatically as hidden for now
        val webView = WebView(this)
        webView.layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )
        webView.visibility = View.GONE // hidden in Phase 2
        (findViewById<RelativeLayout>(R.id.root_layout)).addView(webView)

        // WebView settings ready for Phase 3
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true

        // --- Floating Player Placeholder ---
        val floatingPlayer = findViewById<LinearLayout>(R.id.floating_player)
        // draggable setup can be added later in Phase 3

        // --- Download Overlay Placeholder ---
        val downloadOverlay = findViewById<LinearLayout>(R.id.download_overlay)
        // remains semi-transparent for Phase 2
    }
}