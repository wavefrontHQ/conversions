package com.wavefront.labs.convert.converter.grafana;

import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Properties;

public class GrafanaConverterTest {
	@Test
	public void parse() throws Exception {
		Properties properties = new Properties();
		properties.load(new FileReader(new File("grafana.properties")));

		GrafanaConverter grafanaConverter = new GrafanaConverter();
		grafanaConverter.init(properties);
		grafanaConverter.parse(new File("test_resources/grafana/grafana_dashboard.json"));
	}

	@Test
	public void convert() throws Exception {

		Properties properties = new Properties();
		properties.load(new FileReader(new File("grafana.properties")));

		GrafanaConverter grafanaConverter = new GrafanaConverter();
		grafanaConverter.init(properties);
		grafanaConverter.parse(new File("test_resources/grafana/grafana_dashboard.json"));


		List models = grafanaConverter.convert();

	}

}