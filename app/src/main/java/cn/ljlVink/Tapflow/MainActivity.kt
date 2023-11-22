package cn.ljlVink.Tapflow

import android.app.Application
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import cn.ljlVink.Tapflow.cards.SupportCard
import cn.ljlVink.Tapflow.cards.StatusCard

import cn.ljlVink.Tapflow.pages.ErrorScreen
import cn.ljlVink.Tapflow.ui.NumberInputDialog
import cn.ljlVink.Tapflow.ui.TitleBar
import cn.ljlVink.Tapflow.ui.theme.TapflowTheme
import cn.ljlVink.Tapflow.util.utils
import com.topjohnwu.superuser.Shell
import com.xiaomi.mslgrdp.application.GlobalApp
import com.xiaomi.mslgrdp.presentation.SessionActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.String


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
                        composable("分区管理") {
                            PartttionPage(navController = navController)
                        }
                        composable("日志") {
                            Logscreen(navController = navController)
                        }
                    }
                }
            }
        }

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
            Item("分区管理", R.drawable.baseline_part_24),
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
                    val intent = Intent(context,SessionActivity::class.java)
                    launcher.launch(intent)
                }
            ) {
                Text(text = stringResource(id = R.string.start_mslg))
            }
        }
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun PartttionPage(navController: NavController){
        val myApp = application as GlobalApp
        val scope= rememberCoroutineScope()
        val hostState = remember { SnackbarHostState() }
        val info_usr=myApp.info_usr
        val info_opt=myApp.info_opt
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = hostState) },
            topBar = {TitleBar(title = stringResource(id = R.string.partition_manage))},
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
                    //分区卡片 opt & usr 管理
                    if(info_usr!=null&&info_opt!=null){
                        ShowParttionCard(partinfo = info_opt, str_res = R.string.mount_df_opt, path = "/data/Tapflow_project/need_resize_opt",scope,hostState)
                        ShowParttionCard(partinfo = info_usr, str_res = R.string.mount_df_usr, path = "/data/Tapflow_project/need_resize_usr",scope,hostState)
                    }
                }
            }
        )
    }

    @Composable
    fun ShowParttionCard(partinfo:FileSystemInfo, str_res:Int, path: String, scope:CoroutineScope, hostState: SnackbarHostState){
        ElevatedCard(
            modifier = Modifier
                .fillMaxHeight()
        ) {
            Column(
                modifier = Modifier
                    .padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 16.dp)
            ) {
                var isDialogVisible by remember { mutableStateOf(false) }
                val contents = StringBuilder()

                @Composable
                fun InfoCardItem(label: String, content: String) {
                    contents.appendLine(label).appendLine(content).appendLine()
                    Text(text = label, style = MaterialTheme.typography.bodyLarge)
                    Text(text = content, style = MaterialTheme.typography.bodyMedium)
                }
                InfoCardItem(
                    stringResource(str_res),""
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LinearProgressIndicator(
                        progress = partinfo.use,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = partinfo.usrStr+"("+partinfo.used+"/"+partinfo.size+")",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp)) // 在 Text 和 Button 之间添加 8dp 的间距
                    Button(
                        onClick = {
                            isDialogVisible=true
                        }
                    ) {
                        Text(text = stringResource(id = R.string.expend_part))
                    }

                    if(isDialogVisible){
                        NumberInputDialog(
                            onConfirm = {
                                isDialogVisible = false
                                Shell.cmd("echo "+it.toString()+"G>"+path).exec()
                                scope.launch {
                                    val result=hostState.showSnackbar(
                                            message = "重启生效",
                                            actionLabel = "重启",
                                    )
                                    when (result) {
                                        SnackbarResult.ActionPerformed->{Shell.cmd("reboot").exec()}
                                        SnackbarResult.Dismissed->{}
                                    }
                                }
                            },
                            onDismiss = {
                                isDialogVisible = false
                            }
                        )

                    }

                }
            }
        }

    }







    @Preview
    @Composable
    private fun InfoCard() {
        val optimg=utils.getprop("vendor.mslg.mslgoptimg")
        val usrimg=utils.getprop("vendor.mslg.mslgusrimg")
        val info_opt=utils.parseFileSystemInfo(optimg)
        val info_usr=utils.parseFileSystemInfo(usrimg)
        val myApp = application as GlobalApp
        if(info_usr!=null&&info_opt!=null){
            myApp.info_opt=info_opt
            myApp.info_usr=info_usr
        }
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
                InfoCardItem(stringResource(R.string.android_ver), "Android "+android.os.Build.VERSION.RELEASE+" API("+android.os.Build.VERSION.SDK_INT+") "+android.os.Build.VERSION.INCREMENTAL)
                Spacer(Modifier.height(16.dp))
                InfoCardItem(
                    stringResource(R.string.prog_ver),
                    utils.getAppVersionInfoWithSpace(context)
                )
                Spacer(Modifier.height(16.dp))
                InfoCardItem(
                    stringResource(R.string.module_ver),
                    utils.GetModuleVersion()
                )
                Spacer(Modifier.height(16.dp))
                InfoCardItem(stringResource(R.string.device_model),android.os.Build.MANUFACTURER +" "+android.os.Build.MODEL+"("+android.os.Build.PRODUCT+")")
                Spacer(Modifier.height(16.dp))
                InfoCardItem(stringResource(R.string.rootfs_ver),utils.getprop("ro.vendor.mslg.rootfs.version"))
                Spacer(Modifier.height(16.dp))
                val isStarted=if (utils.getprop("sys.mslg.mounted").equals("1")) "已经启动" else "未启动"
                InfoCardItem(stringResource(R.string.is_mounted_rootfs),isStarted)
                Spacer(Modifier.height(16.dp))
                val enforce=utils.getEnforcing()
                val isInenforce:String
                if(enforce.equals("Enforcing")){
                    isInenforce= stringResource(id = R.string.selinux_enforcing)
                }else{
                    isInenforce= stringResource(id = R.string.selinux_permissive)
                }
                InfoCardItem(stringResource(R.string.selinux_stat),isInenforce)
                Spacer(Modifier.height(16.dp))
                InfoCardItem(stringResource(R.string.mount_losetup_opt),optimg)
                Spacer(Modifier.height(16.dp))
                InfoCardItem(stringResource(R.string.mount_losetup_usr),usrimg)
                Spacer(Modifier.height(16.dp))

            }
        }
    }


}