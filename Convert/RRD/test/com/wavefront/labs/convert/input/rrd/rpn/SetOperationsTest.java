package com.wavefront.labs.convert.input.rrd.rpn;

import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Deque;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SetOperationsTest {
	@Test
	void rev() {
		Deque<String> test = new ArrayDeque();
		test.push("1");
		test.push("5");
		test.push("10");

		SetOperations.rev(test);

		assertEquals("1", test.pop());
	}

}