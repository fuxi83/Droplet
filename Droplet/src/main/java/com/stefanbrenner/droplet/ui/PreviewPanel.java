package com.stefanbrenner.droplet.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.ScrollPane;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import com.stefanbrenner.droplet.model.IDropletContext;
import com.stefanbrenner.droplet.model.internal.Configuration;
import com.stefanbrenner.droplet.utils.Messages;
import com.sun.nio.file.SensitivityWatchEventModifier;

import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("restriction")
@Slf4j
public class PreviewPanel extends JPanel {
	
	private static final long serialVersionUID = -155306413898252667L;
	
	private final PreviewComponent preview;
	
	private final ScrollPane scrollPane;
	
	private WatchService watchService;
	
	public PreviewPanel(final IDropletContext dropletContext) {
		
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(300, 100));
		setBorder(BorderFactory.createTitledBorder(Messages.getString("PreviewPanel.title"))); //$NON-NLS-1$
		
		scrollPane = new ScrollPane();
		
		preview = new PreviewComponent();
		scrollPane.add(preview);
		
		add(scrollPane, BorderLayout.CENTER);
		
		// toolbar
		JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton btnZoomIn = new JButton("+");
		btnZoomIn.addActionListener(e -> preview.zoomIn());
		toolbar.add(btnZoomIn);
		JButton btnZoomOut = new JButton("-");
		btnZoomOut.addActionListener(e -> preview.zoomOut());
		toolbar.add(btnZoomOut);
		JButton btnZoomFit = new JButton("Fit");
		btnZoomFit.addActionListener(e -> preview.fitTo());
		toolbar.add(btnZoomFit);
		JButton btnZoomOriginal = new JButton("1:1");
		btnZoomOriginal.addActionListener(e -> preview.showOriginalSize());
		toolbar.add(btnZoomOriginal);
		add(toolbar, BorderLayout.NORTH);
		
		startWatchService();
		
		Configuration.addPropertyChangeListener(Configuration.CONF_WATCH_FOLDER_URI, e -> {
			try {
				watchService.close();
				preview.clear();
				startWatchService();
			} catch (IOException ex) {
				log.error("Error closing watch service: {}", ex);
			}
		});
		
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent e) {
				preview.resized();
			}
		});
		
		// clear preview when new configuration is loaded
		dropletContext.addPropertyChangeListener(IDropletContext.PROPERTY_FILE, e -> preview.clear());
	}
	
	private void startWatchService() {
		new Thread(() -> listenToDir()).start();
	}
	
	private void listenToDir() {
		try {
			watchService = FileSystems.getDefault().newWatchService();
			Path dir = Paths.get(Configuration.getWatchFolderURI());
			
			/**
			 * due to performance issues on OSX we need to use
			 * SensitivityWatchEventModifier
			 * 
			 * @see https://stackoverflow.com/questions/9588737/is-java-7-watchservice-slow-for-anyone-else
			 */
			dir.register(watchService, new WatchEvent.Kind[] { StandardWatchEventKinds.ENTRY_CREATE },
					SensitivityWatchEventModifier.HIGH);
			
			WatchKey key;
			while ((key = watchService.take()) != null) {
				for (WatchEvent<?> event : key.pollEvents()) {
					@SuppressWarnings("unchecked")
					Path name = ((WatchEvent<Path>) event).context();
					File newImage = dir.resolve(name).toFile();
					String fileExt = FilenameUtils.getExtension(newImage.getName());
					
					// only preview JPG files
					if (StringUtils.equalsAnyIgnoreCase(fileExt, "jpg", "jpeg")) {
						preview.setImage(ImageIO.read(newImage));
					}
					
				}
				key.reset();
			}
			
		} catch (IOException | InterruptedException e) {
			log.error("error watching folder {} for new images", Configuration.getWatchFolderURI());
		} catch (ClosedWatchServiceException e) {
			log.info("Watch service shut down");
		}
	}
	
}
