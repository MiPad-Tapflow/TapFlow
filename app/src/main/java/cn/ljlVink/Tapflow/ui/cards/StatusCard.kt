package cn.ljlVink.Tapflow.ui.cards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.ljlVink.Tapflow.MainActivity

@Preview
@Composable
fun StatusCard(){
    val state= MainActivity.utils.GetState()
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(containerColor = run {
            when(state){
                0 -> MaterialTheme.colorScheme.primary
                1 -> MaterialTheme.colorScheme.error
                2 -> MaterialTheme.colorScheme.errorContainer
                3 -> MaterialTheme.colorScheme.errorContainer
                else -> Color(0xFFFF6A6A)
            }
        })

    ){
        Column (
            modifier= Modifier
                .fillMaxWidth()
                .clickable {}
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ){
            Text(
                text = "状态",
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleLarge

            )
            Text(text = when(state){
                0 ->"模块运行正常"
                1 ->"需要重启"
                2 ->"状态异常，请查看日志"
                3 ->"模块未启动/未安装"
                else -> "?"
            }
            )
        }
    }
}
