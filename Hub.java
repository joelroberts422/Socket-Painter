package sockets;

import java.awt.Color;
import java.awt.Point;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class Hub {

	public static void main(String[] args) {
		
		ArrayList<PaintingPrimitive> drawn = new ArrayList<>();
		ArrayList<TextMessage> spoken = new ArrayList<>();	
		ArrayList<ObjectInputStream> ois = new ArrayList<>();
		ArrayList<ObjectOutputStream> oos = new ArrayList<>();
		
		try {
			ServerSocket server = new ServerSocket(7000);
			ArrayList<Thread> listen = new ArrayList<>();
			ArrayList<CommThread> comms = new ArrayList<>();
			ObjectInputStream i;
			ObjectOutputStream o;
			int name = 0;
			
			while (true) {
				
				System.out.println("server open");
				Socket s = server.accept();
				System.out.println("socket " + name + " accepted");
				
				i = new ObjectInputStream(s.getInputStream());
				o = new ObjectOutputStream(s.getOutputStream());
				ois.add(i);
				oos.add(o);
				int index = oos.indexOf(o);
				System.out.println("made hub streams");
			
				oos.get(index).writeObject(drawn);
				oos.get(index).writeObject(spoken);
				
				CommThread temp = new CommThread(i, oos, comms, listen, drawn, spoken, index);
				comms.add(temp);
				listen.add(new Thread(temp));
				listen.get(index).start();
				System.out.println("running thread");
				++name;
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
