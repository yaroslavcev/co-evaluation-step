package com.crossover.trial.weather;

import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.http.server.*;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.crossover.trial.weather.di.DaoBinder;
import com.crossover.trial.weather.di.ServiceBinder;
import com.crossover.trial.weather.impl.RestWeatherCollectorEndpoint;
import com.crossover.trial.weather.impl.RestWeatherQueryEndpoint;

import java.io.IOException;
import java.net.URI;

import static java.lang.String.*;

/**
 * This main method will be use by the automated functional grader. You
 * shouldn't move this class or remove the main method. You may change the
 * implementation, but we encourage caution.
 *
 * @author code test administrator
 */
public class WeatherServer {
    private static final Logger LOG = LoggerFactory.getLogger(WeatherServer.class);
    
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
        LOG.info("Starting Weather App local testing server: {}", baseUrl);

        final ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.register(RestWeatherCollectorEndpoint.class);
        resourceConfig.register(RestWeatherQueryEndpoint.class);
        registerDaoBinder(resourceConfig);
        registerServiceBinder(resourceConfig);

        server = GrizzlyHttpServerFactory.createHttpServer(URI.create(baseUrl), resourceConfig, false);

        HttpServerProbe probe = new HttpServerProbe.Adapter() {
            @SuppressWarnings("rawtypes")
            public void onRequestReceiveEvent(HttpServerFilter filter, Connection connection, Request request) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Request received. Method [{}], URI [{}]", request.getMethod(), request.getRequestURI());
                }
            }
        };
        ServerConfiguration serverConfiguration = server.getServerConfiguration();
        
        //TODO add API for configuration of thread pools
        serverConfiguration.getMonitoringConfig().getWebServerConfig().addProbes(probe);
        serverConfiguration.setMaxPostSize(5 * 1024); //5K to prevent DoS attacks with big requests 
        serverConfiguration.setName("WeatherServer");
        
        // the autograder waits for this output before running automated tests,
        // please don't remove it
        server.start();
        System.out.println(format("Weather Server started.\n url=%s\n", baseUrl));
    }

    /**
     * Register DAO binders for injecting,
     * @param resourceConfig config to regidter
     */
    protected void registerDaoBinder(ResourceConfig resourceConfig) {
        resourceConfig.register(new DaoBinder());
    }

    /**
     * Register service binders for injecting,
     * @param resourceConfig config to regidter
     */
    protected void registerServiceBinder(ResourceConfig resourceConfig) {
        resourceConfig.register(new ServiceBinder());
    }

    public void stop() {
        LOG.info("Stopping Weather App local testing server: {}", baseUrl);
        if (server != null) {
            server.shutdownNow();
            server = null;
        }
        LOG.info("Weather server has been stopped");
    }

    public static void main(String[] args) {
        try {
            WeatherServer weatherServer = new WeatherServer();

            weatherServer.start();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                weatherServer.stop();
            }));

        } catch (IOException ex) {
            LOG.error("Exception on WeatherServer start", ex);
        }
    }
}
