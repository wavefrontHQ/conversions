package com.wavefront.labs.convert.converter.datadog;

import com.wavefront.labs.convert.Converter;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

public abstract class AbstractDatadogConverter implements Converter {
	protected Properties properties;

	public AbstractDatadogConverter() {
		this.properties = new Properties();
	}

	@Override
	public void init(Properties properties) {
		this.properties = properties;
	}

	@Override
	public abstract void parseDashboards(Object data) throws IOException;

	@Override
	public abstract List convertDashboards();

	@Override
	public abstract void parseAlerts(Object data) throws IOException;

	@Override
	public abstract List convertAlerts();
}
