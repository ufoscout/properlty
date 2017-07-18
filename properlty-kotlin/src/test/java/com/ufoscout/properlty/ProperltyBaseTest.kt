package com.ufoscout.properlty

import java.math.BigDecimal
import java.util.Date

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestName
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class ProperltyBaseTest {

    @Rule @JvmField
    val name = TestName()

    private var startTime: Date? = null
    val logger = LoggerFactory.getLogger(this.javaClass)

    @Before
    fun setUpBeforeTest() {
        startTime = Date()
        logger.info("===================================================================")
        logger.info("BEGIN TEST " + name.methodName)
        logger.info("===================================================================")

    }

    @After
    fun tearDownAfterTest() {
        val time = BigDecimal(Date().time - startTime!!.time).divide(BigDecimal(1000)).toString()
        logger.info("===================================================================")
        logger.info("END TEST " + name.methodName)
        logger.info("Execution time: $time seconds")
        logger.info("===================================================================")
    }

}
