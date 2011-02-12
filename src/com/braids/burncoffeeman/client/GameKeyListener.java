package com.braids.burncoffeeman.client;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import com.braids.burncoffeeman.common.ClientInputModel;
import com.braids.burncoffeeman.common.ClientWantStartMatchModel;

public class GameKeyListener implements KeyListener {

	private final ClientInputModel    clientInputModel;
	private final CommunicationClient comm;

	public GameKeyListener(ClientInputModel clientInputModel, CommunicationClient comm) {
		this.clientInputModel = clientInputModel;
		this.comm = comm;
	}

	public void keyTyped(KeyEvent e) {}

	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			clientInputModel.setUpPress(false);
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			clientInputModel.setDownPress(false);
		} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			clientInputModel.setLeftPress(false);
		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			clientInputModel.setRightPress(false);
		} else if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
			clientInputModel.setAction1Press(false);
		} else if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
			clientInputModel.setAction2Press(false);
		}

		try {
			comm.outComm(clientInputModel.code());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void keyPressed(KeyEvent e) {
		try {
			if (e.getKeyCode() == KeyEvent.VK_UP && !clientInputModel.isUpPress()) {
				clientInputModel.setUpPress(true);
				comm.outComm(clientInputModel.code());
			} else if (e.getKeyCode() == KeyEvent.VK_DOWN && !clientInputModel.isDownPress()) {
				clientInputModel.setDownPress(true);
				comm.outComm(clientInputModel.code());
			} else if (e.getKeyCode() == KeyEvent.VK_LEFT && !clientInputModel.isLeftPress()) {
				clientInputModel.setLeftPress(true);
				comm.outComm(clientInputModel.code());
			} else if (e.getKeyCode() == KeyEvent.VK_RIGHT && !clientInputModel.isRightPress()) {
				clientInputModel.setRightPress(true);
				comm.outComm(clientInputModel.code());
			} else if (e.getKeyCode() == KeyEvent.VK_CONTROL && !clientInputModel.isAction1Press()) {
				clientInputModel.setAction1Press(true);
				comm.outComm(clientInputModel.code());
			} else if (e.getKeyCode() == KeyEvent.VK_SHIFT && !clientInputModel.isAction2Press()) {
				clientInputModel.setAction2Press(true);
				comm.outComm(clientInputModel.code());
			} else if (e.getKeyCode() == KeyEvent.VK_F1) {
				ClientWantStartMatchModel cwsm = new ClientWantStartMatchModel();
				// TODO
				cwsm.setName("dummy");
				comm.outComm(cwsm.code());
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
