package com.stefanbrenner.droplet.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.ScrollPane;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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
import javax.swing.JButton;
import javax.swing.JPanel;

import com.stefanbrenner.droplet.model.IDropletContext;
import com.stefanbrenner.droplet.model.internal.Configuration;
import com.stefanbrenner.droplet.utils.Messages;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PreviewPanel extends JPanel {
	
	private static final long serialVersionUID = -155306413898252667L;
	
	private final PreviewComponent preview;
	
	private final ScrollPane scrollPane;
	
	private static final String TEST_IMAGE = "/Users/stefan/Development/droplet-repo/Droplet/src/main/resources/image.jpg";
	
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
		
		new Thread(() -> listenToDir()).start();
		// try {
		// preview.setImage(ImageIO.read(new File(TEST_IMAGE)));
		// } catch (IOException e1) {
		// e1.printStackTrace();
		// }
		
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent e) {
				preview.resized();
			}
		});
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
					
					preview.setImage(ImageIO.read(newImage));
				}
				key.reset();
			}
			
		} catch (IOException | InterruptedException e) {
			log.error("error watching folder {} for new images", Configuration.getWatchFolderURI());
		}
	}
	
}
