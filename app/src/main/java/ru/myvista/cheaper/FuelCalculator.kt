package ru.myvista.cheaper

object FuelCalculator {

	fun calculateLiters(
		capacity: Double,
		percent: Double
	): Double? {
		if (
			!capacity.isFinite() ||
			!percent.isFinite() ||
			capacity <= 0 ||
			percent !in 0.0..100.0
		) {
			return null
		}

		return capacity * (100 - percent) / 100
	}
}