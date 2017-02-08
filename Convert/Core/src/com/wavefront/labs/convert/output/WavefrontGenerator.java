package com.wavefront.labs.convert.output;

import com.wavefront.labs.convert.Generator;
import com.wavefront.rest.api.ApiClient;
import com.wavefront.rest.api.ApiException;
import com.wavefront.rest.api.client.AlertApi;
import com.wavefront.rest.api.client.DashboardApi;
import com.wavefront.rest.api.client.MaintenanceWindowApi;
import com.wavefront.rest.api.client.UserApi;
import com.wavefront.rest.models.*;

import java.util.List;
import java.util.Properties;

public class WavefrontGenerator implements Generator {

	private Properties properties;
	private AlertApi alertApi;
	private DashboardApi dashboardApi;
	private MaintenanceWindowApi maintenanceWindowApi;
	private UserApi userApi;

	@Override
	public void init(Properties properties) {
		this.properties = properties;
	}

	@Override
	public void generate(List models) {

		String url = cleanUrl(properties.getProperty("convert.publish.url"));
		String token = properties.getProperty("convert.publish.token");

		ApiClient apiClient = new ApiClient();
		apiClient.setBasePath(url);
		apiClient.setApiKey(token);

		for (Object model : models) {

			try {
				if (model instanceof Alert) {
					getAlertApi(apiClient).createAlert((Alert) model);
				} else if (model instanceof Dashboard) {
					createOrUpdateDashboard((Dashboard) model, apiClient);
				} else if (model instanceof MaintenanceWindow) {
					getMaintenanceWindowApi(apiClient).createMaintenanceWindow((MaintenanceWindow) model);
				} else if (model instanceof UserToCreate) {
					boolean sendEmail = Boolean.getBoolean(properties.getProperty("convert.user.sendMail", "false"));
					getUserApi(apiClient).createOrUpdateUser(sendEmail, (UserToCreate) model);
				} else {
					System.out.println("ERROR: model type not supported: " + model.getClass().getName());
				}


			} catch (ApiException e) {
				e.printStackTrace();
			}

		}
	}

	private void createOrUpdateDashboard(Dashboard dashboard, ApiClient apiClient) {
		DashboardApi dashboardApi = getDashboardApi(apiClient);

		boolean exists = false;
		try {
			dashboardApi.getDashboard(dashboard.getUrl());
			exists = true;
		} catch (ApiException e) {
			// do nothing, this indicates dashboard does not exists
		}

		try {
			if (exists) {
				dashboardApi.updateDashboard(dashboard.getUrl(), dashboard);
			} else {
				dashboardApi.createDashboard(dashboard);
			}
		} catch (ApiException e) {
			e.printStackTrace();
		}
	}

	private AlertApi getAlertApi(ApiClient apiClient) {
		if (alertApi == null) {
			alertApi = new AlertApi(apiClient);
		}

		return alertApi;
	}

	private DashboardApi getDashboardApi(ApiClient apiClient) {
		if (dashboardApi == null) {
			dashboardApi = new DashboardApi(apiClient);
		}

		return dashboardApi;
	}

	private MaintenanceWindowApi getMaintenanceWindowApi(ApiClient apiClient) {
		if (maintenanceWindowApi == null) {
			maintenanceWindowApi = new MaintenanceWindowApi(apiClient);
		}

		return maintenanceWindowApi;
	}

	private UserApi getUserApi(ApiClient apiClient) {
		if (userApi == null) {
			userApi = new UserApi(apiClient);
		}

		return userApi;
	}

