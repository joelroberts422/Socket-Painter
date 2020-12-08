package sockets;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class Circle extends PaintingPrimitive {

	private Point center, radiusPoint;
	
	public Circle(Color c, Point center, Point radiusPoint) {
		super(c);
		this.center = center;
		this.radiusPoint = radiusPoint;
	}

	@Override
	protected void drawGeometry(Graphics g) {
		int radius = (int) Math.abs(center.distance(radiusPoint));
		g.drawOval(center.x - radius, center.y - radius, radius*2, radius*2);
	}

}
