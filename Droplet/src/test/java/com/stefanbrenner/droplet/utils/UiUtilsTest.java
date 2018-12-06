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
package com.stefanbrenner.droplet.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.lang3.ObjectUtils;
import org.junit.Test;

/**
 * @author Stefan Brenner
 */
public class UiUtilsTest {
	
	@Test
	public void testFormatMillis() {
		assertThat(UiUtils.formatSeconds(1)).isEqualTo("0h 0min 1sec");
		assertThat(UiUtils.formatSeconds(61)).isEqualTo("0h 1min 1sec");
		assertThat(UiUtils.formatSeconds(3601)).isEqualTo("1h 0min 1sec");
		assertThat(UiUtils.formatSeconds(3599)).isEqualTo("0h 59min 59sec");
		assertThat(UiUtils.formatSeconds(86399)).isEqualTo("23h 59min 59sec");
	}
	
	@Test
	public void testSetEnabledRecursive() {
		JPanel testPanel = new JPanel();
		
		JButton btnTest = new JButton();
		testPanel.add(btnTest);
		JTextField txtTest = new JTextField();
		testPanel.add(txtTest);
		
		JPanel testPanel2 = new JPanel();
		JCheckBox chTest = new JCheckBox();
		testPanel2.add(chTest);
		
		testPanel.add(testPanel2);
		
		List<JComponent> allComps = Arrays.asList(new JComponent[] { testPanel, btnTest, txtTest, testPanel2, chTest });
		
		// disable all components
		UiUtils.setEnabledRecursive(testPanel, false);
		for (JComponent comp : allComps) {
			assertThat(comp.isEnabled()).isFalse();
		}
		// re-enable all components
		UiUtils.setEnabledRecursive(testPanel, true);
		for (JComponent comp : allComps) {
			assertThat(comp.isEnabled()).isTrue();
		}
		
		// disable all except txtTest
		UiUtils.setEnabledRecursive(testPanel, false, txtTest);
		for (JComponent comp : allComps) {
			if (ObjectUtils.notEqual(comp, txtTest)) {
				assertThat(comp.isEnabled()).isFalse();
			} else {
				assertThat(comp.isEnabled()).isTrue();
			}
		}
		// re-enable all components
		UiUtils.setEnabledRecursive(testPanel, true);
		for (JComponent comp : allComps) {
			assertThat(comp.isEnabled()).isTrue();
		}
		
		// disable all except chTest in testPanel2
		UiUtils.setEnabledRecursive(testPanel, false, chTest);
		for (JComponent comp : allComps) {
			if (ObjectUtils.notEqual(comp, chTest)) {
				assertThat(comp.isEnabled()).isFalse();
			} else {
				assertThat(comp.isEnabled()).isTrue();
			}
		}
		// re-enable all components
		UiUtils.setEnabledRecursive(testPanel, true);
		for (JComponent comp : allComps) {
			assertThat(comp.isEnabled()).isTrue();
		}
		
		// disable all except txtTest in testPanel and chTest in testPanel2
		JComponent[] except = new JComponent[] { txtTest, chTest };
		UiUtils.setEnabledRecursive(testPanel, false, except);
		for (JComponent comp : allComps) {
			if (!Arrays.asList(except).contains(comp)) {
				assertThat(comp.isEnabled()).isFalse();
			} else {
				assertThat(comp.isEnabled()).isTrue();
			}
		}
		// re-enable all components
		UiUtils.setEnabledRecursive(testPanel, true);
		for (JComponent comp : allComps) {
			assertThat(comp.isEnabled()).isTrue();
		}
		
	}
	
	@Test
	public void testSetEnabled() {
		JPanel testPanel = new JPanel();
		
		JButton btnTest = new JButton();
		testPanel.add(btnTest);
		JTextField txtTest = new JTextField();
		testPanel.add(txtTest);
		
		JPanel testPanel2 = new JPanel();
		JCheckBox chTest = new JCheckBox();
		testPanel2.add(chTest);
		
		testPanel.add(testPanel2);
		
		List<JComponent> allComps = Arrays.asList(new JComponent[] { testPanel, btnTest, txtTest, testPanel2, chTest });
		List<JComponent> childComps = Arrays.asList(new JComponent[] { testPanel, btnTest, txtTest, testPanel2 });
		
		// disable all components
		UiUtils.setEnabled(testPanel, false);
		for (JComponent comp : allComps) {
			if (childComps.contains(comp)) {
				assertThat(comp.isEnabled()).isFalse();
			} else {
				assertThat(comp.isEnabled()).isTrue();
			}
		}
		// re-enable all components
		UiUtils.setEnabled(testPanel, true);
		for (JComponent comp : allComps) {
			assertThat(comp.isEnabled()).isTrue();
		}
		
		// disable all except txtTest
		UiUtils.setEnabled(testPanel, false, txtTest);
		for (JComponent comp : allComps) {
			if (childComps.contains(comp) && ObjectUtils.notEqual(comp, txtTest)) {
				assertThat(comp.isEnabled()).isFalse();
			} else {
				assertThat(comp.isEnabled()).isTrue();
			}
		}
		// re-enable all components
		UiUtils.setEnabled(testPanel, true);
		for (JComponent comp : allComps) {
			assertThat(comp.isEnabled()).isTrue();
		}
		
		// disable all except chTest in testPanel2
		UiUtils.setEnabled(testPanel, false, chTest);
		for (JComponent comp : allComps) {
			if (childComps.contains(comp) && ObjectUtils.notEqual(comp, chTest)) {
				assertThat(comp.isEnabled()).isFalse();
			} else {
				assertThat(comp.isEnabled()).isTrue();
			}
		}
		// re-enable all components
		UiUtils.setEnabled(testPanel, true);
		for (JComponent comp : allComps) {
			assertThat(comp.isEnabled()).isTrue();
		}
		
		// disable all except txtTest in testPanel and chTest in testPanel2
		JComponent[] except = new JComponent[] { txtTest, chTest };
		UiUtils.setEnabled(testPanel, false, except);
		for (JComponent comp : allComps) {
			if (childComps.contains(comp) && !Arrays.asList(except).contains(comp)) {
				assertThat(comp.isEnabled()).isFalse();
			} else {
				assertThat(comp.isEnabled()).isTrue();
			}
		}
		// re-enable all components
		UiUtils.setEnabled(testPanel, true);
		for (JComponent comp : allComps) {
			assertThat(comp.isEnabled()).isTrue();
		}
		
	}
	
