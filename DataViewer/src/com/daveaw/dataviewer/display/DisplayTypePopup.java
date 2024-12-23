package com.daveaw.dataviewer.display;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;

public class DisplayTypePopup extends JPopupMenu{

	private static final long serialVersionUID = 1L;
	private ButtonGroup bg;
	
	public DisplayTypePopup() {
		JRadioButtonMenuItem rdbtn_none = new JRadioButtonMenuItem("None");
		JRadioButtonMenuItem rdbtn_ascii = new JRadioButtonMenuItem("ASCII");
		JRadioButtonMenuItem rdbtn_decimal = new JRadioButtonMenuItem("Decimal");
		rdbtn_none.setSelected(true);
		
		ActionListener action_listener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (bg.getSelection() == rdbtn_ascii.getModel()) {
					HexDisplay.changeDisplayType(HexDisplay.DisplayType.ASCII);
				}else if (bg.getSelection() == rdbtn_none.getModel()) {
					HexDisplay.changeDisplayType(HexDisplay.DisplayType.NONE);
				}else if (bg.getSelection() == rdbtn_decimal.getModel()) {
					HexDisplay.changeDisplayType(HexDisplay.DisplayType.DECIMAL);
				}
			}
		};
		
		rdbtn_none.addActionListener(action_listener);
		rdbtn_ascii.addActionListener(action_listener);
		rdbtn_decimal.addActionListener(action_listener);
		
		bg = new ButtonGroup();
		bg.add(rdbtn_none);
		bg.add(rdbtn_ascii);
		bg.add(rdbtn_decimal);
		
		//add(this, BorderLayout.NORTH);
		add(rdbtn_none);
		add(rdbtn_ascii);
		add(rdbtn_decimal);
	}
	
}
