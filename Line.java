package sockets;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class Line extends PaintingPrimitive {

	private Point p1, p2;
	
	public Line(Color c, Point p1, Point p2) {
		super(c);
		this.p1 = p1;
		this.p2 = p2;
	}

	@Override
	protected void drawGeometry(Graphics g) {
		g.drawLine(p1.x, p1.y, p2.x, p2.y);
	}

}
