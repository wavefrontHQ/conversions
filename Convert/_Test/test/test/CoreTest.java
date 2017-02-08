package test;

import com.wavefront.labs.convert.Convert;
import org.junit.jupiter.api.Test;

class CoreTest {
	@Test
	void testGrapher() {
		Convert convert = new Convert();
		convert.start(new String[]{"convert.properties", "../Grapher/test_resources/lazlo_dashboards.json"});
	}

}