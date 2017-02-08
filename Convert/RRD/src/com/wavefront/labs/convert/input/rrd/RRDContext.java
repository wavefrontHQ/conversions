package com.wavefront.labs.convert.input.rrd;

import com.wavefront.labs.convert.input.rrd.models.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.stream.Stream;

public class RRDContext {

	private Properties properties;
	private ArrayList<Definition> definitions;
	private ArrayList<Graph> graphs;
	private HashMap<String, String> options;

	private HashMap<String, String> variables;

	public RRDContext() {
		properties = new Properties();
		definitions = new ArrayList();
		graphs = new ArrayList();
		options = new HashMap();
		variables = new HashMap();
	}

	public RRDContext(Properties properties) {
		this();
		this.properties = properties;
	}

	public void parse(String s) {
		new BufferedReader(new StringReader(s)).lines().forEach(this::parseLine);
	}

	public void parse(InputStream is) {
		new BufferedReader(new InputStreamReader(is)).lines().forEach(this::parseLine);
	}

	public void parse(File file) throws IOException {
		parse(file.toPath());
	}

	public void parse(Path path) throws IOException {
		try (Stream<String> stream = Files.lines(path)) {
			stream.forEach(this::parseLine);
		}
	}

	private void parseLine(String line) {
		Definition definition = null;

		if (line.startsWith("DEF:")) {
			definition = new Def(line);
			definitions.add(definition);

		} else if (line.startsWith("VDEF:")) {
			definition = new VDef(line);
			definitions.add(definition);

		} else if (line.startsWith("CDEF:")) {
			definition = new CDef(line);
			definitions.add(definition);

		} else if (line.startsWith("--")) {
			parseOptionLine(line);

		} else {
			graphs.add(new Graph(line, this));
		}

		if (definition != null) {
			variables.put(definition.getVariableName(), definition.calculate(this));
		}
	}

	private void parseOptionLine(String line) {
		String[] opts = line.split(" ");
		for (int i = 0; i < opts.length; i++) {
			if (opts[i].startsWith("--")) {
				String name = opts[i].substring(2);
				if (i + 1 < opts.length && !opts[i + 1].startsWith("--")) {
					options.put(name, opts[i + 1]);
					i++;
				} else {
					options.put(name, "");
				}
			}
		}
	}

	public Properties getProperties() {
		return properties;
	}

	public boolean hasVariable(String name) {
		return variables.containsKey(name);
	}

	public String getVariable(String name) {
		if (variables.containsKey(name)) {
			return variables.get(name);
		} else {
			return null;
		}
	}

	public HashMap<String, String> getVariables() {
		return variables;
	}

	public ArrayList<Definition> getDefinitions() {
		return definitions;
	}

	public ArrayList<Graph> getGraphs() {
		return graphs;
	}

	public HashMap<String, String> getOptions() {
		return options;
	}
}
