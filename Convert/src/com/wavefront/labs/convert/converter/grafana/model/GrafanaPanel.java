package com.wavefront.labs.convert.converter.grafana.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.HashMap;

@JsonIgnoreProperties({"aliasColors", "bars", "fill", "id",
		"lines", "linewidth", "links", "nullPointMode",
		"pointradius", "points", "renderer", "seriesOverrides",
		"span", "steppedLine", "timeFrom", "timeShift", "tooltip", "xaxis"})
public class GrafanaPanel {

	private String datasource;
	private HashMap<String, Object> legend;
	private boolean percentage;
	private HashMap<String, Object> scopedVars;
	private boolean stack;
	private ArrayList<HashMap<String, Object>> targets;
	private ArrayList<Double> thresholds;
	private String title;
	private String type;
	private ArrayList<HashMap> yaxes;

	public String getDatasource() {
		return datasource;
	}

	public void setDatasource(String datasource) {
		this.datasource = datasource;
	}

	public HashMap<String, Object> getLegend() {
		return legend;
	}

	public void setLegend(HashMap<String, Object> legend) {
		this.legend = legend;
	}

	public boolean isPercentage() {
		return percentage;
	}

	public void setPercentage(boolean percentage) {
		this.percentage = percentage;
	}

	public HashMap<String, Object> getScopedVars() {
		return scopedVars;
	}

	public void setScopedVars(HashMap<String, Object> scopedVars) {
		this.scopedVars = scopedVars;
	}

	public boolean isStack() {
		return stack;
	}

	public void setStack(boolean stack) {
		this.stack = stack;
	}

	public ArrayList<HashMap<String, Object>> getTargets() {
		return targets;
	}

	public void setTargets(ArrayList<HashMap<String, Object>> targets) {
		this.targets = targets;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ArrayList<HashMap> getYaxes() {
		return yaxes;
	}

	public void setYaxes(ArrayList<HashMap> yaxes) {
		this.yaxes = yaxes;
	}

	public ArrayList<Double> getThresholds() {
		return thresholds;
	}

	public void setThresholds(ArrayList<Double> thresholds) {
		this.thresholds = thresholds;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
