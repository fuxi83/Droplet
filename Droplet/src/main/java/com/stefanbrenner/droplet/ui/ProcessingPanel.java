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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URI;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.beans.BeanAdapter;
import com.stefanbrenner.droplet.model.IDropletContext;
import com.stefanbrenner.droplet.model.IMetadata;
import com.stefanbrenner.droplet.model.internal.Configuration;
import com.stefanbrenner.droplet.service.IMetadataProcessingService;
import com.stefanbrenner.droplet.service.impl.XMPMetadataService;
import com.stefanbrenner.droplet.utils.DropletFonts;
import com.stefanbrenner.droplet.utils.Messages;
import com.stefanbrenner.droplet.utils.UiUtils;

/**
 * Panel for setting metadata information for the pictures created with droplet.
 * 
 * @author Stefan Brenner
 */
public class ProcessingPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private final JCheckBox cbEnable;
	
	private final JFileChooser fileChooser;
	
	private final IMetadata metadata;
	
	private final IMetadataProcessingService metadataService;
	
	private JTextField txtWatchFolder;
	
	private final JTextArea txtComments;
	
	private final JTextArea txtTags;
	
	/**
	 * Create the panel.
	 * 
	 * @param dropletContext
	 *            the droplet context to be set
	 */
	public ProcessingPanel(final IDropletContext dropletContext) {
		
		this.metadata = dropletContext.getMetadata();
		this.metadataService = new XMPMetadataService(metadata, dropletContext);
		
		setLayout(new GridBagLayout());
		setBorder(BorderFactory.createTitledBorder(Messages.getString("ProcessingPanel.title"))); //$NON-NLS-1$
		setMinimumSize(new Dimension(400, 200));
		
		BeanAdapter<IMetadata> adapter = new BeanAdapter<IMetadata>(metadata, true);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.fill = GridBagConstraints.BOTH;
		
		cbEnable = new JCheckBox(Messages.getString("ProcessingPanel.writeMetadata")); //$NON-NLS-1$
		cbEnable.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent action) {
				updateState();
			}
		});
		UiUtils.editGridBagConstraints(gbc, 0, 0, 0, 0, GridBagConstraints.WEST);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		add(cbEnable, gbc);
		
		// reset gridwidth
		gbc.gridwidth = 1;
		
		// watch folder label
		UiUtils.editGridBagConstraints(gbc, 0, 1, 0, 0, GridBagConstraints.WEST);
		add(new JLabel(Messages.getString("ProcessingPanel.watchFolder")), gbc); //$NON-NLS-1$
		
		{
			JPanel watchFolderPanel = new JPanel();
			watchFolderPanel.setLayout(new BorderLayout());
			UiUtils.editGridBagConstraints(gbc, 1, 1, 1, 0);
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			add(watchFolderPanel, gbc);
			
			// reset gridwidth
			gbc.gridwidth = 1;
			
			txtWatchFolder = new JTextField();
			txtWatchFolder.setEditable(false);
			watchFolderPanel.add(txtWatchFolder, BorderLayout.CENTER);
			
			// file chooser for watch folder
			fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			
			// watch folder button
			JButton btnWatchFolder = new JButton("..."); //$NON-NLS-1$
			btnWatchFolder.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent evt) {
					int returnVal = fileChooser.showOpenDialog(ProcessingPanel.this);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File directory = fileChooser.getSelectedFile();
						txtWatchFolder.setText(directory.getAbsolutePath());
						metadataService.setWatchFolder(directory.toURI());
						Configuration.setWathFolder(directory.toURI());
					}
				}
			});
			watchFolderPanel.add(btnWatchFolder, BorderLayout.EAST);
		}
		
		// comments label
		UiUtils.editGridBagConstraints(gbc, 0, 2, 0, 1, GridBagConstraints.NORTHEAST);
		add(new JLabel(Messages.getString("ProcessingPanel.comments")), gbc); //$NON-NLS-1$
		
		txtComments = BasicComponentFactory.createTextArea(adapter.getValueModel(IMetadata.PROPERTY_DESCRIPTION),
				false);
		txtComments.setRows(4);
		txtComments.setColumns(20);
		txtComments.setFont(DropletFonts.FONT_STANDARD_SMALL);
		txtComments.setLineWrap(true);
		UiUtils.disableTab(txtComments);
		JScrollPane commentsScrollPane = new JScrollPane(txtComments);
		UiUtils.editGridBagConstraints(gbc, 1, 2, 1, 1);
		commentsScrollPane.getVerticalScrollBar().putClientProperty("JComponent.sizeVariant", "mini"); //$NON-NLS-1$ //$NON-NLS-2$
		add(commentsScrollPane, gbc);
		
		// tag label
		UiUtils.editGridBagConstraints(gbc, 2, 2, 0, 1, GridBagConstraints.NORTHEAST);
		add(new JLabel(Messages.getString("ProcessingPanel.tags")), gbc); //$NON-NLS-1$
		
		txtTags = BasicComponentFactory.createTextArea(adapter.getValueModel(IMetadata.PROPERTY_TAGS), false);
		txtTags.setRows(4);
		txtTags.setColumns(20);
		txtTags.setFont(DropletFonts.FONT_STANDARD_SMALL);
		txtTags.setLineWrap(true);
		UiUtils.disableTab(txtTags);
		JScrollPane tagScrollPane = new JScrollPane(txtTags);
		UiUtils.editGridBagConstraints(gbc, 3, 2, 1, 1, GridBagConstraints.EAST);
		tagScrollPane.getVerticalScrollBar().putClientProperty("JComponent.sizeVariant", "mini"); //$NON-NLS-1$ //$NON-NLS-2$
		add(tagScrollPane, gbc);
		
		initValuesFromConfiguration();
		updateState();
	}
	
	/**
	 * Initialize the ui components with stored values from the droplet
	 * configuration.
	 */
	private void initValuesFromConfiguration() {
		
		cbEnable.setSelected(Configuration.isProcessingEnabled());
		
		URI watchFolderURI = Configuration.getWatchFolderURI();
		if (watchFolderURI != null) {
			File directory = new File(watchFolderURI);
			if (directory.exists()) {
				txtWatchFolder.setText(directory.getAbsolutePath());
				metadataService.setWatchFolder(directory.toURI());
			}
		}
		
		txtComments.setText(Configuration.getMetadataComments());
		txtTags.setText(Configuration.getMetadataTags());
	}
	
	/**
	 * Start/stop processing service and update ui components.
	 */
	private void updateState() {
		boolean enabled = cbEnable.isSelected();
		if (enabled) {
			metadataService.start();
		} else {
			metadataService.stop();
		}
		UiUtils.setEnabledRecursive(ProcessingPanel.this, enabled, cbEnable);
		Configuration.setProcessingEnabled(enabled);
	}
	
}
