package com.example.a36food.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.a36food.R
import com.example.a36food.ui.theme._36FoodTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import androidx.compose.foundation.pager.rememberPagerState

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

@OptIn(ExperimentalPagerApi::class)
@Composable
fun IntroduceScreen (onFinish: () -> Unit) {
    val pagerState = rememberPagerState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            count = introPages.size,
            modifier = Modifier.weight(dimensionResource(R.dimen.thickness_divider))
        ) {

        }
    }
}

@Preview(showBackground = true)
@Composable
fun IntroduceScreenPreview() {
    _36FoodTheme {
        IntroduceScreen()
    }
}