	/*
	private void publishDashboard(Dashboard dashboardModel) throws ApiException {

		com.wavefront.rest.models.Dashboard dashboard = new com.wavefront.rest.models.Dashboard();

		dashboard.setUrl(dashboardModel.getUrl());
		dashboard.setName(dashboardModel.getName());

		for (DashboardSection sectionModel : dashboardModel.getSections()) {

			com.wavefront.rest.models.DashboardSection section = new com.wavefront.rest.models.DashboardSection();
			section.setName(sectionModel.getName());

			for (DashboardSectionRow rowModel : sectionModel.getRows()) {

				com.wavefront.rest.models.DashboardSectionRow row = new com.wavefront.rest.models.DashboardSectionRow();

				for (Chart chartModel : rowModel.getCharts()) {

					com.wavefront.rest.models.Chart chart = new com.wavefront.rest.models.Chart();
					chart.setName(chartModel.getName());
					chart.setDescription(chartModel.getDescription());
					chart.setUnits(chartModel.getUnits());
					chart.setBase(chartModel.getyAxisType() == YAxisType.LINEAR ? 1 : 10);
					chart.setSummarization(convertSummarization(chartModel.getChartSummarize()));

					ChartSettings chartSettings = new ChartSettings();
					chartSettings.setType(convertChartType(chartModel.getChartType()));
					//TODO: Bubble up y-axis unit type
					chart.setChartSettings(chartSettings);

					for (ChartQuery queryModel : chartModel.getChartQueries()) {

						ChartSource chartSource = new ChartSource();
						chartSource.setName(queryModel.getName());
						chartSource.setQuery(queryModel.getQuery());
						chartSource.setDisabled(!queryModel.isEnabled());

						chart.addSourcesItem(chartSource);
					}

					row.addChartsItem(chart);
				}

				section.addRowsItem(row);
			}

			dashboard.addSectionsItem(section);
		}

		if (dashboardModel.getVariables().size() > 0) {

			dashboard.setDisplayQueryParameters(true);

			for (DashboardVariable variableModel : dashboardModel.getVariables()) {
				HashMap<String, Object> variable = new HashMap();
				variable.put("parameterType", variableModel.getType().toString());
				variable.put("label", variableModel.getLabel());
				variable.put("defaultValue", variableModel.getDefaultValue());
				variable.put("valuesToReadableStrings", variableModel.getValuesToReadableStrings());
				variable.put("selectedLabel", variableModel.getSelectedLabel());
				variable.put("value", variableModel.getValue());

				dashboard.getParameterDetails().put(variableModel.getName(), variable);
			}
		}

		dashboardApi.saveDashboard(dashboard, false);
	}
	*/

	/*
	private void publishAlert(Alert alertModel) throws ApiException {

		String name = alertModel.getName();
		String condition = alertModel.getCondition();
		Integer minutes = alertModel.getMinutesToFire();
		String notifications = alertModel.getNotifications();
		String severity = alertModel.getSeverity().toString();
		String displayExpression = alertModel.getDisplayExpression();
		Integer resolveMinutes = alertModel.getMinutesToResolve();
		String privateTags = alertModel.getPrivateTags();
		String sharedTags = alertModel.getSharedTags();
		String additionalInformation = alertModel.getAdditionalInformation();

		alertApi.createAlertFromParts(name, condition, minutes, notifications, severity, displayExpression, resolveMinutes, privateTags, sharedTags, additionalInformation);
	}
	*/

	/*
	private SummarizationEnum convertSummarization(ChartSummarize summarize) {
		switch (summarize) {
			case AVERAGE:
				return SummarizationEnum.MEAN;
			case MEDIAN:
				return SummarizationEnum.MEDIAN;
			case COUNT:
				return SummarizationEnum.COUNT;
			case MIN:
				return SummarizationEnum.MIN;
			case MAX:
				return SummarizationEnum.MAX;
			case SUM:
				return SummarizationEnum.SUM;
			case FIRST:
				return SummarizationEnum.FIRST;
			case LAST:
				return SummarizationEnum.LAST;
			default:
				return SummarizationEnum.MEDIAN;
		}
	}

	private TypeEnum convertChartType(ChartType chartType) {
		switch(chartType) {
			case LINE_PLOT:
				return TypeEnum.LINE;
			case POINT_PLOT:
				return TypeEnum.SCATTERPLOT;
			case SCATTER_PLOT:
				return TypeEnum.SCATTERPLOT_XY;
			case STACKED_AREA:
				return TypeEnum.STACKED_AREA;
			case TABULAR:
				return TypeEnum.TABLE;
			case MARKDOWN:
				return TypeEnum.MARKDOWN_WIDGET;
			case SINGLE_STAT:
				return TypeEnum.SPARKLINE;
			default:
				return TypeEnum.LINE;
		}
	}
	*/

	private String cleanUrl(String url) {
		if (url.endsWith("/")) {
			url = url.substring(0, url.length() - 1);
		}

		if (url.endsWith("/api")) {
			url = url.substring(0, url.length() - 4);
		}

		return url;
	}

}
