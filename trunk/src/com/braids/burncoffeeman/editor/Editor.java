package com.braids.burncoffeeman.editor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.Timer;

import com.braids.burncoffeeman.common.AnimTilePhase;
import com.braids.burncoffeeman.common.Constants;

public class Editor extends JPanel {

	private boolean   showAreaBorders;
	private float     zoomRatio;
	private Rectangle selectedRectangle;

	private Color     primaryDrawColor;
	private Color     clearDrawColor;

	public Editor() {
		zoomRatio = 16;
		showAreaBorders = true;
		primaryDrawColor = Color.BLACK;
		clearDrawColor = new Color(0, 0, 0, 0);

		MouseAdapter mouseAdapter = new MouseAdapter() {

			private Point   selectionStart;
			private boolean lastPressWasDraw;
			private boolean lastPressWasSelectionStart;
			private boolean lastPressWasSelectionMove;

			public void mousePressed(MouseEvent e) {

				lastPressWasSelectionStart = false;
				lastPressWasSelectionMove = false;
				lastPressWasDraw = false;

				int onmask = MouseEvent.SHIFT_DOWN_MASK;
				if ((e.getModifiersEx() & (onmask)) == onmask) {
					int x = (int) (e.getX() / zoomRatio);
					int y = (int) (e.getY() / zoomRatio);
					selectionStart = new Point(x, y);
					if (e.getButton() == MouseEvent.BUTTON1) {
						lastPressWasSelectionStart = true;
						selectedRectangle = null;
						repaint();
					} else {
						lastPressWasSelectionMove = true;
					}
				} else {
					if (e.getButton() == MouseEvent.BUTTON1) {
						lastPressWasDraw = true;
					} else {
						lastPressWasDraw = false;
					}
					draw(e);
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (lastPressWasSelectionMove) {
					int x = (int) (e.getX() / zoomRatio);
					int y = (int) (e.getY() / zoomRatio);

					Point newPoint = new Point(x, y);
					if (newPoint.equals(selectionStart)) {
						moveSelection(selectedRectangle, newPoint);
						selectedRectangle = null;
						repaint();
					}
				}
			}

			private void draw(MouseEvent e) {
				int x = (int) (e.getX() / zoomRatio);
				int y = (int) (e.getY() / zoomRatio);
				if (lastPressWasSelectionStart) {
					selectedRectangle = new Rectangle(selectionStart, new Dimension(x - selectionStart.x + 1, y - selectionStart.y + 1));
					if (selectedRectangle.width <= 0 || selectedRectangle.height <= 0) {
						selectedRectangle = null;
					}
					repaint();
				} else if (lastPressWasSelectionMove) {
					// do nothing
				} else {
					if (lastPressWasDraw) {
						drawPixel(x, y, primaryDrawColor);
					} else {
						drawPixel(x, y, clearDrawColor);
					}
				}
			}

			public void mouseDragged(MouseEvent e) {
				draw(e);
			}
		};
		addMouseListener(mouseAdapter);
		addMouseMotionListener(mouseAdapter);

		new Timer(333, new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (selectedRectangle != null) {
					repaint();
				}
			}
		}).start();
	}

	public void drawPixel(int x, int y, Color color) {
		if (x >= 16 || x < 0 || y >= 16 || y < 0) {
			return;
		}
		if (y < EditorManager.getInstance().getCurrentHead().getHeight()) {
			EditorManager.getInstance().getCurrentHead().setPixel(x, y, color);
		} else if (y < EditorManager.getInstance().getCurrentHead().getHeight() + EditorManager.getInstance().getCurrentBody().getHeight()) {
			EditorManager.getInstance().getCurrentBody().setPixel(x, y - EditorManager.getInstance().getCurrentHead().getHeight(), color);
		} else {
			EditorManager.getInstance().getCurrentLeg().setPixel(x,
			        y - EditorManager.getInstance().getCurrentHead().getHeight() - EditorManager.getInstance().getCurrentBody().getHeight(), color);
		}
		repaint();
	}

	public Color getPixel(int x, int y) {
		if (x > 16 || x < 0 || y > 16 || y < 0) {
			return clearDrawColor;
		}
		if (y < EditorManager.getInstance().getCurrentHead().getHeight()) {
			return EditorManager.getInstance().getCurrentHead().getPixel(x, y);
		} else if (y < EditorManager.getInstance().getCurrentHead().getHeight() + EditorManager.getInstance().getCurrentBody().getHeight()) {
			return EditorManager.getInstance().getCurrentBody().getPixel(x, y - EditorManager.getInstance().getCurrentHead().getHeight());
		} else {
			return EditorManager.getInstance().getCurrentLeg().getPixel(x,
			        y - EditorManager.getInstance().getCurrentHead().getHeight() - EditorManager.getInstance().getCurrentBody().getHeight());
		}
	}

	private void moveSelection(Rectangle selRect, Point newPos) {
		if (newPos.x + selRect.width > 16 || newPos.y + selRect.height > 16) {
			// can't move
			return;
		}

		if (newPos.x < selRect.x) {
			if (newPos.y < selRect.y) {
				moveLeftUp(selRect, newPos);
			} else {
				moveLeftDown(selRect, newPos);
			}
		} else {
			if (newPos.y < selRect.y) {
				moveRightUp(selRect, newPos);
			} else {
				moveRightDown(selRect, newPos);
			}
		}
	}

	private void moveLeftUp(Rectangle selRect, Point newPos) {
		for (int y = 0; y < selRect.height; y++) {
			for (int x = 0; x < selRect.width; x++) {
				int oldX = x + selRect.x;
				int oldY = y + selRect.y;
				int newX = x + newPos.x;
				int newY = y + newPos.y;
				drawPixel(newX, newY, getPixel(oldX, oldY));
				drawPixel(oldX, oldY, clearDrawColor);
			}
		}
	}

	private void moveLeftDown(Rectangle selRect, Point newPos) {
		for (int y = selRect.height - 1; y >= 0; y--) {
			for (int x = 0; x < selRect.width; x++) {
				int oldX = x + selRect.x;
				int oldY = y + selRect.y;
				int newX = x + newPos.x;
				int newY = y + newPos.y;
				drawPixel(newX, newY, getPixel(oldX, oldY));
				drawPixel(oldX, oldY, clearDrawColor);
			}
		}
	}

	private void moveRightUp(Rectangle selRect, Point newPos) {
		for (int y = 0; y < selRect.height; y++) {
			for (int x = selRect.width - 1; x >= 0; x--) {
				int oldX = x + selRect.x;
				int oldY = y + selRect.y;
				int newX = x + newPos.x;
				int newY = y + newPos.y;
				drawPixel(newX, newY, getPixel(oldX, oldY));
				drawPixel(oldX, oldY, clearDrawColor);
			}
		}
	}

	private void moveRightDown(Rectangle selRect, Point newPos) {
		for (int y = selRect.height - 1; y >= 0; y--) {
			for (int x = selRect.width - 1; x >= 0; x--) {
				int oldX = x + selRect.x;
				int oldY = y + selRect.y;
				int newX = x + newPos.x;
				int newY = y + newPos.y;
				drawPixel(newX, newY, getPixel(oldX, oldY));
				drawPixel(oldX, oldY, clearDrawColor);
			}
		}
	}

	public Dimension getPreferredSize() {
		return new Dimension((int) (16 * zoomRatio), (int) (16 * zoomRatio));
	}

	public Dimension getSize() {
		return getPreferredSize();
	}

	public void paint(Graphics g) {

		for (int y = 0; y < 16 * 2; y++) {
			for (int x = 0; x < 16 * 2; x++) {
				if ((x + y) % 2 == 0) {
					g.setColor(Constants.TRANSPARENT_COLOR_1);
				} else {
					g.setColor(Constants.TRANSPARENT_COLOR_2);
				}
				g.fillRect((int) (x * zoomRatio / 2), (int) (y * zoomRatio / 2), (int) (zoomRatio / 2), (int) (zoomRatio / 2));
			}
		}

		int yOffset = 0;
		paintTile(g, EditorManager.getInstance().getCurrentHead(), yOffset);
		int prevYOffeset = yOffset;
		yOffset += EditorManager.getInstance().getCurrentHead().getHeight() * zoomRatio;
		if (showAreaBorders) {
			g.setColor(Color.BLACK);
			g.drawRect(0, prevYOffeset, (int) (16 * zoomRatio) - 1, yOffset - prevYOffeset - 1);
		}

		paintTile(g, EditorManager.getInstance().getCurrentBody(), yOffset);
		prevYOffeset = yOffset;
		yOffset += EditorManager.getInstance().getCurrentBody().getHeight() * zoomRatio;
		if (showAreaBorders) {
			g.setColor(Color.BLACK);
			g.drawRect(0, prevYOffeset, (int) (16 * zoomRatio) - 1, yOffset - prevYOffeset - 1);
		}

		paintTile(g, EditorManager.getInstance().getCurrentLeg(), yOffset);
		prevYOffeset = yOffset;
		yOffset += EditorManager.getInstance().getCurrentLeg().getHeight() * zoomRatio;
		if (showAreaBorders) {
			g.setColor(Color.BLACK);
			g.drawRect(0, prevYOffeset, (int) (16 * zoomRatio) - 1, yOffset - prevYOffeset - 1);
		}

		if (selectedRectangle != null) {
			Graphics2D g2 = (Graphics2D) g;
			float dashPhase = System.currentTimeMillis() / 200 % 200;
			Stroke s1 = new BasicStroke(2.0f, // Width
			        BasicStroke.CAP_SQUARE, // End cap
			        BasicStroke.JOIN_MITER, // Join style
			        10.0f, // Miter limit
			        new float[] { 8.0f, 8.0f }, // Dash pattern
			        dashPhase);

			Stroke s2 = new BasicStroke(2.0f, // Width
			        BasicStroke.CAP_SQUARE, // End cap
			        BasicStroke.JOIN_MITER, // Join style
			        10.0f);

			g2.setColor(Color.BLACK);
			g2.setStroke(s2);
			g2.drawRect((int) (selectedRectangle.x * zoomRatio), (int) (selectedRectangle.y * zoomRatio), (int) (selectedRectangle.width * zoomRatio),
			        (int) (selectedRectangle.height * zoomRatio));

			g2.setColor(Color.WHITE);
			g2.setStroke(s1);
			g2.drawRect((int) (selectedRectangle.x * zoomRatio), (int) (selectedRectangle.y * zoomRatio), (int) (selectedRectangle.width * zoomRatio),
			        (int) (selectedRectangle.height * zoomRatio));
		}
	}

	private void paintTile(Graphics g, AnimTilePhase tile, int yOffset) {

		for (int y = 0; y < tile.getHeight(); y++) {
			for (int x = 0; x < tile.getWidth(); x++) {

				Color color = tile.getPixel(x, y);
				if (color.equals(Constants.COLOR_KEY_OWN_COLOR_1)) {
					g.setColor(Color.RED);
					g.fillRect((int) (x * zoomRatio), (int) (y * zoomRatio + yOffset), (int) zoomRatio, (int) zoomRatio);
					g.setColor(Color.BLACK);
					for (int xs = 0; xs < zoomRatio; xs += 2) {
						g.drawLine(xs + (int) (x * zoomRatio), (int) (y * zoomRatio + yOffset), xs + (int) (x * zoomRatio),
						        (int) (y * zoomRatio + yOffset + zoomRatio));
					}
				} else if (color.equals(Constants.COLOR_KEY_OWN_COLOR_2)) {
					g.setColor(Color.YELLOW);
					g.fillRect((int) (x * zoomRatio), (int) (y * zoomRatio + yOffset), (int) zoomRatio, (int) zoomRatio);
					g.setColor(Color.BLACK);
					for (int ys = 0; ys < zoomRatio; ys += 2) {
						g.drawLine((int) (x * zoomRatio), ys + (int) (y * zoomRatio + yOffset), (int) (x * zoomRatio + zoomRatio), ys
						        + (int) (y * zoomRatio + yOffset));
					}
				} else {
					g.setColor(color);
					g.fillRect((int) (x * zoomRatio), (int) (y * zoomRatio + yOffset), (int) zoomRatio, (int) zoomRatio);
				}
			}
		}

	}

	public float getZoomRatio() {
		return this.zoomRatio;
	}

	public void setZoomRatio(float zoomRatio) {
		this.zoomRatio = zoomRatio;
	}

	public void setPrimaryDrawColor(Color primaryDrawColor) {
		this.primaryDrawColor = primaryDrawColor;
	}

	public Color getPrimaryDrawColor() {
		return primaryDrawColor;
	}

	public void animChanged() {
		repaint();
	}

}
