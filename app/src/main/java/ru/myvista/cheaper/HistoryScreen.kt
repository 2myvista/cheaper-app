package ru.myvista.cheaper

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun HistoryScreen(
	onBackClick: () -> Unit,
	onItemClick: (HistoryItem) -> Unit
) {
	val context = LocalContext.current
	val historyStorage = remember {
		HistoryStorage(context)
	}

	var history by remember {
		mutableStateOf(historyStorage.getAll())
	}

	var showClearDialog by remember {
		mutableStateOf(false)
	}

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(16.dp),
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween
		) {
			TextButton(
				onClick = onBackClick
			) {
				Text("← Назад")
			}

			if (history.isNotEmpty()) {
				TextButton(
					onClick = {
						showClearDialog = true
					}
				) {
					Text("Очистить")
				}
			}
		}

		Text("История")

		if (history.isEmpty()) {
			Text("История пуста")
		} else {
			LazyColumn(
				modifier = Modifier.fillMaxWidth(),
				verticalArrangement = Arrangement.spacedBy(12.dp)
			) {
				val groupedHistory = history.withIndex()
					.groupBy { (_, item) ->
						item.dateTime.substringBefore(' ')
					}

				groupedHistory.forEach { (date, indexedItems) ->

					item {
						Text("======= $date")
					}

					itemsIndexed(indexedItems) { _, indexedItem ->
						val index = indexedItem.index
						val item = indexedItem.value

						Card(
							modifier = Modifier
								.fillMaxWidth()
								.clickable {
									onItemClick(item)
								},
							border = BorderStroke(
								1.dp,
								Color.LightGray
							)
						) {
							Column(
								modifier = Modifier.padding(12.dp),
								verticalArrangement = Arrangement.spacedBy(4.dp)
							) {
								Row(
									modifier = Modifier.fillMaxWidth(),
									horizontalArrangement = Arrangement.SpaceBetween
								) {
									Column(
										modifier = Modifier.weight(1f),
										verticalArrangement = Arrangement.spacedBy(4.dp)
									) {
										if (item.comparisonName.isNotBlank()) {
											Text(item.comparisonName)
										}

										Text(item.dateTime)
										Text("Единица: ${item.unit}")
									}

									IconButton(
										onClick = {
											historyStorage.delete(index)
											history = historyStorage.getAll()
										}
									) {
										Icon(
											imageVector = Icons.Default.Delete,
											contentDescription = "Удалить"
										)
									}
								}

								Text(
									"Товар 1: ${formatNumber(item.price1)} ₽ / " +
											"${formatNumber(item.quantity1)} ${item.unit} = " +
											"%.2f ₽/%s".format(
												item.resultPrice1,
												item.unit
											)
								)

								Text(
									"Товар 2: ${formatNumber(item.price2)} ₽ / " +
											"${formatNumber(item.quantity2)} ${item.unit} = " +
											"%.2f ₽/%s".format(
												item.resultPrice2,
												item.unit
											)
								)
							}
						}
					}
				}
			}
			/*LazyColumn(
				modifier = Modifier.fillMaxWidth(),
				verticalArrangement = Arrangement.spacedBy(12.dp)
			) {
				itemsIndexed(history) { index, item ->
					Card(
						modifier = Modifier
							.fillMaxWidth()
							.clickable {
								onItemClick(item)
							},
						border = BorderStroke(
							1.dp,
							Color.LightGray
						)
					) {
						Column(
							modifier = Modifier.padding(12.dp),
							verticalArrangement = Arrangement.spacedBy(4.dp)
						) {
							Row(
								modifier = Modifier.fillMaxWidth(),
								horizontalArrangement = Arrangement.SpaceBetween
							) {
								Column(
									modifier = Modifier.weight(1f),
									verticalArrangement = Arrangement.spacedBy(4.dp)
								) {
									if (item.comparisonName.isNotBlank()) {
										Text(item.comparisonName)
									}

									Text(item.dateTime)
									Text("Единица: ${item.unit}")
								}

								IconButton(
									onClick = {
										historyStorage.delete(index)
										history = historyStorage.getAll()
									}
								) {
									Icon(
										imageVector = Icons.Default.Delete,
										contentDescription = "Удалить"
									)
								}
							}

							Text(
								"Товар 1: ${formatNumber(item.price1)} ₽ / " +
										"${formatNumber(item.quantity1)} ${item.unit} = " +
										"%.2f ₽/%s".format(
											item.resultPrice1,
											item.unit
										)
							)

							Text(
								"Товар 2: ${formatNumber(item.price2)} ₽ / " +
										"${formatNumber(item.quantity2)} ${item.unit} = " +
										"%.2f ₽/%s".format(
											item.resultPrice2,
											item.unit
										)
							)
						}
					}
				}
			}*/
		}
	}

	if (showClearDialog) {
		AlertDialog(
			onDismissRequest = {
				showClearDialog = false
			},
			title = {
				Text("Очистить историю?")
			},
			text = {
				Text("Все сохранённые сравнения будут удалены.")
			},
			confirmButton = {
				TextButton(
					onClick = {
						historyStorage.clear()
						history = emptyList()
						showClearDialog = false
					}
				) {
					Text("Очистить")
				}
			},
			dismissButton = {
				TextButton(
					onClick = {
						showClearDialog = false
					}
				) {
					Text("Отмена")
				}
			}
		)
	}
}