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
	onHistoryItemApplied: () -> Unit
) {
	val context = LocalContext.current

	val historyStorage = remember {
		HistoryStorage(context)
	}

	var unit by remember { mutableStateOf("кг") }
	var expanded by remember { mutableStateOf(false) }

	var comparisonName by remember { mutableStateOf("trtrtrt") }

	var price1 by remember { mutableStateOf("300.2") }
	var quantity1 by remember { mutableStateOf("4") }

	var price2 by remember { mutableStateOf("50.02") }
	var quantity2 by remember { mutableStateOf("8") }

	var resultPrice1 by remember { mutableStateOf<Double?>(null) }
	var resultPrice2 by remember { mutableStateOf<Double?>(null) }

	val units = listOf("г", "кг", "мл", "л", "шт")

	LaunchedEffect(historyItem) {
		historyItem?.let { item ->
			comparisonName = item.comparisonName
			unit = item.unit
			price1 = formatNumber(historyItem.price1)
			quantity1 = formatNumber(historyItem.quantity1)
			price2 = formatNumber(historyItem.price2)
			quantity2 = formatNumber(historyItem.quantity2)
			resultPrice1 = item.resultPrice1
			resultPrice2 = item.resultPrice2

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
			onValueChange = { comparisonName = it },
			label = { Text("Название сравнения, необязательно") },
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
					onClick = { expanded = true }
				) {
					Text("Единица: $unit")
				}

				DropdownMenu(
					expanded = expanded,
					onDismissRequest = { expanded = false }
				) {
					units.forEach { item ->
						DropdownMenuItem(
							text = { Text(item) },
							onClick = {
								unit = item
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
						resultPrice1!! < resultPrice2!!
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
						"%.2f ₽/%s".format(resultPrice1!!, unit)
					)
				}
			}

			OutlinedTextField(
				value = price1,
				onValueChange = { value ->
					if (isValidNumber(value)) {
						price1 = value
						resultPrice1 = null
						resultPrice2 = null
					}
				},
				label = { Text("Цена, ₽") },
				modifier = Modifier.fillMaxWidth()
			)

			OutlinedTextField(
				value = quantity1,
				onValueChange = { value ->
					if (isValidNumber(value)) {
						quantity1 = value
						resultPrice1 = null
						resultPrice2 = null
					}
				},
				label = { Text("Количество, $unit") },
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
						resultPrice2!! < resultPrice1!!
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
						"%.2f ₽/%s".format(resultPrice2!!, unit)
					)
				}
			}

			OutlinedTextField(
				value = price2,
				onValueChange = { value ->
					if (isValidNumber(value)) {
						price2 = value
						resultPrice1 = null
						resultPrice2 = null
					}
				},
				label = { Text("Цена, ₽") },
				modifier = Modifier.fillMaxWidth()
			)

			OutlinedTextField(
				value = quantity2,
				onValueChange = { value ->
					if (isValidNumber(value)) {
						quantity2 = value
						resultPrice1 = null
						resultPrice2 = null
					}
				},
				label = { Text("Количество, $unit") },
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
					resultPrice1 = price1Value / quantity1Value
					resultPrice2 = price2Value / quantity2Value

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
							resultPrice1 = resultPrice1!!,
							resultPrice2 = resultPrice2!!
						)
					)
				}
			},
			enabled = price1.toDoubleOrNull()?.let { it >= 0 } == true &&
					quantity1.toDoubleOrNull()?.let { it > 0 } == true &&
					price2.toDoubleOrNull()?.let { it >= 0 } == true &&
					quantity2.toDoubleOrNull()?.let { it > 0 } == true,
			modifier = Modifier.fillMaxWidth()
		) {
			Text("Сравнить")
		}
	}
}