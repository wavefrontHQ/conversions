package com.wavefront.labs.convert;

import java.util.List;
import java.util.Properties;

public interface Generator {

	void init(Properties properties);

	void generate(List models);
}
