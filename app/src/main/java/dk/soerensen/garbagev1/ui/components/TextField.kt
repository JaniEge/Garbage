package dk.soerensen.garbagev1.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    focusManager: FocusManager? = null,
    isLastField: Boolean = true,
    isError: Boolean = false,
    supportingText: String? = null,
    minLines: Int = 1,
) {
    val imeAction = if (isLastField) ImeAction.Done else ImeAction.Next

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.padding(vertical = 8.dp),
        isError = isError,
        minLines = minLines,
        keyboardOptions = KeyboardOptions(imeAction = imeAction),
        keyboardActions = KeyboardActions(
            onNext = {
                focusManager?.moveFocus(FocusDirection.Next)
            },
            onDone = {
                focusManager?.clearFocus()
            }
        ),
        supportingText = {
            if (supportingText != null) Text(supportingText)
        }
    )
}