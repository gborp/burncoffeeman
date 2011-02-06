package com.braids.burncoffeeman.server;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;

import com.braids.burncoffeeman.common.Constants;

public class PlayerProcessOutput {

	private final Player         player;
	private CountDownLatch       started = new CountDownLatch(1);
	private BufferedOutputStream bos;
	private ByteBuffer           bbOutgoing;

	public PlayerProcessOutput(Socket socket, Player player) throws IOException {
		this.player = player;
		bbOutgoing = ByteBuffer.allocate(Constants.MAX_UNSENT_BUFFER);
		bos = new BufferedOutputStream(socket.getOutputStream(), Constants.MAX_PACKET_SIZE);

		new Thread(new OutComm()).start();

		started.countDown();
	}

	public synchronized boolean waitUntilReady() {
		try {
			started.await();
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
		return true;
	}

	public void append(byte[] data) {
		synchronized (bbOutgoing) {
			bbOutgoing.put((byte) (data.length / 256));
			bbOutgoing.put((byte) (data.length & 255));
			bbOutgoing.put(data);
		}
		synchronized (bbOutgoing) {
			bbOutgoing.notifyAll();
		}
	}

	private class OutComm implements Runnable {

		public void run() {
			try {
				while (true) {
					synchronized (bbOutgoing) {
						try {
							bbOutgoing.wait();
						} catch (InterruptedException ex) {
							ex.printStackTrace();
						}
					}
					byte[] outputData;
					synchronized (bbOutgoing) {
						outputData = new byte[bbOutgoing.position()];
						bbOutgoing.position(0);
						bbOutgoing.get(outputData);
						bbOutgoing.position(0);
					}

					bos.write(outputData);
					bos.flush();

				}
			} catch (Throwable ex) {
				GameManager.getInstance().playerDisconnected(player);
			}
		}

	}
}
