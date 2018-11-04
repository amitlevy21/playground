package com.sheena.playground.layout;

public class Location {
	
	private Double x;
	private Double y;
	
	public Location() {
		super();
		this.x = 0.0;
		this.y = 0.0;
	}

	public Location(Double x, Double y) {
		super();
		this.x = x;
		this.y = y;
	}

	public Double getX() {
		return x;
	}

	public void setX(Double x) {
		this.x = x;
	}

	public Double getY() {
		return y;
	}

	public void setY(Double y) {
		this.y = y;
	}

	@Override
	public String toString() {
		return "Location [x=" + x + ", y=" + y + "]";
	}
}
