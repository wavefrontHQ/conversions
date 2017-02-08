package com.wavefront.labs.convert.input.rrd.models.graph;

import com.wavefront.labs.convert.input.rrd.RRDContext;

public class Stack extends Area {

	public Stack(String line, RRDContext context) {
		super(line, context);
	}

	@Override
	public void process() {
		stacked = true;
		super.process();
	}
}
