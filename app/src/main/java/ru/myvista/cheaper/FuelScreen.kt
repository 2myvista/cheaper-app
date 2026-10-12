package ru.myvista.cheaper

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
import androidx.compose.material3.Checkbox
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

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

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(16.dp),
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {

		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			Text("Бензин")
		}

		Column(
			verticalArrangement = Arrangement.spacedBy(0.dp)
		) {
			OutlinedTextField(
				value = tankCapacity,
				onValueChange = {
					onTankCapacityChange(it)
					onTankCapacityDefaultChange(false)
				},
				label = {
					Text("Объём бака, л")
				},
				modifier = Modifier.fillMaxWidth()
			)

			Row(
				verticalAlignment = Alignment.CenterVertically
			) {
				Checkbox(
					checked = tankCapacityIsDefault,
					onCheckedChange = onTankCapacityDefaultChange
				)

				Text(
					text = "По умолчанию",
					fontSize = 11.sp,
					color = Color.Gray
				)
			}
		}

		OutlinedTextField(
			value = fuelPercent,
			onValueChange = onFuelPercentChange,
			label = {
				Text("Заполнено, %")
			},
			modifier = Modifier.fillMaxWidth()
		)

		Button(
			onClick = {

				val capacity = tankCapacity.toDoubleOrNull()
				val percent = fuelPercent.toDoubleOrNull()

				if (capacity != null && percent != null) {

					val liters =
						capacity * (100 - percent) / 100

					onResultChange(
						"Нужно залить: %.2f л".format(liters)
					)
				}
			},

			enabled =
				tankCapacity.toDoubleOrNull()?.let {
					it > 0
				} == true &&
						fuelPercent.toDoubleOrNull()?.let {
							it in 0.0..100.0
						} == true,

			modifier = Modifier.fillMaxWidth()

		) {
			Text("Рассчитать")
		}

		if (result.isNotEmpty()) {
			Text(result)
		}
	}
}