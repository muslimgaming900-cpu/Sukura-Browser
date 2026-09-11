package com.example.browser.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FindInPage
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.browser.PrivacyEngine
import com.example.browser.model.SearchEngine
import com.example.ui.theme.DarkEmeraldBlack
import com.example.ui.theme.DeepEmeraldCard
import com.example.ui.theme.EmeraldBright
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldGlow
import com.example.ui.theme.GlassBackground
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderBright
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.NeonRedAlert
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ActiveBrowserScreen(
    initialUrl: String,
    searchEngine: SearchEngine,
    onNavigateHome: () -> Unit,
    onPurgeSession: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var currentUrl by remember { mutableStateOf(initialUrl) }
    var pageTitle by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var progress by remember { mutableIntStateOf(0) }
    var canGoBack by remember { mutableStateOf(false) }
    var canGoForward by remember { mutableStateOf(false) }
    var isDesktopMode by remember { mutableStateOf(false) }
    var isEditingUrl by remember { mutableStateOf(false) }
    var inputUrlText by remember { mutableStateOf(initialUrl) }
    var showMenu by remember { mutableStateOf(false) }
    var showSecurityDialog by remember { mutableStateOf(false) }
    var isFindInPageActive by remember { mutableStateOf(false) }
    var findQuery by remember { mutableStateOf("") }

    val focusRequester = remember { FocusRequester() }

    // Intercept back button to navigate WebView back stack first
    BackHandler {
        if (isEditingUrl) {
            isEditingUrl = false
            inputUrlText = currentUrl
        } else if (isFindInPageActive) {
            isFindInPageActive = false
            webViewInstance?.clearMatches()
        } else if (webViewInstance?.canGoBack() == true) {
            webViewInstance?.goBack()
        } else {
            onNavigateHome()
        }
    }

    // Handle initial load or external navigation changes
    LaunchedEffect(initialUrl) {
        if (initialUrl.isNotBlank() && initialUrl != currentUrl) {
            currentUrl = initialUrl
            inputUrlText = initialUrl
            webViewInstance?.loadUrl(initialUrl)
        }
    }

    // Toggle Desktop Mode
    LaunchedEffect(isDesktopMode) {
        webViewInstance?.let { wv ->
            PrivacyEngine.applyZeroTraceSettings(wv.settings, isDesktopMode)
            wv.reload()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
    ) {
        // TOP GLASS APP BAR (Respects status bar insets)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(GlassBackground)
                .windowInsetsPadding(WindowInsets.statusBars)
                .border(BorderStroke(0.8.dp, GlassBorder))
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Home icon
                    GlassIconButton(
                        icon = Icons.Default.Home,
                        contentDescription = "Home",
                        onClick = onNavigateHome,
                        testTag = "browser_nav_home",
                        tint = EmeraldBright
                    )

                    // URL / Search Input Bar
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(DarkEmeraldBlack)
                            .border(
                                BorderStroke(
                                    1.dp,
                                    if (isEditingUrl) EmeraldBright else GlassBorder
                                ),
                                RoundedCornerShape(22.dp)
                            )
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // SSL Lock Icon
                            Icon(
                                imageVector = if (currentUrl.startsWith("https://", ignoreCase = true)) {
                                    Icons.Default.Lock
                                } else {
                                    Icons.Default.Warning
                                },
                                contentDescription = "Security Status",
                                tint = if (currentUrl.startsWith("https://", ignoreCase = true)) {
                                    EmeraldBright
                                } else {
                                    Color(0xFFFFB300)
                                },
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { showSecurityDialog = true }
                                    .testTag("ssl_indicator")
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            if (isEditingUrl) {
                                BasicTextField(
                                    value = inputUrlText,
                                    onValueChange = { inputUrlText = it },
                                    modifier = Modifier
                                        .weight(1f)
                                        .focusRequester(focusRequester)
                                        .testTag("browser_url_input"),
                                    singleLine = true,
                                    textStyle = TextStyle(
                                        color = TextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Normal
                                    ),
                                    cursorBrush = SolidColor(EmeraldBright),
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                                    keyboardActions = KeyboardActions(
                                        onGo = {
                                            val target = PrivacyEngine.resolveInput(inputUrlText, searchEngine)
                                            if (target.isNotBlank()) {
                                                currentUrl = target
                                                webViewInstance?.loadUrl(target)
                                            }
                                            isEditingUrl = false
                                            keyboardController?.hide()
                                        }
                                    )
                                )

                                IconButton(
                                    onClick = {
                                        inputUrlText = ""
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear",
                                        tint = TextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else {
                                // Display formatted domain / title
                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            isEditingUrl = true
                                            inputUrlText = currentUrl
                                        }
                                        .testTag("url_display_bar"),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val displayHost = try {
                                        val uri = Uri.parse(currentUrl)
                                        uri.host ?: currentUrl
                                    } catch (_: Exception) {
                                        currentUrl
                                    }

                                    Text(
                                        text = displayHost.ifEmpty { "Search or type URL" },
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            // Reload / Stop button
                            IconButton(
                                onClick = {
                                    if (isLoading) {
                                        webViewInstance?.stopLoading()
                                    } else {
                                        webViewInstance?.reload()
                                    }
                                },
                                modifier = Modifier.size(28.dp).testTag("browser_reload_button")
                            ) {
                                Icon(
                                    imageVector = if (isLoading) Icons.Default.Close else Icons.Default.Refresh,
                                    contentDescription = if (isLoading) "Stop Loading" else "Reload",
                                    tint = EmeraldBright,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    // More Menu
                    Box {
                        GlassIconButton(
                            icon = Icons.Default.MoreVert,
                            contentDescription = "Menu",
                            onClick = { showMenu = !showMenu },
                            testTag = "browser_overflow_menu",
                            tint = EmeraldBright
                        )

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier
                                .background(DarkEmeraldBlack)
                                .border(BorderStroke(1.dp, GlassBorderBright), RoundedCornerShape(12.dp))
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = if (isDesktopMode) "Mobile Version" else "Desktop Site",
                                        color = TextPrimary,
                                        fontSize = 13.sp
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (isDesktopMode) Icons.Default.Smartphone else Icons.Default.Computer,
                                        contentDescription = null,
                                        tint = EmeraldBright
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    isDesktopMode = !isDesktopMode
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Find in Page", color = TextPrimary, fontSize = 13.sp) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.FindInPage,
                                        contentDescription = null,
                                        tint = EmeraldBright
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    isFindInPageActive = true
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Share Page", color = TextPrimary, fontSize = 13.sp) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = null,
                                        tint = EmeraldBright
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, currentUrl)
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Share via"))
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("0-Trace Security Info", color = TextPrimary, fontSize = 13.sp) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = EmeraldBright
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    showSecurityDialog = true
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "⚡ Purge Session",
                                        color = NeonRedAlert,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.DeleteSweep,
                                        contentDescription = null,
                                        tint = NeonRedAlert
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    onPurgeSession()
                                }
                            )
                        }
                    }
                }

                // Cyber Loading Progress Bar
                CyberProgressBar(progress = progress)
            }
        }

        // FIND IN PAGE FLOATING PANEL
        AnimatedVisibility(
            visible = isFindInPageActive,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DeepEmeraldCard)
                    .border(BorderStroke(1.dp, GlassBorder))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = findQuery,
                    onValueChange = {
                        findQuery = it
                        webViewInstance?.findAllAsync(it)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(ObsidianBlack)
                        .border(BorderStroke(1.dp, GlassBorder), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                        .testTag("find_in_page_input"),
                    textStyle = TextStyle(color = TextPrimary, fontSize = 13.sp),
                    cursorBrush = SolidColor(EmeraldBright),
                    singleLine = true
                )

                IconButton(
                    onClick = { webViewInstance?.findNext(false) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Previous Match",
                        tint = EmeraldBright
                    )
                }

                IconButton(
                    onClick = { webViewInstance?.findNext(true) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Next Match",
                        tint = EmeraldBright
                    )
                }

                IconButton(
                    onClick = {
                        isFindInPageActive = false
                        webViewInstance?.clearMatches()
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Find",
                        tint = TextMuted
                    )
                }
            }
        }

        // WEBVIEW EMBED
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(ObsidianBlack)
        ) {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("browser_webview"),
                factory = { ctx ->
                    WebView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )

                        // Strict Zero-Trace & No Cache Setup
                        PrivacyEngine.applyZeroTraceSettings(settings, isDesktopMode)

                        // Disallow cookies third-party
                        CookieManager.getInstance().setAcceptThirdPartyCookies(this, false)

                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                super.onPageStarted(view, url, favicon)
                                isLoading = true
                                url?.let {
                                    currentUrl = it
                                    if (!isEditingUrl) inputUrlText = it
                                }
                                canGoBack = view?.canGoBack() == true
                                canGoForward = view?.canGoForward() == true
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                isLoading = false
                                progress = 100
                                url?.let {
                                    currentUrl = it
                                    if (!isEditingUrl) inputUrlText = it
                                }
                                canGoBack = view?.canGoBack() == true
                                canGoForward = view?.canGoForward() == true
                            }

                            override fun shouldOverrideUrlLoading(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): Boolean {
                                val reqUrl = request?.url?.toString() ?: return false
                                return if (reqUrl.startsWith("http://") || reqUrl.startsWith("https://")) {
                                    false
                                } else {
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(reqUrl))
                                        context.startActivity(intent)
                                    } catch (_: Exception) {
                                    }
                                    true
                                }
                            }
                        }

                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                progress = newProgress
                                if (newProgress >= 100) isLoading = false
                            }

                            override fun onReceivedTitle(view: WebView?, title: String?) {
                                title?.let { pageTitle = it }
                            }
                        }

                        loadUrl(currentUrl)
                        webViewInstance = this
                    }
                },
                update = { webView ->
                    webViewInstance = webView
                }
            )
        }

        // BOTTOM GLASS CONTROLS BAR (Respects navigation insets)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(GlassBackground)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .border(BorderStroke(0.8.dp, GlassBorder))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Back Button
                GlassIconButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    enabled = canGoBack,
                    onClick = { webViewInstance?.goBack() },
                    testTag = "bottom_nav_back"
                )

                // Forward Button
                GlassIconButton(
                    icon = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Forward",
                    enabled = canGoForward,
                    onClick = { webViewInstance?.goForward() },
                    testTag = "bottom_nav_forward"
                )

                // Central Nuclear Purge Button
                NukeFloatingButton(
                    onClick = onPurgeSession,
                    isMini = true
                )

                // Desktop Mode Toggle Icon
                GlassIconButton(
                    icon = if (isDesktopMode) Icons.Default.Smartphone else Icons.Default.Computer,
                    contentDescription = if (isDesktopMode) "Switch to Mobile" else "Switch to Desktop",
                    tint = if (isDesktopMode) EmeraldBright else TextMuted,
                    onClick = { isDesktopMode = !isDesktopMode },
                    testTag = "bottom_toggle_desktop"
                )

                // Find in page toggle
                GlassIconButton(
                    icon = Icons.Default.FindInPage,
                    contentDescription = "Find",
                    onClick = { isFindInPageActive = !isFindInPageActive },
                    testTag = "bottom_nav_find"
                )
            }
        }
    }

    // Security & Zero-Trace Info Dialog
    if (showSecurityDialog) {
        AlertDialog(
            onDismissRequest = { showSecurityDialog = false },
            confirmButton = {
                TextButton(onClick = { showSecurityDialog = false }) {
                    Text("Close", color = EmeraldBright, fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Zero-Trace",
                        tint = EmeraldBright,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Sakura Security Shield",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "URL: $currentUrl",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "• Cache Mode: LOAD_NO_CACHE (0 Cache Stored)",
                        color = EmeraldBright,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• History Tracking: Permanently Disabled",
                        color = EmeraldBright,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• Form Data / Password Autosave: Disabled",
                        color = EmeraldBright,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• Session Isolation: Cleared on Exit / Purge",
                        color = EmeraldBright,
                        fontSize = 12.sp
                    )
                }
            },
            containerColor = DarkEmeraldBlack,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Clean up WebView on disposal
    DisposableEffect(Unit) {
        onDispose {
            try {
                webViewInstance?.stopLoading()
                webViewInstance?.destroy()
            } catch (_: Exception) {
            }
        }
    }
}
