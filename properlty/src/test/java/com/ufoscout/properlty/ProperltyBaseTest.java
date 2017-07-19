package com.ufoscout.properlty;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;

import java.math.BigDecimal;
import java.util.Date;

public abstract class ProperltyBaseTest {

	@Rule
	public final TestName name = new TestName();

	private Date startTime;

	@Before
	public void setUpBeforeTest() {
		startTime = new Date();
		System.out.println("===================================================================");
		System.out.println("BEGIN TEST " + name.getMethodName());
		System.out.println("===================================================================");

	}

	@After
	public void tearDownAfterTest() {
		final String time = new BigDecimal(new Date().getTime() - startTime.getTime()).divide(new BigDecimal(1000)).toString();
		System.out.println("===================================================================");
		System.out.println("END TEST " + name.getMethodName());
		System.out.println("Execution time: " + time + " seconds");
		System.out.println("===================================================================");
	}

}
