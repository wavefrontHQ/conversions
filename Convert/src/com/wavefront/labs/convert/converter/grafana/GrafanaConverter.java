package com.wavefront.labs.convert.converter.grafana;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wavefront.labs.convert.Converter;
import com.wavefront.labs.convert.ExpressionBuilder;
import com.wavefront.labs.convert.SimpleExpressionBuilder;
import com.wavefront.labs.convert.Utils;
import com.wavefront.labs.convert.converter.datadog.DatadogTimeboardConverter;
import com.wavefront.labs.convert.converter.grafana.model.*;
import com.wavefront.rest.models.*;
import com.wavefront.rest.models.Chart.SummarizationEnum;
import com.wavefront.rest.models.ChartSettings.FixedLegendPositionEnum;
import com.wavefront.rest.models.ChartSettings.StackTypeEnum;
import com.wavefront.rest.models.ChartSettings.TypeEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

public class GrafanaConverter implements Converter {
	private static final Logger logger = LogManager.getLogger(DatadogTimeboardConverter.class);

	private Properties properties;

	private List<GrafanaDashboard> grafanaDashboards;

	private ExpressionBuilder expressionBuilder;

	@Override
	public void init(Properties properties) {
		this.properties = properties;
		String expressionBuilderClass = properties.getProperty("convert.expressionBuilder", "");
		if (!expressionBuilderClass.equals("")) {
			try {
				expressionBuilder = (ExpressionBuilder) Class.forName(expressionBuilderClass).newInstance();
			} catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
				logger.error("Could not create instance of: " + expressionBuilderClass, e);
				expressionBuilder = new SimpleExpressionBuilder();
			}
		} else {
			expressionBuilder = new SimpleExpressionBuilder();
		}
		expressionBuilder.init(properties);
		grafanaDashboards = new ArrayList();
	}

	@Override
	public void parse(Object data) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		GrafanaDashboard grafanaDashboard = mapper.readValue((File) data, GrafanaDashboard.class);
		grafanaDashboards.add(grafanaDashboard);
	}

	@Override
	public List convert() {

		List models = new ArrayList();

		for (GrafanaDashboard grafanaDashboard : grafanaDashboards) {
			Dashboard dashboard = new Dashboard();
			dashboard.setUrl(Utils.sluggify(grafanaDashboard.getTitle()));
			dashboard.setName(grafanaDashboard.getTitle());
			dashboard.setDescription(grafanaDashboard.getDescription());

			List<GrafanaPanel> panels = grafanaDashboard.getPanels();
			panels.sort(Comparator.comparing(GrafanaPanel::getGridPosY).thenComparing(GrafanaPanel::getGridPosX));
			DashboardSection dashboardSection = new DashboardSection();
			dashboardSection.setName("Charts");

			DashboardSectionRow dashboardSectionRow = new DashboardSectionRow();
			int curY = panels.get(0).getGridPosY();
			for (GrafanaPanel panel : panels) {
				if (curY != panel.getGridPosY()) {
					dashboardSection.addRowsItem(dashboardSectionRow);
					dashboardSectionRow = new DashboardSectionRow();
					curY = panel.getGridPosY();
				}

				Chart chart = getChartFromPanel(panel);
				dashboardSectionRow.addChartsItem(chart);
			}

			dashboardSection.addRowsItem(dashboardSectionRow);
			dashboard.addSectionsItem(dashboardSection);

			models.add(dashboard);
		}

		return models;
	}

	private Chart getChartFromPanel(GrafanaPanel panel) {
		Chart chart = new Chart();

		chart.setName(panel.getTitle());
		chart.setDescription(panel.getDescription());
		chart.setSummarization(SummarizationEnum.MEAN);

		ChartSettings chartSettings = new ChartSettings();
		chartSettings.setType(getChartType(panel));
		if (panel.isPercentage()) {
			chartSettings.setStackType(StackTypeEnum.EXPAND);
		}

		// Queries
		for (GrafanaPanelTarget target : panel.getTargets()) {
			ChartSourceQuery chartSourceQuery = new ChartSourceQuery();
			chartSourceQuery.setDisabled(target.isHide());
			chartSourceQuery.setName(target.getRefId());

			String query = target.getTargetFull() != null && !target.getTargetFull().equals("") ? target.getTargetFull() : target.getTarget();
			chartSourceQuery.setQuery(expressionBuilder.buildExpression(query));

			chart.addSourcesItem(chartSourceQuery);
		}

		// Y-axes
		chart.setBase(panel.getYaxes().get(0).getLogBase());
		GrafanaPanelYAxis y0 = panel.getYaxes().get(0);
		if (y0.getMin() != null && !y0.getMin().equals("")) {
			chartSettings.setYmin(Double.valueOf(y0.getMin()));
		}
		if (y0.getMax() != null && !y0.getMax().equals("")) {
			chartSettings.setYmax(Double.valueOf(y0.getMax()));
		}
		if (y0.getLabel() != null && !y0.getLabel().equals("")) {
			chart.setUnits(y0.getLabel());
		}
		if (panel.getYaxes().size() > 1) {
			GrafanaPanelYAxis y1 = panel.getYaxes().get(1);
			if (y1.getMin() != null && !y1.getMin().equals("")) {
				chartSettings.setYmin(Double.valueOf(y1.getMin()));
			}
			if (y1.getMax() != null && !y1.getMax().equals("")) {
				chartSettings.setYmax(Double.valueOf(y1.getMax()));
			}
			if (y1.getLabel() != null && !y1.getLabel().equals("")) {
				chartSettings.setY1Units(y1.getLabel());
			}
		}

		// Legend
		GrafanaPanelLegend legend = panel.getLegend();
		if (legend.isShow()) {
			chartSettings.setFixedLegendEnabled(true);

			if (legend.isRightSide()) {
				chartSettings.setFixedLegendPosition(FixedLegendPositionEnum.RIGHT);
			} else {
				chartSettings.setFixedLegendPosition(FixedLegendPositionEnum.BOTTOM);
			}

			if (legend.isCurrent()) {
				chartSettings.addFixedLegendDisplayStatsItem("CURRENT");
			}
			if (legend.isAvg()) {
				chartSettings.addFixedLegendDisplayStatsItem("MEAN");
			}
			if (legend.isMin()) {
				chartSettings.addFixedLegendDisplayStatsItem("MIN");
			}
			if (legend.isMax()) {
				chartSettings.addFixedLegendDisplayStatsItem("MAX");
			}
			if (legend.isTotal()) {
				chartSettings.addFixedLegendDisplayStatsItem("SUM");
			}
			if (legend.isValues()) {
				chartSettings.addFixedLegendDisplayStatsItem("COUNT");
			}
		}


		return chart;
	}

	private TypeEnum getChartType(GrafanaPanel panel) {
		if (panel.getType().equals("graph")) {
			if (panel.isStack()) {
				return TypeEnum.STACKED_AREA;
			}

			return TypeEnum.LINE;
		}

		return TypeEnum.LINE;
	}
}
