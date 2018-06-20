package com.wavefront.labs.convert.converter.datadog;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

public class DatadogApiConverter extends AbstractDatadogConverter {
	private static final Logger logger = LogManager.getLogger(DatadogApiConverter.class);

	private List<DatadogTimeboardConverter> converters;

	@Override
	public void init(Properties properties) {
		super.init(properties);
		converters = new ArrayList();
	}

	@Override
	public void parse(Object data) throws IOException {

		String apiKey = properties.getProperty("datadog.api.key");
		String applicationKey = properties.getProperty("datadog.application.key");
		String url = "https://app.datadoghq.com/api/v1/dash";
		url += "?api_key=" + apiKey;
		url += "&application_key=" + applicationKey;

		try {
			ObjectMapper mapper = new ObjectMapper();
			List<HashMap> dashes = mapper.convertValue(mapper.readTree(new URL(url)).get("dashes"), new TypeReference<List<HashMap>> (){});
			processDashes(dashes);
		} catch (IOException e) {
			logger.error("Could not get list of available timeboards via API", e);
		}
	}

	private void processDashes(List<HashMap> dashes) throws IOException {

		String titleMatch = properties.getProperty("datadog.timeboard.titleMatch", ".*");
		Pattern titlePattern = Pattern.compile(titleMatch);

		for (HashMap dash : dashes) {
			if (dash.containsKey("id")) {

				String title = dash.get("title").toString();
				if (titlePattern.matcher(title).matches()) {
					DatadogTimeboardConverter converter = new DatadogTimeboardConverter();
					converter.init(properties);
					converter.parse(dash.get("id").toString());
					converters.add(converter);
				}
			}
		}

	}

	@Override
	public List convert() {

		List models = new ArrayList();

		for (DatadogTimeboardConverter converter : converters) {
			models.addAll(converter.convert());
		}

		return models;

	}
}
