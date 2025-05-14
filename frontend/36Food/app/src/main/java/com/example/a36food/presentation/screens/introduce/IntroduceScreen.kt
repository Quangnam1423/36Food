package com.example.a36food.presentation.screens.introduce

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.a36food.R
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.a36food.presentation.viewmodel.IntroduceViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class IntroPage(val image: Int, val title: String, val description: String)

val introPages = listOf(
    IntroPage(
        R.drawable.introduce_image,
        "Những món ăn bạn yêu thích",
        "Đem đến cho bạn tất cả những món ăn mà bạn yêu thích"
    ),
    IntroPage(
        R.drawable.introduce_image,
        "Những món ăn bạn yêu thích",
        "Đa dạng lựa chọn, từ đồ ăn đường phố cho đến nhà hàng sang trọng nhất"
    ),
    IntroPage(
        R.drawable.introduce_image,
        "Những món ăn bạn yêu thích",
        "Rất nhiều ưu đãi, mã giảm giá, khuyến mãi cho bạn"
    )
)

@Composable
fun IntroduceScreen(
    viewModel: IntroduceViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var showLoading by remember { mutableStateOf(true) }
    val state by viewModel.state.collectAsStateWithLifecycle()

    val scale by animateFloatAsState(
        targetValue = if (showLoading) 1.2f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // First check if we should skip intro
    LaunchedEffect(Unit) {
        viewModel.checkFirstLaunch()
    }

    // Handle navigation based on state
    LaunchedEffect(state) {
        if (showLoading) {
            delay(2000) // Show splash for at least 2 seconds
            showLoading = false
        }

        if (state.hasValidToken) {
            // User already logged in, go directly to home
            onNavigateToHome()
        } else if (!state.isFirstLaunch && !state.hasValidToken) {
            // Not first launch, but user not logged in
            onNavigateToLogin()
        }
        // Otherwise, stay on intro screen
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        if (showLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(120.dp)
                        .scale(scale)
                )
            }
        } else if (state.isFirstLaunch && !state.hasValidToken) {
            // Only show intro content on first launch
            IntroContent(
                onNavigateToLogin = {
                    viewModel.setFirstLaunchComplete()
                    onNavigateToLogin()
                }
            )
        }
    }
}

@Composable
private fun IntroContent(onNavigateToLogin: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { introPages.size })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
        ) { page ->
            IntroPage(introPages[page])
        }

        // Indicators
        Row(
            Modifier
                .padding(bottom = 16.dp)
                .height(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(pagerState.pageCount) { index ->
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = if (pagerState.currentPage == index)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            shape = MaterialTheme.shapes.small
                        )
                )
            }
        }

        // Buttons
        if (pagerState.currentPage != introPages.lastIndex) {
            Button(
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF5722)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(50.dp)
            ) {
                Text("Tiếp Theo")
            }

            OutlinedButton(
                onClick = onNavigateToLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp)
            ) {
                Text("Bỏ qua")
            }
        } else {
            Button(
                onClick = onNavigateToLogin,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF5722)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp)
            ) {
                Text("Bắt đầu")
            }
        }
    }
}

@Composable
private fun IntroPage(page: IntroPage) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = page.image),
            contentDescription = null,
            modifier = Modifier.size(200.dp)
        )
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = 32.dp, bottom = 16.dp)
        )
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun IntroduceScreenPreview() {
    IntroduceScreen(
        onNavigateToHome = {},
        onNavigateToLogin = {}
    )
}