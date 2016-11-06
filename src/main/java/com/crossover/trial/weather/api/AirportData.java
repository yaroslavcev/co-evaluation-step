package com.crossover.trial.weather.api;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Basic airport information.
 *
 * @author code test administrator
 */
public class AirportData {

    /** the three letter IATA code */
    private final String iata;

    /** latitude value in degrees */
    private final double latitude;

    /** longitude value in degrees */
    private final double longitude;

    public AirportData(String iata, double latitude, double longitude) {
        this.iata = iata;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getIata() {
        return iata;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((iata == null) ? 0 : iata.hashCode());
        long temp;
        temp = Double.doubleToLongBits(latitude);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AirportData other = (AirportData) obj;
        if (iata == null) {
            if (other.iata != null)
                return false;
        } else if (!iata.equals(other.iata))
            return false;
        if (Double.doubleToLongBits(latitude) != Double.doubleToLongBits(other.latitude))
            return false;
        if (Double.doubleToLongBits(longitude) != Double.doubleToLongBits(other.longitude))
            return false;
        return true;
    }


    public static class AirportDataBuilder {
        String iata;

        double latitude;

        double longitude;
        
        public AirportDataBuilder() {
            
        }

        public AirportDataBuilder setIata(String iata) {
            this.iata = iata;
            return this;
        }

        public AirportDataBuilder setLatitude(double latitude) {
            this.latitude = latitude;
            return this;
        }

        public AirportDataBuilder setLongitude(double longitude) {
            this.longitude = longitude;
            return this;
        }
        
        public AirportData build() {
            if (StringUtils.isBlank(iata)) {
                throw new IllegalArgumentException("IATA code can not be empty for airport");
            }
            
            if (iata.length() != 3) {
                throw new IllegalArgumentException("IATA code must have three letters");
            }
            
            if (latitude > 90 || latitude < -90) {
                throw new IllegalArgumentException("Latitude should be in range [-90, 90]");
            }
            
            if (longitude > 180 || longitude < -180) {
                throw new IllegalArgumentException("Longitude should be in range [-180, 180]");
            }
            
            return new AirportData(iata, latitude, longitude);
        }
    }
}
