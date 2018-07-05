package com.stefanbrenner.droplet.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.ScrollPane;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.stefanbrenner.droplet.model.IDropletContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImageViewer extends JPanel {
	
	private static final long serialVersionUID = -155306413898252667L;
	
	private final JLabel image;
	
	public ImageViewer(final IDropletContext dropletContext) {
		
		setLayout(new BorderLayout());
		setMinimumSize(new Dimension(Short.MIN_VALUE, 200));
		
		ScrollPane scrollPane = new ScrollPane();
		
		image = new JLabel();
		image.setHorizontalAlignment(JLabel.CENTER);
		
		scrollPane.add(image);
		
		add(scrollPane, BorderLayout.CENTER);
	}
	
	public void showImage() {
		String watchFolder = "/Users/stefan/Development/droplet-repo/Droplet/src/main/resources/";
		String imageFile = watchFolder + "image.jpg";
		File file = new File(imageFile);
		try {
			BufferedImage bimg = ImageIO.read(file);
			Image scaled = bimg.getScaledInstance(-1, 500, Image.SCALE_SMOOTH);
			ImageIcon icon = new ImageIcon(scaled);
			image.setIcon(icon);
		} catch (IOException e) {
			log.error("error loading image {}", imageFile);
		}
	}
	
}
