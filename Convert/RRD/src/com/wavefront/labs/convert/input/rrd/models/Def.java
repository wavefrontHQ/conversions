package com.wavefront.labs.convert.input.rrd.models;

import com.wavefront.labs.convert.ExpressionBuilder;
import com.wavefront.labs.convert.input.rrd.RRDContext;
import com.wavefront.labs.convert.input.rrd.RRDExpressionBuilder;

public class Def extends Definition {

	private String rrdFile;
	private String dsName;
	private String CF;

	public Def(String line) {
		super(line);
	}

	@Override
	public String calculate(RRDContext context) {
		String[] parts = expression.split(":");

		rrdFile = stripQuotes(parts[0]);
		dsName = stripQuotes(parts[1]);
		CF = stripQuotes(parts[2]);

		for (int i = 3; i < parts.length; i++) {
			if (parts[i].startsWith("reduce=")) {
				CF = parts[i].split("=")[1];
			}
		}

		ExpressionBuilder expressionBuilder;
		String expressionBuilderClass = context.getProperties().getProperty("convert.expression.builder", "");
		if (!expressionBuilderClass.equals("")) {
			try {
				expressionBuilder = (ExpressionBuilder) Class.forName(expressionBuilderClass).newInstance();
			} catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
				e.printStackTrace();
				expressionBuilder = new RRDExpressionBuilder();
			}
		} else {
			expressionBuilder = new RRDExpressionBuilder();
		}

		expressionBuilder.init(context.getProperties());

		return expressionBuilder.buildExpression(this);
	}

	public String getRRDFile() {
		return rrdFile;
	}

	public String getDsName() {
		return dsName;
	}

	public String getCF() {
		return CF;
	}
}
