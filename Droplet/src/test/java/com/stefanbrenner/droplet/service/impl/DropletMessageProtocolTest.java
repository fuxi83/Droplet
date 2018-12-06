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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.stefanbrenner.droplet.model.ICamera;
import com.stefanbrenner.droplet.model.IDroplet;
import com.stefanbrenner.droplet.model.IFlash;
import com.stefanbrenner.droplet.model.IValve;
import com.stefanbrenner.droplet.model.internal.Action;
import com.stefanbrenner.droplet.model.internal.Camera;
import com.stefanbrenner.droplet.model.internal.Droplet;
import com.stefanbrenner.droplet.model.internal.DurationAction;
import com.stefanbrenner.droplet.model.internal.Flash;
import com.stefanbrenner.droplet.model.internal.Valve;
import com.stefanbrenner.droplet.service.IDropletMessageProtocol;

/**
 * @author Stefan Brenner
 *
 */
public class DropletMessageProtocolTest {
	
	@Test
	public void testCreateStartMessage() {
		IDropletMessageProtocol protocol = new DropletMessageProtocol();
		assertThat(protocol.createStartMessage()).isEqualTo("R;1^1");
		assertThat(protocol.createStartMessage(1, 0)).isEqualTo("R;1^1");
		assertThat(protocol.createStartMessage(5, 0)).isEqualTo("R;5^5");
		assertThat(protocol.createStartMessage(1, 2)).isEqualTo("R;1;2000^2001");
		assertThat(protocol.createStartMessage(5, 10)).isEqualTo("R;5;10000^10005");
	}
	
	@Test
	public void testCreateInfoMessage() {
		IDropletMessageProtocol protocol = new DropletMessageProtocol();
		assertThat(protocol.createInfoMessage()).isEqualTo("I");
	}
	
	@Test
	public void testCreateResetMessage() {
		IDropletMessageProtocol protocol = new DropletMessageProtocol();
		assertThat(protocol.createResetMessage()).isEqualTo("X");
	}
	
	@Test
	public void testCreateSetMessage() {
		IDropletMessageProtocol protocol = new DropletMessageProtocol();
		
		IDroplet droplet = new Droplet();
		
		// devices
		IValve valve1 = new Valve();
		IValve valve2 = new Valve();
		IFlash flash1 = new Flash();
		IFlash flash2 = new Flash();
		ICamera camera1 = new Camera();
		ICamera camera2 = new Camera();
		
		// actions
		DurationAction action1 = new DurationAction();
		DurationAction action2 = new DurationAction();
		DurationAction action3 = new DurationAction();
		Action action4 = new Action();
		Action action5 = new Action();
		
		// empty droplet
		assertThat(protocol.createSetMessage(droplet)).isEmpty();
		
		droplet.addDevice(valve1);
		droplet.addDevice(valve2);
		
		// droplet with empty valves
		assertThat(protocol.createSetMessage(droplet)).isEmpty();
		
		droplet.addDevice(flash1);
		droplet.addDevice(flash2);
		
		// droplet with empty flashes
		assertThat(protocol.createSetMessage(droplet)).isEmpty();
		
		droplet.addDevice(camera1);
		droplet.addDevice(camera2);
		
		// droplet with empty cameras
		assertThat(protocol.createSetMessage(droplet)).isEmpty();
		
		valve1.addAction(action1);
		valve1.addAction(action2);
		valve1.addAction(action3);
		
		assertThat(protocol.createSetMessage(droplet)).isEqualTo("S;1;V;0|0;0|0;0|0^0\n");
		
		valve2.addAction(action3);
		valve2.addAction(action2);
		
		assertThat(protocol.createSetMessage(droplet)).isEqualTo("S;1;V;0|0;0|0;0|0^0\nS;2;V;0|0;0|0^0\n");
		
		flash2.addAction(action4);
		flash2.addAction(action5);
		
		assertThat(protocol.createSetMessage(droplet)).isEqualTo("S;1;V;0|0;0|0;0|0^0\nS;2;V;0|0;0|0^0\nS;4;F;0;0^0\n");
		
		camera1.addAction(action2);
		
		// droplet with actions that have no times
		assertThat(protocol.createSetMessage(droplet))
				.isEqualTo("S;1;V;0|0;0|0;0|0^0\nS;2;V;0|0;0|0^0\nS;4;F;0;0^0\nS;5;C;0|0^0\n");
		
		action1.setOffset(0);
		action1.setDuration(20);
		action2.setOffset(20);
		action2.setDuration(40);
		action3.setOffset(80);
		action3.setDuration(100);
		action4.setOffset(5);
		action5.setOffset(105);
		
		// droplet with actions that have times
		assertThat(protocol.createSetMessage(droplet))
				.isEqualTo("S;1;V;0|20;20|40;80|100^260\nS;2;V;80|100;20|40^240\nS;4;F;5;105^110\nS;5;C;20|40^60\n");
		
		// devices can have all types of actions
		camera2.addAction(action1);
		camera2.addAction(action5);
		
		assertThat(protocol.createSetMessage(droplet)).isEqualTo(
				"S;1;V;0|20;20|40;80|100^260\nS;2;V;80|100;20|40^240\nS;4;F;5;105^110\nS;5;C;20|40^60\nS;6;C;0|20;105^125\n");
		
		// test disabled actions
		action2.setEnabled(false);
		
		assertThat(protocol.createSetMessage(droplet))
				.isEqualTo("S;1;V;0|20;80|100^200\nS;2;V;80|100^180\nS;4;F;5;105^110\nS;6;C;0|20;105^125\n");
		
	}
	
	@Test
	public void testCreateOnMessage() {
		IDropletMessageProtocol protocol = new DropletMessageProtocol();
		
		IDroplet droplet = new Droplet();
		
		// devices
		IValve valve1 = new Valve();
		IValve valve2 = new Valve();
		IFlash flash1 = new Flash();
		ICamera camera1 = new Camera();
		
		droplet.addDevice(valve1);
		droplet.addDevice(valve2);
		droplet.addDevice(flash1);
		droplet.addDevice(camera1);
		
		assertThat(protocol.createDeviceOnMessage(droplet, valve1)).isEqualTo("H;1");
		assertThat(protocol.createDeviceOnMessage(droplet, valve2)).isEqualTo("H;2");
		assertThat(protocol.createDeviceOnMessage(droplet, flash1)).isEqualTo("H;3");
		assertThat(protocol.createDeviceOnMessage(droplet, camera1)).isEqualTo("H;4");
		
	}
	
	@Test
	public void testCreateOffMessage() {
		IDropletMessageProtocol protocol = new DropletMessageProtocol();
		
		IDroplet droplet = new Droplet();
		
		// devices
		IValve valve1 = new Valve();
		IValve valve2 = new Valve();
		IFlash flash1 = new Flash();
		ICamera camera1 = new Camera();
		
		droplet.addDevice(valve1);
		droplet.addDevice(valve2);
		droplet.addDevice(flash1);
		droplet.addDevice(camera1);
		
		assertThat(protocol.createDeviceOffMessage(droplet, valve1)).isEqualTo("L;1");
		assertThat(protocol.createDeviceOffMessage(droplet, valve2)).isEqualTo("L;2");
		assertThat(protocol.createDeviceOffMessage(droplet, flash1)).isEqualTo("L;3");
		assertThat(protocol.createDeviceOffMessage(droplet, camera1)).isEqualTo("L;4");
	}
	
}
