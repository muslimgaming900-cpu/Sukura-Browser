package com.example.browser

import android.content.Context
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebStorage
import android.webkit.WebView
import com.example.browser.model.SearchEngine
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object PrivacyEngine {

    const val DESKTOP_USER_AGENT =
        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36"

    /**
     * Strict zero-trace WebSettings configuration.
     * Enforces zero cache and disables persistent storage features.
     */
    fun applyZeroTraceSettings(settings: WebSettings, isDesktop: Boolean = false) {
        settings.javaScriptEnabled = true
        // CRITICAL: Absolute zero-cache policy
        settings.cacheMode = WebSettings.LOAD_NO_CACHE
        settings.databaseEnabled = false
        settings.saveFormData = false
        @Suppress("DEPRECATION")
        settings.savePassword = false
        settings.domStorageEnabled = true // Required for modern web apps, wiped on clear
        settings.setGeolocationEnabled(false)
        settings.allowFileAccess = false
        settings.allowContentAccess = false
        settings.mediaPlaybackRequiresUserGesture = true
        settings.builtInZoomControls = true
        settings.displayZoomControls = false
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW

        if (isDesktop) {
            settings.userAgentString = DESKTOP_USER_AGENT
        } else {
            settings.userAgentString = null // Default mobile user agent
        }
    }

    /**
     * Performs a complete nuclear purge of all web data, cookies, DOM storage,
     * internal WebView cache, and application cache files.
     */
    fun executeNuclearPurge(context: Context, webView: WebView?) {
        try {
            webView?.stopLoading()
            webView?.clearCache(true)
            webView?.clearHistory()
            webView?.clearFormData()
            webView?.clearSslPreferences()
            webView?.loadUrl("about:blank")

            // Flush all cookies
            val cookieManager = CookieManager.getInstance()
            cookieManager.removeAllCookies(null)
            cookieManager.removeSessionCookies(null)
            cookieManager.flush()

            // Delete all HTML5 Web Storage (localStorage, sessionStorage, indexedDB)
            WebStorage.getInstance().deleteAllData()

            // Clean application cache directories
            context.cacheDir?.deleteRecursively()
            context.codeCacheDir?.deleteRecursively()
        } catch (_: Exception) {
            // Ignore purge cleanup anomalies
        }
    }

    /**
     * Translates user input into a navigable URL or search engine query.
     */
    fun resolveInput(rawInput: String, searchEngine: SearchEngine): String {
        val trimmed = rawInput.trim()
        if (trimmed.isEmpty()) return ""

        if (trimmed.startsWith("http://", ignoreCase = true) ||
            trimmed.startsWith("https://", ignoreCase = true) ||
            trimmed.startsWith("about:", ignoreCase = true) ||
            trimmed.startsWith("file:", ignoreCase = true)
        ) {
            return trimmed
        }

        // Domain heuristic: contains dot, no spaces, not ending with dot
        val isDomainLike = trimmed.contains(".") &&
                !trimmed.contains(" ") &&
                !trimmed.endsWith(".") &&
                trimmed.indexOf(".") > 0

        return if (isDomainLike) {
            "https://$trimmed"
        } else {
            val encodedQuery = URLEncoder.encode(trimmed, StandardCharsets.UTF_8.toString())
            "${searchEngine.searchUrl}$encodedQuery"
        }
    }
}
