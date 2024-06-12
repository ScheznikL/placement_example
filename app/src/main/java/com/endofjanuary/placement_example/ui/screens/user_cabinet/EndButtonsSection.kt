package com.endofjanuary.placement_example.ui.screens.user_cabinet

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.endofjanuary.placement_example.R
import com.endofjanuary.placement_example.domain.repo.SignInState

@Composable
fun EndButtonsSection(
    openSignOutDialog: MutableState<Boolean>,
    isEmailVerified: Boolean,
    onVerifyEmail: () -> Unit,
    authState : SignInState,
    ) {
    Button(
        onClick = {
            openSignOutDialog.value = true
        }, modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = stringResource(R.string.sing_out))
    }
    if (!isEmailVerified) {
        Button(
            onClick = onVerifyEmail,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = stringResource(R.string.verify_email))
        }
        if (authState == SignInState.VERIFY_FAILED) {
            Text(text = stringResource(R.string.logged_out_successfully))
        } else if (authState == SignInState.VERIFYING_EMAIL) {
            Text(text = stringResource(R.string.verifying_email))
        }
    }
}