
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.myvista.cheaper.ui.theme.AppOutlinedTextField
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.foundation.layout.Box

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
	unitIsDefault: Boolean,
	onUnitDefaultChange: (Boolean) -> Unit,
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
	onResultPrice2Change: (Double?) -> Unit,
	units: SnapshotStateList<String>,
	onUnitOrderChange: (List<String>) -> Unit,
	onAddUnit: (String) -> Unit,
) {
	val context = LocalContext.current
	val historyStorage = remember { HistoryStorage(context) }

	val colors = MaterialTheme.colorScheme
	val focusManager = LocalFocusManager.current

	val cheaperBackground =
		if (MaterialTheme.colorScheme.background == Color(0xFF171D1A)) {
			Color(0xFF294536)
		} else {
			Color(0xFFB1E3B3)
		}

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

	val scrollState = rememberScrollState()

	Column(
		modifier = Modifier
			.fillMaxSize()
			.background(colors.background)
			.clickable(
				interactionSource = remember {
					MutableInteractionSource()
				},
				indication = null
			) {
				focusManager.clearFocus()
			}
			.verticalScroll(scrollState)
			.padding(16.dp),
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		Text(
			text = "Что дешевле?",
			color = colors.onBackground
		)

		AppOutlinedTextField(
			value = comparisonName,
			onValueChange = onComparisonNameChange,
			label = "Название сравнения, необязательно",
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
			Box(
				modifier = Modifier.weight(1f)
			) {
				UnitSelectorRow(
					unit = unit,
					units = units,
					unitIsDefault = unitIsDefault,
					onUnitChange = onUnitChange,
					onUnitDefaultChange = { checked ->
						onUnitDefaultChange(checked)
						focusManager.clearFocus()
					},
					onUnitOrderChange = onUnitOrderChange,
					onAddUnit = onAddUnit
				)
			}
			IconButton(
				onClick = {
					focusManager.clearFocus()
					onHistoryClick()
				}
			) {
				Icon(
					imageVector = Icons.Default.History,
					contentDescription = "История",
					tint = colors.onSurface
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
						cheaperBackground
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
				Text(
					text = "Товар 1",
					color = colors.onBackground
				)

				if (resultPrice1 != null) {
					Text(
						text = "%.2f ₽/%s".format(resultPrice1, unit),
						color = colors.onBackground
					)
				}
			}

			AppOutlinedTextField(
				value = price1,
				onValueChange = { value ->
					if (isValidNumber(value)) {
						onPrice1Change(value)
						onResultPrice1Change(null)
						onResultPrice2Change(null)
					}
				},
				label = "Цена, ₽",
				modifier = Modifier.fillMaxWidth(),
				keyboardOptions = KeyboardOptions(
					keyboardType = KeyboardType.Decimal
				)
			)

			AppOutlinedTextField(
				value = quantity1,
				onValueChange = { value ->
					if (isValidNumber(value)) {
						onQuantity1Change(value)
						onResultPrice1Change(null)
						onResultPrice2Change(null)
					}
				},
				label = "Количество",
				modifier = Modifier.fillMaxWidth(),
				keyboardOptions = KeyboardOptions(
					keyboardType = KeyboardType.Decimal
				)
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
						cheaperBackground
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
				Text(
					text = "Товар 2",
					color = colors.onBackground
				)

				if (resultPrice2 != null) {
					Text(
						text = "%.2f ₽/%s".format(resultPrice2, unit),
						color = colors.onBackground
					)
				}
			}

			AppOutlinedTextField(
				value = price2,
				onValueChange = { value ->
					if (isValidNumber(value)) {
						onPrice2Change(value)
						onResultPrice1Change(null)
						onResultPrice2Change(null)
					}
				},
				label = "Цена, ₽",
				modifier = Modifier.fillMaxWidth(),
				keyboardOptions = KeyboardOptions(
					keyboardType = KeyboardType.Decimal
				)
			)

			AppOutlinedTextField(
				value = quantity2,
				onValueChange = { value ->
					if (isValidNumber(value)) {
						onQuantity2Change(value)
						onResultPrice1Change(null)
						onResultPrice2Change(null)
					}
				},
				label = "Количество",
				modifier = Modifier.fillMaxWidth(),
				keyboardOptions = KeyboardOptions(
					keyboardType = KeyboardType.Decimal
				)
			)
		}

		if (
			resultPrice1 != null &&
			resultPrice2 != null &&
			kotlin.math.abs(resultPrice1 - resultPrice2) < 0.000001
		) {
			Text(
				text = "Цена одинаковая",
				color = colors.onBackground,
				modifier = Modifier.fillMaxWidth(),
				textAlign = TextAlign.Center
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
					val result = PriceCalculator.calculate(
						price1 = price1Value,
						quantity1 = quantity1Value,
						price2 = price2Value,
						quantity2 = quantity2Value
					)

					if (result != null) {
						val result1 = result.resultPrice1
						val result2 = result.resultPrice2

						onResultPrice1Change(result1)
						onResultPrice2Change(result2)

						historyStorage.save(
							HistoryItem(
								comparisonName = comparisonName,
								dateTime = java.text.SimpleDateFormat(
									"dd-MM-yyyy HH:mm:ss",
									java.util.Locale.getDefault()
								).format(java.util.Date()),
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
