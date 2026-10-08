
package ru.myvista.cheaper

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp


import androidx.compose.foundation.background
// остальные import...

fun formatNumber(value: Double): String {
	return if (value % 1.0 == 0.0) {
		value.toLong().toString()
	} else {
		value.toString()
	}
}

fun isValidNumber(value: String): Boolean {
	if (value.isEmpty()) return true

	if (!value.matches(Regex("^\\d+(\\.\\d*)?$"))) {
		return false
	}

	val decimalPart = value.substringAfter('.', "")

	return decimalPart.length <= 5
}
@Composable
fun MainScreen(
	onHistoryClick: () -> Unit,
	historyItem: HistoryItem?,
	onHistoryItemApplied: () -> Unit,

	unit: String,
	onUnitChange: (String) -> Unit,

	comparisonName: String,
	onComparisonNameChange: (String) -> Unit,

	price1: String,
	onPrice1Change: (String) -> Unit,

	quantity1: String,
	onQuantity1Change: (String) -> Unit,

	price2: String,
	onPrice2Change: (String) -> Unit,

	quantity2: String,
	onQuantity2Change: (String) -> Unit,

	resultPrice1: Double?,
	onResultPrice1Change: (Double?) -> Unit,

	resultPrice2: Double?,
	onResultPrice2Change: (Double?) -> Unit
) {

	val context = LocalContext.current

	val historyStorage = remember {
		HistoryStorage(context)
	}

	var expanded by remember {
		mutableStateOf(false)
	}

	val units = listOf("г", "кг", "мл", "л", "шт")

	LaunchedEffect(historyItem) {

		historyItem?.let { item ->

			onComparisonNameChange(item.comparisonName)
			onUnitChange(item.unit)

			onPrice1Change(formatNumber(item.price1))
			onQuantity1Change(formatNumber(item.quantity1))

			onPrice2Change(formatNumber(item.price2))
			onQuantity2Change(formatNumber(item.quantity2))

			onResultPrice1Change(item.resultPrice1)
			onResultPrice2Change(item.resultPrice2)

			onHistoryItemApplied()
		}
	}

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(16.dp),
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {

		Text("Что дешевле")

		OutlinedTextField(
			value = comparisonName,
			onValueChange = onComparisonNameChange,
			label = {
				Text("Название сравнения, необязательно")
			},
			keyboardOptions = KeyboardOptions(
				keyboardType = KeyboardType.Text
			),
			modifier = Modifier.fillMaxWidth()
		)

		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {

			Box {

				OutlinedButton(
					onClick = {
						expanded = true
					}
				) {
					Text("Единица: $unit")
				}

				DropdownMenu(
					expanded = expanded,
					onDismissRequest = {
						expanded = false
					}
				) {

					units.forEach { item ->

						DropdownMenuItem(
							text = {
								Text(item)
							},
							onClick = {
								onUnitChange(item)
								expanded = false
							}
						)
					}
				}
			}

			IconButton(
				onClick = onHistoryClick
			) {
				Icon(
					imageVector = Icons.Default.History,
					contentDescription = "История"
				)
			}
		}

		Column(
			modifier = Modifier
				.fillMaxWidth()
				.background(
					if (
						resultPrice1 != null &&
						resultPrice2 != null &&
						resultPrice1 < resultPrice2
					) {
						Color(0xFFB1E3B3)
					} else {
						Color.Transparent
					}
				)
				.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp)
		) {

			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {

				Text("Товар 1")

				if (resultPrice1 != null) {
					Text(
						"%.2f ₽/%s".format(resultPrice1, unit)
					)
				}
			}

			OutlinedTextField(
				value = price1,
				onValueChange = { value ->
					if (isValidNumber(value)) {
						onPrice1Change(value)
						onResultPrice1Change(null)
						onResultPrice2Change(null)
					}
				},
				label = {
					Text("Цена, ₽")
				},
				modifier = Modifier.fillMaxWidth()
			)

			OutlinedTextField(
				value = quantity1,
				onValueChange = { value ->
					if (isValidNumber(value)) {
						onQuantity1Change(value)
						onResultPrice1Change(null)
						onResultPrice2Change(null)
					}
				},
				label = {
					Text("Количество, $unit")
				},
				modifier = Modifier.fillMaxWidth()
			)
		}

		Column(
			modifier = Modifier
				.fillMaxWidth()
				.background(
					if (
						resultPrice1 != null &&
						resultPrice2 != null &&
						resultPrice2 < resultPrice1
					) {
						Color(0xFFB1E3B3)
					} else {
						Color.Transparent
					}
				)
				.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp)
		) {

			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {

				Text("Товар 2")

				if (resultPrice2 != null) {
					Text(
						"%.2f ₽/%s".format(resultPrice2, unit)
					)
				}
			}

			OutlinedTextField(
				value = price2,
				onValueChange = { value ->
					if (isValidNumber(value)) {
						onPrice2Change(value)
						onResultPrice1Change(null)
						onResultPrice2Change(null)
					}
				},
				label = {
					Text("Цена, ₽")
				},
				modifier = Modifier.fillMaxWidth()
			)

			OutlinedTextField(
				value = quantity2,
				onValueChange = { value ->
					if (isValidNumber(value)) {
						onQuantity2Change(value)
						onResultPrice1Change(null)
						onResultPrice2Change(null)
					}
				},
				label = {
					Text("Количество, $unit")
				},
				modifier = Modifier.fillMaxWidth()
			)
		}

		Button(
			onClick = {

				val price1Value = price1.toDoubleOrNull()
				val quantity1Value = quantity1.toDoubleOrNull()
				val price2Value = price2.toDoubleOrNull()
				val quantity2Value = quantity2.toDoubleOrNull()

				if (
					price1Value != null &&
					quantity1Value != null &&
					price2Value != null &&
					quantity2Value != null &&
					quantity1Value > 0 &&
					quantity2Value > 0
				) {

					val result1 = price1Value / quantity1Value
					val result2 = price2Value / quantity2Value

					onResultPrice1Change(result1)
					onResultPrice2Change(result2)

					historyStorage.save(
						HistoryItem(
							comparisonName = comparisonName,
							dateTime = java.time.LocalDateTime.now()
								.format(
									java.time.format.DateTimeFormatter.ofPattern(
										"dd-MM-yyyy HH:mm:ss"
									)
								),
							unit = unit,
							price1 = price1Value,
							quantity1 = quantity1Value,
							price2 = price2Value,
							quantity2 = quantity2Value,
							resultPrice1 = result1,
							resultPrice2 = result2
						)
					)
				}
			},

			enabled =
				price1.toDoubleOrNull()?.let { it >= 0 } == true &&
						quantity1.toDoubleOrNull()?.let { it > 0 } == true &&
						price2.toDoubleOrNull()?.let { it >= 0 } == true &&
						quantity2.toDoubleOrNull()?.let { it > 0 } == true,

			modifier = Modifier.fillMaxWidth()

		) {
			Text("Сравнить")
		}
	}
}