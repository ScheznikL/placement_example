package com.endofjanuary.placement_example.user_cabinet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
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


    Scaffold(bottomBar = { BottomBar(navController) }) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row {
                Text("User's Email:")
                Text(currentUser?.email ?: "")
            }
            Text("is verified: ${currentUser?.isEmailVerified}")


            if (authState == SignInState.NOT_SIGNED_IN || currentUser == null) {
                Text(text = "You logged out successfully")
                Button(onClick = { navController.navigate("reg_screen") }) {
                    Text(text = "To Welcome Screen")
                }
            } else {
                Button(onClick = viewModel::onSinghOut) {
                    Text(text = "Sing Out")
                }
                if(currentUser?.isEmailVerified == false){
                    Button(onClick = viewModel::verifyEmail) {
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