package com.wavefront.labs.convert.input.rrd.rpn;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Deque;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StackProcessingTest {
	Deque<String> queue;


	@BeforeEach
	void beforeEach() {
		queue = new ArrayDeque();
		queue.push("a");
		queue.push("b");
		queue.push("c");
		queue.push("d");
	}

	@Test
	void dup() {

		StackProcessing.dup(queue);

		assertEquals("d", queue.pop());
		assertEquals("d", queue.pop());
	}

	@Test
	void pop() {
		StackProcessing.pop(queue);

		assertEquals("c", queue.pop());
	}

	@Test
	void exc() {

		StackProcessing.exc(queue);

		assertEquals("c", queue.pop());
		assertEquals("d", queue.pop());

	}

	@Test
	void depth() {
		StackProcessing.depth(queue);

		assertEquals("4", queue.pop());
	}

	@Test
	void copy() {

		queue.push("2");

		StackProcessing.copy(queue);

		assertEquals("d", queue.pop());
		assertEquals("c", queue.pop());
		assertEquals("d", queue.pop());
		assertEquals("c", queue.pop());
	}

	@Test
	void index() {
		queue.push("3");

		StackProcessing.index(queue);

		assertEquals("b", queue.pop());
	}

	@Test
	void roll() {

	}

}