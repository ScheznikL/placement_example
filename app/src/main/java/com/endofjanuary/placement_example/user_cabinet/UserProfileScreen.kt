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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.endofjanuary.placement_example.chat.ErrorDialog
import com.endofjanuary.placement_example.repo.SignInState
import com.endofjanuary.placement_example.utils.components.BottomBar
import com.endofjanuary.placement_example.utils.screens.DeleteDialog
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel


@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    navController: NavController, modifier: Modifier = Modifier
) {
    val viewModel = getViewModel<UserProfileViewModel>()
    val authState by viewModel.signInState.collectAsStateWithLifecycle()
    val authError by viewModel.authError.collectAsStateWithLifecycle()


    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle(initialValue = null)

    val viewState by viewModel.state.collectAsStateWithLifecycle()

    val nameEditEnabled = mutableStateOf(false)

    var decorationText = //todo display add name somehow
        if (viewState.displayName.trim()
                .isEmpty()
        ) "Add name" else viewState.displayName


    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    var textInput by remember { mutableStateOf(viewState.displayName) }

    val confirmOut = mutableStateOf(false)
    val openSignOutDialog = mutableStateOf(false)
    val openErrorDialog: MutableState<Boolean> = mutableStateOf(false)
    val context = LocalContext.current


    val sheetState = rememberModalBottomSheetState()
    //val showBottomSheet = mutableStateOf(false)
    var showBottomSheet = remember { mutableStateOf(false) }
    val confirmShowBottomSheet = mutableStateOf(false)
    val scope = rememberCoroutineScope()
    /*
        val isPasswordError by remember { viewModel.isPasswordError }
        val isEmailError by remember { viewModel.isEmailError }
        val isConfirmNewPasswordError by remember { viewModel.isConfirmPasswordError }
        val isNewPasswordError by remember { viewModel.isNewPasswordError }
    */

    LaunchedEffect(viewState.displayName) {
        if (viewState.displayName != textInput) {
            textInput = viewState.displayName
        }
    }
    LaunchedEffect(viewState.error) {
        if (viewState.error.isNotEmpty()) {
            openErrorDialog.value = true
        }
    }

    /*    LaunchedEffect(showBottomSheet) {
            if (showBottomSheet.value && !sheetState.isVisible) {
                sheetState.show()
            }
        }*/



    Scaffold(bottomBar = { BottomBar(navController) }) { padding ->
        if (authState == SignInState.NOT_SIGNED_IN || currentUser == null) {
            SignOutContent(navController)
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
                            value = textInput.uppercase(),
                            onValueChange = {
                                textInput = it
                                decorationText = ""
                            },
                            singleLine = true,
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
                        TextButton(onClick = {
                            showBottomSheet.value = true
                        }, contentPadding = PaddingValues(0.dp)) {
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
                            .padding(19.dp),
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
                        EndButtonsSection(
                            openSignOutDialog = openSignOutDialog,
                            isEmailVerified = currentUser?.isEmailVerified!!,
                            onVerifyEmail = viewModel::verifyEmail,
                            authState = authState
                        )
                    }
                }
            }
        }
    }
    /*
        when (authState) {
            *//*      SignInState.NOT_SIGNED_IN -> {
                  SignOutContent(navController)
              }

              SignInState.AUTHORIZED -> {
                  Column(
                      modifier = Modifier
                          .fillMaxSize()
                          .padding(padding)
                          .imePadding()
                          .verticalScroll(scrollState),
                      verticalArrangement = Arrangement.Center,
                      horizontalAlignment = Alignment.CenterHorizontally
                  ) {
                      if (currentUser != null) {
                          UserProfileScreenContent(
                              profilePictureUrl = currentUser!!.profilePictureUrl,
                              nameEditEnabled = nameEditEnabled,
                              displayName = currentUser!!.displayName ?: "Add name",
                              email = currentUser!!.email,
                              isEmailVerified = currentUser!!.isEmailVerified,
                              autoSaveModel = currentUser!!.autoSaveModel,
                              autoRefineModel = currentUser!!.autoRefineModel,
                              onSaveSwitch = viewModel::onSaveSwitch,
                              onNameChange = viewModel::onNameChange,
                              onRefineSwitch = viewModel::onRefineSwitch,
                              updateUserData = viewModel::updateUserData,
                              modifier = Modifier
                                  .fillMaxSize()
                                  .padding(padding)
                          )
                          Column(
                              verticalArrangement = Arrangement.Center,
                              horizontalAlignment = Alignment.CenterHorizontally,
                              modifier = Modifier
                                  .wrapContentWidth()
                                  .padding(26.dp)
                          ) {
                              EndButtonsSection(
                                  openSignOutDialog = openSignOutDialog,
                                  isEmailVerified = currentUser?.isEmailVerified!!,
                                  onVerifyEmail = viewModel::verifyEmail,
                                  authState = authState
                              )
                          }
                      }

                  }
              }*//*

        SignInState.CREDENTIAL_ERROR -> TODO()
        SignInState.USER_NOT_FOUND -> TODO()
        SignInState.USER_COLLISION -> TODO()
        SignInState.CREDENTIALS_RESET_REQ -> TODO()
        SignInState.CREDENTIALS_RESET_ERR -> TODO()
        SignInState.CREDENTIALS_RESET -> TODO()
        SignInState.REAUTHORIZED -> TODO()
        SignInState.VERIFY_FAILED -> TODO()
        SignInState.VERIFYING_EMAIL -> TODO()

        else -> {}
    }*/

    /*if (authState == SignInState.NOT_SIGNED_IN || currentUser == null) {
        SignOutContent(navController)
    } else if (currentUser != null)

}*/

    DeleteDialog(
        title = "Sing Out Request",
        text = "Are you sure want to continue\r\nAll unsaved models will be lost",
        openDialog = openSignOutDialog, confirm = confirmOut
    ) {
        viewModel.onSignOut()
    }
    ErrorDialog(
        errorMessage = viewState.error,
        openDialog = openErrorDialog
    )

    if (showBottomSheet.value) {
        BottomModalChangePasswordSheet(
            showBottomSheet = showBottomSheet,
            sheetState = sheetState,
            scope = scope,
            email = viewState.email,
            onPasswordValueChanged = viewModel::onPasswordValueChanged,
            isPasswordError = viewModel.isPasswordError,
            onEmailSend = viewModel::askForChangePassword,
            error = authError ?: "",
            state = authState,
            onDismissRequest = {
                showBottomSheet.value = false
                if (authState == SignInState.CREDENTIALS_RESET_REQ) {
                    viewModel.onTempSignOut()
                }
            }
        )
    }
}


