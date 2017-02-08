package com.wavefront.labs.convert;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public interface Converter {

	void init(Properties properties);

	void parse(String content) throws IOException;
	void parse(File file) throws IOException;

	List convert();
}
