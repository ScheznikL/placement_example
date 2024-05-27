package com.endofjanuary.placement_example.user_cabinet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.endofjanuary.placement_example.data.models.User
import com.endofjanuary.placement_example.repo.SignInState
import com.endofjanuary.placement_example.utils.BottomBar
import org.koin.androidx.compose.getViewModel

@Composable
fun UserProfile(
    navController: NavController, modifier: Modifier = Modifier
) {
    val viewModel = getViewModel<UserProfileViewModel>()
    val authState by viewModel.signInState.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle(initialValue = null)


    var isAutoSaveModels by remember { mutableStateOf(false) }
    var isAutoRefine by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = { BottomBar(navController) }
    ) { padding ->
        if (authState == SignInState.NOT_SIGNED_IN || currentUser == null) {
            Text(text = "You logged out successfully")
            Button(onClick = { navController.navigate("reg_screen") }) {
                Text(text = "To Welcome Screen")
            }
        } else if (currentUser != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if (!currentUser!!.profilePictureUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(currentUser?.profilePictureUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "user profile picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .align(Alignment.CenterHorizontally)
                            .clip(CircleShape)
                    )
                } else {
                    IconButton(
                        onClick = { /*TODO*/ },
                        modifier = Modifier
                            .size(120.dp)
                            .background(
                                color = Color.LightGray.copy(alpha = 0.4f), CircleShape
                            )
                        //.clip(CircleShape)
                    ) {
                        Icon(
                            Icons.Default.Person,
                            "no user picture",
                            Modifier.size(60.dp),
                            Color.Gray
                        )
                    }
                }
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 5.dp)
                ) {
                    if (currentUser!!.displayName == null) {
                        Text(
                            "Add name",
                            fontStyle = FontStyle.Italic,
                            modifier = Modifier.clickable { })
                    } else {
                        Text(currentUser!!.displayName!!.uppercase(), fontWeight = FontWeight.W500)
                    }
                    Text(currentUser!!.email, Modifier.padding(top = 5.dp))
                }


                Column(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(26.dp)
                        .background(
                            color = Color.LightGray.copy(alpha = 0.4f),
                            RoundedCornerShape(8.dp)
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.dp,
                                Color.LightGray,
                                RoundedCornerShape(8.dp)
                            )
                            .background(
                                Color.LightGray.copy(alpha = 0.5f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 5.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Password",
                            fontSize = 16.sp,
                            //fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = { }, contentPadding = PaddingValues(0.dp)) {
                            Text(text = "Change")
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.dp,
                                Color.LightGray,
                                RoundedCornerShape(8.dp)
                            )
                            .background(
                                Color.LightGray.copy(alpha = 0.5f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(19.dp)
                            .clickable {
// TODO
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "is email verified",
                            fontSize = 16.sp,
                            // fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = currentUser!!.isEmailVerified.toString(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(66.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            //     .background(Color.LightGray, RoundedCornerShape(8.dp))
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "auto save models to device",
                            fontSize = 16.sp,
                            //  fontWeight = FontWeight.Bold
                        )
                        Switch(
                            checked = isAutoSaveModels,
                            onCheckedChange = { isChecked -> isAutoSaveModels = isChecked }
                        )
                    }
                    // Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            //    .background(Color.LightGray, RoundedCornerShape(8.dp))
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "auto refine created model",
                            fontSize = 16.sp,
                            // fontWeight = FontWeight.Bold
                        )
                        Switch(
                            checked = isAutoRefine,
                            onCheckedChange = { isChecked -> isAutoRefine = isChecked }
                        )
                    }
                }


                if (authState != SignInState.NOT_SIGNED_IN || currentUser != null) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .wrapContentWidth()
                            .padding(26.dp)
                    ) {
                        Button(
                            onClick = {},
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Sing Out")
                        }
                        if (currentUser?.isEmailVerified == false) {
                            Button(
                                onClick = {

                                },
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text(text = "verify Email")
                            }
                            if (authState == SignInState.VERIFY_FAILED) {
                                Text(text = "You logged out successfully")
                            } else if (authState == SignInState.VERIFYING_EMAIL) {
                                Text(text = "Verifying email \r\n check your email box")
                            }
                        }
                    }
                }
            }
        }
    }

}

@Preview
@Composable
fun UserProfilePreview() {
    val authState = SignInState.AUTHORIZED
    val currentUser = User()
    val navController = rememberNavController()


    var isAutoSaveModels by remember { mutableStateOf(false) }
    var isAutoRefine by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = { BottomBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(currentUser?.profilePictureUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "user profile picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(CircleShape)
            )

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 5.dp)
            ) {
                if (currentUser.displayName == null) {
                    Text("Add name", fontStyle = FontStyle.Italic)
                } else {
                    Text(currentUser.displayName.uppercase(), fontWeight = FontWeight.W500)
                }
                Text(currentUser.email, Modifier.padding(top = 5.dp))
            }


            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(26.dp)
                    .background(
                        color = Color.LightGray.copy(alpha = 0.4f),
                        RoundedCornerShape(8.dp)
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            Color.LightGray,
                            RoundedCornerShape(8.dp)
                        )
                        .background(Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 16.dp, vertical = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Password",
                        fontSize = 16.sp,
                        //fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { }, contentPadding = PaddingValues(0.dp)) {
                        Text(text = "Change")
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            Color.LightGray,
                            RoundedCornerShape(8.dp)
                        )
                        .background(Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(19.dp)
                        .clickable {
// TODO
                        },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "is email verified",
                        fontSize = 16.sp,
                        // fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = currentUser.isEmailVerified.toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(66.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        //     .background(Color.LightGray, RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "auto save models to device",
                        fontSize = 16.sp,
                        //  fontWeight = FontWeight.Bold
                    )
                    Switch(
                        checked = isAutoSaveModels,
                        onCheckedChange = { isChecked -> isAutoSaveModels = isChecked }
                    )
                }
                // Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        //    .background(Color.LightGray, RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "auto refine created model",
                        fontSize = 16.sp,
                        // fontWeight = FontWeight.Bold
                    )
                    Switch(
                        checked = isAutoRefine,
                        onCheckedChange = { isChecked -> isAutoRefine = isChecked }
                    )
                }
            }


            if (authState == SignInState.NOT_SIGNED_IN || currentUser == null) {
                Text(text = "You logged out successfully")
                Button(onClick = { navController.navigate("reg_screen") }) {
                    Text(text = "To Welcome Screen")
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(26.dp)
                ) {
                    Button(
                        onClick = {},
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Sing Out")
                    }
                    if (currentUser?.isEmailVerified == false) {
                        Button(
                            onClick = {

                            },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(text = "verify Email")
                        }
                        if (authState == SignInState.VERIFY_FAILED) {
                            Text(text = "You logged out successfully")
                        } else if (authState == SignInState.VERIFYING_EMAIL) {
                            Text(text = "Verifying email \r\n check your email box")
                        }
                    }
                }
            }
        }
    }
}