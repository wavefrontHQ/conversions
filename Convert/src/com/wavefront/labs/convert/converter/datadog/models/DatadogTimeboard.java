package com.wavefront.labs.convert.converter.datadog.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties({"read_only", "created", "created_by", "modified"})
public class DatadogTimeboard {

	private List<DatadogGraph> graphs;
	@JsonProperty("template_variables")
	private List<DatadogTemplateVariable> templateVariables;
	private String description;
	private String title;
	private int id;

	public List<DatadogGraph> getGraphs() {
		return graphs;
	}

	public void setGraphs(List<DatadogGraph> graphs) {
		this.graphs = graphs;
	}

	public List<DatadogTemplateVariable> getTemplateVariables() {
		return templateVariables;
	}

	public void setTemplateVariables(List<DatadogTemplateVariable> templateVariables) {
		this.templateVariables = templateVariables;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
