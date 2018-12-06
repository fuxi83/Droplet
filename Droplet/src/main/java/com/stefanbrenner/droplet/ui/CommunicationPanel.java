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

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.lang3.StringUtils;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.swing.FontIcon;

import com.google.common.collect.Sets;
import com.stefanbrenner.droplet.model.IDropletContext;
import com.stefanbrenner.droplet.model.internal.Configuration;
import com.stefanbrenner.droplet.service.ISerialCommunicationService;
import com.stefanbrenner.droplet.utils.DropletConfig;
import com.stefanbrenner.droplet.utils.Messages;

import lombok.extern.slf4j.Slf4j;

/**
 * Panel that displays controls to connect to a microcontroller.
 *
 * @author Stefan Brenner
 */
@Slf4j
public class CommunicationPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private final IDropletContext dropletContext;
	
	private final JButton btnUpdate;
	private final JComboBox<String> cmbPort;
	private final JLabel lblStatus;
	
	private ISerialCommunicationService commService;
	private Set<String> ports = Sets.newHashSet();
	
	/**
	 * Create the panel.
	 */
	public CommunicationPanel(final IDropletContext context) {
		
		this.dropletContext = context;
		
		setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
		setBorder(BorderFactory.createTitledBorder(Messages.getString("CommunicationPanel.title"))); //$NON-NLS-1$
		
		FontIcon icon = FontIcon.of(FontAwesome.REFRESH);
		icon.setIconColor(Color.GRAY);
		btnUpdate = new JButton(icon);
		btnUpdate.setBorderPainted(false);
		btnUpdate.addActionListener(e -> updatePorts());
		btnUpdate.setToolTipText(Messages.getString("CommunicationPanel.updateTooltip")); //$NON-NLS-1$
		add(btnUpdate);
		
		// port selection combo box
		cmbPort = new JComboBox<String>();
		cmbPort.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(final ItemEvent event) {
				Object selectedItem = cmbPort.getSelectedItem();
				if (selectedItem instanceof String) {
					String portId = (String) selectedItem;
					selectPort(portId);
				}
			}
		});
		add(cmbPort);
		
		// status label
		add(new JLabel(Messages.getString("CommunicationPanel.status"))); //$NON-NLS-1$
		lblStatus = new JLabel(Messages.getString("CommunicationPanel.notConnected")); //$NON-NLS-1$
		lblStatus.setForeground(Color.RED);
		add(lblStatus);
		
		// set communication service from configuration
		log.info("load communication service from config");
		setCommService(Configuration.getSerialCommProvider());
		
		// add listener to selected communication provider
		Configuration.addPropertyChangeListener(Configuration.CONF_SERIAL_COMM_PROVIDER, new PropertyChangeListener() {
			@Override
			public void propertyChange(final PropertyChangeEvent event) {
				// disconnect old service provider
				Object oldValue = event.getOldValue();
				if (oldValue instanceof ISerialCommunicationService) {
					((ISerialCommunicationService) oldValue).close();
				}
				cmbPort.removeAllItems();
				Object newValue = event.getNewValue();
				if (newValue instanceof ISerialCommunicationService) {
					setCommService((ISerialCommunicationService) newValue);
				}
			}
		});
		
		// add listener to update available ports
		if (DropletConfig.isAutoLoadPorts()) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					while (!Thread.interrupted()) {
						
						updatePorts();
						
						// sleep for 2 seconds
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
				}
			}).start();
		}
		
		// set port from configuration
		String serialCommPort = Configuration.getSerialCommPort();
		selectPort(serialCommPort);
		cmbPort.setSelectedItem(serialCommPort);
		
	}
	
	private void updatePorts() {
		
		Set<String> newPorts = Configuration.getSerialCommProvider().getPorts();
		
		// check if new port is available
		for (String port : newPorts) {
			if (!ports.contains(port)) {
				ports.add(port);
				cmbPort.addItem(port);
			}
		}
		
		// check if port has gone
		for (String port : ports) {
			if (!newPorts.contains(port)) {
				ports.remove(port);
				cmbPort.removeItem(port);
			}
		}
	}
	
	private void setCommService(final ISerialCommunicationService commService) {
		log.info("set new communication service: {}", commService.getName());
		this.commService = commService;
		setPorts(commService.getPorts());
	}
	
	private void setPorts(final Set<String> ports) {
		this.ports = ports;
		if ((ports == null) || (ports.size() == 0)) {
			cmbPort.addItem(Messages.getString("CommunicationPanel.NoPortAvailable")); //$NON-NLS-1$
		} else {
			for (String port : ports) {
				cmbPort.addItem(port);
			}
		}
	}
	
	private void selectPort(final String portId) {
		boolean connected = false;
		
		// close previous connection
		if (commService.isConnected()) {
			commService.close();
		}
		
		if (StringUtils.isNotBlank(portId)) {
			dropletContext.setPort(portId);
			connected = commService.connect(portId, dropletContext);
		}
		
		updateStatus(connected);
	}
	
	private void updateStatus(final boolean connected) {
		if (connected) {
			lblStatus.setText(Messages.getString("CommunicationPanel.connected")); //$NON-NLS-1$
			lblStatus.setForeground(Color.GREEN);
		} else {
			lblStatus.setText(Messages.getString("CommunicationPanel.notConnected")); //$NON-NLS-1$
			lblStatus.setForeground(Color.RED);
		}
	}
	
}
