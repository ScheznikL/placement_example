package com.endofjanuary.placement_example.ui.screens.register_screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.endofjanuary.placement_example.R
import com.endofjanuary.placement_example.domain.repo.SignInState
import org.koin.androidx.compose.getViewModel

@Composable
fun RegistrationScreen(
    navController: NavController, modifier: Modifier = Modifier
) {

    val viewModel = getViewModel<RegistrationViewModel>()

    val email by remember { viewModel.emailValueState }
    val password by remember { viewModel.passwordValueState }
    val confirmPassword by remember { viewModel.confirmPasswordValueState }

    val isPasswordError by remember { viewModel.isPasswordError }
    val isEmailError by remember { viewModel.isEmailError }
    val isConfirmPasswordError by remember { viewModel.isConfirmPasswordError }

    var hidePassword by remember { mutableStateOf(true) }
    var register by remember { mutableStateOf(false) }

    var passwordVisualTransformation by remember { mutableStateOf(VisualTransformation.None) }
    passwordVisualTransformation = if (hidePassword) {
        PasswordVisualTransformation()
    } else {
        VisualTransformation.None
    }

    val currentUser by viewModel.currentUser.collectAsState()
    val signInError by viewModel.signInError.collectAsStateWithLifecycle(initialValue = null)

    val authState by viewModel.signInState.collectAsStateWithLifecycle()

    val headingText =
        if (register) stringResource(R.string.sign_up) else stringResource(R.string.sign_in)

    Surface(
        modifier = modifier
            .fillMaxSize()
            .padding(22.dp)
    ) {
        Spacer(Modifier.height(125.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeadingTextComponent(heading = headingText)
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(
                modifier = Modifier.padding(5.dp),
                isError = isEmailError,
                value = email,
                onValueChange = {
                    viewModel.emailValueState.value = it
                    viewModel.onEmailValueChanged()
                },
                label = { Text(text = stringResource(R.string.enter_email)) },
                supportingText = {
                    if (isEmailError) Text(text = stringResource(R.string.invalid_email))
                },
                singleLine = true
            )
            OutlinedTextField(modifier = Modifier.padding(5.dp),
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
                    if (isPasswordError) Text(text = stringResource(R.string.password_requirements))
                },
                label = { Text(text = stringResource(R.string.enter_password)) },
                singleLine = true,
                visualTransformation = passwordVisualTransformation,
                trailingIcon = {
                    IconButton(onClick = {
                        hidePassword = !hidePassword
                    }) {
                        if (hidePassword) Icon(
                            painter = painterResource(id = R.drawable.ic_eye_filled),
                            contentDescription = stringResource(R.string.show_password)
                        )
                        else Icon(
                            painter = painterResource(id = R.drawable.ic_eye_outlined),
                            contentDescription = stringResource(R.string.hide_password)
                        )
                    }
                })


            if (register) {
                OutlinedTextField(modifier = Modifier.padding(5.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    isError = isConfirmPasswordError,
                    value = confirmPassword,
                    onValueChange = {
                        viewModel.confirmPasswordValueState.value = it
                        viewModel.onConfirmPasswordChanged()
                    },
                    label = { Text(text = stringResource(R.string.confirm_password)) },
                    supportingText = {
                        if (isConfirmPasswordError) Text(text = stringResource(R.string.password_doesn_t_match))
                    },
                    singleLine = true,
                    visualTransformation = passwordVisualTransformation,
                    trailingIcon = {
                        IconButton(onClick = {
                            hidePassword = !hidePassword
                        }) {
                            if (hidePassword) Icon(
                                painter = painterResource(id = R.drawable.ic_eye_filled),
                                contentDescription = stringResource(id = R.string.show_password)
                            )
                            else Icon(
                                painter = painterResource(id = R.drawable.ic_eye_outlined),
                                contentDescription = stringResource(id = R.string.hide_password)
                            )
                        }
                    })
            }
            Button(enabled = !isPasswordError && !isConfirmPasswordError && !isEmailError && email.isNotBlank() && password.isNotBlank(),
                onClick = {
                    if (register) {
                        viewModel.onSignUp()
                    } else {
                        viewModel.onSignIn()
                    }
                }) {
                if (register) {
                    Text(stringResource(R.string.register))
                } else {
                    Text(stringResource(id = R.string.sign_in))
                }
            }
            if (!register) {
                TextButton(
                    onClick = {
                        register = true
                    },
                ) {
                    Text(text = stringResource(R.string.no_account_register))
                }
            } else {
                TextButton(
                    onClick = {
                        register = false
                    },
                ) {
                    Text(text = stringResource(R.string.to_signing_in))
                }
            }

            val context = LocalContext.current
            LaunchedEffect(key1 = authState) {
                when (authState) {
                    SignInState.AUTHORIZED -> {
                        navController.navigate("home_screen")
                    }

                    SignInState.CREDENTIAL_ERROR -> Toast.makeText(
                        // todo scaffold & snackbar
                        context,
                        context.getString(R.string.credential_error) + if (!signInError.isNullOrEmpty()) context.getString(
                            R.string.details, signInError.toString()
                        ) else "",
                        Toast.LENGTH_SHORT,
                    ).show()

                    SignInState.USER_NOT_FOUND -> Toast.makeText(
                        context,
                        context.getString(R.string.user_not_found) + if (!signInError.isNullOrEmpty()) context.getString(
                            R.string.details, signInError.toString()
                        ) else "",
                        Toast.LENGTH_SHORT,
                    ).show()

                    SignInState.VERIFY_FAILED -> Toast.makeText(
                        context,
                        context.getString(R.string.email_verification_failed) + if (!signInError.isNullOrEmpty()) context.getString(
                            R.string.details, signInError.toString()
                        ) else "",
                        Toast.LENGTH_SHORT,
                    ).show()

                    SignInState.VERIFYING_EMAIL -> Toast.makeText(
                        context,
                        context.getString(R.string.verification_email_was_sent),
                        Toast.LENGTH_SHORT,
                    ).show()

                    SignInState.USER_COLLISION -> Toast.makeText(
                        context,
                        context.getString(
                            R.string.email_already_exist, email
                        ) + if (!signInError.isNullOrEmpty()) context.getString(
                            R.string.details, signInError.toString()
                        ) else "",

                        Toast.LENGTH_SHORT,
                    ).show()

                    else -> {}
                }
            }
        }

    }

}

@Composable
fun HeadingTextComponent(heading: String) {
    Text(
        text = heading,
        modifier = Modifier.fillMaxWidth(),
        fontSize = 39.sp,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold
    )
}