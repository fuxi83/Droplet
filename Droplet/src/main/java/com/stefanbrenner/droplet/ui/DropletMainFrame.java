/*****************************************************************************
 * Project: Droplet - Toolkit for Liquid Art Photographers
 * Copyright (C) 2012 Stefan Brenner
 *
 * This file is part of Droplet.
 *
 * Droplet is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Droplet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Droplet. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************************/
package com.stefanbrenner.droplet.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.lang3.StringUtils;

import com.apple.mrj.MRJAboutHandler;
import com.apple.mrj.MRJApplicationUtils;
import com.apple.mrj.MRJPrefsHandler;
import com.apple.mrj.MRJQuitHandler;
import com.stefanbrenner.droplet.model.IDropletContext;
import com.stefanbrenner.droplet.model.internal.Configuration;
import com.stefanbrenner.droplet.model.internal.Droplet;
import com.stefanbrenner.droplet.model.internal.DropletContext;
import com.stefanbrenner.droplet.service.IDropletMessageProtocol;
import com.stefanbrenner.droplet.service.ISerialCommunicationService;
import com.stefanbrenner.droplet.ui.actions.ExitAction;
import com.stefanbrenner.droplet.ui.actions.PreferencesAction;
import com.stefanbrenner.droplet.ui.actions.StartAction;
import com.stefanbrenner.droplet.utils.DropletConfig;
import com.stefanbrenner.droplet.utils.DropletFonts;
import com.stefanbrenner.droplet.utils.Messages;
import com.stefanbrenner.droplet.utils.UiUtils;
import com.thizzer.jtouchbar.JTouchBar;
import com.thizzer.jtouchbar.common.Color;
import com.thizzer.jtouchbar.common.Image;
import com.thizzer.jtouchbar.common.ImageName;
import com.thizzer.jtouchbar.item.TouchBarItem;
import com.thizzer.jtouchbar.item.view.TouchBarButton;
import com.thizzer.jtouchbar.item.view.TouchBarView;
import com.thizzer.jtouchbar.item.view.action.TouchBarViewAction;
import com.tngtech.configbuilder.ConfigBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * Mainframe of the droplet application.
 *
 * @author Stefan Brenner
 */
@Slf4j
public class DropletMainFrame extends JFrame implements MRJAboutHandler, MRJQuitHandler, MRJPrefsHandler {
	
	private static final long serialVersionUID = 1L;
	
	// model objects
	private final IDropletContext dropletContext;
	
	// ui components
	private final DropletMenu dropletMenu;
	private final JPanel contentPane;
	private final CommunicationPanel commPanel;
	private final DeviceSetupPanel configPanel;
	private final ProcessingPanel processingPanel;
	private final LoggingPanel loggingPanel;
	private final DropletToolbar toolbarPanel;
	private final PreviewPanel imageViewer;
	
