package com.wavefront.labs.convert.converter.grapher;

import com.wavefront.labs.convert.writer.WavefrontPublisher;
import com.wavefront.rest.models.Dashboard;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class GrapherConverterTest {

	@Test
	public void fullTest() throws IOException {
		Properties properties = new Properties();
		properties.load(new FileReader(new File("grapher.properties")));

		GrapherConverter grapherConverter = new GrapherConverter();
		grapherConverter.init(properties);
		grapherConverter.parseDashboards(new File("test_resources/grapher/lazlo_dashboards.json"));

		List models = grapherConverter.convertDashboards();

		ArrayList toGenerate = new ArrayList();
		Dashboard dashboard = (Dashboard) models.get(1);
		dashboard.setUrl("zzz-convert-" + dashboard.getUrl());
		dashboard.setName("zzz CONVERT " + dashboard.getName());
		toGenerate.add(dashboard);
		//toGenerate.add(models.get(1));

		WavefrontPublisher wavefrontGenerator = new WavefrontPublisher();
		wavefrontGenerator.init(properties);
		//wavefrontGenerator.generate(toGenerate);
	}

}