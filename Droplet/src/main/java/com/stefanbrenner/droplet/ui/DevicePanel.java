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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.StringUtils;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.swing.FontIcon;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.beans.BeanAdapter;
import com.stefanbrenner.droplet.model.IDevice;
import com.stefanbrenner.droplet.model.IDroplet;
import com.stefanbrenner.droplet.utils.DropletColors;
import com.stefanbrenner.droplet.utils.Messages;

/**
 * Panel for action devices.
 * 
 * @param <T>
 *            type ot the action devices to be displayed in this panel
 * @author Stefan Brenner
 */
public class DevicePanel<T extends IDevice> extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	// model objects
	T device;
	
	// UI components
	final JComponent parent;
	final JTextField txtName;
	
	final JPanel actionsPanel;
	
	/**
	 * Create the panel.
	 */
	public DevicePanel(final JComponent parent, final IDroplet droplet, final T device) {
		
		this.parent = parent;
		
		setDevice(device);
		
		setLayout(new BorderLayout(0, 5));
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		setBackground(DropletColors.getBackgroundColor(device));
		
		BeanAdapter<T> adapter = new BeanAdapter<T>(device, true);
		
		// device name textfield
		txtName = BasicComponentFactory.createTextField(adapter.getValueModel(IDevice.PROPERTY_NAME));
		txtName.setHorizontalAlignment(SwingConstants.CENTER);
		txtName.setColumns(1);
		txtName.setToolTipText(device.getName());
		adapter.addBeanPropertyChangeListener(IDevice.PROPERTY_NAME, new PropertyChangeListener() {
			
			@Override
			public void propertyChange(final PropertyChangeEvent event) {
				txtName.setToolTipText(device.getName());
			}
		});
		add(txtName, BorderLayout.NORTH);
		
		// actions panel with scroll pane
		actionsPanel = new JPanel();
		actionsPanel.setLayout(new BoxLayout(actionsPanel, BoxLayout.Y_AXIS));
		actionsPanel.setBackground(getBackground());
		
		JScrollPane scrollPane = new JScrollPane(actionsPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		// resize vertical scrollbar
		scrollPane.getVerticalScrollBar().putClientProperty("JComponent.sizeVariant", "mini"); //$NON-NLS-1$ //$NON-NLS-2$
		SwingUtilities.updateComponentTreeUI(scrollPane);
		// we need no border
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		add(scrollPane, BorderLayout.CENTER);
		
		{
			JPanel panel = new JPanel();
			panel.setLayout(new GridLayout(0, 1));
			
			createAddButton(panel);
			
			// remove button
			FontIcon icon = FontIcon.of(FontAwesome.TRASH);
			icon.setIconColor(Color.GRAY);
			JButton btnRemove = new JButton(Messages.getString("ActionDevicePanel.removeDevice"), icon); //$NON-NLS-1$
			btnRemove.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(final ActionEvent action) {
					int retVal = JOptionPane.showConfirmDialog(DevicePanel.this,
							Messages.getString("ActionDevicePanel.removeDevice") + " '" + device.getName() + "'?", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
							StringUtils.EMPTY, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (retVal == JOptionPane.YES_OPTION) {
						droplet.removeDevice(device);
					}
				}
			});
			btnRemove.setFocusable(false);
			panel.add(btnRemove);
			
			add(panel, BorderLayout.SOUTH);
		}
		
	}
	
	void createAddButton(final JPanel panel) {
		// no add button needed
	}
	
	@Override
	public Dimension getMaximumSize() {
		Dimension size = getPreferredSize();
		size.height = Short.MAX_VALUE;
		return size;
	}
	
	public T getDevice() {
		return device;
	}
	
	public void setDevice(final T device) {
		this.device = device;
	}
	
}
