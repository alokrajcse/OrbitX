package com.example.orbitx.Views

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import com.example.orbitx.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun Exit(modifier: Modifier = Modifier, activity: Activity, navController: NavController
) {

    Box(modifier = Modifier.fillMaxSize()){
        Image(painter = painterResource(id = R.drawable.bg), contentDescription = "", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
    }
    val openAlertDialog = remember { mutableStateOf(true) }


    when {

        openAlertDialog.value -> {
            AlertDialogExample(
                onDismissRequest = {
                    navController.popBackStack()
                    openAlertDialog.value = false },
                onConfirmation = {
                    openAlertDialog.value = false
                    Firebase.auth.signOut()
                    ActivityCompat.finishAffinity(activity)


                },
                dialogTitle = "Logout",

                dialogText = "Are you sure, you want to logout?",
                icon = Icons.Default.Info
            )
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertDialogExample(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
) {
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Example Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}