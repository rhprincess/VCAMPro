package io.twinkle.unreal.ui.page

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.blankj.utilcode.util.FileUtils
import io.twinkle.unreal.BuildConfig
import io.twinkle.unreal.R
import io.twinkle.unreal.ui.viewmodel.MainViewModel
import io.twinkle.unreal.util.toAbsoluteFilePath
import java.io.File
import java.net.URI


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(vm: MainViewModel = viewModel(), isActivated: Boolean = false) {
    val uiState by vm.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val context = LocalContext.current
    val globalVideo = File(uiState.globalVideoPath)
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.app_name))
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        }) { innerPadding ->
        // DashBoard
        Column(
            modifier = Modifier
                .padding(
                    PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = innerPadding.calculateTopPadding(),
                        bottom = innerPadding.calculateBottomPadding()
                    )
                )
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(
                        if (isActivated) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(16.dp)
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(75.dp)
                        .padding(8.dp), contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isActivated) Icons.Filled.Done else Icons.Filled.Close,
                        contentDescription = "isActive",
                        tint = if (isActivated) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
                    )
                }
                Column {
                    Text(
                        text = if (isActivated) "已激活" else "未激活",
                        fontSize = 15.sp,
                        fontWeight = if (isActivated) FontWeight.Bold else FontWeight.Normal
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = "版本: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE}) - ${BuildConfig.BUILD_TYPE}",
                        fontSize = 13.sp
                    )
                }
            }

            var filePath by rememberSaveable { mutableStateOf("") }
            val openSelectVideoLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent(),
                onResult = {//这里返回的是uri？
                    it?.let { uri ->
                        filePath = uri.toAbsoluteFilePath(context)
                        vm.handleFirstFrame(filePath)
                    }
                })


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(
                        MaterialTheme.colorScheme.tertiaryContainer,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .clickable {
                        openSelectVideoLauncher.launch("video/*")
                    },
                contentAlignment = Alignment.CenterStart
            ) {
                Image(
                    bitmap = uiState.globalVideoFirstFrame,
                    contentDescription = "video first frame",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.tertiaryContainer,
                                    MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.75f),
                                    Color.Transparent
                                ),
                                tileMode = TileMode.Decal
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clip(RoundedCornerShape(16.dp))
                )
                Text(text = "选择全局视频", modifier = Modifier.padding(start = 32.dp))

                if (uiState.globalVideoExists) {
                    IconButton(
                        modifier = Modifier
                            .padding(end = 32.dp)
                            .align(Alignment.CenterEnd),
                        onClick = {
                            globalVideo.delete().run {
                                vm.update { it.copy(globalVideoExists = false) }
                                vm.handleFirstFrame()
                            }
                        }) {
                        Icon(imageVector = Icons.Outlined.Delete, contentDescription = "delete")
                    }
                }
            }

        }
    }
}