	@Test
	public void testCreateGridBagConstraints() {
		
		GridBagConstraints gbc = UiUtils.createGridBagConstraints(3, 4);
		assertThat(gbc.gridx).isEqualTo(3);
		assertThat(gbc.gridy).isEqualTo(4);
		assertThat(gbc.gridwidth).isEqualTo(1);
		assertThat(gbc.gridheight).isEqualTo(1);
		assertThat(gbc.weightx).isEqualTo(0.0);
		assertThat(gbc.weighty).isEqualTo(0.0);
		assertThat(gbc.anchor).isEqualTo(GridBagConstraints.CENTER);
		assertThat(gbc.fill).isEqualTo(GridBagConstraints.NONE);
		
		gbc = UiUtils.createGridBagConstraints(5, 6, 7, 8);
		assertThat(gbc.gridx).isEqualTo(5);
		assertThat(gbc.gridy).isEqualTo(6);
		assertThat(gbc.gridwidth).isEqualTo(1);
		assertThat(gbc.gridheight).isEqualTo(1);
		assertThat(gbc.weightx).isEqualTo(7.0);
		assertThat(gbc.weighty).isEqualTo(8.0);
		assertThat(gbc.anchor).isEqualTo(GridBagConstraints.CENTER);
		assertThat(gbc.fill).isEqualTo(GridBagConstraints.NONE);
		
		gbc = UiUtils.createGridBagConstraints(9, 10, 11, 12, GridBagConstraints.WEST);
		assertThat(gbc.gridx).isEqualTo(9);
		assertThat(gbc.gridy).isEqualTo(10);
		assertThat(gbc.gridwidth).isEqualTo(1);
		assertThat(gbc.gridheight).isEqualTo(1);
		assertThat(gbc.weightx).isEqualTo(11.0);
		assertThat(gbc.weighty).isEqualTo(12.0);
		assertThat(gbc.anchor).isEqualTo(GridBagConstraints.WEST);
		assertThat(gbc.fill).isEqualTo(GridBagConstraints.NONE);
		
	}
	
	@Test
	public void testEditGridBagConstraints() {
		GridBagConstraints gbc = new GridBagConstraints(1, 2, 3, 4, 5.0, 6.0, GridBagConstraints.WEST,
				GridBagConstraints.BOTH, new Insets(1, 2, 3, 4), 9, 0);
		
		UiUtils.editGridBagConstraints(gbc, 11, 12);
		assertThat(gbc.gridx).isEqualTo(11);
		assertThat(gbc.gridy).isEqualTo(12);
		assertThat(gbc.gridwidth).isEqualTo(3);
		assertThat(gbc.gridheight).isEqualTo(4);
		assertThat(gbc.weightx).isEqualTo(0.0);
		assertThat(gbc.weighty).isEqualTo(0.0);
		assertThat(gbc.anchor).isEqualTo(GridBagConstraints.CENTER);
		assertThat(gbc.fill).isEqualTo(GridBagConstraints.BOTH);
		
		UiUtils.editGridBagConstraints(gbc, 13, 14, 15.0, 16.0);
		assertThat(gbc.gridx).isEqualTo(13);
		assertThat(gbc.gridy).isEqualTo(14);
		assertThat(gbc.gridwidth).isEqualTo(3);
		assertThat(gbc.gridheight).isEqualTo(4);
		assertThat(gbc.weightx).isEqualTo(15.0);
		assertThat(gbc.weighty).isEqualTo(16.0);
		assertThat(gbc.anchor).isEqualTo(GridBagConstraints.CENTER);
		assertThat(gbc.fill).isEqualTo(GridBagConstraints.BOTH);
		
		UiUtils.editGridBagConstraints(gbc, 17, 18, 19.0, 20.0, GridBagConstraints.WEST);
		assertThat(gbc.gridx).isEqualTo(17);
		assertThat(gbc.gridy).isEqualTo(18);
		assertThat(gbc.gridwidth).isEqualTo(3);
		assertThat(gbc.gridheight).isEqualTo(4);
		assertThat(gbc.weightx).isEqualTo(19.0);
		assertThat(gbc.weighty).isEqualTo(20.0);
		assertThat(gbc.anchor).isEqualTo(GridBagConstraints.WEST);
		assertThat(gbc.fill).isEqualTo(GridBagConstraints.BOTH);
		
	}
	
}
