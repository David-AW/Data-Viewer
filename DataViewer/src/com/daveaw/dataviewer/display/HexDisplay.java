package com.daveaw.dataviewer.display;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.HashMap;

import javax.swing.JPanel;

import com.daveaw.dataviewer.DataViewer;
import com.daveaw.dataviewer.UserSettings;

public class HexDisplay extends JPanel implements UserSettings, AdjustmentListener{

	private static final long serialVersionUID = 1L;
	private final String[] HEXMAP = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
	
	private final int PADDING = 6;
	
	private int char_width;
	private int char_height_offset;
	private int cell_width;
	private int cell_height;
	private int cell_count_horizontal;
	private int cell_count_vertical;

	private final int instance_index;
	private static int instance_count = 0;
	
	public static enum DisplayType{NONE, ASCII, DECIMAL};
	private static DisplayType currentDisplayType = DisplayType.NONE;
	private static DisplayTypePopup popup = new DisplayTypePopup();
	private static HashMap<Integer, HexDisplay> instances = new HashMap<Integer, HexDisplay>();
	
	public HexDisplay() {
		setDoubleBuffered(true);
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) { pressed(e); }
			@Override
			public void mouseReleased(MouseEvent e) { released(e); }
		});
		
		addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent e) { dragged(e); }
			@Override
			public void mouseMoved(MouseEvent e) {}
		});
		
		addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if ((e.getModifiersEx() & MouseWheelEvent.SHIFT_DOWN_MASK) != 0)
					DataViewer.getScrollbarFields().setValue(DataViewer.getScrollbarFields().getValue() + e.getWheelRotation());
				else
					DataViewer.getScrollbarNumbers().setValue(DataViewer.getScrollbarNumbers().getValue() + e.getWheelRotation());
			}
		});
		
		DataViewer.getScrollbarFields().addAdjustmentListener(this);
		DataViewer.getScrollbarNumbers().addAdjustmentListener(this);
		
		instance_index = instance_count++;
		instances.put(instance_index, this);
		setFocusable(true);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.setColor(background_display);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		if (DataViewer.getDataStore() == null) {
			DataViewer.getScrollbarFields().setValues(0, 0, 0, 0);
			return;
		}
		
		calculate_cell_sizes(g);
		Graphics2D g2d = (Graphics2D) g;
		
		int numbers_column_width = char_width * (DataViewer.getDataStore().getCount()+"").length() + PADDING;
		int fields_row_height = char_width * (DataViewer.getDataStore().getMaxLength()+"").length() + PADDING;
		
		g.setColor(background_sub_frame);
		g.fillRect(0, fields_row_height, numbers_column_width, getHeight());
		
		int starting_num = DataViewer.getScrollbarNumbers().getValue();
		int starting_field = DataViewer.getScrollbarFields().getValue();
		int remaining_numbers = DataViewer.getDataStore().getCount() - starting_num;
		int nums_to_render = remaining_numbers > cell_count_vertical ? cell_count_vertical : remaining_numbers;
		int fields_to_render = DataViewer.getDataStore().getMaxLength() - starting_field > cell_count_horizontal ? cell_count_horizontal : DataViewer.getDataStore().getMaxLength() - starting_field;
		
		g.fillRect(0, 0, getWidth(), fields_row_height);
		for (int x = 0; x < fields_to_render; x++) {
			g.draw3DRect(numbers_column_width + cell_width * x, 0, cell_width-1, fields_row_height-1, true);
		}
		
		g2d.rotate(Math.toRadians(-90));
		for (int x = 0; x < fields_to_render; x++) {
			g2d.setColor(foreground);
			//-----------STRING--------- +Y UP DIR------------ +X RIGHT DIR
			g2d.drawString((x+starting_field)+"", -fields_row_height + 2, cell_width*x + cell_width + numbers_column_width - PADDING);
		}
		g2d.rotate(Math.toRadians(90));
		
		for (int y = 0; y < nums_to_render; y++) {
			byte[] data = DataViewer.getDataStore().storage().get(starting_num + y).getData();
			fields_to_render = data.length - starting_field > cell_count_horizontal ? cell_count_horizontal : data.length - starting_field;
			
			g.setColor(background_sub_frame);
			g.draw3DRect(0, y*cell_height+fields_row_height, numbers_column_width, cell_height-1, true);
			
			g.setColor(background);
			g.fillRect(numbers_column_width, y*cell_height + fields_row_height, fields_to_render * cell_width, cell_height);
			g.setColor(foreground);
			int number = y + starting_num;
			g.drawString(number+"", numbers_column_width - ((number+"").length() * char_width), y*cell_height + char_height_offset + fields_row_height + ((cell_height / 2)-(char_height_offset/2)));
			for (int field = 0; field < fields_to_render; field++) {
				g.setColor(foreground);
				g.drawString(toHexString(data[field+starting_field]), cell_width*field + (cell_width/2 - char_width) + numbers_column_width, y*cell_height + char_height_offset + fields_row_height);
				g.setColor(foreground.darker());
				if (currentDisplayType == DisplayType.ASCII) {
					g.drawString(getAsciiValue(data[field+starting_field]), cell_width*field + (cell_width/2 - char_width/2) + numbers_column_width, y*cell_height + char_height_offset*2 + fields_row_height);
				}else if (currentDisplayType == DisplayType.DECIMAL) {
					String decimal_value = Byte.toUnsignedInt(data[field+starting_field])+"";
					g.drawString(decimal_value, cell_width*field + (cell_width/2 - char_width/2) + numbers_column_width - ((decimal_value.length()-1) * char_width / 2), y*cell_height + char_height_offset*2 + fields_row_height + char_height_offset/4);
				}
			}
		}
		
		DataViewer.getScrollbarFields().setValues(starting_field, cell_count_horizontal, 0, DataViewer.getDataStore().getMaxLength()+cell_count_horizontal-1);
		DataViewer.getScrollbarNumbers().setValues(starting_num, cell_count_vertical, 0, DataViewer.getDataStore().getCount()+cell_count_vertical-1);
	}

	private void pressed(MouseEvent e) {
		requestFocus();
		if (e.getButton() == MouseEvent.BUTTON3 && !popup.isVisible()) {
			popup.show(this, e.getX(), e.getY());
		}
	}

	private void released(MouseEvent e) {

	}
	
	private void dragged(MouseEvent e) {

	}

	private void calculate_cell_sizes(Graphics g) {
		g.setFont(font);
		char_width = g.getFontMetrics().charWidth(' ');
		cell_height = g.getFontMetrics().getHeight();
		char_height_offset = cell_height * 3 / 4;
		cell_width = char_width*3;
		if (currentDisplayType == DisplayType.ASCII || currentDisplayType == DisplayType.DECIMAL)
			cell_height *= 2;
		if (currentDisplayType == DisplayType.DECIMAL)
			cell_width = char_width*4;
		
		cell_count_horizontal = getWidth()/cell_width + 1;
		cell_count_vertical = getHeight()/cell_height + 1;
	}
	
	public static void changeDisplayType(DisplayType type) {
		if (type == currentDisplayType)
			return;
		currentDisplayType = type;
		for (HexDisplay d : instances.values()) {
			d.repaint();
		}
	}
	
	@Override
	public void updateUserSettings() {
		setFont(font);
		setForeground(foreground);
		setBackground(background_display);
	}
	
	public String toHexString(byte b) {
		return HEXMAP[(b&0xF0)>>>4] + HEXMAP[b&0xF];
	}
	
	public String getAsciiValue(byte b) {
		if (b < 0x20 || b > 0x7E) 
			return ".";
		return (char) b + "";
	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		repaint();
	}

}