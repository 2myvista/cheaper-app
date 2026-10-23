
package ru.myvista.cheaper

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlin.math.roundToInt
import androidx.compose.foundation.layout.widthIn

@Composable
fun UnitDropdown(
	unit: String,
	units: SnapshotStateList<String>,
	onUnitChange: (String) -> Unit,
	onUnitDefaultChange: (Boolean) -> Unit,
	onUnitOrderChange: (List<String>) -> Unit,
	onAddUnit: (String) -> Unit
) {
	var expanded by remember { mutableStateOf(false) }
	var showAddDialog by remember { mutableStateOf(false) }
	var newUnitName by remember { mutableStateOf("") }
	var buttonWidth by remember { mutableIntStateOf(0) }
	var draggedItem by remember { mutableStateOf<String?>(null) }
	var dragStartIndex by remember { mutableIntStateOf(-1) }
	var dragOffset by remember { mutableFloatStateOf(0f) }

	val density = LocalDensity.current
	val colors = MaterialTheme.colorScheme
	val rowHeightPx = with(density) { 48.dp.toPx() }

	val normalizedName = newUnitName.trim()
	val duplicateName = normalizedName.isNotEmpty() &&
			units.any { it.equals(normalizedName, ignoreCase = true) }

	Column {
		OutlinedButton(
			onClick = { expanded = true },
			modifier = Modifier
				.widthIn(min = 100.dp)
				.onGloballyPositioned {
					buttonWidth = it.size.width
				}
		) {
			Text(
				text = if (unit.isBlank()) "Единица" else unit,
				maxLines = 1,
				softWrap = false,
				overflow = TextOverflow.Ellipsis
			)
		}

		DropdownMenu(
			expanded = expanded,
			onDismissRequest = {
				expanded = false
				draggedItem = null
				dragStartIndex = -1
				dragOffset = 0f
			},
			modifier = Modifier
				.widthIn(min = 180.dp)
				.heightIn(max = 320.dp)
				.clip(RoundedCornerShape(12.dp))
				.background(colors.surface)
				.border(
					1.dp,
					colors.outlineVariant,
					RoundedCornerShape(12.dp)
				)
		) {
			Column(
				modifier = Modifier
					.heightIn(max = 260.dp)
					.verticalScroll(rememberScrollState())
			) {
				units.forEachIndexed { index, item ->
					key(item) {
						val selected = item == unit
						val isDragging = item == draggedItem
						val currentIndex = units.indexOf(item)

						val visualOffset = if (
							isDragging && dragStartIndex >= 0
						) {
							dragOffset -
									(currentIndex - dragStartIndex) * rowHeightPx
						} else {
							0f
						}

						Column {
							Row(
								modifier = Modifier
									.fillMaxWidth()
									.height(48.dp)
									.zIndex(if (isDragging) 1f else 0f)
									.graphicsLayer {
										translationY = visualOffset
										shadowElevation =
											if (isDragging) 8.dp.toPx() else 0f
									}
									.background(
										when {
											isDragging -> colors.secondaryContainer
											selected -> colors.primary.copy(alpha = 0.12f)
											else -> Color.Transparent
										}
									),
								verticalAlignment = Alignment.CenterVertically
							) {
								Box(modifier = Modifier.weight(1f)) {
									DropdownMenuItem(
										text = {
											Text(
												text = item,
												color = if (selected) {
													colors.primary
												} else {
													colors.onSurface
												},
												maxLines = 1,
												softWrap = false,
												overflow = TextOverflow.Ellipsis
											)
										},
										leadingIcon = {
											Box(
												modifier = Modifier.width(20.dp),
												contentAlignment = Alignment.Center
											) {
												if (selected) {
													Icon(
														imageVector = Icons.Default.Check,
														contentDescription = "Выбрано",
														tint = colors.primary
													)
												}
											}
										},
										onClick = {
											onUnitChange(item)
											onUnitDefaultChange(false)
											expanded = false
										},
										modifier = Modifier.fillMaxWidth(),
										contentPadding = PaddingValues(
											horizontal = 12.dp,
											vertical = 0.dp
										)
									)
								}

								Icon(
									imageVector = Icons.Default.Menu,
									contentDescription = "Перетащить единицу",
									tint = colors.onSurfaceVariant,
									modifier = Modifier
										.padding(end = 12.dp)
										.pointerInput(item, units.size) {
											detectDragGestures(
												onDragStart = {
													draggedItem = item
													dragStartIndex = units.indexOf(item)
													dragOffset = 0f
												},
												onDrag = { change, dragAmount ->
													change.consume()

													if (draggedItem != item) {
														return@detectDragGestures
													}

													dragOffset += dragAmount.y
													val startIndex = dragStartIndex

													if (startIndex < 0) {
														return@detectDragGestures
													}

													val targetIndex = (
															startIndex +
																	(dragOffset / rowHeightPx).roundToInt()
															).coerceIn(0, units.lastIndex)

													val current = units.indexOf(item)

													if (current >= 0 && targetIndex != current) {
														val moving = units.removeAt(current)
														units.add(targetIndex, moving)
													}
												},
												onDragEnd = {
													onUnitOrderChange(units.toList())
													draggedItem = null
													dragStartIndex = -1
													dragOffset = 0f
												},
												onDragCancel = {
													onUnitOrderChange(units.toList())
													draggedItem = null
													dragStartIndex = -1
													dragOffset = 0f
												}
											)
										}
								)
							}

							if (index < units.lastIndex) {
								HorizontalDivider(
									color = colors.outlineVariant.copy(alpha = 0.5f),
									thickness = 0.5.dp
								)
							}
						}
					}
				}
			}

			HorizontalDivider(
				color = colors.outlineVariant,
				thickness = 1.dp
			)

			DropdownMenuItem(
				text = {
					Text(
						text = "+ Добавить",
						color = colors.primary
					)
				},
				onClick = {
					expanded = false
					newUnitName = ""
					showAddDialog = true
				}
			)
		}
	}

	if (showAddDialog) {
		AlertDialog(
			onDismissRequest = {
				showAddDialog = false
				newUnitName = ""
			},
			title = { Text("Добавить единицу") },
			text = {
				Column {
					OutlinedTextField(
						value = newUnitName,
						onValueChange = { newUnitName = it },
						label = { Text("Название единицы") },
						placeholder = { Text("Например, упак.") },
						singleLine = true,
						isError = duplicateName,
						supportingText = {
							if (duplicateName) {
								Text("Такая единица уже существует")
							}
						}
					)
				}
			},
			confirmButton = {
				TextButton(
					enabled = normalizedName.isNotEmpty() && !duplicateName,
					onClick = {
						onAddUnit(normalizedName)
						newUnitName = ""
						showAddDialog = false
					}
				) {
					Text("Добавить")
				}
			},
			dismissButton = {
				TextButton(
					onClick = {
						showAddDialog = false
						newUnitName = ""
					}
				) {
					Text("Отмена")
				}
			}
		)
	}
}
