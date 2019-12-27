package com.wavefront.labs.convert.converter.datadog;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

public class DatadogApiConverter extends AbstractDatadogConverter {
	private static final Logger logger = LogManager.getLogger(DatadogApiConverter.class);

	private List<DatadogTimeboardConverter> dashboardConverters;
	private List<DatadogAlertConverter> alertConverters;

	@Override
	public void init(Properties properties) {
		super.init(properties);
		dashboardConverters = new ArrayList();
		alertConverters = new ArrayList();
	}

	@Override
	public void parseDashboards(Object data) throws IOException {

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
					DatadogTimeboardConverter converter = new DatadogTimeboardConverter();
					converter.init(properties);
					converter.parseDashboards(dash.get("id").toString());
					if (!converter.getParsingErrorFlag()) {
						dashboardConverters.add(converter);
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
	public List convertDashboards() {

		List models = new ArrayList();

		for (DatadogTimeboardConverter converter : dashboardConverters) {
			try {
				models.addAll(converter.convertDashboards());
				com.wavefront.labs.convert.utils.Tracker.increment("\"DatadogTimeboardConverter2::convert Successful (Count)\"");
			} catch (Exception ex) {
				logger.error("Exception during convert", ex);
				com.wavefront.labs.convert.utils.Tracker.increment("\"DatadogTimeboardConverter2::convert Exception (Count)\"");
			}
		}

		return models;

	}

	@Override
	public void parseAlerts(Object data) throws IOException {

		String apiKey = properties.getProperty("datadog.api.key");
		String applicationKey = properties.getProperty("datadog.application.key");
		String url = "https://api.datadoghq.com/api/v1/monitor";
		url += "?api_key=" + apiKey;
		url += "&application_key=" + applicationKey;

		try {
			ObjectMapper mapper = new ObjectMapper();
			List<HashMap> alerts = mapper.convertValue(mapper.readTree(new URL(url)), new TypeReference<List<HashMap>>() {
			});
			processAlerts(alerts);
		} catch (IOException e) {
			logger.error("Could not get list of available monitors via API", e);
		}
	}

	private void processAlerts(List<HashMap> alerts) throws IOException {

		String titleMatch = properties.getProperty("datadog.alert.titleMatch", ".*");
		Pattern titlePattern = Pattern.compile(titleMatch);

		for (HashMap alert : alerts) {
			if (alert.containsKey("id")) {

				String name = alert.get("name").toString();
				if (titlePattern.matcher(name).matches()) {
					DatadogAlertConverter converter = new DatadogAlertConverter();
					converter.init(properties);
					converter.parseAlerts(alert.get("id").toString());
					if (!converter.getParsingErrorFlag()) {
						alertConverters.add(converter);
						com.wavefront.labs.convert.utils.Tracker.increment("\"Alerts Successfully Parsed Count\"");
					} else {
						com.wavefront.labs.convert.utils.Tracker.increment("\"Alert Parsing Error Count\"");
					}
				} else {
					com.wavefront.labs.convert.utils.Tracker.increment("\"Unmatched Alert Title Count\"");
				}
			} else {
				com.wavefront.labs.convert.utils.Tracker.increment("\"Alerts Without id Count\"");
			}
		}

	}

	@Override
	public List convertAlerts() {

		List models = new ArrayList();

		for (DatadogAlertConverter converter : alertConverters) {
			try {
				models.addAll(converter.convertAlerts());
				com.wavefront.labs.convert.utils.Tracker.increment("\"DatadogAlertConverter2::convert Successful (Count)\"");
			} catch (Exception ex) {
				logger.error("Exception during convert", ex);
				com.wavefront.labs.convert.utils.Tracker.increment("\"DatadogAlertConverter2::convert Exception (Count)\"");
			}
		}

		return models;

	}
}
