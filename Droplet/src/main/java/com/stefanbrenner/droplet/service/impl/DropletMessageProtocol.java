/*******************************************************************************
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
 *******************************************************************************/
package com.stefanbrenner.droplet.service.impl;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.mangosdk.spi.ProviderFor;

import com.stefanbrenner.droplet.model.IAction;
import com.stefanbrenner.droplet.model.IActionDevice;
import com.stefanbrenner.droplet.model.IDevice;
import com.stefanbrenner.droplet.model.IDroplet;
import com.stefanbrenner.droplet.model.IDurationAction;
import com.stefanbrenner.droplet.model.IInputDevice;
import com.stefanbrenner.droplet.model.internal.Button;
import com.stefanbrenner.droplet.model.internal.Camera;
import com.stefanbrenner.droplet.model.internal.Flash;
import com.stefanbrenner.droplet.model.internal.Valve;
import com.stefanbrenner.droplet.service.IDropletMessageProtocol;

/**
 * This class implements the Droplet Serial Communication Protocol (see
 * doc/dsc-protocol.txt for more information).
 * 
 * @author Stefan Brenner
 */
@ProviderFor(IDropletMessageProtocol.class)
public class DropletMessageProtocol implements IDropletMessageProtocol {
	
	// meta characters
	
	/** Character to separate fields. */
	public static final String FIELD_SEPARATOR = ";"; //$NON-NLS-1$
	/** Character to separate the checksum. */
	public static final String CHKSUM_SEPARATOR = "^"; //$NON-NLS-1$
	/** Character to separate times. */
	public static final String TIME_SEPARATOR = "|"; //$NON-NLS-1$
	/** Character to separate devices. */
	public static final String DEVICE_SEPARATOR = "\n"; //$NON-NLS-1$
	
	// commands
	
	/** Character for the release command. */
	public static final String COMMAND_RELEASE = "R"; //$NON-NLS-1$
	/** Character for the send command. */
	public static final String COMMAND_SEND = "S"; //$NON-NLS-1$
	/** Character for the info command. */
	public static final String COMMAND_INFO = "I"; //$NON-NLS-1$
	/** Character for the reset command. */
	public static final String COMMAND_RESET = "X"; //$NON-NLS-1$
	/** Character for the cancel command. */
	public static final String COMMAND_CANCEL = "C"; //$NON-NLS-1$
	/** Character for the HIGH command. */
	public static final String COMMAND_HIGH = "H"; //$NON-NLS-1$
	/** Character for the LOW command. */
	public static final String COMMAND_LOW = "L"; //$NON-NLS-1$
	
	// devices
	
	/** Character for valves. */
	public static final String DEVICE_VALVE = "V"; //$NON-NLS-1$
	/** Character for flashes. */
	public static final String DEVICE_FLASH = "F"; //$NON-NLS-1$
	/** Character for cameras. */
	public static final String DEVICE_CAMERA = "C"; //$NON-NLS-1$
	/** Character for buttons. */
	public static final String DEVICE_BUTTON = "B"; //$NON-NLS-1$
	
	/** Mapping for devices and shortcuts. */
	public static final HashMap<Class<?>, String> DEVICE_SHORTS = new HashMap<Class<?>, String>();
	
	static {
		DropletMessageProtocol.DEVICE_SHORTS.put(Valve.class, DropletMessageProtocol.DEVICE_VALVE);
		DropletMessageProtocol.DEVICE_SHORTS.put(Flash.class, DropletMessageProtocol.DEVICE_FLASH);
		DropletMessageProtocol.DEVICE_SHORTS.put(Camera.class, DropletMessageProtocol.DEVICE_CAMERA);
		DropletMessageProtocol.DEVICE_SHORTS.put(Button.class, DropletMessageProtocol.DEVICE_BUTTON);
	}
	
	@Override
	public String getName() {
		return "Droplet Serial Communication Protocol";
	}
	
	@Override
	public String createStartMessage() {
		return createStartMessage(1, 0);
	}
	
	@Override
	public String createStartMessage(final int rounds, final int delay) {
		String result = DropletMessageProtocol.COMMAND_RELEASE + DropletMessageProtocol.FIELD_SEPARATOR + rounds;
		
		int chksum = rounds;
		
		if (delay > 0) {
			int delayInMillis = delay * 1000;
			chksum += delayInMillis;
			result += DropletMessageProtocol.FIELD_SEPARATOR + delayInMillis;
		}
		
		result += DropletMessageProtocol.CHKSUM_SEPARATOR + chksum;
		
		return result;
	}
	
	@Override
	public String createSetMessage(final IDroplet droplet) {
		
		String result = StringUtils.EMPTY;
		
		for (IActionDevice d : droplet.getDevices(IActionDevice.class)) {
			
			Class<? extends IActionDevice> deviceClass = d.getClass();
			
			// don't add device with no actions defined
			List<IAction> enabledActions = d.getEnabledActions();
			if (enabledActions.isEmpty()) {
				continue;
			}
			
			result += DropletMessageProtocol.COMMAND_SEND + DropletMessageProtocol.FIELD_SEPARATOR;
			
			result += d.getNumber() + DropletMessageProtocol.FIELD_SEPARATOR;
			
			result += DropletMessageProtocol.DEVICE_SHORTS.get(deviceClass);
			
			int chksum = 0;
			for (IAction a : enabledActions) {
				chksum += a.getOffset();
				result += DropletMessageProtocol.FIELD_SEPARATOR + a.getOffset();
				if (a instanceof IDurationAction) {
					chksum += ((IDurationAction) a).getDuration();
					result += DropletMessageProtocol.TIME_SEPARATOR + ((IDurationAction) a).getDuration();
				}
			}
			result += DropletMessageProtocol.CHKSUM_SEPARATOR + chksum + DropletMessageProtocol.DEVICE_SEPARATOR;
		}
		
		for (IInputDevice d : droplet.getDevices(IInputDevice.class)) {
			result += DropletMessageProtocol.COMMAND_SEND + DropletMessageProtocol.FIELD_SEPARATOR;
			result += d.getNumber() + DropletMessageProtocol.FIELD_SEPARATOR;
			result += DropletMessageProtocol.DEVICE_SHORTS.get(d.getClass());
			result += DropletMessageProtocol.CHKSUM_SEPARATOR + d.getNumber() + DropletMessageProtocol.DEVICE_SEPARATOR;
			
		}
		
		return result;
	}
	
	@Override
	public String createInfoMessage() {
		return DropletMessageProtocol.COMMAND_INFO;
	}
	
	@Override
	public String createResetMessage() {
		return DropletMessageProtocol.COMMAND_RESET;
	}
	
	@Override
	public String createCancelMessage() {
		return DropletMessageProtocol.COMMAND_CANCEL;
	}
	
	@Override
	public String createDeviceOffMessage(final IDroplet droplet, final IDevice device) {
		return DropletMessageProtocol.COMMAND_LOW + DropletMessageProtocol.FIELD_SEPARATOR + device.getNumber();
	}
	
	@Override
	public String createDeviceOnMessage(final IDroplet droplet, final IDevice device) {
		return DropletMessageProtocol.COMMAND_HIGH + DropletMessageProtocol.FIELD_SEPARATOR + device.getNumber();
	}
	
}
