package com.braids.burncoffeeman.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import javax.net.ServerSocketFactory;

public class CommunicationServer {

	private static final int DEFAULT_PORT = 44905;
	public static boolean    shutdown;
	private ServerSocket     serverSocket;

	public void execute() throws Exception {

		int port = DEFAULT_PORT;

		try {
			serverSocket = ServerSocketFactory.getDefault().createServerSocket(port);
		} catch (IOException ex) {
			System.out.println("Port is not free");
			System.exit(-5);
		}

		GameManager.init();
		GameManager.getInstance().startMatch();

		while (!shutdown) {
			try {
				Socket socket = serverSocket.accept();
				GameManager.getInstance().addPlayer(socket);
			} catch (SocketTimeoutException ex) {
				shutdown = true;
			} catch (IOException ex) {
				shutdown = true;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public void shutdown() {
		shutdown = true;
		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException e) {}
		}
	}

}
