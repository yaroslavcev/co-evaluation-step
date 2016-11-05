package com.crossover.trial.weather;

import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.http.server.*;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;
import org.glassfish.grizzly.threadpool.ThreadPoolConfig;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import com.crossover.trial.weather.di.DaoBinder;
import com.crossover.trial.weather.di.ServiceBinder;
import com.crossover.trial.weather.impl.RestWeatherCollectorEndpoint;
import com.crossover.trial.weather.impl.RestWeatherQueryEndpoint;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.*;


/**
 * This main method will be use by the automated functional grader. You
 * shouldn't move this class or remove the main method. You may change the
 * implementation, but we encourage caution.
 *
 * @author code test administrator
 */
public class WeatherServer {

    private static final String BASE_URL = "http://localhost:9090/";
    
    private String baseUrl;
    private HttpServer server;
    
    public WeatherServer(String baseUrl) {
    	this.baseUrl = baseUrl;
    }
    
    public WeatherServer() {
    	this(BASE_URL);
    }
    
    public void start() throws IOException {
        System.out.println("Starting Weather App local testing server: " + baseUrl);

        final ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.register(RestWeatherCollectorEndpoint.class);
        resourceConfig.register(RestWeatherQueryEndpoint.class);
        registerDaoBinder(resourceConfig);
        registerServiceBinder(resourceConfig);

        server = GrizzlyHttpServerFactory.createHttpServer(URI.create(baseUrl), resourceConfig, false);

        HttpServerProbe probe = new HttpServerProbe.Adapter() {
            public void onRequestReceiveEvent(HttpServerFilter filter, Connection connection, Request request) {
                System.out.println(request.getRequestURI());
            }
        };
        server.getServerConfiguration().getMonitoringConfig().getWebServerConfig().addProbes(probe);

        // the autograder waits for this output before running automated tests, please don't remove it
        server.start();
        System.out.println(format("Weather Server started.\n url=%s\n", baseUrl));
    }
    
    protected void registerDaoBinder(ResourceConfig resourceConfig) {
    	resourceConfig.register(new DaoBinder());
    }
    
    protected void registerServiceBinder(ResourceConfig resourceConfig) {
    	resourceConfig.register(new ServiceBinder());
    }
    
    public void stop() {
    	if (server != null) {
    		server.shutdownNow();
    		server = null;
    	}
    }
    
    public static void main(String[] args) {
        try {
        	WeatherServer weatherServer = new WeatherServer();
        	
        	weatherServer.start();
        	
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            	weatherServer.stop();
            }));
            
        } catch (IOException ex) {
            Logger.getLogger(WeatherServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
