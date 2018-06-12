package com.wavefront.labs.convert.converter.grafana.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;


@JsonIgnoreProperties({"__requires", "editable", "gnetId", "graphTooltip", "hideControls",
		"id", "links", "refresh", "schemaVersion", "style", "tags",
		"templating", "time", "timepicker", "timezone", "version"})
public class GrafanaDashboard {

	@JsonProperty("__inputs")
	private ArrayList<HashMap<String, String>> inputs;


	private HashMap<String, ArrayList> annotations;
	private ArrayList<GrafanaDashboardRow> rows;
	private String title;

	public ArrayList<HashMap<String, String>> getInputs() {
		return inputs;
	}

	public void setInputs(ArrayList<HashMap<String, String>> inputs) {
		this.inputs = inputs;
	}

	public HashMap<String, ArrayList> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(HashMap<String, ArrayList> annotations) {
		this.annotations = annotations;
	}

	public ArrayList<GrafanaDashboardRow> getRows() {
		return rows;
	}

	public void setRows(ArrayList<GrafanaDashboardRow> rows) {
		this.rows = rows;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
