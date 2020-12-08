package sockets;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.io.Serializable;

public abstract class PaintingPrimitive implements Serializable {

	private Color c;
	
	public PaintingPrimitive(Color c) {
		this.c = c;
	}
	
	public final void draw(Graphics g) {
		g.setColor(this.c);
		drawGeometry(g);
	}
	
	protected abstract void drawGeometry(Graphics g);
}
