package ch.danue;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class DanueServerThread extends Thread {
	private DanueServer server = null;
	private Socket socket = null;
	private int serverId = -1;
	private DataInputStream streamIn = null;

	public DanueServerThread(DanueServer server, Socket socket) {
		super();
		this.server = server;
		this.socket = socket;
		this.serverId = socket.getPort();
	}

	public int getServerId() {
		return serverId;
	}

	public void run() {
		while (true) {
			try {
				System.out.println("[ Server Thread " + serverId
						+ " running ]:: " + streamIn.readUTF());
			} catch (IOException ioe) {
				System.out.println(serverId + " ERROR reading: "
						+ ioe.getMessage());
				stop();
			}
		}
	}

	public void open() throws IOException {
		this.streamIn = new DataInputStream(new BufferedInputStream(
				socket.getInputStream()));
	}

	public void close() throws IOException {
		if (this.socket != null)
			this.socket.close();
		if (this.streamIn != null)
			this.streamIn.close();
	}
}
