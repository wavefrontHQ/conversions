package com.wavefront.labs.utilities.metrics;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MetricsFetcher {

	private final int FETCH_SIZE = 50;

	private BufferedWriter writer;
	private String baseURL;
	private String token;
	private ObjectMapper mapper;
	private int totalMetrics = 0;

	private void start(String[] args) {

		long startTime = System.currentTimeMillis();
		int grandTotalMetrics = 0;

		System.out.println("Starting Metrics Fetcher...");

		String baseOutputPath = args[0];
		baseURL = args[1];
		token = args[2];

		try {
			mapper = new ObjectMapper();

			ArrayList<String> metrics = ((HashMap<String, ArrayList<String>>) mapper.readValue(new File("top-level-metrics.json"), HashMap.class)).get("metrics");

			for (String metric : metrics) {
				grandTotalMetrics += totalMetrics;
				totalMetrics = 0;

				System.out.println();
				System.out.println("Top Level Metric: " + metric);
				System.out.printf("%6d", 0);

				writer = new BufferedWriter(new FileWriter(new File(baseOutputPath + metric + "txt")));
				fetchMetrics(metric, "");
				writer.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		long elapsed = System.currentTimeMillis() - startTime;

		System.out.println();
		System.out.println("Finished Metrics Fetcher!");
		System.out.println("Metrics: " + grandTotalMetrics);
		System.out.println("Time: " + elapsed);
	}

	private void fetchMetrics(String query, String lastFound) throws IOException {

		if (lastFound.equals("")) {
			System.out.print(".");
		} else {
			System.out.print("_");
		}

		InputStream is = getHttpResponseStream(baseURL + "/chart/metrics/all?trie=true&q=" + query + "&p=" + lastFound + "&l=" + FETCH_SIZE);

		ArrayList<String> metrics;
		try {
			metrics = ((HashMap<String, ArrayList<String>>) mapper.readValue(is, HashMap.class)).get("metrics");
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		for (String metric : metrics) {
			if (!metric.startsWith("conveyor.PT1M.docker.conveyor-canary-app-")) {
				if (metric.endsWith(".")) {
					fetchMetrics(metric, "");
				} else {
					writer.write(metric);
					writer.newLine();
					totalMetrics++;

					if (totalMetrics % 50 == 0) {
						System.out.println();
						System.out.printf("%6d", totalMetrics);
					}
				}
			}
		}

		if (metrics.size() == FETCH_SIZE) {
			fetchMetrics(query, metrics.get(metrics.size() - 2));
		}

	}

	private InputStream getHttpResponseStream(String surl) {
		int attempts = 0;
		while (attempts < 5) {
			try {
				attempts++;
				URL url = new URL(surl);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setRequestMethod("GET");
				httpURLConnection.setRequestProperty("X-AUTH-TOKEN", token);

				int responseCode = httpURLConnection.getResponseCode();
				if (responseCode == 200) {
					return httpURLConnection.getInputStream();
				}
			} catch (IOException e) {
				System.out.println("ERROR: getHttpResponseStream: " + surl);
				System.out.println("Attempt #: " + attempts);
				e.printStackTrace();
			}
		}

		return null;
	}

	public static void main(String[] args) throws IOException {
		MetricsFetcher metricsFetcher = new MetricsFetcher();
		metricsFetcher.start(args);
	}

}
