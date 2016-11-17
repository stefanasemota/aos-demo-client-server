package ch.danue;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class GameClient implements Runnable {
	private Socket socket = null;
	private Thread thread = null;
	private DataInputStream console = null;
	private DataOutputStream streamOut = null;
	private GameClientThread client = null;

	public GameClient(String serverName, int serverPort) {
		try {
			System.out.println("Establishing connection. Please wait ...");
			this.socket = new Socket(serverName, serverPort);
			System.out.println("Connected: " + socket + "\n");
			start();
		} catch (UnknownHostException uhe) {
			System.out.println("Host unknown: " + uhe.getMessage());
		} catch (IOException ioe) {
			System.out.println("Unexpected exception: " + ioe.getMessage());
		}
	}

	public void run() {
		while (this.thread != null) {
			try {
				this.streamOut.writeUTF(this.console.readLine());
				this.streamOut.flush();
			} catch (IOException ioe) {
				System.out.println("Sending error: " + ioe.getMessage());
				stop();
			}
		}
	}

	public void handle(String msg) {
		if (msg.equals(".bye")) {
			System.out.println("Good bye. Press RETURN to exit ...");
			stop();
		} else
			System.out.println(msg);
	}

	public void start() throws IOException {
		this.console = new DataInputStream(System.in);
		this.streamOut = new DataOutputStream(socket.getOutputStream());
		if (this.thread == null) {
			this.client = new GameClientThread(this, socket);
			this.thread = new Thread(this);
			this.thread.start();
		}
	}

	public void stop() {
		if (this.thread != null) {
			this.thread.stop();
			this.thread = null;
		}
		try {
			if (this.console != null)
				this.console.close();
			if (this.streamOut != null)
				this.streamOut.close();
			if (this.socket != null)
				this.socket.close();
		} catch (IOException ioe) {
			System.out.println("Error closing ...");
		}
		this.client.close();
		this.client.stop();
	}

	public static void main(String args[]) {
		GameClient client = null;
		if (args.length != 2)
			System.out.println("Usage: java ChatClient host port");
		else
			client = new GameClient(args[0], Integer.parseInt(args[1]));
	}
}
