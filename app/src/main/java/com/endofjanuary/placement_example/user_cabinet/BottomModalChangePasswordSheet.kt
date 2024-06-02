package com.endofjanuary.placement_example.user_cabinet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.endofjanuary.placement_example.R
import com.endofjanuary.placement_example.repo.SignInState
import com.endofjanuary.placement_example.utils.components.EdgeButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomModalChangePasswordSheet(
    showBottomSheet: MutableState<Boolean>,
    sheetState: SheetState,
    scope: CoroutineScope,
    email: String,
    isPasswordError: MutableState<Boolean>,
    onPasswordValueChanged: (String) -> Unit,
    onEmailSend: () -> Unit,
    state: SignInState,
    error: String,
    onDismissRequest: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = RoundedCornerShape(18.dp),
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            var visible by remember { mutableStateOf(true) }
            var errorVisible by remember { mutableStateOf(true) }
            val density = LocalDensity.current
            AnimatedVisibility(visible = visible, enter = slideInHorizontally {
                with(density) { -40.dp.roundToPx() }
            } + fadeIn(
                initialAlpha = 0.3f
            ), exit = slideOutHorizontally() + fadeOut()) {
                SendingEmailContent(
                    email = email,
                    isPasswordError = isPasswordError.value,
                    onPasswordValueChanged = onPasswordValueChanged,
                    onEmailSend = onEmailSend,
                )
            }
            if (state != SignInState.REAUTHORIZED && error.isNotEmpty()) {
                visible = false
                AnimatedVisibility(visible = errorVisible, enter = slideInHorizontally {
                    with(density) { -40.dp.roundToPx() }
                } + fadeIn(
                    initialAlpha = 0.3f
                ), exit = slideOutHorizontally() + fadeOut()) {
                    ErrorContent(message = error,
                        scope = scope,
                        showBottomSheet = showBottomSheet,
                        sheetState = sheetState,
                        onTryAgain = {
                            visible = true
                            errorVisible = false
                        })
                }
            } else if (state == SignInState.CREDENTIALS_RESET_REQ) {
                visible = false
                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        text = "Reset Link",
                        fontWeight = FontWeight.W500,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "has been sent to\r\n$email",
                        fontWeight = FontWeight.W400,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                onDismissRequest()
                            }
                        }
                    }) {
                        Text("Done")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ErrorContent(
    modifier: Modifier = Modifier,
    message: String,
    scope: CoroutineScope,
    sheetState: SheetState,
    showBottomSheet: MutableState<Boolean>,
    onTryAgain: () -> Unit
) {
    Column(modifier = modifier.padding(8.dp)) {
        Text(
            text = "Error occurred",
            fontWeight = FontWeight.W500,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(20.dp))
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.background(
                MaterialTheme.colorScheme.errorContainer, RoundedCornerShape(10.dp)
            ),
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error.copy(red = 0.7F),
                modifier = modifier.padding(8.dp)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = modifier.fillMaxWidth(),
        ) {
            EdgeButton(
                onProceedClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showBottomSheet.value = false
                        }
                    }
                }, title = "Try later", modifier = Modifier.align(Alignment.CenterStart)
            )
            EdgeButton(
                onProceedClick = onTryAgain,
                title = "Try again",
                modifier = Modifier.align(Alignment.CenterEnd),
                inverseShape = true
            )
        }
    }
}

@Composable
fun SendingEmailContent(
    modifier: Modifier = Modifier,
    email: String,
    isPasswordError: Boolean,
    onPasswordValueChanged: (String) -> Unit,
    onEmailSend: () -> Unit,
) {
    Column(modifier = modifier.imePadding()) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = "Log In again",
                fontWeight = FontWeight.W500,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = email,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.tertiary
            )
            Spacer(modifier = Modifier.height(20.dp))
            PasswordInputComponent(
                labelVal = "password",
                isPasswordError = isPasswordError,
                onPasswordValueChanged = onPasswordValueChanged,
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp), horizontalArrangement = Arrangement.End
        ) {
            EdgeButton(
                onProceedClick = onEmailSend, title = "Proceed", enabled = !isPasswordError
            )
        }
    }
}

@Composable
fun PasswordInputComponent(
    labelVal: String,
    isPasswordError: Boolean,
    onPasswordValueChanged: (String) -> Unit,
    error: String = "Password has to contain at least one special character, big letter and digit.\r\nAlso at least 8 characters "
) {
    var password by remember {
        mutableStateOf("")
    }
    var isShowPassword by remember {
        mutableStateOf(false)
    }
    OutlinedTextField(
        value = password,
        onValueChange = {
            password = it
            onPasswordValueChanged(it)
        },
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        placeholder = {
            Text(text = labelVal, color = MaterialTheme.colorScheme.tertiary)
        },
        leadingIcon = {
            Icon(
                Icons.Default.Lock,
                contentDescription = "at symbol",
                tint = MaterialTheme.colorScheme.tertiary
            )
        },
        supportingText = {
            if (isPasswordError) Text(text = error)
        },
        trailingIcon = {
            val description = if (isShowPassword) "Show Password" else "Hide Password"
            val iconImage =
                if (isShowPassword) R.drawable.ic_eye_filled else R.drawable.ic_eye_outlined
            IconButton(onClick = {
                isShowPassword = !isShowPassword
            }) {
                Icon(
                    painter = painterResource(id = iconImage),
                    contentDescription = description,
                    tint = MaterialTheme.colorScheme.tertiary,
                )
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = if (isShowPassword) VisualTransformation.None else PasswordVisualTransformation()
    )
}