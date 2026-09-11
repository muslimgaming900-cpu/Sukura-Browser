package com.example.browser.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.browser.model.QuickSite
import com.example.browser.model.SearchEngine
import com.example.browser.model.defaultQuickSites
import com.example.ui.theme.DarkEmeraldBlack
import com.example.ui.theme.DeepEmeraldCard
import com.example.ui.theme.EmeraldBright
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldGlow
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderBright
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.MintAccent
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.SakuraPinkAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BrowserHomeScreen(
    selectedEngine: SearchEngine,
    onEngineSelected: (SearchEngine) -> Unit,
    onNavigate: (String) -> Unit,
    onPurgeSession: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    val submitSearch = {
        if (searchQuery.isNotBlank()) {
            keyboardController?.hide()
            onNavigate(searchQuery)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Hero Banner Art
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.sakura_home_banner),
                    contentDescription = "Sakura Cyber Banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Atmospheric Glass Gradient Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    ObsidianBlack.copy(alpha = 0.6f),
                                    ObsidianBlack
                                )
                            )
                        )
                )

                // Top Floating Status Badge
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ZeroTraceStatusBadge(text = "0 CACHE • 0 HISTORY")
                }
            }
        }

        // Title and Attribution
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "SAKURA BROWSER",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 3.sp,
                    color = EmeraldBright,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(ForestGreen.copy(alpha = 0.8f))
                            .border(BorderStroke(1.dp, GlassBorder), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "by MuslimGaming69",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MintAccent,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "•  Pure Ephemeral Engine",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }
        }

        // Glass Search Bar
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_search_input"),
                    placeholder = {
                        Text(
                            text = "Search with ${selectedEngine.displayName} or enter URL…",
                            color = TextMuted,
                            fontSize = 14.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = EmeraldBright,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = submitSearch,
                                modifier = Modifier.testTag("home_search_go")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = "Go",
                                    tint = EmeraldBright
                                )
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { submitSearch() }),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = GlassSurface,
                        unfocusedContainerColor = GlassSurface,
                        focusedBorderColor = EmeraldBright,
                        unfocusedBorderColor = GlassBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = EmeraldBright
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Search Engine Switcher Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Engine:",
                        fontSize = 11.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        SearchEngine.entries.forEach { engine ->
                            val isSelected = engine == selectedEngine
                            val interactionSource = remember { MutableInteractionSource() }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) EmeraldDark else DarkEmeraldBlack.copy(alpha = 0.7f)
                                    )
                                    .border(
                                        BorderStroke(
                                            1.dp,
                                            if (isSelected) EmeraldBright else GlassBorder
                                        ),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable(
                                        interactionSource = interactionSource,
                                        indication = ripple(bounded = true, color = EmeraldBright),
                                        onClick = { onEngineSelected(engine) }
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .testTag("engine_${engine.name.lowercase()}")
                            ) {
                                Text(
                                    text = engine.displayName,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) EmeraldBright else TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Zero-Trace Privacy Architecture Matrix Card
        item {
            Spacer(modifier = Modifier.height(20.dp))
            GlassPanel(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                backgroundColor = DeepEmeraldCard,
                borderColor = GlassBorderBright
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Shield",
                                tint = EmeraldBright,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Zero-Trace Privacy Architecture",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(ForestGreen)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "ACTIVE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldBright
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 4 Pillars Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PrivacyPillItem(
                            title = "0 Cache",
                            detail = "LOAD_NO_CACHE",
                            modifier = Modifier.weight(1f)
                        )
                        PrivacyPillItem(
                            title = "No History",
                            detail = "0 Logs Stored",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PrivacyPillItem(
                            title = "Ephemeral RAM",
                            detail = "Auto-Sanitize",
                            modifier = Modifier.weight(1f)
                        )
                        PrivacyPillItem(
                            title = "Anti-Tracker",
                            detail = "Cookie Isolated",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Quick Dials Header
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "QUICK DIALS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    color = TextSecondary
                )

                Text(
                    text = "Tap to launch",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Quick Sites Grid
        item {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                maxItemsInEachRow = 2
            ) {
                defaultQuickSites.forEach { site ->
                    QuickSiteCard(
                        site = site,
                        onClick = { onNavigate(site.url) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Instant Purge Banner
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF07140B),
                                    Color(0xFF0F2C1B)
                                )
                            )
                        )
                        .border(
                            BorderStroke(1.dp, GlassBorderBright),
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { onPurgeSession() }
                        .padding(16.dp)
                        .testTag("instant_nuke_card"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(ForestGreen)
                                .border(BorderStroke(1.dp, GlassBorderBright), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Purge",
                                tint = EmeraldBright,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Emergency Session Purge",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Wipe cookies, cache & RAM immediately",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Execute Purge",
                        tint = EmeraldBright,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PrivacyPillItem(
    title: String,
    detail: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(DarkEmeraldBlack.copy(alpha = 0.9f))
            .border(BorderStroke(1.dp, GlassBorder), RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(EmeraldBright)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldBright
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = detail,
                fontSize = 10.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun QuickSiteCard(
    site: QuickSite,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0B1710),
                        Color(0xFF08120D)
                    )
                )
            )
            .border(BorderStroke(1.dp, GlassBorder), RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = EmeraldBright),
                onClick = onClick
            )
            .padding(12.dp)
            .testTag("quicksite_${site.title.lowercase().replace(" ", "_")}")
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(ForestGreen)
                    .border(BorderStroke(1.dp, GlassBorderBright), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = site.iconLetter,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldBright,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = site.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = site.category,
                    fontSize = 10.sp,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
