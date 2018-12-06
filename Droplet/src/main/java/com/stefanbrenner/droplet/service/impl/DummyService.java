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

import java.util.Set;

import org.mangosdk.spi.ProviderFor;

import com.google.common.collect.Sets;
import com.stefanbrenner.droplet.model.IDropletContext;
import com.stefanbrenner.droplet.service.ISerialCommunicationService;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * Dummy service for serial communication.
 * <p>
 *
 * @author Stefan Brenner
 */
@ProviderFor(ISerialCommunicationService.class)
@Slf4j
public class DummyService implements ISerialCommunicationService {
	
	private boolean connected = false;
	
	@Override
	public String getName() {
		return "Dummy Service";
	}
	
	@Override
	public Set<String> getPorts() {
		return Sets.newHashSet("TestPort-1", "TestPort-2", "TestPort-3");
	}
	
	@Override
	public boolean connect(final String portId, final IDropletContext context) {
		connected = true;
		return connected;
	}
	
	@Override
	public void close() {
		connected = false;
	}
	
	@Override
	public void sendData(final String message) {
		log.info("send message: {}", message);
	}
	
	@Override
	public boolean isConnected() {
		return connected;
	}
	
}
