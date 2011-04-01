package com.braids.burncoffeeman.server;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

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

		CellConstraints cc = new CellConstraints();
		JFrame frame = new JFrame("Burn Coffeeman Server");
		frame.setLayout(new FormLayout("2dlu,f:100dlu:g,2dlu", "2dlu,100dlu:g,2dlu"));

		JPanel pnlMain = new JPanel(new FormLayout("r:50dlu,2dlu,p:g", "p:g,2dlu,p:g,2dlu,p:g"));
		JButton btnStartMatch = new JButton(new StartMatchAction());
		JButton btnStopMatch = new JButton(new StopMatchAction());
		JButton btnPauseMatch = new JButton(new PauseMatchAction());

		pnlMain.add(btnStartMatch, cc.xy(3, 1));
		pnlMain.add(btnStopMatch, cc.xy(3, 3));
		pnlMain.add(btnPauseMatch, cc.xy(3, 5));

		frame.add(pnlMain, cc.xy(2, 2));

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		new MainServer();
	}

	private class StartMatchAction extends AbstractAction {

		public StartMatchAction() {
			super("Start match");
		}

		public void actionPerformed(ActionEvent e) {

		}

	}

	private class StopMatchAction extends AbstractAction {

		public StopMatchAction() {
			super("Stop match");
		}

		public void actionPerformed(ActionEvent e) {

		}

	}

	private class PauseMatchAction extends AbstractAction {

		public PauseMatchAction() {
			super("Pause match");
		}

		public void actionPerformed(ActionEvent e) {

		}

	}
}
