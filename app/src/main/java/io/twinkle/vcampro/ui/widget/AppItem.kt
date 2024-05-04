package io.twinkle.vcampro.ui.widget

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppItem(painter: Painter, appName: String, packageName: String) {
    Box(Modifier.clickable {  }) {
        Row(
            Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 75.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.size(56.dp), contentAlignment = Alignment.Center) {
                Image(
                    painter = painter, contentDescription = "", modifier = Modifier
                        .size(48.dp)
                        .padding(5.dp)
                )
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(text = appName, fontSize = 15.sp)
                Spacer(Modifier.height(3.dp))
                Text(
                    text = packageName,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                )
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
        packageName = "test"
    )
}