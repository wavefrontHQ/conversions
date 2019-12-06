package com.wavefront.labs.convert.converter.datadog;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

public class DatadogApiConverter2 extends AbstractDatadogConverter {
	private static final Logger logger = LogManager.getLogger(DatadogApiConverter2.class);

	private List<DatadogTimeboardConverter2> converters;

	@Override
	public void init(Properties properties) {
		super.init(properties);
		converters = new ArrayList();
	}

	@Override
	public void parse(Object data) throws IOException {

		String apiKey = properties.getProperty("datadog.api.key");
		String applicationKey = properties.getProperty("datadog.application.key");
		String url = "https://api.datadoghq.com/api/v1/dashboard";
		url += "?api_key=" + apiKey;
		url += "&application_key=" + applicationKey;

		try {
			ObjectMapper mapper = new ObjectMapper();
			List<HashMap> dashes = mapper.convertValue(mapper.readTree(new URL(url)).get("dashboards"), new TypeReference<List<HashMap>>() {
			});
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
					DatadogTimeboardConverter2 converter = new DatadogTimeboardConverter2();
					converter.init(properties);
					converter.parse(dash.get("id").toString());
					if (!converter.getParsingErrorFlag()) {
						converters.add(converter);
						com.wavefront.labs.convert.utils.Tracker.increment("\"Dashboards Successfully Parsed Count\"");
					} else {
						com.wavefront.labs.convert.utils.Tracker.increment("\"Dashboard Parsing Error Count\"");
					}
				} else {
					com.wavefront.labs.convert.utils.Tracker.increment("\"Unmatched Dashboard Title Count\"");
				}
			} else {
				com.wavefront.labs.convert.utils.Tracker.increment("\"Dashboards Without id Count\"");
			}
		}

	}

	@Override
	public List convert() {

		List models = new ArrayList();

		for (DatadogTimeboardConverter2 converter : converters) {
			try {
				models.addAll(converter.convert());
				com.wavefront.labs.convert.utils.Tracker.increment("\"DatadogTimeboardConverter2::convert Successful (Count)\"");
			} catch (Exception ex) {
				logger.error("Exception during convert", ex);
				com.wavefront.labs.convert.utils.Tracker.increment("\"DatadogTimeboardConverter2::convert Exception (Count)\"");
			}
		}

		return models;

	}
}
