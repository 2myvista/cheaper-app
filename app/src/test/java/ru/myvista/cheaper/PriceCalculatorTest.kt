package ru.myvista.cheaper

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PriceCalculatorTest {

    @Test
    fun firstProductIsCheaper() {
        val result = PriceCalculator.calculate(100.0, 2.0, 150.0, 2.0)

        assertEquals(50.0, result?.resultPrice1 ?: 0.0, 0.000001)
        assertEquals(75.0, result?.resultPrice2 ?: 0.0, 0.000001)
    }

    @Test
    fun secondProductIsCheaper() {
        val result = PriceCalculator.calculate(100.0, 1.0, 150.0, 2.0)

        assertEquals(100.0, result?.resultPrice1 ?: 0.0, 0.000001)
        assertEquals(75.0, result?.resultPrice2 ?: 0.0, 0.000001)
    }

    @Test
    fun productsHaveEqualUnitPrice() {
        val result = PriceCalculator.calculate(100.0, 1.0, 200.0, 2.0)

        assertEquals(100.0, result?.resultPrice1 ?: 0.0, 0.000001)
        assertEquals(100.0, result?.resultPrice2 ?: 0.0, 0.000001)
    }

    @Test
    fun zeroQuantityReturnsNull() {
        val result = PriceCalculator.calculate(100.0, 0.0, 200.0, 2.0)

        assertNull(result)
    }

    @Test
    fun negativeQuantityReturnsNull() {
        val result = PriceCalculator.calculate(100.0, -1.0, 200.0, 2.0)

        assertNull(result)
    }
}