	/**
	 * Launch the application.
	 */
	public static void main(final String[] args) throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, UnsupportedLookAndFeelException {
		
		log.info("Droplet - Toolkit for High-Speed-Photography");
		log.info("Version 0.1");
		log.info("Open Source Project created by Stefan Brenner 2012");
		log.info("http://www.droplet.at");
		log.info("==================================================");
		log.info("Starting ...");
		
		// TODO brenner: move to own class
		// log system properties
		Properties properties = System.getProperties();
		Collections.list(properties.keys()).forEach(prop -> {
			log.debug("{}: {}", prop, properties.get(prop));
		});
		
		// TODO brenner: make language editable in configurations
		// only for testing purposes
		// Locale.setDefault(Locale.GERMAN);
		
		// identify that we run on a mac
		String lcOSName = System.getProperty("os.name").toLowerCase();
		boolean IS_MAC = lcOSName.startsWith("mac os x");
		
		if (IS_MAC) {
			// put jmenubar on mac menu bar
			System.setProperty("apple.laf.useScreenMenuBar", "true"); // $NON-NLS-1$ //$NON-NLS-2$
			// use smoother fonts
			System.setProperty("apple.awt.textantialiasing", "true");
			// set the brushed metal look and feel, if desired
			System.setProperty("apple.awt.brushMetalLook", "true");
			// use the system look and feel
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} else {
			try {
				for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
					if ("Nimbus".equals(info.getName())) {
						UIManager.setLookAndFeel(info.getClassName());
						break;
					}
				}
			} catch (Exception e) {
				// use the system look and feel
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
		}
		
		// initialize DropletConfig
		log.info("initialize config");
		new ConfigBuilder<DropletConfig>(DropletConfig.class).build();
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					DropletMainFrame frame = new DropletMainFrame();
					frame.setVisible(true);
					
					// init touchbar
					JTouchBar jTouchBar = new JTouchBar();
					jTouchBar.setCustomizationIdentifier("DropletJavaTouchBar");
					jTouchBar.show(frame);
					frame.initTouchBar(jTouchBar);
					
				} catch (Exception e) {
					log.error("A fatal error occured: ", e);
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Create the frame.
	 */
	public DropletMainFrame() {
		
		log.info("start UI");
		
		// create new context
		dropletContext = new DropletContext();
		
		// create new droplet model and add to context
		Droplet droplet = new Droplet();
		droplet.initializeWithDefaults();
		dropletContext.setDroplet(droplet);
		
		// basic frame setup
		updateTitle();
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setIconImage(new ImageIcon("icons/droplet_icon_48x48.png").getImage());
		
		contentPane = new JPanel();
		contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout());
		setContentPane(contentPane);
		
		// create menu
		dropletMenu = new DropletMenu(this, dropletContext);
		setJMenuBar(dropletMenu);
		
		commPanel = new CommunicationPanel(dropletContext);
		contentPane.add(commPanel, BorderLayout.NORTH);
		
		{
			configPanel = new DeviceSetupPanel(dropletContext.getDroplet());
			processingPanel = new ProcessingPanel(dropletContext);
			loggingPanel = new LoggingPanel(dropletContext);
			imageViewer = new PreviewPanel(dropletContext);
			
			JSplitPane splitPaneBottom = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, processingPanel, imageViewer);
			splitPaneBottom.setOneTouchExpandable(true);
			splitPaneBottom.setDividerLocation(0.5d);
			splitPaneBottom.setResizeWeight(0.5d);
			splitPaneBottom.setContinuousLayout(true);
			
			JSplitPane splitPaneMain = new JSplitPane(JSplitPane.VERTICAL_SPLIT, configPanel, splitPaneBottom);
			splitPaneMain.setOneTouchExpandable(true);
			splitPaneMain.setDividerLocation(0.5d);
			splitPaneMain.setResizeWeight(0.5d);
			splitPaneMain.setContinuousLayout(true);
			contentPane.add(splitPaneMain, BorderLayout.CENTER);
		}
		
		toolbarPanel = new DropletToolbar(this, dropletContext);
		contentPane.add(toolbarPanel, BorderLayout.SOUTH);
		
