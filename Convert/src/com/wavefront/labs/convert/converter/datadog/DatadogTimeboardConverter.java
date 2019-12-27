package com.wavefront.labs.convert.converter.datadog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wavefront.labs.convert.Utils;
import com.wavefront.labs.convert.converter.datadog.models.*;
import com.wavefront.labs.convert.converter.datadog.query.Variable;
import com.wavefront.rest.models.*;
import com.wavefront.rest.models.Chart.SummarizationEnum;
import com.wavefront.rest.models.ChartSettings.TypeEnum;
import com.wavefront.rest.models.DashboardParameterValue.ParameterTypeEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class DatadogTimeboardConverter extends AbstractDatadogConverter {
	private static final Logger logger = LogManager.getLogger(DatadogTimeboardConverter.class);

	private DatadogTimeboard datadogTimeboard;
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

		if (data instanceof String) {
			parseFromId(data.toString());
		} else if (data instanceof File) {
			parseFromFile((File) data);
		}

	}

	private void parseFromId(String id) {
		String apiKey = properties.getProperty("datadog.api.key");
		String applicationKey = properties.getProperty("datadog.application.key");
		String url = "https://api.datadoghq.com/api/v1/dashboard/" + id;
		url += "?api_key=" + apiKey;
		url += "&application_key=" + applicationKey;

		try {
			ObjectMapper mapper = new ObjectMapper();
			datadogTimeboard = mapper.convertValue(mapper.readTree(new URL(url)), DatadogTimeboard.class);
		} catch (Exception e) {
			parsingErrorFlag = true;
			logger.error("Could not get dashboard via API: " + id, e);
		}
	}

	private void parseFromFile(File file) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		datadogTimeboard = mapper.convertValue(mapper.readTree(file).get("dash"), DatadogTimeboard.class);
	}

	@Override
	public List convertDashboards() {

		logger.info("Converting Datadog Timeboard: " + datadogTimeboard.getId() + "/" + datadogTimeboard.getTitle());

		expressionBuilder.initVariablesMap(datadogTimeboard.getTemplateVariables());

		Dashboard dashboard = new Dashboard();
		dashboard.setUrl(Utils.sluggify(datadogTimeboard.getTitle()));
		dashboard.setName(datadogTimeboard.getTitle());
		dashboard.setDescription(datadogTimeboard.getDescription());

		dashboard.setDisplayDescription(false);
		dashboard.setDisplaySectionTableOfContents(false);
		dashboard.setDisplayQueryParameters(false);

		convertSectionFromWidgets(dashboard, datadogTimeboard.getWidgets(), "Charts");

		HashMap<String, Variable> variablesMap = expressionBuilder.getVariablesMap();
		if (variablesMap.size() > 0) {
			Map<String, DashboardParameterValue> dashboardParameters = new HashMap();
			for (Variable variable : variablesMap.values()) {
				dashboardParameters.put(variable.getName(), createDashboardParameter(variable));
			}
			dashboard.setParameterDetails(dashboardParameters);
			dashboard.setDisplayQueryParameters(true);
		}

		List models = new ArrayList();
		models.add(dashboard);
		return models;
	}

	private void convertSectionFromWidgets(Dashboard dashboard, List<DatadogWidget> widgets, String title) {
		DashboardSection dashboardSection = new DashboardSection();
		dashboardSection.setName(title);
		dashboard.addSectionsItem(dashboardSection);

		DashboardSectionRow dashboardSectionRow = null;

		for (int i = 0; i < widgets.size(); i++) {
			List<DatadogWidget> nestedWidgets = widgets.get(i).getDefinition().getWidgets();
			if (nestedWidgets != null && nestedWidgets.size() > 0) {
				if (i==0) {
					dashboard.getSections().remove(dashboard.getSections().size()-1);
				}
				convertSectionFromWidgets(dashboard, nestedWidgets, widgets.get(i).getDefinition().getTitle());
			} else {
				if (i % 3 == 0) {
					dashboardSectionRow = new DashboardSectionRow();
					dashboardSection.addRowsItem(dashboardSectionRow);
				}

				try {
					Chart chart = createChartfromDatadogWidget(widgets, i);
					dashboardSectionRow.addChartsItem(chart);
				} catch (Exception ex) {
					logger.error("Exception while creating chart", ex);
					com.wavefront.labs.convert.utils.Tracker.addToList("\"Chart Creation Exception\"", "Dashboard (" + datadogTimeboard.getTitle() + ") Chart (" + widgets.get(i).getDefinition().getTitle() + ")");
					com.wavefront.labs.convert.utils.Tracker.increment("\"Chart Creation Exception Count\"");
				}
				if (!dashboard.getSections().contains(dashboardSection)) {
					dashboard.addSectionsItem(dashboardSection);
				}
			}
		}
	}

	private Chart createChartfromDatadogWidget(List<DatadogWidget> widgets, int i) {
		DatadogWidget datadogWidget = widgets.get(i);
		Chart chart = new Chart();
		String chartTitle = datadogWidget.getDefinition().getTitle();
		if (chartTitle == null) {
			chartTitle = "Chart-" + (i+1);
		}
		chart.setName(chartTitle);

		chart.setSummarization(getChartSummarization("avg"));
		ChartSettings chartSettings = new ChartSettings();
		chartSettings.setType(TypeEnum.LINE);

		if (datadogWidget.getDefinition() != null) {
			DatadogGraphDefinition definition = datadogWidget.getDefinition();

			String viz = definition.getType();

			if (definition.getRequests() != null && definition.getRequests().size() > 0) {
				String vizType = definition.getRequests().get(0).getType();
				chartSettings.setType(getChartType(viz, vizType));

				if (chartSettings.getType() == TypeEnum.SPARKLINE && definition.getTitle() != null && !definition.getTitle().equals("")) {
					// TODO: Get the correct precision
					chartSettings.setSparklineDecimalPrecision(Integer.parseInt("1"));
				}

				String aggregator = definition.getRequests().get(0).getAggregator();
				chart.setSummarization(getChartSummarization(aggregator));

				for (DatadogGraphRequest request : definition.getRequestsAsList()) {
					String[] queries = expressionBuilder.buildExpression(request.getQuery()).split(DatadogExpressionBuilder.QUERY_SEPARATOR_SPLIT);
					for (String query : queries) {
						ChartSourceQuery chartSourceQuery = new ChartSourceQuery();
						chartSourceQuery.setQuery(query.trim());
						chartSourceQuery.setName("Query");
						chart.addSourcesItem(chartSourceQuery);
					}
				}
			}

			if ("image".equals(viz)) {
				chartSettings.setPlainMarkdownContent("![alt text](" + definition.getUrl() + " \\\"Image\\\")");
			}

			DatadogYAxis yaxis = definition.getYaxis();
			if (yaxis != null) {
				if (yaxis.getMin() != null && !yaxis.getMin().equals("")) {
					chartSettings.setYmin(Double.parseDouble(yaxis.getMin()));
				}

				if (yaxis.getMax() != null && !yaxis.getMax().equals("")) {
					chartSettings.setYmax(Double.parseDouble(yaxis.getMax()));
				}

				if (yaxis.getScale() != null) {
					switch (yaxis.getScale()) {
						case "linear":
							chart.setBase(0);
							break;
						case "log":
							chart.setBase(10);
							break;
						default:
							logger.warn("Y-Axis scale type not supported: " + yaxis.getScale());
							chart.setBase(0);
							break;
					}
				}

			}
		}

		chart.setChartSettings(chartSettings);

		return chart;
	}

	private DashboardParameterValue createDashboardParameter(Variable variable) {
		DashboardParameterValue dashboardParameterValue = new DashboardParameterValue();

		dashboardParameterValue.setParameterType(ParameterTypeEnum.DYNAMIC);
		dashboardParameterValue.setLabel(variable.getName());
		dashboardParameterValue.setDynamicFieldType(DashboardParameterValue.DynamicFieldTypeEnum.TAG_KEY);
		dashboardParameterValue.setQueryValue("ts(system.load15)");
		//dashboardParameterValue.setDefaultValue(variable.getValue());
		dashboardParameterValue.setDefaultValue("production");
		//dashboardParameterValue.setTagKey(variable.getTagName());
		dashboardParameterValue.setTagKey("cht_env");
		dashboardParameterValue.putValuesToReadableStringsItem("Label", "production");

		return dashboardParameterValue;
	}

	private TypeEnum getChartType(String viz, String vizType) {
		if (viz == null) {
			viz = "timeseries";
		}
		if (vizType == null) {
			vizType = "line";
		}

		switch (viz) {
			case "timeseries":
				switch (vizType) {
					case "line":
						return TypeEnum.LINE;
					case "area":
						return TypeEnum.STACKED_AREA;
					default:
						logger.warn("Unsupported type for timeseries: " + vizType);
						return TypeEnum.LINE;
				}
			case "query_value":
				return TypeEnum.SPARKLINE;
			case "toplist":
				return TypeEnum.TABLE;
			case "image":
				return TypeEnum.MARKDOWN_WIDGET;
			default:
				logger.warn("Unsupported chart type: " + viz);
				return TypeEnum.LINE;
		}
	}

	private SummarizationEnum getChartSummarization(String aggregator) {
		if (aggregator == null) {
			aggregator = "avg";
		}

		switch (aggregator) {
			case "avg":
				return SummarizationEnum.MEAN;
			case "min":
				return SummarizationEnum.MIN;
			case "max":
				return SummarizationEnum.MAX;
			case "sum":
				return SummarizationEnum.SUM;
			case "last":
				return SummarizationEnum.LAST;
			default:
				return SummarizationEnum.MEAN;
		}
	}

	@Override
	public void parseAlerts(Object data) throws IOException {
		throw new RuntimeException("Method not implemented");
	}

	@Override
	public List convertAlerts() {
		throw new RuntimeException("Method not implemented");
	}
}
