package cn.ljlVink.Tapflow.ui.screen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.ljlVink.Tapflow.ui.theme.red

@ExperimentalMaterial3Api
@Composable
fun ErrorScreen(message: String) {
    Scaffold { paddingValues ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Filled.Warning,
                    modifier = Modifier
                        .width(48.dp)
                        .height(48.dp),
                    tint = red,
                    contentDescription = message
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    message,
                    modifier = Modifier.padding(32.dp, 0.dp, 32.dp, 32.dp),
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        }
    }
}