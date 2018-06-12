package com.wavefront.labs.convert;

public class Utils {

	public static String sluggify(String source) {
		return sluggify(source, "-");
	}

	public static String sluggify(String source, String slug) {
		return source.replaceAll("[^a-zA-Z0-9-]", slug).toLowerCase();
	}
}
