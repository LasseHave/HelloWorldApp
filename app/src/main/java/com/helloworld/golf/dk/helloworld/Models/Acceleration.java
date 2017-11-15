package com.helloworld.golf.dk.helloworld.Models;

public class Acceleration{
	private float x;
	private float y;
	private float z;

	public Acceleration(float v, float v1, float v2) {
		// empty Ctor
	}

	public float getX() {
		return x;
	}
	public void setX(float x) {
		this.x = x;
	}
	public float getY() {
		return y;
	}
	public void setY(float y) {
		this.y = y;
	}
	public float getZ() {
		return z;
	}
	public void setZ(float z) {
		this.z = z;
	}
	@Override
	public String toString(){
		return "X: "+x+" Y: "+y+" Z: "+z;
	}

	public double[] toDoubleArray(){
        double[] output = new double[3];
        output[0] = (double) getX();
        output[1] = (double) getY();
        output[2] = (double) getZ();

        return output;
    }
}

