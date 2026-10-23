package ru.myvista.cheaper

data class PriceComparisonResult(
	val resultPrice1: Double,
	val resultPrice2: Double
)

object PriceCalculator {

	fun calculate(
		price1: Double,
		quantity1: Double,
		price2: Double,
		quantity2: Double
	): PriceComparisonResult? {
		if (
			price1 < 0 ||
			price2 < 0 ||
			quantity1 <= 0 ||
			quantity2 <= 0
		) {
			return null
		}

		return PriceComparisonResult(
			resultPrice1 = price1 / quantity1,
			resultPrice2 = price2 / quantity2
		)
	}
}