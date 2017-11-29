package com.helloworld.golf.dk.helloworld.Models;

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
