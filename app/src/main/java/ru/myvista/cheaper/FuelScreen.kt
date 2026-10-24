package ru.myvista.cheaper

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.myvista.cheaper.ui.theme.AppOutlinedTextField

@Composable
fun FuelScreen(
	tankCapacity: String,
	onTankCapacityChange: (String) -> Unit,
	tankCapacityIsDefault: Boolean,
	onTankCapacityDefaultChange: (Boolean) -> Unit,
	fuelPercent: String,
	onFuelPercentChange: (String) -> Unit,
	result: String,
	onResultChange: (String) -> Unit,
) {
	val colors = MaterialTheme.colorScheme
	val focusManager = LocalFocusManager.current
	val scrollState = rememberScrollState()

	Column(
		modifier = Modifier
			.fillMaxSize()
			.background(colors.background)
			.clickable(
				interactionSource = remember { MutableInteractionSource() },
				indication = null
			) {
				focusManager.clearFocus()
			}
			.verticalScroll(scrollState)
			.padding(16.dp),
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			Text(
				text = "Бензин",
				color = colors.onBackground
			)
		}

		Column(
			verticalArrangement = Arrangement.spacedBy(0.dp)
		) {
			AppOutlinedTextField(
				value = tankCapacity,
				onValueChange = { value ->
					if (isValidNumber(value)) {
						onTankCapacityChange(value)
						onResultChange("")
					}
				},
				label = "Объём бака, л",
				modifier = Modifier.fillMaxWidth(),
				keyboardOptions = KeyboardOptions(
					keyboardType = KeyboardType.Decimal
				)
			)

			Row(
				verticalAlignment = Alignment.CenterVertically
			) {
				Checkbox(
					checked = tankCapacityIsDefault,
					onCheckedChange = { checked ->
						onTankCapacityDefaultChange(checked)
						focusManager.clearFocus()
					}
				)

				Text(
					text = "По умолчанию",
					fontSize = 11.sp,
					color = colors.onSurfaceVariant
				)
			}
		}

		AppOutlinedTextField(
			value = fuelPercent,
			onValueChange = { value ->
				if (isValidNumber(value)) {
					onFuelPercentChange(value)
					onResultChange("")
				}
			},
			label = "Заполнено, %",
			modifier = Modifier.fillMaxWidth(),
			keyboardOptions = KeyboardOptions(
				keyboardType = KeyboardType.Decimal
			)
		)

		Button(
			onClick = {
				focusManager.clearFocus()

				val capacity = tankCapacity.toDoubleOrNull()
				val percent = fuelPercent.toDoubleOrNull()

				if (capacity != null && percent != null) {
					val liters = FuelCalculator.calculateLiters(
						capacity = capacity,
						percent = percent
					)

					if (liters != null) {
						onResultChange(
							"Нужно залить: %.2f л".format(liters)
						)
					}
				}
			},
			enabled =
				tankCapacity.toDoubleOrNull()?.let { it > 0 } == true &&
						fuelPercent.toDoubleOrNull()?.let {
							it in 0.0..100.0
						} == true,
			modifier = Modifier.fillMaxWidth()
		) {
			Text("Рассчитать")
		}

		if (result.isNotEmpty()) {
			Text(
				text = result,
				color = colors.onBackground
			)
		}
	}
}