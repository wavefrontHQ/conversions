package com.wavefront.labs.convert.input.rrd;

import com.wavefront.labs.convert.Converter;
import com.wavefront.labs.convert.input.rrd.models.Graph;
import com.wavefront.labs.convert.input.rrd.models.graph.GraphItem;
import com.wavefront.rest.models.Chart;
import com.wavefront.rest.models.ChartSettings;
import com.wavefront.rest.models.ChartSettings.TypeEnum;
import com.wavefront.rest.models.ChartSourceQuery;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class RRDConverter implements Converter {

	protected Properties properties;
	protected RRDContext rrdContext;

	public RRDConverter() {
		this.properties = new Properties();
	}

	@Override
	public void init(Properties properties) {
		this.properties = properties;
	}

	@Override
	public void parse(String rrd) throws IOException {
		rrdContext = new RRDContext(properties);
		rrdContext.parse(rrd);
	}

	@Override
	public void parse(File file) throws IOException {
		rrdContext = new RRDContext(properties);
		rrdContext.parse(file);
	}

	@Override
	public List convert() {

		ArrayList models = new ArrayList();
		Chart chart = new Chart();

		boolean stacked = true;

		for (Graph graph : rrdContext.getGraphs()) {

			GraphItem graphItem = graph.getGraphItem();

			stacked = stacked && graphItem.isStacked();

			ChartSourceQuery chartSourceQuery = new ChartSourceQuery();
			chartSourceQuery.setName(graphItem.getLegend());

			String value = graphItem.getValue();
			if (rrdContext.hasVariable(value)) {
				value = rrdContext.getVariable(value);
			}
			chartSourceQuery.setQuery(value);

			chart.getSources().add(chartSourceQuery);
		}

		ChartSettings chartSettings = new ChartSettings();
		if (stacked) {
			chartSettings.setType(TypeEnum.STACKED_AREA);
		} else {
			chartSettings.setType(TypeEnum.LINE);
		}
		chart.setChartSettings(chartSettings);


		models.add(chart);
		return models;
	}
}
