package com.wavefront.labs.convert;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class Convert {

	private Properties properties;

	public void start(String[] args) {
		try {
			properties = new Properties();
			properties.load(new FileReader(new File(args[0])));

			List models = doConvert(args);

			doGenerate(models);

		} catch (IOException | InstantiationException | ClassNotFoundException | IllegalAccessException e) {
			e.printStackTrace();
		}

	}

	private List doConvert(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, IOException {
		Converter converter = (Converter) Class.forName(properties.getProperty("convert.converter")).newInstance();
		converter.init(properties);

		String filename = null;
		if (args.length > 1) {
			filename = args[1];
		} else {
			filename = properties.getProperty("convert.file");
		}

		if (filename == null) {
			System.out.println("WARNING: No file to parse into converter has been specified.");

			converter.parse("");
		} else {
			converter.parse(new File(filename));
		}

		return converter.convert();
	}

	private void doGenerate(List models) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
		Generator generator = (Generator) Class.forName(properties.getProperty("convert.generator")).newInstance();
		generator.init(properties);
		generator.generate(models);
	}

	public static void main(String[] args) {

		Convert convert = new Convert();
		convert.start(args);

	}
}
