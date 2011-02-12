package com.braids.burncoffeeman.server;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

import com.braids.burncoffeeman.common.ClientInputModel;
import com.braids.burncoffeeman.common.ClientWantStartMatchModel;
import com.braids.burncoffeeman.common.Constants;
import com.braids.burncoffeeman.common.PacketMessageType;
import com.braids.burncoffeeman.common.PlayerInfoModel;

public class PlayerProcessInput implements Runnable {

	private BufferedInputStream bis;
	private final Player        player;
	private CountDownLatch      started = new CountDownLatch(1);

	public PlayerProcessInput(Socket socket, Player player) throws IOException {
		this.player = player;
		bis = new BufferedInputStream(socket.getInputStream(), Constants.MAX_PACKET_SIZE);
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

	public void run() {
		try {
			byte[] packetSizeBuffer = new byte[2];
			byte[] packetBuffer = new byte[Constants.MAX_PACKET_SIZE];

			while (!CommunicationServer.shutdown) {
				int readCount = bis.read(packetSizeBuffer);
				if (readCount < 0) {
					CommunicationServer.shutdown = true;
					continue;
				}

				int packetSize = packetSizeBuffer[0] * 256 + packetSizeBuffer[1];
				bis.read(packetBuffer, 0, packetSize);

				int offset = 0;
				while (offset < packetSize) {
					byte type = packetBuffer[offset];
					offset++;
					PacketMessageType messageType = PacketMessageType.values()[type];
					switch (messageType) {
						case CLIENT_INPUT:
							offset += processClientInput(packetBuffer, offset);
							break;
						case PLAYER_INFO:
							offset += processPlayerInfo(packetBuffer, offset);
							break;
						case CLIENT_WANT_START_MATCH:
							offset += processClientWantStartMatch(packetBuffer, offset);
							break;
						default:
							System.out.println("ProcessInput.run() invalid input: " + messageType);
					}
				}

			}

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private int processClientWantStartMatch(byte[] bytes, int offset) {
		ClientWantStartMatchModel data = new ClientWantStartMatchModel();
		int resultOffsetIncrement = data.decode(bytes, offset);
		GameManager.getInstance().clientWantStartMatch();

		return resultOffsetIncrement;
	}

	private int processClientInput(byte[] bytes, int offset) {

		ClientInputModel data = new ClientInputModel();
		int resultOffsetIncrement = data.decode(bytes, offset);
		player.addClientInput(data);

		return resultOffsetIncrement;
	}

	private int processPlayerInfo(byte[] bytes, int offset) {

		PlayerInfoModel data = new PlayerInfoModel();
		int resultOffsetIncrement = data.decode(bytes, offset);
		player.setPlayerInfoModel(data);

		return resultOffsetIncrement;
	}

}
