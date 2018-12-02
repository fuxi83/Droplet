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
package com.stefanbrenner.droplet.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;

import org.apache.commons.lang3.StringUtils;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.SpinnerAdapterFactory;
import com.jgoodies.binding.beans.BeanAdapter;
import com.stefanbrenner.droplet.model.IAction;
import com.stefanbrenner.droplet.model.IActionDevice;
import com.stefanbrenner.droplet.model.IDurationAction;
import com.stefanbrenner.droplet.ui.components.MouseWheelSpinner;
import com.stefanbrenner.droplet.utils.DropletColors;
import com.stefanbrenner.droplet.utils.Messages;
import com.stefanbrenner.droplet.utils.UiUtils;

/**
 * Panel for action devices.
 * 
 * @author Stefan Brenner
 * 
 * @param <T>
 *            type of action device to be displayed in this panel
 */
public class ActionPanel<T extends IAction> extends JPanel {
	
	private static final int MAX_TIME_INPUT = 99999;
	
	private static final long serialVersionUID = 1L;
	
	// model objects
	private IActionDevice device;
	private T action;
	
	// UI components
	private final JCheckBox cbEnable;
	private final JSpinner spOffset;
	private final JSpinner spDuration;
	private final JButton btnRemove;
	
	/**
	 * Create the panel.
	 */
	public ActionPanel(final IActionDevice device, final T action) {
		
		setDevice(device);
		setAction(action);
		
		setLayout(new GridBagLayout());
		setBackground(DropletColors.getBackgroundColor(getDevice()));
		
		GridBagConstraints gbc = UiUtils.createGridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(2, 2, 2, 2);
		
		BeanAdapter<IAction> adapter = new BeanAdapter<IAction>(action, true);
		
		// enabled checkbox
		cbEnable = BasicComponentFactory.createCheckBox(adapter.getValueModel(IAction.PROPERTY_ENABLED),
				StringUtils.EMPTY);
		cbEnable.setToolTipText(Messages.getString("ActionPanel.enableAction.tooltip")); //$NON-NLS-1$
		cbEnable.setFocusable(false);
		UiUtils.editGridBagConstraints(gbc, 0, 0, 0, 0);
		add(cbEnable, gbc);
		
		// offset spinner
		spOffset = new MouseWheelSpinner(true);
		spOffset.setToolTipText(Messages.getString("ActionPanel.Offset.Tooltip")); //$NON-NLS-1$
		spOffset.setModel(SpinnerAdapterFactory.createNumberAdapter(adapter.getValueModel(IAction.PROPERTY_OFFSET), 0,
				0, ActionPanel.MAX_TIME_INPUT, 1));
		((JSpinner.DefaultEditor) spOffset.getEditor()).getTextField().setColumns(3);
		UiUtils.editGridBagConstraints(gbc, 1, 0, 0, 0);
		add(spOffset, gbc);
		
		// duration spinner
		spDuration = new MouseWheelSpinner(true);
		spDuration.setToolTipText(Messages.getString("ActionPanel.Duration.Tooltip")); //$NON-NLS-1$
		if (action instanceof IDurationAction) {
			spDuration.setModel(SpinnerAdapterFactory.createNumberAdapter(
					adapter.getValueModel(IDurationAction.PROPERTY_DURATION), 0, 0, ActionPanel.MAX_TIME_INPUT, 1));
			((JSpinner.DefaultEditor) spDuration.getEditor()).getTextField().setColumns(3);
			UiUtils.editGridBagConstraints(gbc, 2, 0, 0, 0);
			add(spDuration, gbc);
		}
		
		// remove button
		btnRemove = new JButton(Messages.getString("ActionPanel.removeAction")); //$NON-NLS-1$
		btnRemove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				remove();
			}
		});
		btnRemove.setToolTipText(Messages.getString("ActionPanel.removeAction.tooltip")); //$NON-NLS-1$
		btnRemove.setFocusable(false);
		UiUtils.editGridBagConstraints(gbc, 3, 0, 0, 0);
		add(btnRemove, gbc);
		
	}
	
	private void remove() {
		getDevice().removeAction(getAction());
	}
	
	@Override
	public Dimension getMaximumSize() {
		Dimension size = getPreferredSize();
		size.width = Short.MAX_VALUE;
		return size;
	}
	
	public IActionDevice getDevice() {
		return device;
	}
	
	public void setDevice(final IActionDevice device) {
		this.device = device;
	}
	
	public T getAction() {
		return action;
	}
	
	public void setAction(final T action) {
		this.action = action;
	}
	
}
