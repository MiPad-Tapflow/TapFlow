package cn.ljlVink.Tapflow.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cn.ljlVink.Tapflow.MainActivity
import cn.ljlVink.Tapflow.R
import cn.ljlVink.Tapflow.ui.TitleBar
import cn.ljlVink.Tapflow.ui.tools.CustomAlertDialog
import cn.ljlVink.Tapflow.ui.tools.IconButtonWithText
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen() {
    val snackbarHostState = remember { SnackbarHostState() }
    val scaffoldState = rememberCoroutineScope()
    var showWarnDialog by remember { mutableStateOf(false) }
    val rebootTakeEffect = stringResource(id = R.string.reboot_take_effect)
    val operationDone = stringResource(id = R.string.operation_done)
    val context = LocalContext.current

    if (showWarnDialog) {
        CustomAlertDialog(
            showDialog = showWarnDialog,
            title = stringResource(id = R.string.warn),
            text = stringResource(id = R.string.dialog_warn_reinstall_rootfs),
            confirmButtonText =stringResource(id = R.string.confirm),
            dismissButtonText =stringResource(id = R.string.dismiss),
            onConfirm = {
                MainActivity.utils.SetReinstallRootfs()
                scaffoldState.launch {
                    snackbarHostState.showSnackbar(rebootTakeEffect)
                }
                showWarnDialog = false
            },
            onDismiss = { showWarnDialog = false }
        )
    }

    Scaffold (
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { TitleBar(title = stringResource(id = R.string.settings)) },
        content = {
                innerPadding->
            Column (
                modifier= Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(
                        rememberScrollState()
                    ),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                IconButtonWithText(
                    icon = { Icon(Icons.Default.Warning, contentDescription = null) },
                    text = stringResource(id = R.string.set_selinux_permissive),
                    onClick = {
                        MainActivity.utils.SetSELinuxPermissive()
                        scaffoldState.launch {
                            snackbarHostState.showSnackbar(operationDone)
                        }
                    }
                )

                IconButtonWithText(
                    icon = { Icon(Icons.Outlined.Clear, contentDescription = null) },
                    text = stringResource(id = R.string.reinstall_rootfs),
                    onClick = {
                        showWarnDialog = true
                    }
                )

                IconButtonWithText(
                    icon = { Icon(Icons.Outlined.MailOutline, contentDescription = null) },
                    text = stringResource(id = R.string.report_bug_in_github),
                    onClick = {
                        val githubUrl = "https://github.com/MiPad-Tapflow/Tapflow/issues/new/"
                        val uri = Uri.parse(githubUrl)
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        context.startActivity(intent)
                    }
                )
            }
        }
    )

}