package com.stefanbrenner.droplet.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.ScrollPane;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.stefanbrenner.droplet.model.IDropletContext;
import com.stefanbrenner.droplet.model.internal.Configuration;
import com.stefanbrenner.droplet.utils.Messages;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImageViewer extends JPanel {
	
	private static final long serialVersionUID = -155306413898252667L;
	
	private final JLabel image;
	
	private final ScrollPane scrollPane;
	
	private final String TEST_IMAGE = "/Users/stefan/Development/droplet-repo/Droplet/src/main/resources/image.jpg";
	
	private BufferedImage originalImage;
	private Image scaledImage;
	private int baseHeight;
	
	private static final float ZOOM_FACTOR = 1.2f;
	
	// TODO: failsafe
	// TODO: loading indicator / dummy image
	// TODO: zoom to center
	// TODO: performance - lowres image for faster zoom?
	// TODO: zoom with scrolling
	// TODO: navigation with space
	// TODO: reset on configuration load
	// TODO: zoom to fit on resize of window
	
	public ImageViewer(final IDropletContext dropletContext) {
		
		setLayout(new BorderLayout());
		setSize(new Dimension(Short.MIN_VALUE, 400));
		setBorder(BorderFactory.createTitledBorder(Messages.getString("PreviewPanel.title"))); //$NON-NLS-1$
		
		scrollPane = new ScrollPane();
		
		image = new JLabel();
		image.setHorizontalAlignment(JLabel.CENTER);
		
		scrollPane.add(image);
		
		add(scrollPane, BorderLayout.CENTER);
		
		// toolbar
		JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton btnZoomIn = new JButton("+");
		btnZoomIn.addActionListener(e -> {
			if (originalImage != null) {
				zoomIn();
			}
		});
		toolbar.add(btnZoomIn);
		JButton btnZoomOut = new JButton("-");
		btnZoomOut.addActionListener(e -> {
			if (originalImage != null) {
				zoomOut();
			}
		});
		toolbar.add(btnZoomOut);
		JButton btnZoomFit = new JButton("Fit");
		btnZoomFit.addActionListener(e -> {
			if (originalImage != null) {
				showFitImage();
			}
		});
		toolbar.add(btnZoomFit);
		JButton btnZoomOriginal = new JButton("1:1");
		btnZoomOriginal.addActionListener(e -> {
			if (originalImage != null) {
				showOriginalFileSize();
			}
		});
		toolbar.add(btnZoomOriginal);
		add(toolbar, BorderLayout.NORTH);
		
		new Thread(() -> listenToDir()).start();
		// showImage(new File(TEST_IMAGE));
		
	}
	
	private void listenToDir() {
		try {
			WatchService watchService = FileSystems.getDefault().newWatchService();
			Path dir = Paths.get(Configuration.getWatchFolderURI());
			dir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
			
			WatchKey key;
			while ((key = watchService.take()) != null) {
				for (WatchEvent<?> event : key.pollEvents()) {
					Path name = ((WatchEvent<Path>) event).context();
					File newImage = dir.resolve(name).toFile();
					
					showImage(newImage);
				}
				key.reset();
			}
			
		} catch (IOException | InterruptedException e) {
			log.error("error watching folder {} for new images", Configuration.getWatchFolderURI());
		}
	}
	
	private void showImage(final File newImageFile) {
		try {
			originalImage = ImageIO.read(newImageFile);
			showFitImage();
		} catch (IOException e) {
			log.error("error loading image {}", newImageFile);
		}
	}
	
	private void showOriginalFileSize() {
		displayImage(-1);
	}
	
	private void showFitImage() {
		displayImage(scrollPane.getHeight());
	}
	
	private void zoomIn() {
		displayImage((int) (baseHeight * ZOOM_FACTOR));
	}
	
	private void zoomOut() {
		displayImage((int) (baseHeight / ZOOM_FACTOR));
	}
	
	private void displayImage(final int height) {
		scaledImage = originalImage.getScaledInstance(-1, height, Image.SCALE_SMOOTH);
		ImageIcon icon = new ImageIcon(scaledImage);
		image.setIcon(icon);
		baseHeight = icon.getIconHeight();
	}
	
}
