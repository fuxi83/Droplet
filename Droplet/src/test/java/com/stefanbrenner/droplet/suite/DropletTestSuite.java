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
package com.stefanbrenner.droplet.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.stefanbrenner.droplet.model.ConfigurationTest;
import com.stefanbrenner.droplet.model.DeviceComperationTest;
import com.stefanbrenner.droplet.service.PluginProviderTest;
import com.stefanbrenner.droplet.service.impl.DropletMessageProtocolTest;
import com.stefanbrenner.droplet.ui.MessagesTest;
import com.stefanbrenner.droplet.utils.FileUtilsTest;
import com.stefanbrenner.droplet.utils.UiUtilsTest;

/**
 * Test Suite for all Droplet Tests.
 * 
 * @author Stefan Brenner
 */
@RunWith(Suite.class)
@SuiteClasses({ UiUtilsTest.class, PluginProviderTest.class, DeviceComperationTest.class, ConfigurationTest.class,
		DropletMessageProtocolTest.class, DropletMessageProtocolTest.class, MessagesTest.class, FileUtilsTest.class })
public class DropletTestSuite {
	
}
