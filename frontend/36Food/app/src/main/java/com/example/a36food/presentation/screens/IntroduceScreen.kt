package com.example.a36food.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.a36food.R
import com.example.a36food.ui.theme._36FoodTheme
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
fun IntroduceScreen (
    onFinish: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = {introPages.size})
    val coroutineScope = rememberCoroutineScope()

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager (
            state = pagerState,
            modifier = Modifier.weight(1f).padding(dimensionResource(R.dimen.padding_medium))
        ) {
            page ->
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = introPages[page].image),
                    contentDescription = null,
                    modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium)).size(350.dp)
                )
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_medium)))
                Text(
                    text = introPages[page].title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_medium)))
                Text(
                    text = introPages[page].description,
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        }

        if (pagerState.currentPage  != introPages.lastIndex) {
            Button(
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                } ,
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_medium))
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
            ) {
                Text(if (pagerState.currentPage == introPages.lastIndex) "Bat Dau" else "Tiep Theo")
            }

            OutlinedButton (
                onClick = {/*TO DO*/},
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_medium))
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Bỏ qua")
            }
        } else {

            Button(
                onClick = {/*TO DO*/},
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_medium))
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
            ) {
                Text(text = "Bắt đầu")
            }
        }


    }
}

@Preview(showBackground = true)
@Composable
fun IntroduceScreenPreview() {
    _36FoodTheme {
        IntroduceScreen(onFinish = {/*TO DO*/})
    }
}