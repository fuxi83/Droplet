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

import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.swing.FontIcon;

import com.stefanbrenner.droplet.model.IDropletContext;
import com.stefanbrenner.droplet.model.internal.Configuration;
import com.stefanbrenner.droplet.service.IDropletMessageProtocol;
import com.stefanbrenner.droplet.service.ISerialCommunicationService;
import com.stefanbrenner.droplet.utils.Messages;

/**
 * Send a cancel message to the serial controller.
 *
 * @author Stefan Brenner
 */
@SuppressWarnings("serial")
public class CancelAction extends AbstractSerialAction {
	
	public CancelAction(final JFrame frame, final IDropletContext dropletContext) {
		super(frame, dropletContext, Messages.getString("CancelAction.title")); //$NON-NLS-1$
		putValue(Action.SHORT_DESCRIPTION, Messages.getString("CancelAction.description")); //$NON-NLS-1$
		
		FontIcon icon = FontIcon.of(FontAwesome.STOP);
		icon.setIconSize(14);
		icon.setIconColor(Color.GRAY);
		putValue(Action.SMALL_ICON, icon);
	}
	
	@Override
	public void actionPerformed(final ActionEvent event) {
		ISerialCommunicationService serialCommProvider = Configuration.getSerialCommProvider();
		IDropletMessageProtocol messageProtocolProvider = Configuration.getMessageProtocolProvider();
		
		String message = messageProtocolProvider.createCancelMessage();
		serialCommProvider.sendData(message);
	}
	
}
