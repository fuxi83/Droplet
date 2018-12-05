package com.stefanbrenner.droplet.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

final class PreviewMouseAdapter extends MouseAdapter {
	
	int startX;
	int startY;
	
	private final PreviewMouseListener listener;
	
	public PreviewMouseAdapter(final PreviewMouseListener listener) {
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
		
		listener.mouseDragged(endX - startX, endY - startY);
		
		startX = endX;
		startY = endY;
	}
	
	@Override
	public void mouseClicked(final MouseEvent e) {
		if (e.getClickCount() >= 2) {
			listener.mouseDoubleClick();
		}
	}
	
}
