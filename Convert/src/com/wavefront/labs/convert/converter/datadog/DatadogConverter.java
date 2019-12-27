package com.wavefront.labs.convert.converter.datadog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DatadogConverter extends AbstractDatadogConverter {

	private List<AbstractDatadogConverter> converters;

	@Override
	public void init(Properties properties) {
		super.init(properties);
		converters = new ArrayList();
	}

	@Override
	public void parseDashboards(Object data) throws IOException {

		AbstractDatadogConverter converter;

		if (data == null) {
			converter = new DatadogApiConverter();
		} else {
			converter = new DatadogTimeboardConverter();
		}

		converter.init(properties);
		converter.parseDashboards(data);

		converters.add(converter);
	}

	@Override
	public List convertDashboards() {

		List models = new ArrayList();

		for (AbstractDatadogConverter converter : converters) {
			models.addAll(converter.convertDashboards());
		}

		return models;

	}

	@Override
	public void parseAlerts(Object data) throws IOException {
		AbstractDatadogConverter converter;

		if (data == null) {
			converter = new DatadogApiConverter();
		} else {
			converter = new DatadogTimeboardConverter();
		}

		converter.init(properties);
		converter.parseAlerts(data);

		converters.add(converter);
	}

	@Override
	public List convertAlerts() {

		List models = new ArrayList();

		for (AbstractDatadogConverter converter : converters) {
			models.addAll(converter.convertAlerts());
		}

		return models;

	}
}
