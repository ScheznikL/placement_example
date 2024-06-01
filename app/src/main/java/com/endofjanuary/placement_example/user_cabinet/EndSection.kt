package com.endofjanuary.placement_example.user_cabinet

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import com.endofjanuary.placement_example.repo.SignInState

@Composable
fun EndSection(
    openSignOutDialog: MutableState<Boolean>,
    isEmailVerified: Boolean,
    onVerifyEmail: () -> Unit,
    authState :SignInState,
    ) {
    Button(
        onClick = {
            openSignOutDialog.value = true
        }, modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "Sing Out")
    }
    if (!isEmailVerified) {
        Button(
            onClick = onVerifyEmail,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = "verify email")
        }
        if (authState == SignInState.VERIFY_FAILED) {
            Text(text = "You logged out successfully")
        } else if (authState == SignInState.VERIFYING_EMAIL) {
            Text(text = "Verifying email \r\n check your email box")
        }
    }
}