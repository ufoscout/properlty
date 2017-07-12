package com.properlty;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ProperltyBaseTest {

	@Rule
	public final TestName name = new TestName();

	private Date startTime;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public Logger getLogger() {
		return logger;
	}

	@Before
	public void setUpBeforeTest() {
		startTime = new Date();
		logger.info("===================================================================");
		logger.info("BEGIN TEST " + name.getMethodName());
		logger.info("===================================================================");

	}

	@After
	public void tearDownAfterTest() {
		final String time = new BigDecimal(new Date().getTime() - startTime.getTime()).divide(new BigDecimal(1000)).toString();
		logger.info("===================================================================");
		logger.info("END TEST " + name.getMethodName());
		logger.info("Execution time: " + time + " seconds");
		logger.info("===================================================================");
	}

}
