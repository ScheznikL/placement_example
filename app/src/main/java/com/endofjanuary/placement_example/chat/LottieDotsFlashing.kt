package com.endofjanuary.placement_example.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.endofjanuary.placement_example.R

@Composable
fun LottieDotsFlashing(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.lottiethreedots))
    LottieAnimation(
        composition = composition,
        modifier = modifier,
        iterations = 100
    )
}