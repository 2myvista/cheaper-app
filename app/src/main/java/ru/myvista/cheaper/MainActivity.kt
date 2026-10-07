package ru.myvista.cheaper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.myvista.cheaper.ui.theme.ЧтоДешевлеTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ЧтоДешевлеTheme {
                AppScreen()
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppScreen() {
    var showHistory by remember { mutableStateOf(false) }
    var selectedHistoryItem by remember { mutableStateOf<HistoryItem?>(null) }

    if (showHistory) {
        HistoryScreen(
            onBackClick = {
                showHistory = false
            },
            onItemClick = { item ->
                selectedHistoryItem = item
                showHistory = false
            }
        )
    } else {
        val pagerState = rememberPagerState(
            initialPage = 0,
            pageCount = { 2 }
        )

        HorizontalPager(
            state = pagerState
        ) { page ->
            when (page) {
                0 -> MainScreen(
                    onHistoryClick = {
                        showHistory = true
                    },
                    historyItem = selectedHistoryItem,
                    onHistoryItemApplied = {
                        selectedHistoryItem = null
                    }
                )

                1 -> FuelScreen()
            }
        }
    }
}

@Composable
fun FuelScreen() {
    var tankCapacity by remember { mutableStateOf("55") }
    var fuelPercent by remember { mutableStateOf("55") }
    var result by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Бензин")

        OutlinedTextField(
            value = tankCapacity,
            onValueChange = { tankCapacity = it },
            label = { Text("Объём бака, л") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = fuelPercent,
            onValueChange = { fuelPercent = it },
            label = { Text("Заполнено, %") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val capacity = tankCapacity.toDoubleOrNull()
                val percent = fuelPercent.toDoubleOrNull()

                if (capacity != null && percent != null) {
                    val liters = capacity * (100 - percent) / 100
                    result = "Нужно залить: %.2f л".format(liters)
                }
            },
            enabled = tankCapacity.toDoubleOrNull()?.let { it > 0 } == true &&
                    fuelPercent.toDoubleOrNull()?.let { it in 0.0..100.0 } == true,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Рассчитать")
        }

        if (result.isNotEmpty()) {
            Text(result)
        }
    }
}