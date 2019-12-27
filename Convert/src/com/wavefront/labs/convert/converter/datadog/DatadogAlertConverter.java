package com.wavefront.labs.convert.converter.datadog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wavefront.labs.convert.converter.datadog.models.*;
import com.wavefront.rest.models.Alert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class DatadogAlertConverter extends AbstractDatadogConverter {
	private static final Logger logger = LogManager.getLogger(DatadogAlertConverter.class);

	private DatadogAlert datadogAlert;
	private DatadogExpressionBuilder expressionBuilder;

	private boolean parsingErrorFlag = false;

	public boolean getParsingErrorFlag () {
		return parsingErrorFlag;
	}

	@Override
	public void init(Properties properties) {
		super.init(properties);

		String expressionBuilderClass = properties.getProperty("convert.expressionBuilder", "");
		if (!expressionBuilderClass.equals("")) {
			try {
				expressionBuilder = (DatadogExpressionBuilder) Class.forName(expressionBuilderClass).newInstance();
			} catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
				logger.error("Could not create instance of: " + expressionBuilderClass, e);
				expressionBuilder = new DatadogExpressionBuilder();
			}
		} else {
			expressionBuilder = new DatadogExpressionBuilder();
		}
		expressionBuilder.init(properties);
	}

	@Override
	public void parseDashboards(Object data) throws IOException {
		throw new RuntimeException("Method not implemented");
	}

	private void parseFromId(String id) {
		String apiKey = properties.getProperty("datadog.api.key");
		String applicationKey = properties.getProperty("datadog.application.key");
		String url = "https://api.datadoghq.com/api/v1/monitor/" + id;
		url += "?api_key=" + apiKey;
		url += "&application_key=" + applicationKey;

		try {
			ObjectMapper mapper = new ObjectMapper();
			datadogAlert = mapper.convertValue(mapper.readTree(new URL(url)), DatadogAlert.class);
		} catch (Exception e) {
			parsingErrorFlag = true;
			logger.error("Could not get dashboard via API: " + id, e);
		}
	}

	private void parseFromFile(File file) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		datadogAlert = mapper.convertValue(mapper.readTree(file).get("dash"), DatadogAlert.class);
	}

	@Override
	public List convertDashboards() {
		throw new RuntimeException("Method not implemented");
	}

	@Override
	public void parseAlerts(Object data) throws IOException {
		if (data instanceof String) {
			parseFromId(data.toString());
		} else if (data instanceof File) {
			parseFromFile((File) data);
		}
	}

	@Override
	public List convertAlerts() {

		logger.info("Converting Datadog Timeboard: " + datadogAlert.getId() + "/" + datadogAlert.getName());

		Alert alert = new Alert();

		alert.setName(datadogAlert.getName());
		alert.setCondition(expressionBuilder.buildExpression(datadogAlert.getQuery()));

		List models = new ArrayList();
		models.add(alert);
		return models;
	}
}
