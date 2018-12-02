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
package com.stefanbrenner.droplet.ui;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.junit.Test;

/**
 * @author Stefan Brenner
 *
 */
public class MessagesTest {
	
	private static final String BUNDLE_NAME = "testmessages"; //$NON-NLS-1$
	
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
	
	/**
	 * Read localized message from resources and format it.
	 *
	 * @see MessageFormat
	 */
	public static String getString(final String key, final Object... args) {
		try {
			String message = RESOURCE_BUNDLE.getString(key);
			MessageFormat format = new MessageFormat(message);
			return format.format(args);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
	
	@Test
	public void testGetMessage() {
		assertThat(MessagesTest.getString("MessagesTest")).isEqualTo("Basic");
		assertThat(MessagesTest.getString("MessagesTest.Simple")).isEqualTo("Simple");
		assertThat(MessagesTest.getString("MessagesTest.Advanced")).isEqualTo("Advanced {0}");
		
		assertThat(MessagesTest.getString("MessagesTest.Advanced", "Test")).isEqualTo("Advanced Test");
		assertThat(MessagesTest.getString("MessagesTest.Multiple.Ascending", "Test", "multiple", "!"))
				.isEqualTo("Advanced Test with multiple Parameters !");
		assertThat(MessagesTest.getString("MessagesTest.Multiple.Unsorted", "multiple", "!", "Test"))
				.isEqualTo("Advanced Test with multiple Parameters !");
		assertThat(MessagesTest.getString("MessagesTest.Multiple.Repeat", "Test")).isEqualTo("Advanced Test with Test");
		
		GregorianCalendar cal = new GregorianCalendar(2012, 0, 1);
		Object[] arguments = { new Integer(7), cal.getTime(), "a disturbance in the Force" };
		assertThat(MessagesTest.getString("MessagesTest.Number", arguments))
				.isEqualTo("At " + SimpleDateFormat.getTimeInstance().format(cal.getTime()) + " on "
						+ SimpleDateFormat.getDateInstance().format(cal.getTime())
						+ ", there was a disturbance in the Force on planet 7.");
		
	}
	
}
