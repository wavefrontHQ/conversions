package com.wavefront.labs.convert;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

public interface Converter {

	void init(Properties properties);

	void parseDashboards(Object data) throws IOException;

	List convertDashboards();

	void parseAlerts(Object data) throws IOException;

	List convertAlerts();
}
