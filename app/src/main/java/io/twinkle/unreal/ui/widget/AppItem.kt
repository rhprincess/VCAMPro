package io.twinkle.unreal.ui.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.twinkle.unreal.R

@Composable
fun AppItem(
    modifier: Modifier = Modifier,
    painter: Painter,
    appName: String,
    packageName: String,
    isCameraApp: Boolean,
    isEnable: Boolean
) {
    Box(modifier) {
        Row(
            Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 75.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.size(56.dp), contentAlignment = Alignment.Center) {
                Image(
                    painter = painter, contentDescription = "", modifier = Modifier.size(48.dp)
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(text = appName, fontSize = 15.sp)
                if (isEnable) {
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = "已启用",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 15.sp
                    )
                }
                Spacer(Modifier.height(3.dp))
                Text(
                    text = packageName,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                )
            }
            if (isCameraApp) {
                Box(
                    Modifier
                        .size(42.dp)
                        .alpha(0.15f), contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.outline_photo_camera_24),
                        contentDescription = "isCameraApp"
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun AppItemPreview() {
    AppItem(
        painter = rememberVectorPainter(image = Icons.Default.Done),
        appName = "Test",
        packageName = "test",
        isCameraApp = true,
        isEnable = true
    )
}