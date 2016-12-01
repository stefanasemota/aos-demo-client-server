package ch.danue;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class DanueServerThread extends Thread {
	private DanueServer server = null;
	private Socket socket = null;
	private int serverId = -1;
	private DataInputStream streamIn = null;
	private DataOutputStream streamOut = null;

	public DanueServerThread(DanueServer server, Socket socket) {
		super();
		this.server = server;
		this.socket = socket;
		this.serverId = socket.getPort();
	}

	public void send(String msg) {
		try {
			streamOut.writeUTF(msg);
			streamOut.flush();
		} catch (IOException ioe) {
			System.out
					.println(serverId + " ERROR sending: " + ioe.getMessage());
			server.removeClient(serverId);
			stop();
		}
	}

	public int getServerId() {
		return serverId;
	}

	public void open() throws IOException {
		this.streamIn = new DataInputStream(new BufferedInputStream(
				socket.getInputStream()));
		this.streamOut = new DataOutputStream(new BufferedOutputStream(
				socket.getOutputStream()));
	}

	public void run() {
		while (true) {
			try {
				server.handle(serverId, streamIn.readUTF());
			} catch (IOException ioe) {
				System.out.println(serverId + " ERROR reading: "
						+ ioe.getMessage());
				stop();
			}
		}
	}

	public void close() throws IOException {
		if (this.socket != null)
			this.socket.close();
		if (this.streamIn != null)
			this.streamIn.close();
		if (streamOut != null)
			streamOut.close();
	}
}
