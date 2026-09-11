package com.example.browser.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkEmeraldBlack
import com.example.ui.theme.EmeraldBright
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.GlassBackground
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderBright
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.NeonRedAlert
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@Composable
fun GlassPanel(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(18.dp),
    backgroundColor: Color = GlassSurface,
    borderColor: Color = GlassBorder,
    borderWidth: Dp = 1.dp,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        backgroundColor.copy(alpha = 0.85f),
                        backgroundColor.copy(alpha = 0.60f)
                    )
                )
            )
            .border(
                border = BorderStroke(borderWidth, borderColor),
                shape = shape
            )
    ) {
        content()
    }
}

@Composable
fun GlassIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "",
    enabled: Boolean = true,
    tint: Color = EmeraldBright,
    size: Dp = 48.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val effectiveTint = if (enabled) tint else TextMuted.copy(alpha = 0.4f)

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = EmeraldBright),
                onClick = onClick
            )
            .testTag(if (testTag.isNotEmpty()) testTag else contentDescription),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = effectiveTint,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
fun ZeroTraceStatusBadge(
    modifier: Modifier = Modifier,
    text: String = "0 CACHE • NO HISTORY",
    isSecure: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(DarkEmeraldBlack.copy(alpha = 0.8f))
            .border(BorderStroke(1.dp, GlassBorder), RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(EmeraldBright.copy(alpha = dotAlpha))
        )
        Spacer(modifier = Modifier.width(7.dp))
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
            color = if (isSecure) EmeraldBright else TextPrimary
        )
    }
}

@Composable
fun CyberProgressBar(
    progress: Int,
    modifier: Modifier = Modifier
) {
    if (progress in 1..99) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(ObsidianBlack)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = progress / 100f)
                    .height(3.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                EmeraldDark,
                                EmeraldBright,
                                Color(0xFF69F0AE)
                            )
                        )
                    )
            )
        }
    }
}

@Composable
fun NukeFloatingButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isMini: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF0C2618),
                        Color(0xFF143825)
                    )
                )
            )
            .border(
                BorderStroke(1.2.dp, GlassBorderBright),
                RoundedCornerShape(28.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = EmeraldBright),
                onClick = onClick
            )
            .padding(horizontal = if (isMini) 12.dp else 16.dp, vertical = if (isMini) 8.dp else 10.dp)
            .testTag("nuke_purge_button"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(EmeraldBright)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "⚡ PURGE SESSION",
            fontSize = if (isMini) 11.sp else 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = EmeraldBright
        )
    }
}
