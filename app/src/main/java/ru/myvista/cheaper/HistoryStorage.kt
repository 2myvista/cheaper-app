package ru.myvista.cheaper

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class HistoryItem(
	val comparisonName: String,
	val dateTime: String,
	val unit: String,
	val price1: Double,
	val quantity1: Double,
	val price2: Double,
	val quantity2: Double,
	val resultPrice1: Double,
	val resultPrice2: Double
)

class HistoryStorage(private val context: Context) {

	private val preferences =
		context.getSharedPreferences("history", Context.MODE_PRIVATE)

	fun clear() {
		preferences.edit()
			.remove("items")
			.apply()
	}

	fun delete(index: Int) {
		val history = getAll().toMutableList()

		if (index !in history.indices) {
			return
		}

		history.removeAt(index)

		val jsonArray = JSONArray()

		history.forEach { historyItem ->
			val json = JSONObject().apply {
				put("comparisonName", historyItem.comparisonName)
				put("dateTime", historyItem.dateTime)
				put("unit", historyItem.unit)
				put("price1", historyItem.price1)
				put("quantity1", historyItem.quantity1)
				put("price2", historyItem.price2)
				put("quantity2", historyItem.quantity2)
				put("resultPrice1", historyItem.resultPrice1)
				put("resultPrice2", historyItem.resultPrice2)
			}

			jsonArray.put(json)
		}

		preferences.edit()
			.putString("items", jsonArray.toString())
			.apply()
	}

	fun save(item: HistoryItem) {
		val history = getAll().toMutableList()

		history.add(0, item)

		if (history.size > 50) {
			history.removeAt(history.lastIndex)
		}

		val jsonArray = JSONArray()

		history.forEach { historyItem ->
			val json = JSONObject().apply {
				put("comparisonName", historyItem.comparisonName)
				put("dateTime", historyItem.dateTime)
				put("unit", historyItem.unit)
				put("price1", historyItem.price1)
				put("quantity1", historyItem.quantity1)
				put("price2", historyItem.price2)
				put("quantity2", historyItem.quantity2)
				put("resultPrice1", historyItem.resultPrice1)
				put("resultPrice2", historyItem.resultPrice2)
			}

			jsonArray.put(json)
		}

		preferences.edit()
			.putString("items", jsonArray.toString())
			.apply()
	}

	fun getAll(): List<HistoryItem> {
		val jsonString = preferences.getString("items", null)
			?: return emptyList()

		val jsonArray = JSONArray(jsonString)
		val result = mutableListOf<HistoryItem>()

		for (i in 0 until jsonArray.length()) {
			val json = jsonArray.getJSONObject(i)

			result.add(
				HistoryItem(
					comparisonName = json.getString("comparisonName"),
					dateTime = json.getString("dateTime"),
					unit = json.getString("unit"),
					price1 = json.getDouble("price1"),
					quantity1 = json.getDouble("quantity1"),
					price2 = json.getDouble("price2"),
					quantity2 = json.getDouble("quantity2"),
					resultPrice1 = json.getDouble("resultPrice1"),
					resultPrice2 = json.getDouble("resultPrice2")
				)
			)
		}

		return result
	}
}