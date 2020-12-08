package sockets;

import java.io.Serializable;

public class TextMessage implements Serializable {

	private String name, message;
	
	public TextMessage(String name, String message) {
		this.name = name;
		this.message = message;
	}
	
	public String getName() {
		return name;
	}
	
	public String getMessage() {
		return message;
	}
}
