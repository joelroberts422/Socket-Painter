package sockets;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

public class Painter extends JFrame implements ActionListener, MouseListener, MouseMotionListener {

	private static final long serialVersionUID = 1L;
	private static Socket s;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private ArrayList<PaintingPrimitive> buffer;
	private ArrayList<TextMessage> allchat;
	private Color c = Color.red;
	private String primitive = "line";
	private Point p1, p2;
	private String name = "anonymous";
	private JTextArea chattext;
	
	public Painter() {
		setSize(500,500);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		JPanel holder = new JPanel();
		holder.setLayout(new BorderLayout());
		
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new GridLayout(3,1));
		
		JButton redPaint = new JButton();
		redPaint.setBackground(Color.RED);
		redPaint.setOpaque(true);
		redPaint.setBorderPainted(false);
		redPaint.addActionListener(this);
		redPaint.setActionCommand("red");
		leftPanel.add(redPaint);
		
		JButton greenPaint = new JButton();
		greenPaint.setBackground(Color.GREEN);
		greenPaint.setOpaque(true);
		greenPaint.setBorderPainted(false);
		greenPaint.addActionListener(this);
		greenPaint.setActionCommand("green");
		leftPanel.add(greenPaint);
		
		JButton bluePaint = new JButton();
		bluePaint.setBackground(Color.BLUE);
		bluePaint.setOpaque(true);
		bluePaint.setBorderPainted(false);
		bluePaint.addActionListener(this);
		bluePaint.setActionCommand("blue");
		leftPanel.add(bluePaint);
		
		holder.add(leftPanel, BorderLayout.WEST);
		
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridLayout(1,2));
		
		JButton circle = new JButton("circle");
		circle.addActionListener(this);
		circle.setActionCommand("circle");
		topPanel.add(circle);
		
		JButton line = new JButton("line");
		line.addActionListener(this);
		line.setActionCommand("line");
		topPanel.add(line);
		
		holder.add(topPanel, BorderLayout.NORTH);

		PaintingPanel canvas = new PaintingPanel();
		canvas.addMouseListener(this);
		canvas.addMouseMotionListener(this);
		holder.add(canvas, BorderLayout.CENTER);

		JPanel chat = new JPanel();
		chat.setLayout(new BorderLayout());
		
		JPanel sendpanel = new JPanel();
		sendpanel.setLayout(new BorderLayout());
		
		JTextArea messageinput = new JTextArea();
		sendpanel.add(messageinput, BorderLayout.CENTER);
		chattext = messageinput;
		
		JButton sendbutton = new JButton("send");
		sendbutton.addActionListener(this);
		sendbutton.setActionCommand("sendmessage");
		sendpanel.add(sendbutton, BorderLayout.EAST);
		
		chat.add(sendpanel, BorderLayout.NORTH);
		
		JTextArea messages = new JTextArea("", 5, 1);
		messages.setEditable(false);
		JScrollPane scrollmessages = new JScrollPane(messages);
		
		
		chat.add(scrollmessages, BorderLayout.CENTER);
		
		holder.add(chat, BorderLayout.SOUTH);
		
		
		setContentPane(holder);
		setVisible(true);
		
		name = JOptionPane.showInputDialog("Enter your name");
		
		try {
			s = new Socket("localhost", 7000);
			System.out.println("connected");
			oos = new ObjectOutputStream(s.getOutputStream());
			System.out.println("made 1 stream");
			ois = new ObjectInputStream(s.getInputStream());
			System.out.println("made all streams");
			
			buffer = (ArrayList<PaintingPrimitive>) ois.readObject();
			allchat = (ArrayList<TextMessage>) ois.readObject();
			
			System.out.println("got board and chat");
			for (PaintingPrimitive buffershape : buffer) {
				canvas.addPrimitive(buffershape);
				canvas.paintComponent(canvas.getGraphics());
			}
			for (TextMessage t : allchat) {
				messages.append(t.getName() + ": " + t.getMessage());
			}
			
			buffer.clear();
			CommThread hublisten = new CommThread(ois, canvas, messages);
			Thread hub = new Thread(hublisten);
			hub.start();	
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent arg0) {}

	@Override
	public void mouseMoved(MouseEvent arg0) {}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		p1 = e.getPoint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		p2 = e.getPoint();	
		
		PaintingPrimitive shape = null;
		
		if (primitive == "line")
			shape = new Line(c, p1, p2);
		else if (primitive == "circle")
			shape = new Circle(c, p1, p2);
		
		((PaintingPanel) e.getComponent()).addPrimitive(shape);
		((PaintingPanel) e.getComponent()).paintComponent(e.getComponent().getGraphics());
	
		try {
			oos.writeObject(shape);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent a) {
		String command = a.getActionCommand();
		
		if (command == "red")
			this.c = Color.red;
		else if (command == "blue")
			this.c = Color.blue;
		else if (command == "green")
			this.c = Color.green;
		else if (command == "sendmessage") {
			String s = chattext.getText();
			TextMessage m = new TextMessage(name, s+"\n");
			
			try {
				oos.writeObject(m);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			chattext.setText("");
		} else
			this.primitive = command;
	}
	
	public static void main(String[] args) {
		Painter p = new Painter();
	}
}