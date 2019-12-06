package com.wavefront.labs.convert.converter.datadog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DatadogConverter2 extends AbstractDatadogConverter {

	private List<AbstractDatadogConverter> converters;

	@Override
	public void init(Properties properties) {
		super.init(properties);
		converters = new ArrayList();
	}

	@Override
	public void parse(Object data) throws IOException {

		AbstractDatadogConverter converter;

		if (data == null) {
			converter = new DatadogApiConverter2();
		} else {
			converter = new DatadogTimeboardConverter2();
		}

		converter.init(properties);
		converter.parse(data);

		converters.add(converter);
	}

	@Override
	public List convert() {

		List models = new ArrayList();

		for (AbstractDatadogConverter converter : converters) {
			models.addAll(converter.convert());
		}

		return models;

	}
}
