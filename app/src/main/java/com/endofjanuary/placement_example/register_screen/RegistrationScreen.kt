package com.endofjanuary.placement_example.register_screen

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.endofjanuary.placement_example.R
import com.endofjanuary.placement_example.repo.SignInState
import org.koin.androidx.compose.getViewModel

@Composable
fun RegistrationScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {

    val viewModel = getViewModel<RegistrationViewModel>()

    val email by remember { viewModel.emailValueState }
    val password by remember { viewModel.passwordValueState }
    val confirmPassword by remember { viewModel.confirmPasswordValueState }

    val isPasswordError by remember { viewModel.isPasswordError }
    val isEmailError by remember { viewModel.isEmailError }
    val isConfirmPasswordError by remember { viewModel.isConfirmPasswordError }

    var hidePassword by remember { mutableStateOf(true) } // todo hide one more
    var register by remember { mutableStateOf(false) }

    var passwordVisualTransformation by remember { mutableStateOf(VisualTransformation.None) }
    passwordVisualTransformation = if (hidePassword) {
        PasswordVisualTransformation()
    } else {
        VisualTransformation.None
    }

    val currentUser by viewModel.currentUser.collectAsState() // to initialize user
    val signInError by viewModel.signInError.collectAsStateWithLifecycle(initialValue = null)

    val authState by viewModel.signInState.collectAsStateWithLifecycle()

    val headingText = if (register) "Sign Up" else "Sign In"

    /*    LaunchedEffect(currentUser) {
            Log.d("email stat", "Verified: ${currentUser?.isEmailVerified}")
            if (authState == SignInState.AUTHORIZED *//*&& currentUser?.isEmailVerified == true*//*) {
            navController.navigate("home_screen")
        }
    }*/

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
                modifier = Modifier
                    .padding(5.dp),
                isError = isEmailError,
                value = email,
                onValueChange = {
                    viewModel.emailValueState.value = it
                    viewModel.onEmailValueChanged()
                },
                label = { Text(text = "Enter email") },
                supportingText = {
                    if (isEmailError)
                        Text(text = "Invalid Email") //todo password
                },
                singleLine = true
            )
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


            if (register) {
                OutlinedTextField(
                    modifier = Modifier
                        .padding(5.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    isError = isConfirmPasswordError,
                    value = confirmPassword,
                    onValueChange = {
                        viewModel.confirmPasswordValueState.value = it
                        viewModel.onConfirmPasswordChanged()
                    },
                    label = { Text(text = "Confirm password") },
                    supportingText = {
                        if (isConfirmPasswordError)
                            Text(text = "Password doesn't match")
                    },
                    //   keyboardOptions =  KeyboardOptions(keyboardType = KeyboardType.Password),
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
            }
            Button(
                enabled = !isPasswordError && !isConfirmPasswordError && !isEmailError && email.isNotBlank() && password.isNotBlank(),
                onClick = {
                    //  navController.navigate("home_screen")
                    if (register) {
                        viewModel.onSignUp()
                    } else {
                        viewModel.onSignIn()
                    }
                }) {
                if (register) {
                    Text("Register")
                } else {
                    Text("Sign In")
                }
            }
            if (!register) {
                TextButton(
                    onClick = {
                        register = true
                    },
                ) {
                    Text(text = "Don't have an account ? Register!")
                }
            } else {
                TextButton(
                    onClick = {
                        register = false
                    },
                ) {
                    Text(text = "Back to signing in ...")
                }
            }

            val context = LocalContext.current
/*            LaunchedEffect(signInError) {
                if (signInError != null) {
                    Toast.makeText(
                        context,
                        "Error: $signInError",
                        Toast.LENGTH_LONG,
                    ).show()
                }
            }*/
            when (authState) {
                SignInState.AUTHORIZED -> {
                    navController.navigate("home_screen")
                }

                SignInState.CREDENTIAL_ERROR -> Toast.makeText( // todo scaffold & snackbar
                    context,
                    "Authentication failed cause of credential error."+
                            if (!signInError.isNullOrEmpty()) "Details: $signInError" else "",
                    Toast.LENGTH_SHORT,
                ).show()

                SignInState.USER_NOT_FOUND -> Toast.makeText(
                    context,
                    "No such user exist" +
                            if (!signInError.isNullOrEmpty()) "\r\nDetails: $signInError" else "",
                    Toast.LENGTH_SHORT,
                ).show()

                SignInState.VERIFY_FAILED -> Toast.makeText(
                   context,
                    "Email verification failed :("+
                            if (!signInError.isNullOrEmpty()) "\r\nDetails: $signInError" else "",
                    Toast.LENGTH_SHORT,
                ).show()

                SignInState.VERIFYING_EMAIL -> Toast.makeText(
                    context,
                    "Verification email was sent",
                    Toast.LENGTH_SHORT,
                ).show()

                SignInState.USER_COLLISION -> Toast.makeText(
                    context,
                    "$email already exist"+
                            if (!signInError.isNullOrEmpty()) "\r\nDetails: $signInError" else "",

                    Toast.LENGTH_SHORT,
                ).show()

                else -> {}
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

@Composable
fun PasswordField(
    hint: String,
    modifier: Modifier = Modifier,
    isConfirmPasswordError: Boolean,
    confirmPassword: String,
    viewModel: RegistrationViewModel,
    register: Boolean,
) {

    var hidePassword by remember { mutableStateOf(true) } // todo hide one more
    var passwordVisualTransformation by remember { mutableStateOf(VisualTransformation.None) }

    passwordVisualTransformation = if (hidePassword) {
        PasswordVisualTransformation()
    } else {
        VisualTransformation.None
    }

    OutlinedTextField(
        modifier = modifier
            .padding(5.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        ),
        isError = isConfirmPasswordError,
        value = confirmPassword,
        onValueChange = {
            /*viewModel.confirmPasswordValueState.value = it
            viewModel.onTextValueChanged(register)*/
        },
        label = { Text(text = hint) },//conf pas vs entr pas
        supportingText = {
            if (isConfirmPasswordError)
                Text(text = "Password doesn't match")
        },
        //   keyboardOptions =  KeyboardOptions(keyboardType = KeyboardType.Password),
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
}