		// remove focus from text components if user clicks outside
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				requestFocusInWindow();
				super.mouseClicked(e);
			}
		});
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent event) {
				handleQuit();
			}
		});
		
		// register action shortcuts
		log.debug("Register action shortcuts");
		// TODO brenner: don't consume keys in JTextComponents
		contentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "start"); //$NON-NLS-1$ //$NON-NLS-2$
		contentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F4"), "start"); //$NON-NLS-1$ //$NON-NLS-2$
		contentPane.getActionMap().put("start", new StartAction(this, dropletContext)); //$NON-NLS-1$
		
		// add listener
		dropletContext.addPropertyChangeListener(IDropletContext.PROPERTY_DROPLET, new PropertyChangeListener() {
			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				// TODO brenner: use listener instead
				configPanel.setDroplet(dropletContext.getDroplet());
			}
		});
		
		// set default fonts
		log.debug("set default fonts");
		Enumeration<Object> keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof javax.swing.plaf.FontUIResource) {
				UIManager.put(key, DropletFonts.FONT_STANDARD_SMALL);
			}
		}
		
		// register handlers for mac events
		log.debug("register handlers for mac events");
		if (UiUtils.isMacOS()) {
			MRJApplicationUtils.registerAboutHandler(this);
			MRJApplicationUtils.registerQuitHandler(this);
			MRJApplicationUtils.registerPrefsHandler(this);
		}
		
		// finishing frame settings
		// make frame as small as possible
		// pack();
		// set to full screen mode
		setExtendedState(Frame.MAXIMIZED_BOTH);
		// set minimum size to packed size
		setMinimumSize(new Dimension(300, 200));
		// update ui for nimbus component resizing
		SwingUtilities.updateComponentTreeUI(this);
		
		dropletContext.addPropertyChangeListener(IDropletContext.PROPERTY_FILE, new PropertyChangeListener() {
			@Override
			public void propertyChange(final PropertyChangeEvent event) {
				updateTitle();
			}
		});
		
		// add welcome message to logging panel
		dropletContext.addLoggingMessage("Welcome to Droplet!");
		
		log.info("Droplet started successfully!");
		
	}
	
	// TODO refactor
	private void initTouchBar(final JTouchBar touchBar) {
		
		// space
		touchBar.addItem(new TouchBarItem(TouchBarItem.NSTouchBarItemIdentifierFlexibleSpace));
		
		// send button
		TouchBarButton sendButton = new TouchBarButton();
		// sendButton.setTitle("Send");
		sendButton.setImage(new Image(ImageName.NSImageNameTouchBarRefreshTemplate, false));
		sendButton.setBezelColor(Color.LIGHT_GRAY);
		sendButton.setAction(new TouchBarViewAction() {
			@Override
			public void onCall(final TouchBarView view) {
				// TODO call action
				System.out.println("Send config to MC");
			}
		});
		touchBar.addItem(new TouchBarItem("Send", sendButton, true));
		
		// space
		touchBar.addItem(new TouchBarItem(TouchBarItem.NSTouchBarItemIdentifierFixedSpaceSmall));
		
		// start button
		TouchBarButton startButton = new TouchBarButton();
		// startButton.setTitle("Start");
		startButton.setImage(new Image(ImageName.NSImageNameTouchBarPlayTemplate, false));
		startButton.setBezelColor(Color.DARK_GRAY);
		startButton.setAction(new TouchBarViewAction() {
			@Override
			public void onCall(final TouchBarView view) {
				// TODO: call service instead
				{
					ISerialCommunicationService serialCommProvider = Configuration.getSerialCommProvider();
					IDropletMessageProtocol messageProtocolProvider = Configuration.getMessageProtocolProvider();
					
					// send configuration if it changed since last send
					String setMessage = messageProtocolProvider.createSetMessage(dropletContext.getDroplet());
					if (!StringUtils.equals(setMessage, dropletContext.getLastSetMessage())) {
						String resetMessage = messageProtocolProvider.createResetMessage();
						serialCommProvider.sendData(resetMessage);
						serialCommProvider.sendData(setMessage);
						dropletContext.setLastSetMessage(setMessage);
					}
					
					Integer rounds = dropletContext.getRounds();
					Integer roundDelay = dropletContext.getRoundDelay();
					
					String message = messageProtocolProvider.createStartMessage(rounds, roundDelay);
					serialCommProvider.sendData(message);
				}
			}
		});
		touchBar.addItem(new TouchBarItem("Start", startButton, true));
		
		// space
		touchBar.addItem(new TouchBarItem(TouchBarItem.NSTouchBarItemIdentifierFlexibleSpace));
	}
	
	/**
	 * Updates the frame title.
	 * <p>
	 * Additionally displays the absolute file name of an opened or saved file.
	 */
	private void updateTitle() {
		File file = dropletContext.getFile();
		String title = Messages.getString("DropletMainFrame.title"); //$NON-NLS-1$
		if (file != null) {
			title += " - " + file.getAbsolutePath();
		}
		setTitle(title);
	}
	
	@Override
	public void handlePrefs() throws IllegalStateException {
		new PreferencesAction(this, dropletContext).actionPerformed(null);
	}
	
	@Override
	public void handleQuit() {
		new ExitAction(this, dropletContext).actionPerformed(null);
	}
	
	@Override
	public void handleAbout() {
		new AboutDialog(this).setVisible(true);
	}
	
}
