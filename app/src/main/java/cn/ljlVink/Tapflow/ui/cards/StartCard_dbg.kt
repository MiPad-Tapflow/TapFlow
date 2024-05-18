package cn.ljlVink.Tapflow.ui.cards

import android.content.ComponentName
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
import cn.ljlVink.Tapflow.R

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
                context.startActivity(intent)
                val intent2 = Intent()
                intent2.setComponent(
                    ComponentName(
                        context.packageName,
                        "com.xiaomi.mslgrdp.multwindow.MultiWindowService"
                    )
                )
                context.startForegroundService(intent2)
                launcher.launch(intent)
            }
        ) {
            Text(text = stringResource(id = R.string.start_mslg))
        }
    }
}
