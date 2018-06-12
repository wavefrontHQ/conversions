package com.wavefront.labs.convert.converter.rrd;

import com.wavefront.rest.models.Chart;
import com.wavefront.rest.models.ChartSourceQuery;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class RRDConverterTest {

	@Test
	public void fullTest() throws IOException {
		RRDConverter rrdConverter = new RRDConverter();
		rrdConverter.parse(new File("test_resources/rrd/response_times_def.txt"));

		List models = rrdConverter.convert();

		for (Object model : models) {
			if (model instanceof Chart) {
				Chart chart = (Chart) model;
				for (ChartSourceQuery query : chart.getSources()) {
					System.out.println(query.getName() + " --> " + query.getQuery());
				}
			}
		}
	}

}