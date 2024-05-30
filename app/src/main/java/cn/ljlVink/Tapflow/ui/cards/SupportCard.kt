package cn.ljlVink.Tapflow.ui.cards

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.ljlVink.Tapflow.R
import cn.ljlVink.Tapflow.ui.tools.HtmlText

@Preview
@Composable
fun SupportCard() {
    ElevatedCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = stringResource(R.string.help_text_title),
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                modifier = Modifier.padding(vertical = 8.dp),
                text = stringResource(R.string.help_text),
                style = MaterialTheme.typography.bodyMedium
            )
            HtmlText(
                stringResource(
                    R.string.home_view_source_code,
                    "<b><a href=\"https://github.com/ljlVink/TapFlow\">GitHub</a></b>",
                    "<b><a href=\"http://qm.qq.com/cgi-bin/qm/qr?_wv=1027&k=xRWDf3imMuP3IXC4TLTAySKr00hTTN50&authKey=fOygXE91t%2BVxd3Q6XyWmFQgxrmSen9ngw4v1%2F1r0om%2Bl8FblA5YNz2X1rRd8%2BTB%2B&noverify=0&group_code=839763091\">QQ</a></b>"
                )
            )

        }
    }
}