/*@Composable
fun PasswordField() {
    OutlinedTextField(
        modifier = Modifier
            .padding(5.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
        ),
        isError = isPasswordError,
        value = password,
        onValueChange = {
            viewModel.passwordValueState.value = it
            viewModel.onPasswordValueChanged()
        },
        supportingText = {
            if (isPasswordError)
                Text(text = "Password has to contain special characters and at least one big letter ") //todo password
        },
        label = { Text(text = "Enter password") },
        singleLine = true,
        visualTransformation = passwordVisualTransformation,
        trailingIcon = {
            IconButton(onClick = {
                hidePassword = !hidePassword
            }) {
                if (hidePassword)
                    Icon(
                        painter = painterResource(id = R.drawable.ic_eye_filled),
                        contentDescription = "show password"
                    )
                else
                    Icon(
                        painter = painterResource(id = R.drawable.ic_eye_outlined),
                        contentDescription = "hide password"
                    )
            }
        }
    )
}*/

@Composable
fun UserProfileScreenContent(
    modifier: Modifier = Modifier,
    profilePictureUrl: String?,
    nameEditEnabled: MutableState<Boolean>,
    displayName: String,
    email: String,
    isEmailVerified: Boolean,
    autoSaveModel: Boolean,
    autoRefineModel: Boolean,
    onSaveSwitch: (Boolean) -> Unit,
    onNameChange: (String) -> Unit,
    onRefineSwitch: (Boolean) -> Unit,
    updateUserData: () -> Unit,
) {

    val scrollState = rememberScrollState()
    var textInput by remember { mutableStateOf(displayName) }

    Column(
        modifier = modifier
            .imePadding()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (!profilePictureUrl.isNullOrBlank()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(profilePictureUrl).crossfade(true).build(),
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
                    .padding(horizontal = 36.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
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
                        onNameChange(textInput)
                        updateUserData(/*refine = isAutoRefine!!,save = isAutoSaveModels!!*/)
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
                    value = textInput.uppercase(),
                    onValueChange = {
                        textInput = it
                        //decorationText = ""
                    },
                    singleLine = true,
                )
                IconButton(onClick = {
                    nameEditEnabled.value = !nameEditEnabled.value
                }) {
                    Icon(Icons.Default.Edit, contentDescription = "edit")
                }
            }
            Text(email, Modifier.padding(top = 5.dp))
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
                TextButton(onClick = {

                }, contentPadding = PaddingValues(0.dp)) {
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
                    .padding(19.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "is email verified",
                    fontSize = 16.sp,
                    // fontWeight = FontWeight.Bold
                )
                Text(
                    text = isEmailVerified.toString(),
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
                Switch(checked = autoSaveModel/*isAutoSaveModels?:false*/,
                    onCheckedChange = { isChecked ->
                        //isAutoSaveModels = isChecked
                        onSaveSwitch(isChecked)
                        //  viewModel.updateUserData(/*refine = isAutoRefine!!,save = isAutoSaveModels!!*/)
                    })
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "auto refine created model",
                    fontSize = 16.sp,

                    // fontWeight = FontWeight.Bold
                )
                Switch(checked = autoRefineModel/*isAutoRefine?:false*/,
                    onCheckedChange = { isChecked ->
                        //isAutoRefine = isChecked
                        onRefineSwitch(isChecked)
                        //  viewModel.updateUserData(/*refine = isAutoRefine!!,save = isAutoSaveModels!!*/)
                    })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun ModalBottomSheetSample() {
    var openBottomSheet by remember { mutableStateOf(false) }
    var skipPartiallyExpanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded
    )

    // App content
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            Modifier.toggleable(
                value = skipPartiallyExpanded,
                role = Role.Checkbox,
                onValueChange = { checked -> skipPartiallyExpanded = checked }
            )
        ) {
            Checkbox(checked = skipPartiallyExpanded, onCheckedChange = null)
            Spacer(Modifier.width(16.dp))
            Text("Skip partially expanded State")
        }
        Button(
            onClick = { openBottomSheet = !openBottomSheet },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Show Bottom Sheet")
        }
    }

    // Sheet content
    if (openBottomSheet) {

        ModalBottomSheet(
            onDismissRequest = { openBottomSheet = false },
            sheetState = bottomSheetState,
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(
                    // Note: If you provide logic outside of onDismissRequest to remove the sheet,
                    // you must additionally handle intended state cleanup, if any.
                    onClick = {
                        scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                            if (!bottomSheetState.isVisible) {
                                openBottomSheet = false
                            }
                        }
                    }
                ) {
                    Text("Hide Bottom Sheet")
                }
            }
            var text by remember { mutableStateOf("") }
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.padding(horizontal = 16.dp),
                label = { Text("Text field") }
            )
            LazyColumn {
                items(25) {
                    ListItem(
                        headlineContent = { Text("Item $it") },
                        leadingContent = {
                            Icon(
                                Icons.Default.Favorite,
                                contentDescription = "Localized description"
                            )
                        },
                        colors = ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                        ),
                    )
                }
            }
        }
    }
}

