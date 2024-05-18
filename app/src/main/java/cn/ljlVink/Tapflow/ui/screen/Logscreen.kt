package cn.ljlVink.Tapflow.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cn.ljlVink.Tapflow.MainActivity
import cn.ljlVink.Tapflow.R
import cn.ljlVink.Tapflow.ui.TitleBar

@Composable
fun Logscreen(){
    Scaffold (
        topBar = { TitleBar(title = stringResource(id = R.string.logs)) },
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
                Text(text = MainActivity.utils.GetLogs_losetup())
            }
        }
    )
}
