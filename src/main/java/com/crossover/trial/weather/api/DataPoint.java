package com.crossover.trial.weather.api;

/**
 * A collected point, including some information about the range of collected values
 *
 * @author code test administrator
 */
public class DataPoint {
    DataPointType type;
    
    /** the last time this data was updated, in milliseconds since UTC epoch */
    private long lastUpdateTime;
    
    public double mean = 0.0;

    public int first = 0;

    public int second = 0;

    public int third = 0;

    public int count = 0;

    /** private constructor, use the builder to create this object */
    private DataPoint() { }

    protected DataPoint(int first, int second, int mean, int third, int count) {
        this.setFirst(first);
        this.setMean(mean);
        this.setSecond(second);
        this.setThird(third);
        this.setCount(count);
    }

    /** the mean of the observations */
    public double getMean() {
        return mean;
    }

    protected void setMean(double mean) { this.mean = mean; }

    /** 1st quartile -- useful as a lower bound */
    public int getFirst() {
        return first;
    }

    protected void setFirst(int first) {
        this.first = first;
    }

    /** 2nd quartile -- median value */
    public int getSecond() {
        return second;
    }

    protected void setSecond(int second) {
        this.second = second;
    }

    /** 3rd quartile value -- less noisy upper value */
    public int getThird() {
        return third;
    }

    protected void setThird(int third) {
        this.third = third;
    }

    /** the total number of measurements */
    public int getCount() {
        return count;
    }

    protected void setCount(int count) {
        this.count = count;
    }

    public DataPointType getType() {
        return type;
    }

    public void setType(DataPointType type) {
        this.type = type;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    static public class Builder {
        int first;
        int mean;
        int median;
        int last;
        int count;

        public Builder() { }

        public Builder withFirst(int first) {
            first= first;
            return this;
        }

        public Builder withMean(int mean) {
            mean = mean;
            return this;
        }

        public Builder withMedian(int median) {
            median = median;
            return this;
        }

        public Builder withCount(int count) {
            count = count;
            return this;
        }

        public Builder withLast(int last) {
            last = last;
            return this;
        }

        public DataPoint build() {
            return new DataPoint(this.first, this.mean, this.median, this.last, this.count);
        }
    }
}
