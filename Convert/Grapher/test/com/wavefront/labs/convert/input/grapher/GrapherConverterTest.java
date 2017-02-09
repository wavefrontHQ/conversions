package com.wavefront.labs.convert.input.grapher;

import com.wavefront.labs.convert.output.WavefrontGenerator;
import com.wavefront.rest.models.Dashboard;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

class GrapherConverterTest {
	@Test
	void fullTest() throws IOException {
		Properties properties = new Properties();
		properties.load(new FileReader(new File("grapher.properties")));

		GrapherConverter grapherConverter = new GrapherConverter();
		grapherConverter.init(properties);
		grapherConverter.parse(new File("test_resources/lazlo_dashboards.json"));

		List models = grapherConverter.convert();

		ArrayList toGenerate = new ArrayList();
		Dashboard dashboard = (Dashboard) models.get(1);
		dashboard.setUrl("zzz-convert-" + dashboard.getUrl());
		dashboard.setName("zzz CONVERT " + dashboard.getName());
		toGenerate.add(dashboard);
		//toGenerate.add(models.get(1));

		WavefrontGenerator wavefrontGenerator = new WavefrontGenerator();
		wavefrontGenerator.init(properties);
		wavefrontGenerator.generate(toGenerate);
	}

}