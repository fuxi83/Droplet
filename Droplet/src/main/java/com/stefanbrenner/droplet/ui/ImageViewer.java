package com.stefanbrenner.droplet.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
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
	
	public ImageViewer(final IDropletContext dropletContext) {
		
		setLayout(new BorderLayout());
		setSize(new Dimension(Short.MIN_VALUE, 400));
		setBorder(BorderFactory.createTitledBorder(Messages.getString("PreviewPanel.title"))); //$NON-NLS-1$
		
		scrollPane = new ScrollPane();
		
		image = new JLabel();
		image.setHorizontalAlignment(JLabel.CENTER);
		
		scrollPane.add(image);
		
		add(scrollPane, BorderLayout.CENTER);
		
		new Thread(() -> listenToDir()).start();
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
	
	public void showImage(final File newImageFile) {
		try {
			BufferedImage bimg = ImageIO.read(newImageFile);
			Image scaled = bimg.getScaledInstance(-1, scrollPane.getHeight(), Image.SCALE_SMOOTH);
			ImageIcon icon = new ImageIcon(scaled);
			image.setIcon(icon);
		} catch (IOException e) {
			log.error("error loading image {}", newImageFile);
		}
	}
	
}
