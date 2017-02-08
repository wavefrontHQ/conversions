package com.wavefront.labs.convert;

import java.util.Properties;

public class DefaultExpressionBuilder implements ExpressionBuilder {

	protected Properties properties;

	@Override
	public void init(Properties properties) {
		this.properties = properties;
	}

	@Override
	public String buildMetricName(String orig) {
		return orig.replaceAll("[^a-zA-Z0-9_,./\\-]", "-");
	}

	@Override
	public String buildExpression(Object data) {
		return "ts(\"" + buildMetricName(data.toString()) + "\")";
	}
}
