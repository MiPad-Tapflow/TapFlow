package cn.ljlVink.Tapflow.ui.cards

import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.ljlVink.Tapflow.MainActivity
import cn.ljlVink.Tapflow.R

@Preview
@Composable
fun InfoCard() {
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
            InfoCardItem(
                stringResource(R.string.device_info),
                "Android "+ Build.VERSION.RELEASE+" API("+ Build.VERSION.SDK_INT+") "+ Build.VERSION.INCREMENTAL+" "+
                        Build.MANUFACTURER +" "+ Build.MODEL+" ("+ Build.PRODUCT+")"+
                        " SELinux "+ MainActivity.utils.GetEnforcing()
            )
            Spacer(Modifier.height(16.dp))
            InfoCardItem(
                stringResource(R.string.prog_ver),
                "app: "+ MainActivity.utils.GetAppVersionInfoWithSpace(context)+" losetup.sh-go: "+ MainActivity.utils.GetModuleVersion()
            )
            Spacer(Modifier.height(16.dp))
            InfoCardItem(stringResource(R.string.rootfs_ver), MainActivity.utils.GetMslgVersion())
            Spacer(Modifier.height(16.dp))
            val isStarted=if (MainActivity.utils.GetIsMountedMslg()) "已经启动" else "未启动"
            InfoCardItem(stringResource(R.string.is_mounted_rootfs),isStarted)
            Spacer(Modifier.height(16.dp))
            InfoCardItem(stringResource(R.string.debug_info), MainActivity.utils.GetDebugInfo())

        }
    }
}
