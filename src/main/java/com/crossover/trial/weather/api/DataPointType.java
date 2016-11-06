package com.crossover.trial.weather.api;

/**
 * The various types of data points we can collect.
 *
 * @author code test administrator
 */
public enum DataPointType {
    WIND {
        @Override
        public boolean check(DataPoint dp) {
            return dp.getMean() >= 0;
        }

        @Override
        public void appendToAtmosphericInformation(AtmosphericInformation ai, DataPoint wind) {
            ai.setWind(wind);
        }
    },
    TEMPERATURE {
        @Override
        public boolean check(DataPoint dp) {
            return dp.getMean() >= -50 && dp.getMean() < 100;
        }

        @Override
        public void appendToAtmosphericInformation(AtmosphericInformation ai, DataPoint temperature) {
            ai.setTemperature(temperature);
        }
    },
    HUMIDTY {
        @Override
        public boolean check(DataPoint dp) {
            return dp.getMean() >= 0 && dp.getMean() < 100;
        }

        @Override
        public void appendToAtmosphericInformation(AtmosphericInformation ai, DataPoint humidity) {
            ai.setHumidity(humidity);
        }
    },
    PRESSURE {
        @Override
        public boolean check(DataPoint dp) {
            return dp.getMean() >= 650 && dp.getMean() < 800;
        }

        @Override
        public void appendToAtmosphericInformation(AtmosphericInformation ai, DataPoint pressure) {
            ai.setPressure(pressure);
        }
    },
    CLOUDCOVER {
        @Override
        public boolean check(DataPoint dp) {
            return dp.getMean() >= 0 && dp.getMean() < 100;
        }

        @Override
        public void appendToAtmosphericInformation(AtmosphericInformation ai, DataPoint cloudCover) {
            ai.setCloudCover(cloudCover);
        }
    },
    PRECIPITATION {
        @Override
        public boolean check(DataPoint dp) {
            return dp.getMean() >= 0 && dp.getMean() < 100;
        }

        @Override
        public void appendToAtmosphericInformation(AtmosphericInformation ai, DataPoint precipitation) {
            ai.setPrecipitation(precipitation);
        }
    };

    /**
     * Check if data point have legitimate value.
     * @param dp data point to check
     * @return true if data is acceptable false otherwise
     */
    public abstract boolean check(DataPoint dp);

    /**
     * Append data of the data point to atmospheric information.
     * @param ai atmospheric information collecting data
     * @param dp data point to be added
     */
    public abstract void appendToAtmosphericInformation(AtmosphericInformation ai, DataPoint dp);
}
