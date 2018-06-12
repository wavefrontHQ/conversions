package com.wavefront.labs.convert.converter.grafana;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wavefront.labs.convert.Converter;
import com.wavefront.labs.convert.converter.grafana.model.GrafanaDashboard;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class GrafanaConverter implements Converter {
	private Properties properties;

	private GrafanaDashboard grafanaDashboard;

	@Override
	public void init(Properties properties) {
		this.properties = properties;
	}

	@Override
	public void parse(Object data) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		grafanaDashboard = mapper.readValue((File) data, GrafanaDashboard.class);
		parseModel();
	}

	private void parseModel() {
		System.out.println(grafanaDashboard.getTitle());
	}

	@Override
	public List convert() {
		return null;
	}
}
