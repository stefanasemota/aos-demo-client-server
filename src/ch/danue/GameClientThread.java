package ch.danue;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class GameClientThread extends Thread {
	private Socket socket = null;
	private GameClient client = null;
	private DataInputStream streamIn = null;

	public GameClientThread(GameClient client, Socket socket) {
		this.client = client;
		this.socket = socket;
		open();
		start();
	}

	public void open() {
		try {
			this.streamIn = new DataInputStream(this.socket.getInputStream());
		} catch (IOException ioe) {
			System.out.println("Error getting input stream: " + ioe);
			client.stop();
		}
	}

	public void close() {
		try {
			if (this.streamIn != null)
				this.streamIn.close();
		} catch (IOException ioe) {
			System.out.println("Error closing input stream: " + ioe);
		}
	}

	public void run() {
		while (true) {
			try {
				this.client.handle(this.streamIn.readUTF());
			} catch (IOException ioe) {
				System.out.println("Listening error: " + ioe.getMessage());
				this.client.stop();
			}
		}
	}
}
