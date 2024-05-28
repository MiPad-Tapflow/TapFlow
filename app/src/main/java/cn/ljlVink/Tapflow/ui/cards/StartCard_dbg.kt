package cn.ljlVink.Tapflow.ui.cards

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import cn.ljlVink.Tapflow.MainActivity
import cn.ljlVink.Tapflow.R
import com.freerdp.freerdpcore.services.LibFreeRDP
import com.xiaomi.mslgrdp.multwindow.MultiWindowManager


@Composable
fun StartCard_dbg(){
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->}
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(
            onClick = {
                val intent = Intent("com.xiaomi.action.mslgrdp.client")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.setPackage(context.packageName)
                intent.putExtra("StartFromMSLG", true)
                intent.putExtra("StarMslgApp", "cajviewer")
                launcher.launch(intent)
            }
        ) {
            Text(text = stringResource(id = R.string.start_mslg))
        }
        Button(
            onClick = {
                MainActivity.utils.SetProp("sys.mslg.restart","0")
                MainActivity.utils.SetProp("sys.mslg.restart","1")
            }
        ) {
            Text(text = "restart mslgd")
        }
        Button(
            onClick = {
                MainActivity.utils.Kill_Linux_proc("qq")
                MainActivity.utils.Kill_Linux_proc("obsidian")
                MainActivity.utils.Kill_Linux_proc("StubWindow")

            }
        ) {
            Text(text = "kill process qq")
        }
        Button(
            onClick = {
                MainActivity.utils.Start_miui_dkt(true)
            }
        ) {
            Text(text = "start dkt")
        }
        Button(
            onClick = {
                MainActivity.utils.Start_miui_dkt(false)
            }
        ) {
            Text(text = "stop dkt")
        }
        Button(
            onClick = {
                val session = MultiWindowManager.getSessionManager().currentSession
                LibFreeRDP.openRemoteApp(session.instance, "/opt/caj/Obsidian/start.sh",null);
            }
        ) {
            Text(text = "start obsidian")
        }

    }
}
