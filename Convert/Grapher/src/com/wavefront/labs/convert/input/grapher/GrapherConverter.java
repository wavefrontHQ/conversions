package com.wavefront.labs.convert.input.grapher;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wavefront.labs.convert.input.grapher.models.GrapherDashboard;
import com.wavefront.rest.models.Dashboard;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GrapherConverter extends AbstractGrapherConverter {

	private List<GrapherDashboard> grapherDashboards;

	@Override
	public void parse(String content) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		grapherDashboards = mapper.readValue(content, new TypeReference<List<GrapherDashboard>>(){});
		parseModel();
	}

	@Override
	public void parse(File file) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		grapherDashboards = mapper.readValue(file, new TypeReference<List<GrapherDashboard>>(){});
		parseModel();
	}

	private void parseModel() throws IOException {



		for (GrapherDashboard grapherDashboard : grapherDashboards) {
			GrapherDashboardConverter grapherDashboardConverter = new GrapherDashboardConverter();
			grapherDashboardConverter.init(properties);
			grapherDashboardConverter.parse(grapherDashboard.getData());
			grapherDashboard.setConverter(grapherDashboardConverter);
		}
	}

	@Override
	public List convert() {

		List models = new ArrayList();

		for (GrapherDashboard grapherDashboard : grapherDashboards) {
			if (grapherDashboard.getConverter() != null) {
				List dashboardModels = grapherDashboard.getConverter().convert();

				int count = 0;
				String name = grapherDashboard.getName();
				String url = name.toLowerCase().replaceAll("[^a-zA-Z0-9_\\-]", "-");
				for (Object model : dashboardModels) {
					if (model instanceof Dashboard) {
						Dashboard dashboard = (Dashboard) model;
						if (count == 0) {
							dashboard.setUrl(url);
							dashboard.setName(name);
						} else {
							dashboard.setUrl(url + "-" + count);
							dashboard.setName(name + " - " + count);
						}
						count++;
					}
				}

				models.addAll(dashboardModels);
			}
		}

		return models;
	}
}
