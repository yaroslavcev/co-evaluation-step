package com.crossover.trial.weather.di;

import javax.inject.Singleton;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import com.crossover.trial.weather.dao.AirportDao;
import com.crossover.trial.weather.dao.impl.AirportDaoInMemoryImpl;

public class DaoBinder extends AbstractBinder {

    @Override
    protected void configure() {
        bind(AirportDaoInMemoryImpl.class).in(Singleton.class).to(AirportDao.class);
    }
}