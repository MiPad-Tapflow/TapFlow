package cn.ljlVink.Tapflow.ui.screen

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import cn.ljlVink.Tapflow.R


enum class BottomBarItem(val iconResid:Int , val label: Int) {
    Home(R.drawable.baseline_home_24,R.string.home_text),
    Settings(R.drawable.baseline_settings_24,R.string.settings),
    Logs(R.drawable.baseline_receipt_24,R.string.logs)
}
