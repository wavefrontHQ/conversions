package com.wavefront.labs.convert.converter.datadog;

import org.junit.Test;

import java.io.File;

public class DatadogConverterTest {
	@Test
	public void parseString() throws Exception {
	}

	@Test
	public void parseFile() throws Exception {
		DatadogConverter datadogConverter = new DatadogConverter();
		datadogConverter.parseDashboards(new File("test_resources/datadog/datadog_timeboard.json"));


	}

	@Test
	public void convert() throws Exception {
	}

}