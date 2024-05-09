package io.twinkle.unreal.ui.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.twinkle.unreal.ui.theme.VcamProTheme

@Composable
fun WarningBar(
    modifier: Modifier = Modifier,
    visible: Boolean = false,
    text: @Composable () -> Unit,
    warningIcon: @Composable () -> Unit = {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = "warning"
        )
    },
    containerColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
    onContainerContentColor: Color = MaterialTheme.colorScheme.onTertiaryContainer,
    actions: @Composable RowScope.() -> Unit = {},
    onBarClick: () -> Unit = {}
) {
    if (visible) {
        Box(modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 42.dp)
                    .background(
                        color = containerColor,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clip(
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { onBarClick() },
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    Modifier.padding(horizontal = 16.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CompositionLocalProvider(LocalContentColor provides onContainerContentColor) {
                        warningIcon()
                    }
                    Spacer(Modifier.width(16.dp))
                    Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                        ProvideTextStyle(
                            value = TextStyle(
                                color = onContainerContentColor,
                                fontSize = 15.sp
                            ), content = text
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    CompositionLocalProvider(LocalContentColor provides onContainerContentColor) {
                        Row(content = actions)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun WarningBarPreview() {
    VcamProTheme {
        WarningBar(visible = true, text = { Text(text = "这是一条警示信息！") }, actions = {
            TextButton(onClick = { }) {
                Text(text = "OK")
            }
        })
    }
}