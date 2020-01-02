package com.wavefront.labs.convert;

import org.junit.Test;

public class ConvertTest {
	@Test
	public void testDatadog() throws Exception {

		Convert convert = new Convert();
		convert.start(new String[]{"datadog.properties", "test_resources/datadog"}); //, "test_resources/datadog/datadog_timeboard.json"});

	}

}