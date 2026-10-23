package ru.myvista.cheaper.ui.theme

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun AppOutlinedTextField(
	value: String,
	onValueChange: (String) -> Unit,
	label: String,
	modifier: Modifier = Modifier,
	keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
	enabled: Boolean = true
) {
	val interactionSource = remember { MutableInteractionSource() }
	val colors = OutlinedTextFieldDefaults.colors()

	BasicTextField(
		value = value,
		onValueChange = onValueChange,
		modifier = modifier,
		enabled = enabled,
		singleLine = true,
		keyboardOptions = keyboardOptions,
		interactionSource = interactionSource,
		cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
		textStyle = MaterialTheme.typography.bodyLarge.copy(
			color = MaterialTheme.colorScheme.onSurface
		),
		decorationBox = { innerTextField ->
			OutlinedTextFieldDefaults.DecorationBox(
				value = value,
				innerTextField = innerTextField,
				enabled = enabled,
				singleLine = true,
				visualTransformation = VisualTransformation.None,
				interactionSource = interactionSource,
				label = { Text(label) },
				colors = colors,
				container = {
					OutlinedTextFieldDefaults.Container(
						enabled = enabled,
						isError = false,
						interactionSource = interactionSource,
						colors = colors,
						shape = OutlinedTextFieldDefaults.shape,
						focusedBorderThickness = 1.dp,
						unfocusedBorderThickness = 1.dp
					)
				}
			)
		}
	)
}