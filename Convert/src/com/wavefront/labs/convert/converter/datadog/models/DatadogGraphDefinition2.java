package com.wavefront.labs.convert.converter.datadog.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties({"show_last_triggered","autoscale", "status", "markers", "style", "group", "no_metric_hosts", "scope", "no_group_hosts", "notes", "show_legend", "legend_size", "text_align", "custom_unit", "size_by", "tick_pos", "color_by_groups", "node_type", "show_tick", "compare_to", "xaxis", "color_by", "events", "tick_edge", "q", "group_by", "content", "font_size", "background_color", "precision", "layout_type", "title_size", "title_align", "color", "viz_type", "url", "time", "text", "alert_id", "tags_execution", "sizing", "query", "sort", "tags", "event_size", "margin", "count", "check", "hide_zero_counts", "grouping", "unit", "start", "color_preference", "display_format"})
public class DatadogGraphDefinition2 {

	private String type;
	private String title;
	private DatadogGraphRequests requests;
	private List<DatadogWidget> widgets;
	private DatadogYAxis yaxis;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public DatadogGraphRequests getRequests() { return requests; }

	public List<DatadogGraphRequest> getRequestsAsList() { return requests.getRequests(); }

	public void setRequests(DatadogGraphRequests requests) {
		this.requests = requests;
	}

	public List<DatadogWidget> getWidgets() { return widgets; }

	public void setWidgets(List<DatadogWidget> widgets) { this.widgets = widgets; }

	public DatadogYAxis getYaxis() { return yaxis; }

	public void setYaxis(DatadogYAxis yaxis) { this.yaxis = yaxis; }
}
