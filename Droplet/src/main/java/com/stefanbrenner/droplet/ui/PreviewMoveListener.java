package com.stefanbrenner.droplet.ui;

@FunctionalInterface
public interface PreviewMoveListener {
	void moved(int diffX, int diffY);
}
