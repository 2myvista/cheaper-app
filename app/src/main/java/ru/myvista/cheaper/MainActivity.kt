package ru.myvista.cheaper

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ru.myvista.cheaper.data.DatabaseInitializer
import ru.myvista.cheaper.data.DatabaseProvider
import ru.myvista.cheaper.ui.theme.ЧтоДешевлеTheme
import androidx.compose.runtime.mutableStateListOf
import ru.myvista.cheaper.data.UnitEntity
import kotlinx.coroutines.withContext

// TODO: При первом запуске приложения открыть экран «Настройки».
// Пользователь должен сначала настроить единицы измерения и основные параметры,
// после чего перейти к основным экранам приложения.

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val database = DatabaseProvider.getDatabase(applicationContext)
                DatabaseInitializer(database).initialize()

                database.openHelper.writableDatabase

                Log.d("CheaperDB", "База данных Room успешно открыта")
            } catch (e: Exception) {
                Log.e("CheaperDB", "Ошибка открытия базы данных Room", e)
            }
        }

        setContent {
            AppRoot()
        }
    }
}

@Composable
fun AppRoot() {
    val context = LocalContext.current
    val database = remember(context) {
        DatabaseProvider.getDatabase(context)
    }

    val systemDarkTheme = isSystemInDarkTheme()

    var darkTheme by remember {
        mutableStateOf(systemDarkTheme)
    }

    var themeLoaded by remember {
        mutableStateOf(false)
    }

    val onThemeChange: (Boolean) -> Unit = { isDark ->
        darkTheme = isDark

        CoroutineScope(Dispatchers.IO).launch {
            database.settingsDao().updateThemeMode(
                if (isDark) "dark" else "light"
            )
        }
    }


    ЧтоДешевлеTheme(darkTheme = darkTheme) {
        AppScreen(
            darkTheme = darkTheme,
            onThemeChange = onThemeChange
        )
    }
    /*if (themeLoaded) {
        ЧтоДешевлеTheme(darkTheme = darkTheme) {
            AppScreen(
                darkTheme = darkTheme,
                onThemeChange = onThemeChange
            )
        }
    }*/
}

