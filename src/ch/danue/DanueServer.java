package ch.danue;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class DanueServer implements Runnable {
	// Hey Danue FIXME broadcastingmaessig
	private DanueServerThread client[] = new DanueServerThread[3];
	private ServerSocket server = null;
	private Thread thread = null;
	private int totalClient = 0;

	public DanueServer(int port) {
		try {
			System.out
					.println("Binding to port " + port + ", please wait  ...");
			this.server = new ServerSocket(port);
			System.out
					.println("Server started: " + server + " on port " + port);
			start();
		} catch (IOException ioe) {
			System.out.println("Error port binding:: " + port + ": "
					+ ioe.getMessage());
		}
	}

	public void run() {
		while (this.thread != null) {
			try {
				addClient(this.server.accept());
			} catch (IOException ioe) {
				System.out.println("Server accept error: " + ioe);
				stop();
			}
		}
	}

	public void start() {
		if (this.thread == null) {
			this.thread = new Thread(this);
			this.thread.start();
		}
	}

	@SuppressWarnings("deprecation")
	public void stop() {
		if (this.thread != null) {
			this.thread.stop();
			this.thread = null;
		}
	}

	/**
	 * Hey Danue FIXME broadcastingmaessig
	 * 
	 * @param clientId
	 * @return int
	 */
	private int clientLookup(int clientId) {
		for (int i = 0; i < totalClient; i++)
			if (client[i].getServerId() == clientId)
				return i;
		return -1;
	}

	/**
	 * Hey Danue FIXME broadcastingmaessig
	 * 
	 * @param clientId
	 * @param input
	 */
	public synchronized void handle(int clientId, String input) {
		if (input.equals(".stopp")) {
			client[clientLookup(clientId)].send("stopp");
			removeClient(clientId);
		} else
			for (int i = 0; i < totalClient; i++)
				client[i].send(clientId + ": " + input);
	}

	// Hey Danue FIXME broadcastingmaessig
	private void addClient(Socket socket) {
		if (totalClient < client.length) {
			System.out.println("Client accepted: " + socket);
			try {
				client[totalClient] = new DanueServerThread(this, socket);
				client[totalClient].open();
				client[totalClient].start();
				totalClient++;
			} catch (IOException e) {
				System.out.println("Error opening thread: " + e);
			}
		}
	}

	// Hey Danue FIXME broadcastingmaessig
	@SuppressWarnings("deprecation")
	public synchronized void removeClient(int id) {
		int pos = clientLookup(id);
		if (pos >= 0) {
			DanueServerThread danueServerThread = client[pos];
			System.out.println("Removing client thread " + id + " at " + pos);
			if (pos < totalClient - 1)
				for (int i = pos + 1; i < totalClient; i++)
					client[i - 1] = client[i];
			totalClient--;
			try {
				danueServerThread.close();
			} catch (IOException ioe) {
				System.out.println("Error closing thread: " + ioe);
			}
			danueServerThread.stop();
		}
	}

	public static void main(String args[]) {
		@SuppressWarnings("unused")
		DanueServer server = null;
		if (args.length != 1)
			System.out.println("Usage: java DanueServer port");
		else
			server = new DanueServer(Integer.parseInt(args[0]));
	}
}