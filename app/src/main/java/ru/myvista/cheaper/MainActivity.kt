package ru.myvista.cheaper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import ru.myvista.cheaper.ui.theme.ЧтоДешевлеTheme
import android.util.Log
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.myvista.cheaper.data.DatabaseProvider
import ru.myvista.cheaper.data.DatabaseInitializer
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.CoroutineScope

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
            ЧтоДешевлеTheme {
                AppScreen()
            }
        }
    }
}

@Composable
fun AppScreen() {
    val context = LocalContext.current

    val database = remember(context) {
        DatabaseProvider.getDatabase(context)
    }

    var showHistory by remember {
        mutableStateOf(false)
    }

    var selectedHistoryItem by remember {
        mutableStateOf<HistoryItem?>(null)
    }

    // Состояние "Что дешевле"
    var unit by remember {
        mutableStateOf<String?>(null)
    }
    var unitIsDefault by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(database) {
        val settings = database.settingsDao()
            .getSettings()
            .filterNotNull()
            .first()

        val units = database.unitDao()
            .getAll()
            .first()

        val defaultUnit = units.firstOrNull {
            it.id == settings.defaultUnitId
        }

        unit = defaultUnit?.name ?: "кг"
        unitIsDefault = defaultUnit != null
    }

    var comparisonName by remember {
        mutableStateOf("trtrtrt")
    }

    var price1 by remember {
        mutableStateOf("300.2")
    }

    var quantity1 by remember {
        mutableStateOf("4")
    }

    var price2 by remember {
        mutableStateOf("50.02")
    }

    var quantity2 by remember {
        mutableStateOf("8")
    }

    var resultPrice1 by remember {
        mutableStateOf<Double?>(null)
    }

    var resultPrice2 by remember {
        mutableStateOf<Double?>(null)
    }

    // Состояние "Бензин"
    var tankCapacity by remember {
        mutableStateOf("55")
    }

    var tankCapacityIsDefault by remember {
        mutableStateOf(false)
    }

    var fuelPercent by remember {
        mutableStateOf("55")
    }

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

    var fuelResult by remember {
        mutableStateOf("")
    }

    if (unit == null && !showHistory) {
        return
    }

    if (showHistory) {
        HistoryScreen(
            onBackClick = { showHistory = false },
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
                    }
                )

                1 -> FuelScreen(
                    tankCapacity = tankCapacity,
                    onTankCapacityChange = { tankCapacity = it },

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
                    onFuelPercentChange = { fuelPercent = it },

                    result = fuelResult,
                    onResultChange = { fuelResult = it }
                )
            }
        }
    }
}

