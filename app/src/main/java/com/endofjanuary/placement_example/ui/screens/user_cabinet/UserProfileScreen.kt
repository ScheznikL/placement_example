package com.endofjanuary.placement_example.ui.screens.user_cabinet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.endofjanuary.placement_example.R
import com.endofjanuary.placement_example.ui.screens.chat.ErrorDialog
import com.endofjanuary.placement_example.domain.repo.SignInState
import com.endofjanuary.placement_example.ui.components.BottomBar
import com.endofjanuary.placement_example.ui.dialogs.DeleteDialog
import org.koin.androidx.compose.getViewModel


@OptIn(ExperimentalMaterial3Api::class)
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

    val scrollState = rememberScrollState()
    val context = LocalContext.current

    var textInput by remember { mutableStateOf(context.getString(R.string.add_name) ) }

    val confirmOut = mutableStateOf(false)
    val openSignOutDialog = mutableStateOf(false)
    val openErrorDialog: MutableState<Boolean> = mutableStateOf(false)


    val sheetState = rememberModalBottomSheetState()
    val showBottomSheet = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(viewState.displayName) {
        if (viewState.displayName.isNotEmpty()) {
            textInput = viewState.displayName
        }
    }
    LaunchedEffect(viewState.error) {
        if (viewState.error.isNotEmpty()) {
            openErrorDialog.value = true
        }
    }

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
                        contentDescription = stringResource(R.string.user_profile_picture),
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
                            ), contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = stringResource(R.string.no_user_picture),
                            tint = Color.Gray,
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 5.dp)
                ) {
                    Row(
                        Modifier.padding(horizontal = 36.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        BasicTextField(
                            enabled = nameEditEnabled.value,
                            modifier = Modifier.padding(end = 3.dp, start = 15.dp),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Default,
                                capitalization = KeyboardCapitalization.Words
                            ),
                            keyboardActions = KeyboardActions(onDone = {
                                viewModel.onNameChange(textInput)
                                viewModel.updateUserData()
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
                            },
                            singleLine = true,
                        )
                        IconButton(onClick = {
                            nameEditEnabled.value = !nameEditEnabled.value
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = stringResource(id = R.string.edit_icon))
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
                            text = stringResource(id = R.string.password),
                            fontSize = 16.sp,
                        )
                        TextButton(onClick = {
                            showBottomSheet.value = true
                        }, contentPadding = PaddingValues(0.dp)) {
                            Text(text = stringResource(R.string.change))
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
                            text = stringResource(R.string.is_email_verified),
                            fontSize = 16.sp,
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
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.auto_save_models_to_device),
                            fontSize = 16.sp,
                        )
                        Switch(checked = viewState.autoSaveModel,
                            onCheckedChange = { isChecked ->
                                viewModel.onSaveSwitch(isChecked)
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
                            text = stringResource(R.string.auto_refine_created_model),
                            fontSize = 16.sp,
                        )
                        Switch(checked = viewState.autoRefineModel,
                            onCheckedChange = { isChecked ->
                                viewModel.onRefineSwitch(isChecked)
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
    DeleteDialog(
        title = stringResource(R.string.sing_out_request),
        text = stringResource(R.string.sing_out_dialog),
        openDialog = openSignOutDialog,
        confirm = confirmOut
    ) {
        viewModel.onSignOut()
    }
    ErrorDialog(
        errorMessage = viewState.error, openDialog = openErrorDialog
    )

    if (showBottomSheet.value) {
        BottomModalChangePasswordSheet(showBottomSheet = showBottomSheet,
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
            })
    }
}

