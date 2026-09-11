package com.example.browser.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.browser.PrivacyEngine
import com.example.browser.model.SearchEngine
import com.example.ui.theme.DarkEmeraldBlack
import com.example.ui.theme.EmeraldBright
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.GlassBorderBright
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun SakuraBrowserApp() {
    val context = LocalContext.current
    var activeUrl by remember { mutableStateOf<String?>(null) }
    var selectedEngine by remember { mutableStateOf(SearchEngine.DUCK_DUCK_GO) }
    var showPurgeBanner by remember { mutableStateOf(false) }

    // Auto-dismiss the purge banner after 2.8s
    LaunchedEffect(showPurgeBanner) {
        if (showPurgeBanner) {
            delay(2800)
            showPurgeBanner = false
        }
    }

    val handlePurgeSession = {
        PrivacyEngine.executeNuclearPurge(context, null)
        activeUrl = null
        showPurgeBanner = true
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack),
        containerColor = ObsidianBlack,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (activeUrl == null) {
                BrowserHomeScreen(
                    selectedEngine = selectedEngine,
                    onEngineSelected = { selectedEngine = it },
                    onNavigate = { queryOrUrl ->
                        val target = PrivacyEngine.resolveInput(queryOrUrl, selectedEngine)
                        if (target.isNotBlank()) {
                            activeUrl = target
                        }
                    },
                    onPurgeSession = handlePurgeSession
                )
            } else {
                ActiveBrowserScreen(
                    initialUrl = activeUrl ?: "",
                    searchEngine = selectedEngine,
                    onNavigateHome = { activeUrl = null },
                    onPurgeSession = handlePurgeSession
                )
            }

            // Cyber Purge Toast Feedback
            AnimatedVisibility(
                visible = showPurgeBanner,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(top = 16.dp, start = 20.dp, end = 20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkEmeraldBlack)
                        .border(BorderStroke(1.2.dp, GlassBorderBright), RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .testTag("purge_notification_banner")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(ForestGreen)
                                .border(BorderStroke(1.dp, EmeraldBright), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlashOn,
                                contentDescription = "Purged",
                                tint = EmeraldBright,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "⚡ ZERO-TRACE PURGE EXECUTED",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp,
                                color = EmeraldBright
                            )
                            Text(
                                text = "0 Cache • 0 History • Cookies & RAM Sanitized",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}
