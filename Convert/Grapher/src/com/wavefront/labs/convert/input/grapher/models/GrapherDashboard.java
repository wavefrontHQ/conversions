package com.wavefront.labs.convert.input.grapher.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavefront.labs.convert.input.grapher.GrapherDashboardConverter;

import java.util.Date;

public class GrapherDashboard {

	private int id;

	@JsonProperty("user_id")
	private String userId;

	private String name;
	private String data;

	@JsonProperty("created_at")
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private Date created;

	@JsonProperty("updated_at")
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private Date updated;

	private GrapherDashboardConverter converter;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public GrapherDashboardConverter getConverter() {
		return converter;
	}

	public void setConverter(GrapherDashboardConverter converter) {
		this.converter = converter;
	}
}
