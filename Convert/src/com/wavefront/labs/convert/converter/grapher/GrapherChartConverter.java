package com.wavefront.labs.convert.converter.grapher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.wavefront.labs.convert.converter.grapher.models.GrapherChart;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GrapherChartConverter extends AbstractGrapherConverter {

	private static final Logger logger = LogManager.getLogger(GrapherChartConverter.class);

	private GrapherChart grapherChart;

	private GrapherRRDConverter rrdDefConverter;
	private GrapherRRDConverter rrdDefHostConverter;

	@Override
	public void parseDashboards(Object data) throws IOException {

		if (!(data instanceof String)) {
			logger.error("Invalid type to parse: " + data.getClass().getName());
			return;
		}

		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		grapherChart = mapper.readValue((String) data, GrapherChart.class);
		parseModel();
	}

	private void parseModel() throws IOException {

		grapherChart.setTitle(grapherChart.getTitle().replaceAll("\\*", "-"));

		if (grapherChart.getRrdDef() != null) {
			rrdDefConverter = new GrapherRRDConverter();
			rrdDefConverter.init(properties);
			rrdDefConverter.setGrapherChart(grapherChart);
			rrdDefConverter.parseDashboards(grapherChart.getRrdDef());
		}

		if (grapherChart.getRrdDefHost() != null) {
			rrdDefHostConverter = new GrapherRRDConverter();
			rrdDefHostConverter.init(properties);
			rrdDefHostConverter.setGrapherChart(grapherChart);
			rrdDefHostConverter.parseDashboards(grapherChart.getRrdDefHost());
		}
	}

	@Override
	public List convertDashboards() {

		ArrayList models = new ArrayList();

		if (rrdDefConverter != null) {
			models.addAll(rrdDefConverter.convertDashboards());
		}

		if (rrdDefHostConverter != null) {
			models.addAll(rrdDefHostConverter.convertDashboards());
		}

		return models;
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
