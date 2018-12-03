package com.stefanbrenner.droplet.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

import com.stefanbrenner.droplet.utils.Messages;

public class PreviewComponent extends JComponent {
	
	private final class MouseAdapterExtension extends MouseAdapter {
		
		int startX;
		int startY;
		
		private final PreviewMoveListener listener;
		
		public MouseAdapterExtension(final PreviewMoveListener listener) {
			this.listener = listener;
		}
		
		@Override
		public void mousePressed(final MouseEvent e) {
			startX = e.getX();
			startY = e.getY();
		}
		
		@Override
		public void mouseDragged(final MouseEvent e) {
			int endX = e.getX();
			int endY = e.getY();
			
			listener.moved(endX - startX, endY - startY);
			
			startX = endX;
			startY = endY;
		}
	}
	
	private static final long serialVersionUID = -3302237154031424831L;
	
	private static final double ZOOM_FACTOR = 1.2;
	private static final int ZOOM_MAX = 10000;
	private static final int ZOOM_MIN = 100;
	
	private Image image;
	
	private int w;
	private int h;
	private int x = 0;
	private int y = 0;
	
	// TODO: failsafe
	// TODO: loading indicator / dummy image
	// TODO: reset on configuration load
	
	public PreviewComponent() {
		addMouseWheelListener(e -> {
			if (e.getPreciseWheelRotation() < -0.1) {
				zoomIn();
			} else if (e.getPreciseWheelRotation() > 0.1) {
				zoomOut();
			}
		});
		MouseAdapterExtension myMouseAdapter = new MouseAdapterExtension(
				(final int diffX, final int diffY) -> move(diffX, diffY));
		addMouseListener(myMouseAdapter);
		addMouseMotionListener(myMouseAdapter);
	}
	
	@Override
	protected void paintComponent(final Graphics g) {
		if (image == null) {
			
			// draw dashed rectangle
			Graphics2D g2d = (Graphics2D) g.create();
			Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 9 }, 0);
			g2d.setStroke(dashed);
			g2d.setColor(Color.LIGHT_GRAY);
			g2d.drawRoundRect(50, 50, getWidth() - 100, getHeight() - 100, 20, 20);
			g2d.dispose();
			
			// draw string
			String text = Messages.getString("PreviewComponent.noImageToShow"); //$NON-NLS-1$
			g.setFont(new Font("Arial", Font.BOLD, 20));
			g.setColor(Color.LIGHT_GRAY);
			FontMetrics fm = g.getFontMetrics();
			int x = ((getWidth() - fm.stringWidth(text)) / 2);
			int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
			g.drawString(text, x, y);
			
			return;
		}
		g.drawImage(image, x, y, w, h, this);
	}
	
	public void setImage(final Image image) {
		this.image = image;
		
		fitTo();
	}
	
	public void zoomIn() {
		if (image == null) {
			return;
		}
		// prevent from zooming further in than ZOOM_MAX
		if (w > ZOOM_MAX || h > ZOOM_MAX) {
			return;
		}
		zoom(ZOOM_FACTOR);
	}
	
	public void zoomOut() {
		if (image == null) {
			return;
		}
		// prevent from zooming further out than ZOOM_MIN
		if (w < ZOOM_MIN || h < ZOOM_MIN) {
			return;
		}
		zoom(1 / ZOOM_FACTOR);
	}
	
	private void zoom(final double factor) {
		if (image == null) {
			return;
		}
		
		w *= factor;
		
		// to avoid rounding issues we use the original ratio to calculate
		// the new height
		double originalRatio = (double) image.getHeight(this) / image.getWidth(this);
		h = (int) (w * originalRatio);
		
		// move to center of current view
		x = (int) -(((-x + (getWidth() / 2)) * factor) - getWidth() / 2);
		y = (int) -(((-y + (getHeight() / 2)) * factor) - getHeight() / 2);
		
		repaint();
	}
	
	private void moveToCenter() {
		x = -((w - getWidth()) / 2);
		y = -((h - getHeight()) / 2);
		
		repaint();
	}
	
	public void fitTo() {
		if (image == null) {
			return;
		}
		
		int maxHeight = getHeight();
		int maxWidth = getWidth();
		
		int originalWidth = image.getWidth(this);
		int originalHeight = image.getHeight(this);
		
		double ratioWidth = (double) maxWidth / originalWidth;
		double ratioHeight = (double) maxHeight / originalHeight;
		double ratioMin = Math.min(ratioWidth, ratioHeight);
		
		w = (int) (originalWidth * ratioMin);
		h = (int) (originalHeight * ratioMin);
		
		x = y = 0;
		
		moveToCenter();
	}
	
	public void showOriginalSize() {
		if (image == null) {
			return;
		}
		
		w = image.getWidth(this);
		h = image.getHeight(this);
		
		moveToCenter();
	}
	
	public void clear() {
		this.image = null;
		repaint();
	}
	
	public void resized() {
		if (image == null) {
			repaint();
		} else {
			fitTo();
		}
	}
	
	@Override
	public void move(final int diffX, final int diffY) {
		x += diffX;
		y += diffY;
		
		repaint();
	}
	
}
