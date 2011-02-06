package com.braids.burncoffeeman.server;

public class MainServer {

	private CommunicationServer comm;

	public MainServer() {
		comm = new CommunicationServer();
		new Thread(new Runnable() {

			public void run() {
				try {
					comm.execute();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}).start();
	}

	public static void main(String[] args) {
		new MainServer();
	}
}
