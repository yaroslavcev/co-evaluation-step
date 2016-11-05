package com.crossover.trial.weather;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WeatherIntegrationTest {
	private static String BASE_URI = "http://localhost:9090";
	
	protected WeatherServer weatherServer;
	protected WeatherClient weatherClient;
	
	@Before
	public void startWeatherServer() throws IOException {
		weatherServer = new WeatherServer(BASE_URI);
		weatherServer.start();
		
		weatherClient = new WeatherClient(BASE_URI);//"http://ipv4.fiddler:9090");//
	}
	
	@Test
	public void runTest() {
	    weatherClient.pingQuery();
	    weatherClient.pingCollect();
	    weatherClient.addAirport("BOS", "10", "10");
	    weatherClient.populate("WIND", 1, 10, 5, 5, 100);
		//weatherClient.pingQuery();
		//weatherClient.pingQuery();
	}
	
	@After
	public void stopWeatherServer() {
		weatherServer.stop();
	    //while (true){}
	}
}
