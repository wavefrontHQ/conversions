package com.wavefront.labs.convert.input.grapher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.wavefront.labs.convert.input.grapher.models.GrapherChart;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GrapherChartConverter extends AbstractGrapherConverter {

	private GrapherChart grapherChart;

	private GrapherRRDConverter rrdDefConverter;
	private GrapherRRDConverter rrdDefHostConverter;

	@Override
	public void parse(String content) throws IOException {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		grapherChart = mapper.readValue(content, GrapherChart.class);
		parseModel();
	}

	@Override
	public void parse(File file) throws IOException {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		grapherChart = mapper.readValue(file, GrapherChart.class);
		parseModel();
	}

	private void parseModel() throws IOException {
		grapherChart.setTitle(grapherChart.getTitle().replaceAll("\\*", "-"));

		if (grapherChart.getRrdDef() != null) {
			rrdDefConverter = new GrapherRRDConverter();
			rrdDefConverter.init(properties);
			rrdDefConverter.setGrapherChart(grapherChart);
			rrdDefConverter.parse(grapherChart.getRrdDef());
		}

		if (grapherChart.getRrdDefHost() != null) {
			rrdDefHostConverter = new GrapherRRDConverter();
			rrdDefHostConverter.init(properties);
			rrdDefHostConverter.setGrapherChart(grapherChart);
			rrdDefHostConverter.parse(grapherChart.getRrdDefHost());
		}
	}

	@Override
	public List convert() {

		ArrayList models = new ArrayList();

		if (rrdDefConverter != null) {
			models.addAll(rrdDefConverter.convert());
		}

		if (rrdDefHostConverter != null) {
			models.addAll(rrdDefHostConverter.convert());
		}

		return models;
	}
}
