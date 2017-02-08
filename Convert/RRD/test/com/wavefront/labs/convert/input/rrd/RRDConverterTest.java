package com.wavefront.labs.convert.input.rrd;

import com.wavefront.rest.models.Chart;
import com.wavefront.rest.models.ChartSourceQuery;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

class RRDConverterTest {
	@Test
	void init() {

	}

	@Test
	void parse() {

	}

	@Test
	void parse1() {

	}

	@Test
	void convert() {

	}

	@Test
	void fullTest() throws IOException {
		RRDConverter rrdConverter = new RRDConverter();
		rrdConverter.parse(new File("response_times_def.txt"));

		List models = rrdConverter.convert();

		for (Object model : models) {
			if (model instanceof Chart) {
				Chart chart = (Chart) model;
				for (ChartSourceQuery query : chart.getSources()) {
					System.out.println(query.getName() + " ::: " + query.getQuery());
				}
			}
		}
	}

}