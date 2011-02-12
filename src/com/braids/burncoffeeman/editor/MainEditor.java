package com.braids.burncoffeeman.editor;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.braids.burncoffeeman.common.Activity;
import com.braids.burncoffeeman.common.AnimTilePhaseType;
import com.braids.burncoffeeman.common.Direction;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class MainEditor {

	private JSpinner    spnPhase;
	private JComboBox   cbType;
	private JComboBox   cbDirection;
	private Editor      editor;
	private AnimPreview animPreview;
	private JComboBox   cbGroupHead;
	private JComboBox   cbGroupBody;
	private JComboBox   cbGroupLegs;

	public MainEditor() throws IOException {

		EditorManager.init();

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		CellConstraints cc = new CellConstraints();

		JFrame frame = new JFrame();

		editor = new Editor();

		JPanel pnlPalette = new JPanel();
		pnlPalette.setLayout(new FlowLayout());
		for (PaletteItem li : PaletteItem.createItems(editor)) {
			pnlPalette.add(li);
		}

		JPanel pnlCommands = new JPanel(new FlowLayout());
		JButton btnSaveAll = new JButton(new SaveAllAction());

		ItemListener switchAnimPhase = new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				switchAnimPhase();
			}
		};

		spnPhase = new JSpinner(new SpinnerNumberModel(1, 1, 1, 1));
		spnPhase.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				switchAnimPhase();
			}
		});

		ArrayList<Activity> lstAnimatedActivities = new ArrayList<Activity>();
		for (Activity li : Activity.values()) {
			if (li.hasOwnGfx) {
				lstAnimatedActivities.add(li);
			}
		}
		cbType = new JComboBox(lstAnimatedActivities.toArray());
		cbType.setSelectedItem(Activity.STANDING);
		cbType.addItemListener(switchAnimPhase);

		cbDirection = new JComboBox(Direction.values());
		cbDirection.setSelectedItem(Direction.LEFT);
		cbDirection.addItemListener(switchAnimPhase);

		pnlCommands.add(spnPhase);
		pnlCommands.add(cbType);
		pnlCommands.add(cbDirection);
		pnlCommands.add(new JSeparator(SwingConstants.VERTICAL));
		pnlCommands.add(btnSaveAll);

		cbGroupHead = new JComboBox(EditorManager.getInstance().getGroupListForHead().toArray());
		cbGroupHead.setSelectedItem("default");
		cbGroupHead.addItemListener(switchAnimPhase);

		cbGroupBody = new JComboBox(EditorManager.getInstance().getGroupListForBody().toArray());
		cbGroupBody.setSelectedItem("default");
		cbGroupBody.addItemListener(switchAnimPhase);

		cbGroupLegs = new JComboBox(EditorManager.getInstance().getGroupListForLegs().toArray());
		cbGroupLegs.setSelectedItem("default");
		cbGroupLegs.addItemListener(switchAnimPhase);

		animPreview = new AnimPreview();
		JPanel pnlActions = new JPanel(new FormLayout("p,2dlu,p,2dlu,p,2dlu,p", "2dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p"));
		pnlActions.add(new JSeparator(), cc.xywh(1, 1, 7, 1, CellConstraints.FILL, CellConstraints.DEFAULT));
		pnlActions.add(new JButton(new CopyHeadAction()), cc.xy(1, 2));
		pnlActions.add(new JButton(new PasteHeadAction()), cc.xy(3, 2));
		pnlActions.add(new JButton(new MirrorHeadAction()), cc.xy(5, 2));
		pnlActions.add(cbGroupHead, cc.xy(7, 2));

		pnlActions.add(new JSeparator(), cc.xywh(1, 3, 7, 1, CellConstraints.FILL, CellConstraints.DEFAULT));
		pnlActions.add(new JButton(new CopyBodyAction()), cc.xy(1, 4));
		pnlActions.add(new JButton(new PasteBodyAction()), cc.xy(3, 4));
		pnlActions.add(new JButton(new MirrorBodyAction()), cc.xy(5, 4));
		pnlActions.add(cbGroupBody, cc.xy(7, 4));

		pnlActions.add(new JSeparator(), cc.xywh(1, 5, 7, 1, CellConstraints.FILL, CellConstraints.DEFAULT));
		pnlActions.add(new JButton(new CopyLegAction()), cc.xy(1, 6));
		pnlActions.add(new JButton(new PasteLegAction()), cc.xy(3, 6));
		pnlActions.add(new JButton(new MirrorLegAction()), cc.xy(5, 6));
		pnlActions.add(cbGroupLegs, cc.xy(7, 6));

		pnlActions.add(new JSeparator(), cc.xywh(1, 7, 7, 1, CellConstraints.FILL, CellConstraints.DEFAULT));
		pnlActions.add(animPreview, cc.xywh(1, 8, 7, 1, CellConstraints.CENTER, CellConstraints.CENTER));

		pnlActions.add(new JSeparator(), cc.xywh(1, 9, 7, 1, CellConstraints.FILL, CellConstraints.DEFAULT));
		pnlActions.add(new JButton(new CopyAllAction()), cc.xy(1, 10));
		pnlActions.add(new JButton(new PasteAllAction()), cc.xy(3, 10));

		JPanel pnlCenter = new JPanel(new FormLayout("p,2dlu,p", "t:p:g"));
		pnlCenter.add(editor, cc.xy(1, 1));
		pnlCenter.add(pnlActions, cc.xy(3, 1));

		frame.setLayout(new FormLayout("l:p:g", "p,2dlu,p,2dlu,p"));

		frame.add(pnlCommands, cc.xy(1, 1));
		frame.add(pnlCenter, cc.xy(1, 3));
		frame.add(pnlPalette, cc.xy(1, 5));

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	private void switchAnimPhase() {

		Activity type = (Activity) cbType.getSelectedItem();
		Direction direction = (Direction) cbDirection.getSelectedItem();
		Integer phase = (Integer) spnPhase.getValue();

		int maxAnimPhases = type.getIterations();
		if (phase > maxAnimPhases) {
			phase = maxAnimPhases;
		}

		spnPhase.setModel(new SpinnerNumberModel((int) phase, 1, maxAnimPhases, 1));

		EditorManager em = EditorManager.getInstance();

		em.setCurrentHead(em.getCreateAnimTilePhase((String) cbGroupHead.getSelectedItem(), AnimTilePhaseType.HEAD, type, direction, phase));
		em.setCurrentBody(em.getCreateAnimTilePhase((String) cbGroupBody.getSelectedItem(), AnimTilePhaseType.BODY, type, direction, phase));
		em.setCurrentLeg(em.getCreateAnimTilePhase((String) cbGroupLegs.getSelectedItem(), AnimTilePhaseType.LEGS, type, direction, phase));
		editor.animChanged();
	}

	private class SaveAllAction extends AbstractAction {

		public SaveAllAction() {
			super("Save All");
		}

		public void actionPerformed(ActionEvent e) {
			EditorManager.getInstance().saveAll();
		}

	}

	private class CopyHeadAction extends AbstractAction {

		public CopyHeadAction() {
			super("Copy");
		}

		public void actionPerformed(ActionEvent e) {
			EditorManager.getInstance().copyHead();
		}
	}

	private class PasteHeadAction extends AbstractAction {

		public PasteHeadAction() {
			super("Paste");
		}

		public void actionPerformed(ActionEvent e) {
			EditorManager.getInstance().pasteHead();
			editor.animChanged();
		}
	}

	private class CopyBodyAction extends AbstractAction {

		public CopyBodyAction() {
			super("Copy");
		}

		public void actionPerformed(ActionEvent e) {
			EditorManager.getInstance().copyBody();
		}
	}

	private class PasteBodyAction extends AbstractAction {

		public PasteBodyAction() {
			super("Paste");
		}

		public void actionPerformed(ActionEvent e) {
			EditorManager.getInstance().pasteBody();
			editor.animChanged();
		}
	}

	private class CopyLegAction extends AbstractAction {

		public CopyLegAction() {
			super("Copy");
		}

		public void actionPerformed(ActionEvent e) {
			EditorManager.getInstance().copyLeg();
		}
	}

	private class PasteLegAction extends AbstractAction {

		public PasteLegAction() {
			super("Paste");
		}

		public void actionPerformed(ActionEvent e) {
			EditorManager.getInstance().pasteLeg();
			editor.animChanged();
		}
	}

	private class MirrorHeadAction extends AbstractAction {

		public MirrorHeadAction() {
			super("Mirror");
		}

		public void actionPerformed(ActionEvent e) {
			EditorManager.getInstance().mirrorHead();
			editor.animChanged();
		}
	}

	private class MirrorBodyAction extends AbstractAction {

		public MirrorBodyAction() {
			super("Mirror");
		}

		public void actionPerformed(ActionEvent e) {
			EditorManager.getInstance().mirrorBody();
			editor.animChanged();
		}
	}

	private class MirrorLegAction extends AbstractAction {

		public MirrorLegAction() {
			super("Mirror");
		}

		public void actionPerformed(ActionEvent e) {
			EditorManager.getInstance().mirrorLeg();
			editor.animChanged();
		}
	}

	private class CopyAllAction extends AbstractAction {

		public CopyAllAction() {
			super("Copy All");
		}

		public void actionPerformed(ActionEvent e) {
			EditorManager.getInstance().copyHead();
			EditorManager.getInstance().copyBody();
			EditorManager.getInstance().copyLeg();
		}
	}

	private class PasteAllAction extends AbstractAction {

		public PasteAllAction() {
			super("Paste All");
		}

		public void actionPerformed(ActionEvent e) {
			EditorManager.getInstance().pasteHead();
			EditorManager.getInstance().pasteBody();
			EditorManager.getInstance().pasteLeg();
			editor.animChanged();
		}
	}

	public static void main(String[] args) throws IOException {
		new MainEditor();

	}
}
