package com.wavefront.labs.convert.converter.grapher;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.wavefront.labs.convert.converter.grapher.models.GrapherChart;
import com.wavefront.labs.convert.converter.grapher.models.GrapherChartDefinition;
import com.wavefront.labs.convert.converter.grapher.models.GrapherDashboard;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;

public class WriteYamls {

	@Test
	public void doIt() throws IOException {
		String outFolder = "test_resources/grapher/out/";
		new File(outFolder + "dashboards/").mkdirs();
		new File(outFolder + "charts/").mkdirs();
		new File(outFolder + "rrd/").mkdirs();
		new File(outFolder + "rrd_host/").mkdirs();

		ObjectMapper jsonMapper = new ObjectMapper();
		ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

		List<GrapherDashboard> grapherDashboards = jsonMapper.readValue(new File("test_resources/grapher/lazlo_dashboards.json"), new TypeReference<List<GrapherDashboard>>(){});
		for (int i = 0; i < grapherDashboards.size(); i++) {
			GrapherDashboard grapherDashboard = grapherDashboards.get(i);

			String filename = String.format("%05d", i) + "-" + grapherDashboard.getName();
			HashMap<String, Object> grapherDashboardGraphs = yamlMapper.readValue(grapherDashboard.getData(), HashMap.class);
			yamlMapper.writeValue(new File(outFolder + "dashboards/" + filename + ".yml"), grapherDashboardGraphs);
		}


		List<GrapherChartDefinition> grapherChartDefinitions = jsonMapper.readValue(new File("test_resources/grapher/lazlo_charts.json"), new TypeReference<List<GrapherChartDefinition>>(){});
		for (int i = 0; i < grapherChartDefinitions.size(); i++) {
			GrapherChartDefinition def = grapherChartDefinitions.get(i);

			GrapherChart grapherChart = yamlMapper.readValue(def.getData(), GrapherChart.class);

			String title = grapherChart.getTitle().replaceAll("[^a-zA-Z0-9\\-_]+", "_");
			String filename = String.format("%07d", def.getId()) + "-" + title;

			yamlMapper.writeValue(new File(outFolder + "charts/" + filename + ".yml"), grapherChart);

			Files.write(new File(outFolder + "rrd/" + filename + ".rrd").toPath(), grapherChart.getRrdDef().getBytes());

			if (grapherChart.getRrdDefHost() != null) {
				Files.write(new File(outFolder + "rrd/" + filename + ".rrd").toPath(), grapherChart.getRrdDefHost().getBytes());
			}

			System.out.println(filename);
		}

	}
}
