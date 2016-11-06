package com.crossover.trial.weather.api;

/**
 * A collected point, including some information about the range of collected values
 *
 * @author code test administrator
 */
public class DataPoint {
    private final DataPointType type;
    /** the last time this data was updated, in milliseconds since UTC epoch */
    private final long lastUpdateTime;
    private final double mean;
    private final int first;
    private final int second;
    private final int third;
    private final int count;

    public DataPoint(DataPointType type, long lastUpdateTime, double mean, int first, int second, int third,
            int count) {
        super();
        this.type = type;
        this.lastUpdateTime = lastUpdateTime;
        this.mean = mean;
        this.first = first;
        this.second = second;
        this.third = third;
        this.count = count;
    }

    /** the mean of the observations */
    public double getMean() {
        return mean;
    }

    /** 1st quartile -- useful as a lower bound */
    public int getFirst() {
        return first;
    }

    /** 2nd quartile -- median value */
    public int getSecond() {
        return second;
    }

    /** 3rd quartile value -- less noisy upper value */
    public int getThird() {
        return third;
    }

    /** the total number of measurements */
    public int getCount() {
        return count;
    }

    public DataPointType getType() {
        return type;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + count;
        result = prime * result + first;
        result = prime * result + (int) (lastUpdateTime ^ (lastUpdateTime >>> 32));
        long temp;
        temp = Double.doubleToLongBits(mean);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + second;
        result = prime * result + third;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        DataPoint other = (DataPoint) obj;
        if (count != other.count)
            return false;
        if (first != other.first)
            return false;
        if (lastUpdateTime != other.lastUpdateTime)
            return false;
        if (Double.doubleToLongBits(mean) != Double.doubleToLongBits(other.mean))
            return false;
        if (second != other.second)
            return false;
        if (third != other.third)
            return false;
        if (type != other.type)
            return false;
        return true;
    }



    public static class Builder {
        DataPointType type;
        long lastUpdateTime;
        double mean;
        int first;
        int second;
        int third;
        int count;

        public Builder withType(DataPointType type) {
            this.type = type;
            return this;
        }
        
        public Builder withLastUpdate(long lastUpdateTime) {
            this.lastUpdateTime = lastUpdateTime;
            return this;
        }
        
        public Builder withFirst(int first) {
            this.first = first;
            return this;
        }

        public Builder withMean(double mean) {
            this.mean = mean;
            return this;
        }

        public Builder withSecond(int second) {
            this.second = second;
            return this;
        }

        public Builder withCount(int count) {
            this.count = count;
            return this;
        }

        public Builder withThird(int third) {
            this.third = third;
            return this;
        }

        public DataPoint build() {
            return new DataPoint(type, lastUpdateTime, mean, first, second, third, count);
        }
    }
}
