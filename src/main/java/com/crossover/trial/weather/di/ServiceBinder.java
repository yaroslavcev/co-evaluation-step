package com.crossover.trial.weather.di;

import javax.inject.Singleton;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import com.crossover.trial.weather.service.AirportService;
import com.crossover.trial.weather.service.AtmosphericInformationService;
import com.crossover.trial.weather.service.WeatherQueryStatisticService;
import com.crossover.trial.weather.service.impl.AirportServiceImpl;
import com.crossover.trial.weather.service.impl.AtmosphericInformationServiceImpl;
import com.crossover.trial.weather.service.impl.WeatherQueryStatisticServiceImpl;

/**
 * Binds services to implementations.
 * @author Ilya
 *
 */
public class ServiceBinder extends AbstractBinder {

    @Override
    protected void configure() {
		bind(AirportServiceImpl.class).in(Singleton.class).to(AirportService.class);
		bind(AtmosphericInformationServiceImpl.class).in(Singleton.class).to(AtmosphericInformationService.class);
		
		bind(WeatherQueryStatisticServiceImpl.class).in(Singleton.class).to(WeatherQueryStatisticService.class);
    }
}