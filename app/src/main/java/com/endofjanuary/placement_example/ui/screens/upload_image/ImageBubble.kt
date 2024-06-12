//package com.endofjanuary.placement_example.upload_image
//
//import android.graphics.drawable.shapes.Shape
//import androidx.compose.foundation.layout.Row
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//
//class TriangleEdgeShape(val offset: Int) : Shape() {
//
//    override fun createOutline(
//        size: Size,
//        layoutDirection: LayoutDirection,
//        density: Density
//    ): Outline {
//        val trianglePath = Path().apply {
//            moveTo(x = 0f, y = size.height-offset)
//            lineTo(x = 0f, y = size.height)
//            lineTo(x = 0f + offset, y = size.height)
//        }
//        return Outline.Generic(path = trianglePath)
//    }
//}
//
//@Composable
//fun ImageBubble() {
//    Row(Modifier.height(IntrinsicSize.Max)) {
//        Column(
//            modifier = Modifier.background(
//                color = Color.xxx,
//                shape = RoundedCornerShape(4.dp,4.dp,0.dp,4.dp)
//            ).width(xxxx)
//        ) {
//            Text("Chat")
//        }
//        Column(
//            modifier = Modifier.background(
//                color = Color.xxx,
//                shape = TriangleEdgeShape(10))
//                .width(8.dp)
//                .fillMaxHeight()
//        ){
//        }
//}