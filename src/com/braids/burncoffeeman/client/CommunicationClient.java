package com.braids.burncoffeeman.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.CountDownLatch;

import com.braids.burncoffeeman.common.BombModel;
import com.braids.burncoffeeman.common.Constants;
import com.braids.burncoffeeman.common.GfxByteModel;
import com.braids.burncoffeeman.common.Helper;
import com.braids.burncoffeeman.common.LevelTileModel;
import com.braids.burncoffeeman.common.PacketMessageType;
import com.braids.burncoffeeman.common.PlayerInfoModel;
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

		private BufferedInputStream bis;

		public ProcessInput(Socket socket) throws IOException {
			bis = new BufferedInputStream(socket.getInputStream(), Constants.MAX_PACKET_SIZE);
			started.countDown();
		}

		private void readToFull(byte[] buffer, int size) throws IOException {
			int offset = 0;
			int read = 0;
			while (offset < size && (read = bis.read(buffer, offset, size - offset)) != -1) {
				offset += read;
			}
		}

		public void run() {
			try {
				byte[] packetSizeBuffer = new byte[3];
				byte[] packetBuffer = new byte[Constants.MAX_PACKET_SIZE];

				while (!shutdown) {
					readToFull(packetSizeBuffer, 2);
					statisticsInputBytes += 2;
					int packetSize = Helper.bytesToInt(packetSizeBuffer[0], packetSizeBuffer[1]);

					if (packetSize == 0xffff) {
						readToFull(packetSizeBuffer, 3);
						statisticsInputBytes += 3;
						packetSize = (int) ((packetSizeBuffer[0] & 0xFF) << 16) + ((packetSizeBuffer[1] & 0xFF) << 8) + (int) (packetSizeBuffer[2] & 0xFF);
					}

					readToFull(packetBuffer, packetSize);
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
								offset += processPlayerInfo(packetBuffer, offset);
								break;
							default:
								System.out.println("CommunicationClient invalid input: " + messageType);
						}
					}
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

		private int processPlayerInfo(byte[] bytes, int offset) {
			PlayerInfoModel data = new PlayerInfoModel();
			int resultOffsetIncrement = data.decode(bytes, offset);

			mainClient.setPlayerInfoModel(data);

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

		private int processAnimTileModel(byte[] bytes, int offset) throws IOException {
			GfxByteModel data = new GfxByteModel();
			int resultOffsetIncrement = data.decode(bytes, offset);

			switch (data.getType()) {
				case ANIM_TILE:
					mainClient.addAnimTileModel(data);
					break;
				case WALL:
					mainClient.setWallImage(data);
					break;
				case FIRE:
					mainClient.setFireImage(data);
					break;
				case BOMB:
					mainClient.setBombImage(data);
					break;
			}

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
