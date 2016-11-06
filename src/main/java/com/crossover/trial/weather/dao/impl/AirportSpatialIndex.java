package com.crossover.trial.weather.dao.impl;

import java.util.List;

import com.crossover.trial.weather.api.AirportData;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.geometry.Rectangle;

/**
 * Spatial index for airports. Must be always used under lock.
 * @author Ilya
 *
 */
public class AirportSpatialIndex {
    private RTree<AirportData, Point> rTree = RTree.create();
    
    /**
     * Add airport data to the spatial index. Must be always called under lock.
     * @param airport airport
     */
    public void add(AirportData airport) {
        rTree = rTree.add(airport, toPoint(airport));
    }
    
    /**
     * Remove airport from the spatial index. Must be always called under lock.
     * @param airport airport
     */
    public void remove(AirportData airport) {
        rTree = rTree.delete(airport, toPoint(airport));
    }
    
    private Point toPoint(AirportData airport) {
        return Geometries.pointGeographic(airport.getLongitude(), airport.getLatitude());
    }
    
    /**
     * Find all airports that are located in specified rectangle.
     * @param lowerLeftLatitudeInDegrees latitude in degrees
     * @param lowerLeftLongitudeInDegrees longitude in degrees
     * @param upperRightLatitudeInDegrees latitude in degrees
     * @param upperRightLongitudeInDegrees longitude in degrees
     * @return list of matching airports
     */
    public List<AirportData> findAllAirportInsideRectangle(double lowerLeftLatitudeInDegrees,
            double lowerLeftLongitudeInDegrees, double upperRightLatitudeInDegrees,
            double upperRightLongitudeInDegrees) {
        Rectangle r = Geometries.rectangleGeographic(lowerLeftLongitudeInDegrees, lowerLeftLatitudeInDegrees,
                upperRightLongitudeInDegrees, upperRightLatitudeInDegrees);  
        return rTree.search(r).map(e -> e.value()).toList().toBlocking().single(); 
    }
}
