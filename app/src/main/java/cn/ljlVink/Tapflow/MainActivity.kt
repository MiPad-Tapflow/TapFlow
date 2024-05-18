package cn.ljlVink.Tapflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cn.ljlVink.Tapflow.ui.TitleBar
import cn.ljlVink.Tapflow.ui.screen.ErrorScreen
import cn.ljlVink.Tapflow.ui.screen.BottomBarItem
import cn.ljlVink.Tapflow.ui.screen.HomeScreen
import cn.ljlVink.Tapflow.ui.screen.Logscreen
import cn.ljlVink.Tapflow.ui.screen.SettingsScreen
import cn.ljlVink.Tapflow.ui.theme.TapflowTheme
import cn.ljlVink.Tapflow.ui.tools.CustomAlertDialog
import cn.ljlVink.Tapflow.util.utils
import com.topjohnwu.superuser.Shell


class MainActivity : ComponentActivity() {

    companion object{
        lateinit var utils : utils
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        utils = utils()
        super.onCreate(savedInstanceState)

        if(!checkPermission()){
            return
        }

        setContent {
            TapflowTheme{
                TitleBar(title = stringResource(id = R.string.app_name))
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = {
                        BuildNav(navController)
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = BottomBarItem.Home.name,
                        Modifier.padding(innerPadding)
                    ) {
                        composable(BottomBarItem.Home.name) {
                            HomeScreen()
                        }
                        composable(BottomBarItem.Settings.name){
                            SettingsScreen()
                        }
                        composable(BottomBarItem.Logs.name) {
                            Logscreen()
                        }
                    }
                }
                ShowMountMslgDialog()
            }

        }

    }
    @Composable
    fun ShowMountMslgDialog() {
        var showDialog by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            if (!utils.GetIsMountedMslg()) {
                showDialog = true
            }
        }
        CustomAlertDialog(
            showDialog = showDialog,
            title = "Mslg not starting",
            text = "Start mslg?",
            confirmButtonText = "Start",
            dismissButtonText = "No, I want to Terminate.",
            onConfirm = {
                utils.SetMountMslg()
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
    @OptIn(ExperimentalMaterial3Api::class)
    fun checkPermission():Boolean{
        Shell.getShell()
        val result = Shell.isAppGrantedRoot()
        if (false==result) {
            setContent {
                TapflowTheme {
                    ErrorScreen(stringResource(R.string.root_required))
                }
            }
            return false;
        }
        utils.Grantpermission(this)
        return true
    }
    override fun onResume(){
        super.onResume()
        utils.KillApplication("com.xiaomi.mslgrdp")
    }
    @Composable
    private fun BuildNav(navController: NavHostController){
        NavigationBar (
            tonalElevation = 8.dp,
        ){
            BottomBarItem.values().forEach { item ->
                NavigationBarItem(
                    icon = {
                        Icon(painterResource(id = item.iconResid),stringResource(id = item.label))
                    },
                    selected = navController.currentBackStackEntry?.destination?.route == item.name,
                    onClick = {
                        navController.navigate(item.name) {

                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                            anim {
                                enter = 0
                                exit = 0
                            }
                        }
                    }
                )
            }
        }
    }

}