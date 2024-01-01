package com.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class Myservlet
 */
public class Myservlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Myservlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
		response.sendRedirect("index.html");

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub

		// API setup
		String apiKey = "a66030a74d3efb1ed367423518349b89";

		// Get the city from the form input
		String city = request.getParameter("userInput");

		// create the url for gthe Open weather api request
		String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey;

		try {
			URL url = new URL(apiUrl);

			// API integration
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");

			// reading the data from network
			InputStream inputStream = connection.getInputStream();
			InputStreamReader reader = new InputStreamReader(inputStream);
			System.out.println(reader);
			// want to store in string
			StringBuilder responseContent = new StringBuilder();

			// input from the reader
			Scanner sc = new Scanner(reader);
			while (sc.hasNext()) {
				responseContent.append(sc.nextLine());
			}
			sc.close();
//		System.out.println(responseContent);
			Gson gson = new Gson();
			JsonObject jsonObject = gson.fromJson(responseContent.toString(), JsonObject.class);

			// date and time
			long dateTimestamp = jsonObject.get("dt").getAsLong() * 1000;
			String date = new Date(dateTimestamp).toString();

			// Temperature
			double temperatureKelvin = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
			int temperatureCelsius = (int) (temperatureKelvin - 273.15);

			// humidity
			int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();

			// wind speed
			double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();

			// weather condition
			String weatherCondition = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main")
					.getAsString();

			// set the data as request attributes (for sending to the jsp page)
			request.setAttribute("date", date);
			request.setAttribute("city", city);
			request.setAttribute("temperature", temperatureCelsius);
			request.setAttribute("weatherCondition", weatherCondition);
			request.setAttribute("humidity", humidity);
			request.setAttribute("weatherData", responseContent.toString());

			connection.disconnect();
			System.out.println(city);
			System.out.println(temperatureCelsius);

		} catch (Exception e) {
			e.printStackTrace();
		}
		// forward the request to the weather.jsp page for rendering

		request.getRequestDispatcher("index.jsp").forward(request, response);

	}

}
