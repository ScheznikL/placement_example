package com.endofjanuary.placement_example.user_cabinet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun UserProfileScreen(
    navController: NavController, modifier: Modifier = Modifier
) {
    val viewModel = getViewModel<UserProfileViewModel>()
    val authState by viewModel.signInState.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle(initialValue = null)

    val viewState by viewModel.state.collectAsStateWithLifecycle()

    /*
        var displayNameInput by remember { mutableStateOf(viewModel.displayNameInput.value) }
        var isAutoSaveModels by remember { mutableStateOf(currentUser?.autoSaveModel) }
        var isAutoRefine by remember { mutableStateOf(currentUser?.autoRefineModel) }
    */

    /*
        val isAutoSaveModels by viewModel.autoSaveModel.collectAsStateWithLifecycle()
        val isAutoRefine by viewModel.autoRefineModel.collectAsStateWithLifecycle()
    */

    val nameEditEnabled = mutableStateOf(false)

    /*
        val decorationText = mutableStateOf(
            if (currentUser?.displayName?.trim()
                    .isNullOrEmpty()
            ) "Add name" else currentUser?.displayName.toString()
        )
    */
    val decorationText = mutableStateOf(
        if (viewState.displayName.trim()
                .isEmpty()
        ) "Add name" else viewState.displayName
    )

    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    var textInput by remember { mutableStateOf(viewState.displayName) }

    LaunchedEffect(viewState.displayName) {
        if (viewState.displayName != textInput) {
            textInput = viewState.displayName
        }
    }

    val context = LocalContext.current

    Scaffold(bottomBar = { BottomBar(navController) }) { padding ->
        if (authState == SignInState.NOT_SIGNED_IN || currentUser == null) {
            Text(text = "You logged out successfully")
            Button(onClick = { navController.navigate("reg_screen") }) {
                Text(text = "To Welcome Screen")
            }
        } else if (currentUser != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .imePadding()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if (!currentUser!!.profilePictureUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(currentUser?.profilePictureUrl).crossfade(true).build(),
                        contentDescription = "user profile picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .padding(10.dp)
                            .align(Alignment.CenterHorizontally)
                            .clip(CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(
                                color = Color.LightGray.copy(alpha = 0.4f), CircleShape
                            )
                            .clickable {

                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "no user picture",
                            tint = Color.Gray,
                            modifier = Modifier
                                .size(60.dp)
                        )
                    }
                }
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 5.dp)
                ) {
                    Row(
                        Modifier
                            // .fillMaxWidth()
                            .padding(horizontal = 36.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        // if (currentUser!!.displayName.isNullOrEmpty()) {


                        BasicTextField(
                            enabled = nameEditEnabled.value,
                            modifier = Modifier
                                .padding(end = 3.dp, start = 15.dp),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Default,
                                capitalization = KeyboardCapitalization.Words
                            ),
                            keyboardActions = KeyboardActions(onDone = {
                                //    if (textInput.value.isNotBlank()) {
                                viewModel.onNameChange(textInput)
                                viewModel.updateUserData(/*refine = isAutoRefine!!,save = isAutoSaveModels!!*/)
                                //   focusManager.clearFocus()
                                // textInput.value = ""
                                // }
                            }),
                            textStyle = TextStyle(
                                lineHeight = 1.5.em,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.W500,
                            ),
                            value = /*viewState.displayName.uppercase(),*/textInput.uppercase(),
                            onValueChange = {
                                textInput = it
                                //  displayNameInput = it
                               // viewModel.onNameChange(it)
                                decorationText.value = ""
                            },
                            singleLine = true,
                            /*               decorationBox = { innerTextField ->
                                               Row(
                                                   horizontalArrangement = Arrangement.Center,
                                                   verticalAlignment = Alignment.CenterVertically,
                                                   // modifier = Modifier.fillMaxWidth()
                                               ) {
                                                   Text(
                                                       decorationText.value.uppercase(),
                                                       fontWeight = FontWeight.W500,
                                                       textAlign = TextAlign.Center
                                                   )
                                                   innerTextField()
                                               }
                                           },*/
                        )
                        IconButton(onClick = {
                            nameEditEnabled.value = !nameEditEnabled.value
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "edit")
                        }
                    }
                    Text(currentUser!!.email, Modifier.padding(top = 5.dp))
                }
                Column(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(26.dp)
                        .background(
                            color = Color.LightGray.copy(alpha = 0.4f), RoundedCornerShape(8.dp)
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.dp, Color.LightGray, RoundedCornerShape(8.dp)
                            )
                            .background(
                                Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 5.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Password",
                            fontSize = 16.sp,
                        )
                        TextButton(onClick = { }, contentPadding = PaddingValues(0.dp)) {
                            Text(text = "Change")
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.dp, Color.LightGray, RoundedCornerShape(8.dp)
                            )
                            .background(
                                Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(8.dp)
                            )
                            .padding(19.dp)
                            .clickable {
// TODO change password
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
                        Switch(checked = viewState.autoSaveModel/*isAutoSaveModels?:false*/,
                            onCheckedChange = { isChecked ->
                                //isAutoSaveModels = isChecked
                                viewModel.onSaveSwitch(isChecked)
                                //  viewModel.updateUserData(/*refine = isAutoRefine!!,save = isAutoSaveModels!!*/)
                            })
                    }
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
                        Switch(checked = viewState.autoRefineModel/*isAutoRefine?:false*/,
                            onCheckedChange = { isChecked ->
                                //isAutoRefine = isChecked
                                viewModel.onRefineSwitch(isChecked)
                                //  viewModel.updateUserData(/*refine = isAutoRefine!!,save = isAutoSaveModels!!*/)
                            })
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
                            onClick = {
                                viewModel.onSinghOut()
                            }, modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Sing Out")
                        }
                        if (currentUser?.isEmailVerified == false) {
                            Button(
                                onClick = {
                                    viewModel.verifyEmail()
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

    Scaffold(bottomBar = { BottomBar(navController) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(currentUser?.profilePictureUrl).crossfade(true).build(),
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
                        color = Color.LightGray.copy(alpha = 0.4f), RoundedCornerShape(8.dp)
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp, Color.LightGray, RoundedCornerShape(8.dp)
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
                            1.dp, Color.LightGray, RoundedCornerShape(8.dp)
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
                    Switch(checked = isAutoSaveModels,
                        onCheckedChange = { isChecked -> isAutoSaveModels = isChecked })
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
                    Switch(checked = isAutoRefine,
                        onCheckedChange = { isChecked -> isAutoRefine = isChecked })
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
                        onClick = {}, modifier = Modifier.fillMaxWidth()
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