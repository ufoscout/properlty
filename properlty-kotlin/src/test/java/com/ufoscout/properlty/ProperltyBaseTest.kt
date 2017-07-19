package com.ufoscout.properlty

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestName
import java.math.BigDecimal
import java.util.*

abstract class ProperltyBaseTest {

    @Rule @JvmField
    val name = TestName()

    private var startTime: Date? = null

    @Before
    fun setUpBeforeTest() {
        startTime = Date()
        println("===================================================================")
        println("BEGIN TEST " + name.methodName)
        println("===================================================================")

    }

    @After
    fun tearDownAfterTest() {
        val time = BigDecimal(Date().time - startTime!!.time).divide(BigDecimal(1000)).toString()
        println("===================================================================")
        println("END TEST " + name.methodName)
        println("Execution time: $time seconds")
        println("===================================================================")
    }

}
