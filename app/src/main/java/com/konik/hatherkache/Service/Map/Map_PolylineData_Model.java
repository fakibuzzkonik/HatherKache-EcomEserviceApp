package com.konik.hatherkache.Service.Map;

import com.google.android.gms.maps.model.Polyline;
        import com.google.maps.model.DirectionsLeg;

public class Map_PolylineData_Model {

    private Polyline polyline;
    private DirectionsLeg leg;

    public Map_PolylineData_Model(Polyline polyline, DirectionsLeg leg) {
        this.polyline = polyline;
        this.leg = leg;
    }

    public Polyline getPolyline() {
        return polyline;
    }

    public void setPolyline(Polyline polyline) {
        this.polyline = polyline;
    }

    public DirectionsLeg getLeg() {
        return leg;
    }

    public void setLeg(DirectionsLeg leg) {
        this.leg = leg;
    }

    @Override
    public String toString() {
        return "Map_PolylineData_Model{" +
                "polyline=" + polyline +
                ", leg=" + leg +
                '}';
    }
}