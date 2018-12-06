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

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.mangosdk.spi.ProviderFor;

import com.stefanbrenner.droplet.model.IAction;
import com.stefanbrenner.droplet.model.IActionDevice;
import com.stefanbrenner.droplet.model.IDevice;
import com.stefanbrenner.droplet.model.IDroplet;
import com.stefanbrenner.droplet.model.IDurationAction;
import com.stefanbrenner.droplet.model.internal.Camera;
import com.stefanbrenner.droplet.model.internal.Flash;
import com.stefanbrenner.droplet.model.internal.Valve;
import com.stefanbrenner.droplet.service.IDropletMessageProtocol;

/**
 * Communication Protocol for the ArduXposure.
 * 
 * @author Stefan Brenner
 */
@ProviderFor(IDropletMessageProtocol.class)
public class ArduXposureMessageProtocol implements IDropletMessageProtocol {
	
	// meta characters
	public static final String FIELD_SEPARATOR = ";"; //$NON-NLS-1$
	public static final String CHKSUM_SEPARATOR = "^"; //$NON-NLS-1$
	public static final String TIME_SEPARATOR = "|"; //$NON-NLS-1$
	public static final String DEVICE_SEPARATOR = "\n"; //$NON-NLS-1$
	
	// commands
	public static final byte COMMAND_RELEASE = 1;
	public static final String COMMAND_SEND = "S"; //$NON-NLS-1$
	public static final String COMMAND_INFO = "I"; //$NON-NLS-1$
	public static final String COMMAND_RESET = "X"; //$NON-NLS-1$
	public static final String COMMAND_CANCEL = "C"; //$NON-NLS-1$
	public static final String COMMAND_HIGH = "H"; //$NON-NLS-1$
	public static final String COMMAND_LOW = "L"; //$NON-NLS-1$
	
	// devices
	public static final String DEVICE_VALVE = "V"; //$NON-NLS-1$
	public static final String DEVICE_FLASH = "F"; //$NON-NLS-1$
	public static final String DEVICE_CAMERA = "C"; //$NON-NLS-1$
	
	// mapping for devices and shortcuts
	public static final HashMap<Class<?>, String> DEVICE_SHORTS = new HashMap<Class<?>, String>();
	
	static {
		ArduXposureMessageProtocol.DEVICE_SHORTS.put(Valve.class, ArduXposureMessageProtocol.DEVICE_VALVE);
		ArduXposureMessageProtocol.DEVICE_SHORTS.put(Flash.class, ArduXposureMessageProtocol.DEVICE_FLASH);
		ArduXposureMessageProtocol.DEVICE_SHORTS.put(Camera.class, ArduXposureMessageProtocol.DEVICE_CAMERA);
	}
	
	@Override
	public String getName() {
		return "ArduXposure Message Protocol";
	}
	
	@Override
	public String createStartMessage() {
		return createStartMessage(1, 0);
	}
	
	@Override
	public String createStartMessage(final int rounds, final int delay) {
		
		byte[] result = new byte[] { ArduXposureMessageProtocol.COMMAND_RELEASE, (byte) rounds, (byte) (delay * 1000) };
		
		return new String(result);
	}
	
	/**
	 * Concat bytes to one byte array.
	 */
	private static final byte[] concat(final byte[]... bytes) {
		byte[] result = new byte[] {};
		
		for (byte[] b : bytes) {
			for (byte element : b) {
				ArrayUtils.add(result, element);
			}
		}
		
		return result;
	}
	
	private static final byte[] intToByteArray(final int value, final int bytes) {
		byte[] result = new byte[] { (byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) value };
		return ArrayUtils.subarray(result, 0, bytes - 1);
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
			
			result += ArduXposureMessageProtocol.COMMAND_SEND + ArduXposureMessageProtocol.FIELD_SEPARATOR;
			
			// get absolute position
			// result += (droplet.getDevices(IActionDevice.class).indexOf(d) +
			// 1) + FIELD_SEPARATOR;
			result += d.getNumber() + ArduXposureMessageProtocol.FIELD_SEPARATOR;
			
			result += ArduXposureMessageProtocol.DEVICE_SHORTS.get(deviceClass);
			
			int chksum = 0;
			for (IAction a : enabledActions) {
				chksum += a.getOffset();
				result += ArduXposureMessageProtocol.FIELD_SEPARATOR + a.getOffset();
				if (a instanceof IDurationAction) {
					chksum += ((IDurationAction) a).getDuration();
					result += ArduXposureMessageProtocol.TIME_SEPARATOR + ((IDurationAction) a).getDuration();
				}
			}
			result += ArduXposureMessageProtocol.CHKSUM_SEPARATOR + chksum
					+ ArduXposureMessageProtocol.DEVICE_SEPARATOR;
		}
		
		return result;
	}
	
	@Override
	public String createInfoMessage() {
		return ArduXposureMessageProtocol.COMMAND_INFO;
	}
	
	@Override
	public String createResetMessage() {
		return ArduXposureMessageProtocol.COMMAND_RESET;
	}
	
	@Override
	public String createCancelMessage() {
		return ArduXposureMessageProtocol.COMMAND_CANCEL;
	}
	
	@Override
	public String createDeviceOffMessage(final IDroplet droplet, final IDevice device) {
		// int deviceNumber = droplet.getDevices().indexOf(device) + 1;
		return ArduXposureMessageProtocol.COMMAND_LOW + ArduXposureMessageProtocol.FIELD_SEPARATOR + device.getNumber();
	}
	
	@Override
	public String createDeviceOnMessage(final IDroplet droplet, final IDevice device) {
		// int deviceNumber = droplet.getDevices().indexOf(device) + 1;
		return ArduXposureMessageProtocol.COMMAND_HIGH + ArduXposureMessageProtocol.FIELD_SEPARATOR
				+ device.getNumber();
	}
	
}
