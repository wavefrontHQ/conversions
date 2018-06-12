package com.wavefront.labs.convert.converter.datadog.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties({"autoscale", "status", "markers", "style", "group", "noMetricHosts", "scope", "noGroupHosts", "notes"})
public class DatadogGraphDefinition {

	private String viz;
	private String precision;
	private List<DatadogGraphRequest> requests;
	private DatadogYAxis yaxis;

	public String getViz() {
		return viz;
	}

	public void setViz(String viz) {
		this.viz = viz;
	}

	public String getPrecision() {
		return precision;
	}

	public void setPrecision(String precision) {
		this.precision = precision;
	}

	public List<DatadogGraphRequest> getRequests() {
		return requests;
	}

	public void setRequests(List<DatadogGraphRequest> requests) {
		this.requests = requests;
	}

	public DatadogYAxis getYaxis() {
		return yaxis;
	}

	public void setYaxis(DatadogYAxis yaxis) {
		this.yaxis = yaxis;
	}
}
