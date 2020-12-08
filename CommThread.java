package sockets;

import java.awt.Component;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketException;
import java.util.ArrayList;

import javax.swing.JTextArea;

public class CommThread implements Runnable {

	private int name;
	private ObjectInputStream ois;
    private ArrayList<ObjectOutputStream> oos;
    private ArrayList<PaintingPrimitive> hub;
    private ArrayList<TextMessage> hubmessages;
    private ArrayList<CommThread> comms;
    private ArrayList<Thread> listen;
    private PaintingPanel canvas;
    private JTextArea chat;
	
    // for use in Painter
    public CommThread(ObjectInputStream ois, PaintingPanel canvas, JTextArea chat) {
		this.ois = ois;
		this.oos = null;
		this.hub = null;
		this.hubmessages = null;
		this.comms = null;
		this.listen = null;
		this.name = -1;
		this.canvas = canvas;
		this.chat = chat;
	}
    
    // for use in Hub
	public CommThread(ObjectInputStream ois, ArrayList<ObjectOutputStream> oos, ArrayList<CommThread> comms, ArrayList<Thread> listen, ArrayList<PaintingPrimitive> hub, ArrayList<TextMessage> hubmessages, int name) {
		this.ois = ois;
		this.oos = oos;
		this.comms = comms;
		this.listen = listen;
		this.hub = hub;
		this.hubmessages = hubmessages;
		this.name = name;
		this.canvas = null;
		this.chat = null;
	}
	
	@Override
	public void run() {
		try {
			Object p;
			while (true) {
				p = ois.readObject();
				
				if (hub == null) { // if in Painter
					if (p instanceof PaintingPrimitive) {
						canvas.addPrimitive((PaintingPrimitive) p);
						canvas.paintComponent(canvas.getGraphics());
					} else {
						String s = ((TextMessage) p).getName() + ": " + ((TextMessage) p).getMessage();
						chat.append(s);
					}
				} 
				
				else { // else in Hub
					if (p instanceof PaintingPrimitive) {
						this.hubAdd((PaintingPrimitive) p);
						for (int i = 0; i < oos.size(); ++i) {
							if (i != name) // don't send it back from whence it came
								try {
									oos.get(i).writeObject(p);
								} catch (SocketException e) {
									this.removeOutputStream(i);
								}
						}
					} else {
						this.hubmessagesAdd((TextMessage) p); 
						for (int i = 0; i < oos.size(); ++i) {
							try {
								oos.get(i).writeObject(p);
							} catch (SocketException e) {
								this.removeOutputStream(i);
							}
						}
					}
				}
			}
			
		} catch (SocketException e) {
			if (hub == null)
				System.out.println("Painter thread dies");
			else
				System.out.println("Hub thread " + name + "dies");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public synchronized void hubAdd(PaintingPrimitive p) {
		hub.add(p);
	}
	
	public synchronized void hubmessagesAdd(TextMessage p) {
		hubmessages.add(p);
	}
	
	public synchronized void removeOutputStream(int i) {
		try {
			listen.get(i).join();
			oos.remove(i);
			comms.remove(i);
			listen.remove(i);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		for (int j = i; j < comms.size(); ++j)
			comms.get(j).decrementName();
	}
	
	public void decrementName() {
		--name;
	}
}
