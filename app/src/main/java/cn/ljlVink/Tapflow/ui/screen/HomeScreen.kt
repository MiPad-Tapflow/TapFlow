package cn.ljlVink.Tapflow.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cn.ljlVink.Tapflow.R
import cn.ljlVink.Tapflow.ui.cards.StatusCard
import cn.ljlVink.Tapflow.ui.cards.SupportCard
import cn.ljlVink.Tapflow.ui.TitleBar
import cn.ljlVink.Tapflow.ui.cards.InfoCard
import cn.ljlVink.Tapflow.ui.cards.StartCard_dbg

@Composable
fun HomeScreen(){
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
