package com.crossover.trial.weather.di;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import com.crossover.trial.weather.dao.AirportDao;
import com.crossover.trial.weather.dao.WeatherDataPointDao;
import com.crossover.trial.weather.dao.impl.InMemoryStoreImpl;

/**
 * Binds DAO to implementation.
 * @author Ilya
 *
 */
public class DaoBinder extends AbstractBinder {
    @Override
    protected void configure() {
        InMemoryStoreImpl impl = new InMemoryStoreImpl();
        bind(impl).to(AirportDao.class);
        bind(impl).to(WeatherDataPointDao.class);
    }
}