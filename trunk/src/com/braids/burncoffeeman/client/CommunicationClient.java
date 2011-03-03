package com.braids.burncoffeeman.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.CountDownLatch;

import com.braids.burncoffeeman.common.GfxByteModel;
import com.braids.burncoffeeman.common.BombModel;
import com.braids.burncoffeeman.common.Constants;
import com.braids.burncoffeeman.common.Helper;
import com.braids.burncoffeeman.common.LevelTileModel;
import com.braids.burncoffeeman.common.PacketMessageType;
import com.braids.burncoffeeman.common.PlayerModel;

public class CommunicationClient {

	private static final int     DEFAULT_PORT = 44905;
	private boolean              shutdown;
	private BufferedOutputStream bos;
	private CountDownLatch       started      = new CountDownLatch(1);
	private final MainClient     mainClient;
	private int                  statisticsOutputBytes;
	private int                  statisticsInputBytes;

	public CommunicationClient(MainClient mainClient) {
		this.mainClient = mainClient;
	}

	public void execute() throws Exception {

		int port = DEFAULT_PORT;
		String host = "localhost";

		Socket socket = null;
		try {
			socket = new Socket(host, port);
		} catch (IOException ex) {
			System.out.println("Port is not free or server is not running there");
			System.exit(-5);
		}

		bos = new BufferedOutputStream(socket.getOutputStream(), Constants.MAX_PACKET_SIZE);

		try {

			new Thread(new ProcessInput(socket)).start();

		} catch (SocketTimeoutException ex) {
			shutdown = true;
		} catch (IOException ex) {
			shutdown = true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public synchronized boolean waitUntilServerReady() {
		try {
			started.await();
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
		return true;
	}

	public synchronized void outComm(byte[] bytes) throws IOException {
		byte[] packetSizeBuffer = new byte[2];

		packetSizeBuffer[0] = (byte) (bytes.length / 256);
		packetSizeBuffer[1] = (byte) (bytes.length & 255);

		statisticsOutputBytes += 2 + bytes.length;

		bos.write(packetSizeBuffer);
		bos.write(bytes);
	}

	public synchronized void flush() throws IOException {
		bos.flush();
	}

	public void shutdown() {
		shutdown = true;
	}

	private class ProcessInput implements Runnable {

		private final Socket        socket;
		private BufferedInputStream bis;

		public ProcessInput(Socket socket) throws IOException {
			this.socket = socket;
			bis = new BufferedInputStream(socket.getInputStream(), Constants.MAX_PACKET_SIZE);
			started.countDown();
		}

		public void run() {
			try {
				byte[] packetSizeBuffer = new byte[2];
				byte[] packetBuffer = new byte[Constants.MAX_PACKET_SIZE];

				while (!shutdown) {
					bis.read(packetSizeBuffer);
					statisticsInputBytes += 2;

					int packetSize = Helper.bytesToInt(packetSizeBuffer[0], packetSizeBuffer[1]);
					bis.read(packetBuffer, 0, packetSize);
					statisticsInputBytes += packetSize;

					int offset = 0;
					while (offset < packetSize) {
						byte type = packetBuffer[offset];
						offset++;
						PacketMessageType messageType = PacketMessageType.values()[type];
						switch (messageType) {
							case LEVEL_TILE:
								offset += processLevelTile(packetBuffer, offset);
								break;
							case PLAYER_MODEL:
								offset += processPlayerModel(packetBuffer, offset);
								break;
							case BOMB:
								offset += processBombModel(packetBuffer, offset);
								break;
							case GFX_BYTE_MODEL:
								offset += processAnimTileModel(packetBuffer, offset);
								break;
							case PLAYER_INFO:
								// TODO
								break;
							default:
								System.out.println("CommunicationClient invalid input: " + messageType);
						}
					}
					mainClient.refreshDisplay();
				}

			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		private int processLevelTile(byte[] bytes, int offset) {
			LevelTileModel data = new LevelTileModel();
			int resultOffsetIncrement = data.decode(bytes, offset);

			mainClient.getLevelModel().setTile(data);

			return resultOffsetIncrement;
		}

		private int processPlayerModel(byte[] bytes, int offset) {
			PlayerModel data = new PlayerModel();
			int resultOffsetIncrement = data.decode(bytes, offset);

			mainClient.setPlayerModel(data);

			return resultOffsetIncrement;
		}

		private int processBombModel(byte[] bytes, int offset) {
			BombModel data = new BombModel();
			int resultOffsetIncrement = data.decode(bytes, offset);

			mainClient.setBombModel(data);

			return resultOffsetIncrement;
		}

		private int processAnimTileModel(byte[] bytes, int offset) {
			GfxByteModel data = new GfxByteModel();
			int resultOffsetIncrement = data.decode(bytes, offset);

			mainClient.addAnimTileModel(data);

			return resultOffsetIncrement;
		}

	}

	public void resetInputOutputStatistics() {
		statisticsInputBytes = 0;
		statisticsOutputBytes = 0;
	}

	public int getStatisticsOutputBytes() {
		return this.statisticsOutputBytes;
	}

	public int getStatisticsInputBytes() {
		return this.statisticsInputBytes;
	}

}
