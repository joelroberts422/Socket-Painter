package sockets;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JPanel;

public class PaintingPanel extends JPanel {

	private ArrayList<PaintingPrimitive> todraw = new ArrayList<>();
	private Point p1, p2;
	
	public PaintingPanel() {
		this.setBackground(Color.WHITE);
	}
	
	public void addPrimitive(PaintingPrimitive p) {
		todraw.add(p);
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		for (PaintingPrimitive p : todraw)
			p.draw(g);
		
	}
}
