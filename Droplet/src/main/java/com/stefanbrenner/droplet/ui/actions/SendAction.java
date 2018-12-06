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
package com.stefanbrenner.droplet.ui.actions;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.swing.FontIcon;

import com.stefanbrenner.droplet.model.IActionDevice;
import com.stefanbrenner.droplet.model.IDroplet;
import com.stefanbrenner.droplet.model.IDropletContext;
import com.stefanbrenner.droplet.model.internal.Configuration;
import com.stefanbrenner.droplet.service.IDropletMessageProtocol;
import com.stefanbrenner.droplet.service.ISerialCommunicationService;
import com.stefanbrenner.droplet.utils.Messages;

/**
 * Action to send a message containing the current droplet configuration to the
 * serial controller.
 *
 * @author Stefan Brenner
 */
@SuppressWarnings("serial")
public class SendAction extends AbstractSerialAction {
	
	public SendAction(final JFrame frame, final IDropletContext dropletContext) {
		super(frame, dropletContext, Messages.getString("SendAction.title")); //$NON-NLS-1$
		putValue(Action.SHORT_DESCRIPTION, Messages.getString("SendAction.description")); //$NON-NLS-1$
		
		FontIcon icon = FontIcon.of(FontAwesome.EXCHANGE);
		icon.setIconSize(14);
		icon.setIconColor(Color.GRAY);
		putValue(Action.SMALL_ICON, icon);
		
		initListener();
	}
	
	@Override
	public void actionPerformed(final ActionEvent event) {
		
		// cannot send no devices
		if (getDroplet().getDevices().isEmpty()) {
			JOptionPane.showMessageDialog(getFrame(), Messages.getString("SendAction.NoDevicesDefined"), //$NON-NLS-1$
					Messages.getString("SendAction.NoDevices"), //$NON-NLS-1$
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		// cannot send if no enabled actions were found
		boolean foundAction = false;
		for (IActionDevice device : getDroplet().getDevices(IActionDevice.class)) {
			if (!device.getEnabledActions().isEmpty()) {
				foundAction = true;
				break;
			}
		}
		if (!foundAction) {
			JOptionPane.showMessageDialog(getFrame(), Messages.getString("SendAction.NoEnabledActions"), //$NON-NLS-1$
					Messages.getString("SendAction.NoActions"), //$NON-NLS-1$
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		ISerialCommunicationService serialCommProvider = Configuration.getSerialCommProvider();
		IDropletMessageProtocol messageProtocolProvider = Configuration.getMessageProtocolProvider();
		
		// first reset actions
		String message = messageProtocolProvider.createResetMessage();
		serialCommProvider.sendData(message);
		
		message = messageProtocolProvider.createSetMessage(getDroplet());
		
		// save message to context
		getDropletContext().setLastSetMessage(message);
		
		serialCommProvider.sendData(message);
	}
	
	// TODO brenner: add listener to actions
	private void initListener() {
		PropertyChangeListener listener = new PropertyChangeListener() {
			@Override
			public void propertyChange(final PropertyChangeEvent event) {
				updateVisibility();
			}
		};
		getDroplet().addPropertyChangeListener(IDroplet.ASSOCIATION_DEVICES, listener);
	}
	
	private void updateVisibility() {
		setEnabled(!getDroplet().getDevices(IActionDevice.class).isEmpty());
	}
	
}
