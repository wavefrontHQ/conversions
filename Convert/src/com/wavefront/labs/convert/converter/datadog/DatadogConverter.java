package com.wavefront.labs.convert.converter.datadog;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DatadogConverter extends AbstractDatadogConverter {

	private AbstractDatadogConverter converter;

	@Override
	public void parse(Object data) throws IOException {

		if (data == null) {
			converter = new DatadogApiConverter();
		} else if (data instanceof File) {
			converter = new DatadogTimeboardConverter();
		}

		converter.init(properties);
		converter.parse(data);

	}

	@Override
	public List convert() {

		return converter.convert();

	}
}
