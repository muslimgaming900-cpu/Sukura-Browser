package com.example.browser.model

enum class SearchEngine(val displayName: String, val searchUrl: String, val homeUrl: String) {
    DUCK_DUCK_GO("DuckDuckGo", "https://duckduckgo.com/?q=", "https://duckduckgo.com"),
    BRAVE("Brave", "https://search.brave.com/search?q=", "https://search.brave.com"),
    STARTPAGE("Startpage", "https://www.startpage.com/sp/search?query=", "https://www.startpage.com"),
    GOOGLE("Google", "https://www.google.com/search?q=", "https://www.google.com"),
    ECOSIA("Ecosia", "https://www.ecosia.org/search?q=", "https://www.ecosia.org")
}

data class QuickSite(
    val title: String,
    val url: String,
    val category: String,
    val iconLetter: String
)

val defaultQuickSites = listOf(
    QuickSite("DuckDuckGo", "https://duckduckgo.com", "Private Search", "D"),
    QuickSite("Quran.com", "https://quran.com", "Knowledge", "Q"),
    QuickSite("Wikipedia", "https://wikipedia.org", "Encyclopedia", "W"),
    QuickSite("GitHub", "https://github.com", "Developer", "G"),
    QuickSite("IslamicFinder", "https://www.islamicfinder.org", "Prayer & Times", "I"),
    QuickSite("Hacker News", "https://news.ycombinator.com", "Tech News", "H"),
    QuickSite("Startpage", "https://www.startpage.com", "Private Search", "S"),
    QuickSite("Brave Search", "https://search.brave.com", "Ad-Free Search", "B")
)

data class BrowserTab(
    val id: String = java.util.UUID.randomUUID().toString(),
    val url: String = "",
    val title: String = "New Ephemeral Tab",
    val isLoading: Boolean = false,
    val progress: Int = 0,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val isSecure: Boolean = false,
    val isDesktopMode: Boolean = false
)

data class PrivacyStats(
    val cacheSize: String = "0 KB",
    val historyEntries: Int = 0,
    val cookiesBlocked: Boolean = true,
    val ephemeralRamOnly: Boolean = true,
    val trackingProtection: Boolean = true
)
