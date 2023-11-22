package cn.ljlVink.Tapflow.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cn.ljlVink.Tapflow.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumberInputDialog(onConfirm: (Int) ->  Unit, onDismiss: () -> Unit) {
    var inputValue by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(id = R.string.input_expend_tip))
        },
        text = {
            TextField(
                value = inputValue,
                onValueChange = {
                    inputValue = it.filter { char -> char.isDigit() }
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (inputValue.isNotEmpty()) {
                        onConfirm(inputValue.toInt())
                    }
                    onDismiss()
                }
            ) {
                Text(stringResource(id = R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(stringResource(id = R.string.dismiss))
            }
        }
    )
}
