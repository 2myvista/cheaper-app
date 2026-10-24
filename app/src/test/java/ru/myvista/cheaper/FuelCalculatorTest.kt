package ru.myvista.cheaper

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FuelCalculatorTest {

	@Test
	fun partiallyFilledTank() {
		val result = FuelCalculator.calculateLiters(55.0, 55.0)

		assertEquals(24.75, result!!, 0.000001)
	}

	@Test
	fun fullTank() {
		val result = FuelCalculator.calculateLiters(55.0, 100.0)

		assertEquals(0.0, result!!, 0.000001)
	}

	@Test
	fun emptyTank() {
		val result = FuelCalculator.calculateLiters(55.0, 0.0)

		assertEquals(55.0, result!!, 0.000001)
	}

	@Test
	fun zeroCapacityReturnsNull() {
		assertNull(FuelCalculator.calculateLiters(0.0, 50.0))
	}

	@Test
	fun negativeCapacityReturnsNull() {
		assertNull(FuelCalculator.calculateLiters(-10.0, 50.0))
	}

	@Test
	fun percentBelowZeroReturnsNull() {
		assertNull(FuelCalculator.calculateLiters(55.0, -1.0))
	}

	@Test
	fun percentAboveHundredReturnsNull() {
		assertNull(FuelCalculator.calculateLiters(55.0, 101.0))
	}

	@Test
	fun infiniteCapacityReturnsNull() {
		assertNull(FuelCalculator.calculateLiters(Double.POSITIVE_INFINITY, 50.0))
	}

	@Test
	fun nanPercentReturnsNull() {
		assertNull(FuelCalculator.calculateLiters(55.0, Double.NaN))
	}
}