@Composable
fun AppScreen(
    darkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val database = remember(context) {
        DatabaseProvider.getDatabase(context)
    }

    var showHistory by remember { mutableStateOf(false) }

    var selectedHistoryItem by remember {
        mutableStateOf<HistoryItem?>(null)
    }

    var unit by remember { mutableStateOf<String?>(null) }
    var unitIsDefault by remember { mutableStateOf(true) }

    val defaultUnitNames = listOf("г", "кг", "мл", "л", "шт")

    val unitOrder = remember {
        mutableStateListOf("г", "кг", "мл", "л", "шт")
    }

    LaunchedEffect(database) {
        val settings = database.settingsDao()
            .getSettings()
            .filterNotNull()
            .first()

        val dbUnits = database.unitDao().getAll().first()
        val availableNames = dbUnits.map { it.name }

        Log.d("CheaperUnits", "Единицы из базы: ${dbUnits.map { "[${it.name}]" }}")

        val savedOrder = runCatching {
            val json = org.json.JSONArray(settings.unitOrder)

            (0 until json.length()).mapNotNull { index ->
                json.optString(index).takeIf { it in availableNames }
            }
        }.getOrDefault(emptyList())

        val validOrder = (
                savedOrder + availableNames.filterNot { it in savedOrder }
                ).distinct()

        unitOrder.clear()
        unitOrder.addAll(validOrder)

        val defaultUnit = dbUnits.firstOrNull {
            it.id == settings.defaultUnitId
        }

        unit = defaultUnit?.name ?: "кг"
        unitIsDefault = defaultUnit != null
    }

    var comparisonName by remember { mutableStateOf("trtrtrt") }
    var price1 by remember { mutableStateOf("300.2") }
    var quantity1 by remember { mutableStateOf("4") }
    var price2 by remember { mutableStateOf("50.02") }
    var quantity2 by remember { mutableStateOf("8") }

    var resultPrice1 by remember { mutableStateOf<Double?>(null) }
    var resultPrice2 by remember { mutableStateOf<Double?>(null) }

    var tankCapacity by remember { mutableStateOf("55") }
    var tankCapacityIsDefault by remember { mutableStateOf(false) }
    var fuelPercent by remember { mutableStateOf("55") }

    LaunchedEffect(database) {
        val settings = database.settingsDao()
            .getSettings()
            .filterNotNull()
            .first()

        tankCapacity = if (settings.tankCapacity > 0) {
            settings.tankCapacity.toString()
        } else {
            ""
        }

        tankCapacityIsDefault = settings.tankCapacity > 0
        fuelPercent = ""
    }

    var fuelResult by remember { mutableStateOf("") }

    if (unit == null && !showHistory) {
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
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
                            },
                            unit = unit ?: "кг",
                            onUnitChange = {
                                unit = it
                            },
                            unitIsDefault = unitIsDefault,
                            onUnitDefaultChange = { checked ->
                                unitIsDefault = checked

                                if (checked) {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        val units = database.unitDao()
                                            .getAll()
                                            .first()

                                        val selectedUnit = units.firstOrNull {
                                            it.name == unit
                                        }

                                        if (selectedUnit != null) {
                                            database.settingsDao()
                                                .updateDefaultUnit(selectedUnit.id)
                                        }
                                    }
                                }
                            },
                            comparisonName = comparisonName,
                            onComparisonNameChange = {
                                comparisonName = it
                            },
                            price1 = price1,
                            onPrice1Change = {
                                price1 = it
                            },
                            quantity1 = quantity1,
                            onQuantity1Change = {
                                quantity1 = it
                            },
                            price2 = price2,
                            onPrice2Change = {
                                price2 = it
                            },
                            quantity2 = quantity2,
                            onQuantity2Change = {
                                quantity2 = it
                            },
                            resultPrice1 = resultPrice1,
                            onResultPrice1Change = {
                                resultPrice1 = it
                            },
                            resultPrice2 = resultPrice2,
                            onResultPrice2Change = {
                                resultPrice2 = it
                            },
                            units = unitOrder,
                            onUnitOrderChange = { newOrder ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    database.settingsDao().updateUnitOrder(
                                        org.json.JSONArray(newOrder).toString()
                                    )
                                }
                            },
                            onAddUnit = { newName ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    val existingUnits = database.unitDao().getAll().first()

                                    val alreadyExists = existingUnits.any {
                                        it.name.equals(newName, ignoreCase = true)
                                    }

                                    if (!alreadyExists) {
                                        database.unitDao().insert(
                                            UnitEntity(
                                                name = newName,
                                                sortOrder = existingUnits.size
                                            )
                                        )

                                        val updatedOrder = withContext(Dispatchers.Main) {
                                            unitOrder.add(newName)
                                            unit = newName
                                            unitIsDefault = false
                                            unitOrder.toList()
                                        }

                                        database.settingsDao().updateUnitOrder(
                                            org.json.JSONArray(updatedOrder).toString()
                                        )
                                    }
                                }
                            },
                        )

                        1 -> FuelScreen(
                            tankCapacity = tankCapacity,
                            onTankCapacityChange = {
                                tankCapacity = it
                            },
                            tankCapacityIsDefault = tankCapacityIsDefault,
                            onTankCapacityDefaultChange = { checked ->
                                tankCapacityIsDefault = checked

                                if (checked) {
                                    val capacity = tankCapacity.toDoubleOrNull()

                                    if (capacity != null && capacity > 0) {
                                        CoroutineScope(Dispatchers.IO).launch {
                                            database.settingsDao()
                                                .updateTankCapacity(capacity)
                                        }
                                    }
                                }
                            },
                            fuelPercent = fuelPercent,
                            onFuelPercentChange = {
                                fuelPercent = it
                            },
                            result = fuelResult,
                            onResultChange = {
                                fuelResult = it
                            }
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(end = 8.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(
                onClick = {
                    onThemeChange(!darkTheme)
                }
            ) {
                Icon(
                    imageVector = if (darkTheme) {
                        Icons.Default.LightMode
                    } else {
                        Icons.Default.DarkMode
                    },
                    contentDescription = if (darkTheme) {
                        "Включить светлую тему"
                    } else {
                        "Включить тёмную тему"
                    },
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}