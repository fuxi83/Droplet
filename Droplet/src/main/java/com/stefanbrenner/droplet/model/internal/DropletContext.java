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
package com.stefanbrenner.droplet.model.internal;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import com.stefanbrenner.droplet.model.IDroplet;
import com.stefanbrenner.droplet.model.IDropletContext;
import com.stefanbrenner.droplet.model.IMetadata;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Class that contains all context informations and objects that are used in
 * droplet.
 *
 * @author Stefan Brenner
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DropletContext extends AbstractModelObject implements IDropletContext {
	
	/** */
	private static final long serialVersionUID = 1L;
	
	/** Default round delay in seconds. */
	private static final int DEFAULT_ROUND_DELAY = 1;
	
	/**
	 * The file in which the droplet configuration will be saved.
	 */
	private File file;
	
	/**
	 * Communication port that is sused to communicate with the serial
	 * controller.
	 */
	private String port;
	
	/**
	 * Droplet object that holds the current configuration.
	 */
	private IDroplet droplet;
	
	/**
	 * Number of rounds to execute on the serial controller. Default value is 1.
	 */
	private Integer rounds = 1;
	
	/**
	 * Delay in milliseconds before each round. Default is set to 1 second.
	 */
	private Integer roundDelay = DropletContext.DEFAULT_ROUND_DELAY;
	
	/**
	 * List of logging messages.
	 */
	private List<String> loggingMessages = new ArrayList<String>();
	
	/**
	 * Last message that was sent to the serial controller.
	 */
	private String lastSetMessage = StringUtils.EMPTY;
	
	/**
	 * Metadata informations that can be added to the picture taken with
	 * droplet.
	 */
	private IMetadata metadata = new Metadata();
	
	@Override
	public void setMetadata(final IMetadata metadata) {
		firePropertyChange(IDropletContext.PROPERTY_METADATA, this.metadata, this.metadata = metadata);
	}
	
	@Override
	public void setFile(final File file) {
		firePropertyChange(IDropletContext.PROPERTY_FILE, this.file, this.file = file);
	}
	
	@Override
	public void setPort(final String port) {
		firePropertyChange(IDropletContext.PROPERTY_PORT, this.port, this.port = port);
	}
	
	@Override
	public void setDroplet(final IDroplet droplet) {
		firePropertyChange(IDropletContext.PROPERTY_DROPLET, this.droplet, this.droplet = droplet);
	}
	
	@Override
	public String getLoggingMessages() {
		return StringUtils.join(loggingMessages, '\n');
	}
	
	@Override
	public void addLoggingMessage(final String message) {
		String oldValue = getLoggingMessages();
		loggingMessages = new ArrayList<String>(loggingMessages);
		
		// add timestamp to message
		DateFormat format = DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.getDefault());
		String timestamp = format.format(new Date(System.currentTimeMillis()));
		String logEntry = timestamp + ": " + message; //$NON-NLS-1$
		
		loggingMessages.add(logEntry);
		
		firePropertyChange(IDropletContext.PROPERTY_LOGGING, oldValue, getLoggingMessages());
	}
	
	@Override
	public void clearLoggingMessages() {
		String oldValue = getLoggingMessages();
		loggingMessages = new ArrayList<String>();
		firePropertyChange(IDropletContext.PROPERTY_LOGGING, oldValue, getLoggingMessages());
	}
	
	@Override
	public void setRounds(final Integer rounds) {
		firePropertyChange(IDropletContext.PROPERTY_ROUNDS, this.rounds, this.rounds = rounds);
	}
	
	@Override
	public void setRoundDelay(final Integer roundDelay) {
		firePropertyChange(IDropletContext.PROPERTY_ROUND_DELAY, this.roundDelay, this.roundDelay = roundDelay);
	}
	
}
