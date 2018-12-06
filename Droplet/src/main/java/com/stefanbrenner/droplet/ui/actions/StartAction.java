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

import javax.swing.Action;
import javax.swing.JFrame;

import org.apache.commons.lang3.StringUtils;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.swing.FontIcon;

import com.stefanbrenner.droplet.model.IDropletContext;
import com.stefanbrenner.droplet.model.internal.Configuration;
import com.stefanbrenner.droplet.service.IDropletMessageProtocol;
import com.stefanbrenner.droplet.service.ISerialCommunicationService;
import com.stefanbrenner.droplet.utils.Messages;

/**
 * Action to send a start message to the serial controller to start the number
 * of rounds with the specified delay between them.
 *
 * @author Stefan Brenner
 */
@SuppressWarnings("serial")
public class StartAction extends AbstractSerialAction {
	
	public StartAction(final JFrame frame, final IDropletContext dropletContext) {
		super(frame, dropletContext, Messages.getString("StartAction.title")); //$NON-NLS-1$
		putValue(Action.SHORT_DESCRIPTION, Messages.getString("StartAction.description")); //$NON-NLS-1$
		
		FontIcon icon = FontIcon.of(FontAwesome.PLAY);
		icon.setIconSize(14);
		icon.setIconColor(Color.GRAY);
		putValue(Action.SMALL_ICON, icon);
	}
	
	@Override
	public void actionPerformed(final ActionEvent event) {
		ISerialCommunicationService serialCommProvider = Configuration.getSerialCommProvider();
		IDropletMessageProtocol messageProtocolProvider = Configuration.getMessageProtocolProvider();
		
		// send configuration if it changed since last send
		String setMessage = messageProtocolProvider.createSetMessage(getDroplet());
		if (!StringUtils.equals(setMessage, getDropletContext().getLastSetMessage())) {
			String resetMessage = messageProtocolProvider.createResetMessage();
			serialCommProvider.sendData(resetMessage);
			serialCommProvider.sendData(setMessage);
			getDropletContext().setLastSetMessage(setMessage);
		}
		
		Integer rounds = getDropletContext().getRounds();
		Integer roundDelay = getDropletContext().getRoundDelay();
		
		String message = messageProtocolProvider.createStartMessage(rounds, roundDelay);
		serialCommProvider.sendData(message);
	}
	
}
