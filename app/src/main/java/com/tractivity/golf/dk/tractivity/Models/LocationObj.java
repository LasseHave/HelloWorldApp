package com.tractivity.golf.dk.tractivity.Models;

import android.location.Location;


public class LocationObj extends Location {

    public LocationObj(Location location)
    {
        super(location);
    }

    @Override
    public float getSpeed() {
        return super.getSpeed() * 3.6f;
    }



}
