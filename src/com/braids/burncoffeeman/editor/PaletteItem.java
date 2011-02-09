package com.braids.burncoffeeman.editor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

import com.braids.burncoffeeman.common.Constants;

public class PaletteItem extends JToggleButton {

	private static final String[] COLORS = new String[] { "TRANSPARENT", "OWN1", "OWN2", "00 00 00", "FF FF FF", "68 37 2B", "70 A4 B2", "6F 3D 86",
	        "58 8D 43", "35 28 79", "B8 C7 6F", "6F 4F 25", "43 39 00", "9A 67 59", "44 44 44", "6C 6C 6C", "9A D2 84", "6C 5E B5", "95 95 95" };

	public static List<PaletteItem> createItems(Editor editor) {
		List<PaletteItem> result = new ArrayList<PaletteItem>();
		ButtonGroup group = new ButtonGroup();
		for (String color : COLORS) {
			PaletteItem li;
			if (color.equals("TRANSPARENT")) {
				li = new PaletteItem(editor, new Color(0, 0, 0, 0));
			} else if (color.equals("OWN1")) {
				li = new PaletteItem(editor, Constants.COLOR_KEY_OWN_COLOR_1);
			} else if (color.equals("OWN2")) {
				li = new PaletteItem(editor, Constants.COLOR_KEY_OWN_COLOR_2);
			} else {
				int r = Integer.parseInt(color.substring(0, 2), 16);
				int g = Integer.parseInt(color.substring(3, 5), 16);
				int b = Integer.parseInt(color.substring(6, 8), 16);
				li = new PaletteItem(editor, new Color(r, g, b, 255));
			}
			group.add(li);
			result.add(li);
		}

		return result;
	}

	public PaletteItem(Editor editor, Color color) {
		super(new Action(editor, color));

	}

	private static class Action extends AbstractAction {

		private Color  color;
		private Editor editor;

		public Action(Editor editor, Color color) {
			super(null, getIcon(color));
			this.editor = editor;
			this.color = color;
		}

		private static ImageIcon getIcon(Color color) {
			BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
			Graphics g = image.getGraphics();

			if (color.getAlpha() == 0) {

				for (int y = 0; y < 16 / 4; y++) {
					for (int x = 0; x < 16 / 4; x++) {
						if ((x + y) % 2 == 0) {
							g.setColor(Constants.TRANSPARENT_COLOR_1);
						} else {
							g.setColor(Constants.TRANSPARENT_COLOR_2);
						}
						g.fillRect(x * 4, y * 4, 4, 4);
					}
				}

			} else if (color.equals(Constants.COLOR_KEY_OWN_COLOR_1)) {
				g.setColor(Color.RED);
				g.fillRect(0, 0, 16, 16);
				g.setColor(Color.BLACK);
				for (int x = 0; x < 16; x += 2) {
					g.drawLine(x, 0, x, 15);
				}
			} else if (color.equals(Constants.COLOR_KEY_OWN_COLOR_2)) {
				g.setColor(Color.YELLOW);
				g.fillRect(0, 0, 16, 16);
				g.setColor(Color.BLACK);
				for (int y = 0; y < 16; y += 2) {
					g.drawLine(0, y, 15, y);
				}
			} else {
				g.setColor(color);
				g.fillRect(0, 0, 16, 16);
			}
			return new ImageIcon(image);
		}

		public void actionPerformed(ActionEvent e) {
			this.editor.setPrimaryDrawColor(this.color);
		}

	}
}
