package com.wavefront.labs.convert.converter.grafana.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties({"collapse", "height", "repeat", "repeatIteration", "repeatRowId", "titleSize"})
public class GrafanaDashboardRow {

	private ArrayList<GrafanaPanel> panels;

	private boolean showTitle;
	private String title;

	public ArrayList<GrafanaPanel> getPanels() {
		return panels;
	}

	public void setPanels(ArrayList<GrafanaPanel> panels) {
		this.panels = panels;
	}

	public boolean isShowTitle() {
		return showTitle;
	}

	public void setShowTitle(boolean showTitle) {
		this.showTitle = showTitle;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
