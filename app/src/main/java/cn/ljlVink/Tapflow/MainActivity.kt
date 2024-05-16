package cn.ljlVink.Tapflow

import android.app.Application
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cn.ljlVink.Tapflow.cards.StatusCard
import cn.ljlVink.Tapflow.cards.SupportCard
import cn.ljlVink.Tapflow.pages.ErrorScreen
import cn.ljlVink.Tapflow.ui.TitleBar
import cn.ljlVink.Tapflow.ui.theme.TapflowTheme
import cn.ljlVink.Tapflow.util.utils
import com.topjohnwu.superuser.Shell
import com.xiaomi.mslgrdp.application.GlobalApp
import es.dmoral.toasty.Toasty


class MainActivity : ComponentActivity() {

    companion object{
        lateinit var myApp : Application
        lateinit var utils : utils
    }
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        utils = utils()
        myApp=application as GlobalApp
        super.onCreate(savedInstanceState)
        Shell.getShell()
        val result = Shell.isAppGrantedRoot()
        if (false==result) {
            setContent {
                TapflowTheme {
                    ErrorScreen(stringResource(R.string.root_required))
                }
            }
            return
        }
        utils.grantpermission(this)
        setContent {
            TapflowTheme{
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = {
                        BuildNav(navController)
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "主页",
                        Modifier.padding(innerPadding)
                    ) {
                        composable("主页") {
                            HomeScreen(navController = navController)
                        }
                        composable("日志") {
                            Logscreen(navController = navController)
                        }
                    }
                }
            }
        }

    }
    override fun onResume(){
        super.onResume()
        utils.KillApplication("com.xiaomi.mslgrdp")

    }
    data class Item(
        val name: String,
        val icon: Int
    )
    @Composable
    private fun BuildNav(navController: NavHostController){
        var selectedItem by remember {
            mutableStateOf(0)
        }
        val items = listOf(
            Item("主页", R.drawable.baseline_home_24),
            Item("日志", R.drawable.baseline_receipt_24),
            )
        NavigationBar(
            tonalElevation = 8.dp
        ){
          //  val currentRoute = currentRoute(navController)
            items.forEachIndexed { index,item ->
                NavigationBarItem(
                    selected = selectedItem == index,
                    icon = {
                        Icon(
                            painterResource(id = item.icon), null
                        )
                    },
                    onClick = {
                        selectedItem = index
                        navController.navigate(item.name){
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }

                    },
                    alwaysShowLabel = false,
                    label = {
                        Text(text = item.name) },
                    )
            }
        }
    }
    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun Logscreen(navController: NavController){
        Scaffold (
            topBar = {TitleBar(title = stringResource(id = R.string.app_name))},
            content = {
                    innerPadding->
                Column (
                    modifier= Modifier
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(
                            rememberScrollState()
                        ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(text =utils.GetLogs_losetup())
                }
            }
        )
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun HomeScreen(navController: NavController){
        Scaffold(
            topBar = { TitleBar(title = stringResource(id = R.string.app_name)) },
            content = {
                    innerPadding->
                Column (
                    modifier= Modifier
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(
                            rememberScrollState()
                        ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StartCard_dbg()
                    StatusCard()
                    InfoCard()
                    SupportCard()
                }
            }
        )
    }
    @Composable
    private  fun StartCard_dbg(){
        val context = LocalContext.current
        val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->}
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    Shell.cmd("setprop sys.mslg.mounted 1").exec()
                }
            ) {
                Text(text = stringResource(id = R.string.start_prop))
            }
            Button(
                onClick = {
                    val intent = Intent("com.xiaomi.action.mslgrdp.client")
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.setPackage(packageName)
                    intent.putExtra("StartFromMSLG", true)
                    intent.putExtra("StarMslgApp", "cajviewer")
                    startActivity(intent)
                    val intent2 = Intent()
                    intent2.setComponent(
                        ComponentName(
                            packageName,
                            "com.xiaomi.mslgrdp.multwindow.MultiWindowService"
                        )
                    )
                    startForegroundService(intent2)
                    launcher.launch(intent)
                }
            ) {
                Text(text = stringResource(id = R.string.start_mslg))
            }
        }
    }

    @Preview
    @Composable
    private fun InfoCard() {
        val context = LocalContext.current
        ElevatedCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 16.dp)
            ) {
                val contents = StringBuilder()
                @Composable
                fun InfoCardItem(label: String, content: String) {
                    contents.appendLine(label).appendLine(content).appendLine()
                    Text(text = label, style = MaterialTheme.typography.bodyLarge)
                    Text(text = content, style = MaterialTheme.typography.bodyMedium)
                }
                InfoCardItem(stringResource(R.string.device_info),
                    "Android "+android.os.Build.VERSION.RELEASE+" API("+android.os.Build.VERSION.SDK_INT+") "+android.os.Build.VERSION.INCREMENTAL+" "+
                            android.os.Build.MANUFACTURER +" "+android.os.Build.MODEL+" ("+android.os.Build.PRODUCT+")"+
                            " SELinux "+utils.getEnforcing()
                            )
                Spacer(Modifier.height(16.dp))
                InfoCardItem(
                    stringResource(R.string.prog_ver),
                    "app: "+utils.getAppVersionInfoWithSpace(context)+" losetup.sh-go: "+utils.GetModuleVersion()
                )
                Spacer(Modifier.height(16.dp))
                InfoCardItem(stringResource(R.string.rootfs_ver),utils.getprop("ro.vendor.mslg.rootfs.version"))
                Spacer(Modifier.height(16.dp))
                val isStarted=if (utils.getprop("sys.mslg.mounted").equals("1")) "已经启动" else "未启动"
                InfoCardItem(stringResource(R.string.is_mounted_rootfs),isStarted)
                Spacer(Modifier.height(16.dp))
                InfoCardItem(stringResource(R.string.debug_info),utils.GetDebugInfo())

            }
        }
    }


}