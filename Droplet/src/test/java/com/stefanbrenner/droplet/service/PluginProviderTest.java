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
package com.stefanbrenner.droplet.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.stefanbrenner.droplet.utils.PluginLoader;

/**
 * @author Stefan Brenner
 *
 */
public class PluginProviderTest {
	
	@Test
	public void testPluginLoader() {
		// find both service provider
		assertThat(PluginLoader.getPlugins(ITestService.class).size()).isEqualTo(2);
		
		List<String> results = new ArrayList<String>();
		for (ITestService service : PluginLoader.getPlugins(ITestService.class)) {
			results.add(service.getServiceName());
		}
		
		assertThat(results.contains("TestServiceProvider1")).isTrue();
		assertThat(results.contains("TestServiceProvider2")).isTrue();
		
	}
	
}
