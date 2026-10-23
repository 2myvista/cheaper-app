
package ru.myvista.cheaper

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun UnitSelectorRow(
	unit: String,
	units: SnapshotStateList<String>,
	unitIsDefault: Boolean,
	onUnitChange: (String) -> Unit,
	onUnitDefaultChange: (Boolean) -> Unit,
	onUnitOrderChange: (List<String>) -> Unit,
	onAddUnit: (String) -> Unit,
) {
	val colors = MaterialTheme.colorScheme

	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(4.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		UnitDropdown(
			unit = unit,
			units = units,
			onUnitChange = onUnitChange,
			onUnitDefaultChange = onUnitDefaultChange,
			onUnitOrderChange = onUnitOrderChange,
			onAddUnit = onAddUnit
		)

		Spacer(modifier = Modifier.width(2.dp))

		Row(
			modifier = Modifier.widthIn(max = 132.dp),
			horizontalArrangement = Arrangement.spacedBy(2.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			Checkbox(
				checked = unitIsDefault,
				onCheckedChange = onUnitDefaultChange,
				modifier = Modifier.widthIn(max = 40.dp)
			)

			Text(
				text = "По умолчанию",
				fontSize = 11.sp,
				color = colors.onSurfaceVariant,
				maxLines = 1,
				softWrap = false,
				overflow = TextOverflow.Clip
			)
		}
	}
}
