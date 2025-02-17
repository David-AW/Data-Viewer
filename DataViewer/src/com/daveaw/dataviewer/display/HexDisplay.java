package com.daveaw.dataviewer.display;

import java.awt.Color;
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
import com.daveaw.dataviewer.frame.DataFrame;

public class HexDisplay extends JPanel implements UserSettings, AdjustmentListener{

	private static final long serialVersionUID = 1L;

	private final int PADDING = 6;

	private int char_width;
	private int char_height_offset;
	private int cell_width = 1;
	private int cell_height = 1;
	private int cell_count_horizontal;
	private int cell_count_vertical;
	private boolean is_mouse_down = false;

	int numbers_column_width;
	int fields_row_height;
	int starting_num;
	int starting_field;

	private final int instance_index;
	private static int instance_count = 0;

	public static enum DisplayType{NONE, ASCII, DECIMAL};
	private static DisplayType display_type = DisplayType.NONE;
	private static DisplayTypePopup popup = new DisplayTypePopup();
	private static HashMap<Integer, HexDisplay> instances = new HashMap<Integer, HexDisplay>();
	private static Color[] data_color_map = {foreground, foreground.brighter(), foreground_highlighted_constant, foreground_highlighted_constant.brighter()};

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

		/* ===============
		 * FILL BACKGROUND
		 * ===============*/

		// fill screen with background color
		g.setColor(background_display);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		if (DataViewer.getDataStore() == null) {
			DataViewer.getScrollbarFields().setValues(0, 0, 0, 0); // Hide the handles on the scroll bar if there is no data to show
			return;
		}

		calculateCellSizes(g); // Calculates the cell sizes based off the size of a 'space' character and modified by display mode
		numbers_column_width = char_width * (DataViewer.getDataStore().getCount()+"").length() + PADDING;
		fields_row_height = char_width * (DataViewer.getDataStore().getMaxLength()+"").length() + PADDING;

		/* ================================================
		 * DRAW BACKGROUND FOR FIELD AND NUMBERS COMPONENTS
		 * ================================================*/
		
		//Draw background for fields display
		g.setColor(background_sub_frame);
		g.fillRect(0, fields_row_height, numbers_column_width, getHeight());

		//Draw background for numbers display
		g.fillRect(0, 0, getWidth(), fields_row_height);

		/* ===================================
		 * CALCULATE VALUES NEEDED FOR CULLING
		 * ===================================*/
		
		starting_num = DataViewer.getScrollbarNumbers().getValue();
		starting_field = DataViewer.getScrollbarFields().getValue();

		// These are to limit the amount of data drawn to what is actually shown on screen (culling)
		int nums_to_render = DataViewer.getDataStore().getCount() - starting_num > cell_count_vertical ? cell_count_vertical : DataViewer.getDataStore().getCount() - starting_num;
		int fields_to_render = DataViewer.getDataStore().getMaxLength() - starting_field > cell_count_horizontal ? cell_count_horizontal : DataViewer.getDataStore().getMaxLength() - starting_field;

		int selection_start = DataViewer.getSelectionStartX() - starting_field;
		int selection_screen_width = DataViewer.getSelectionWidth() + Math.min(selection_start, 0);
		
		int half_cell_width = cell_width / 2;
		int half_character_width = char_width / 2;
		int eighth_cell_height = cell_height / 8;
		int display_type_offset = (int)(char_height_offset*2.25);
		
		boolean is_selection_within_shown_fields = !(DataViewer.getSelectionEndX() < starting_field || DataViewer.getSelectionStartX() > starting_field + fields_to_render);
		
		/* ================================
		 * PAINT FIELD SELECTION BACKGROUND
		 * ================================*/
		
		if (is_selection_within_shown_fields) {
			g.setColor(background_sub_frame.brighter());
			g.fillRect(numbers_column_width +  Math.max(selection_start, 0) * cell_width, 0, selection_screen_width * cell_width, fields_row_height);
		}
		
		for (int y = 0; y < nums_to_render; y++) {

			/* ===========================================
			 * CALCULATE NUMBER OF BYTES TO DRAW FOR FRAME
			 * ===========================================*/
			
			int y_offset = y*cell_height + fields_row_height;
			int y_value = y + starting_num;
			boolean is_selected_y = y_value >= DataViewer.getSelectionStartY() && y_value < DataViewer.getSelectionEndY();
			
			DataFrame data = DataViewer.getDataStore().storage().get(y_value);
			int bytes_on_screen = data.count() - starting_field > cell_count_horizontal ? cell_count_horizontal : data.count() - starting_field;
			
			/* ==================================
			 * PAINT NUMBERS SELECTION BACKGROUND
			 * ==================================*/
			
			if (is_selected_y) {
				g.setColor(background_sub_frame.brighter());
				g.fillRect(0, y_offset, numbers_column_width, cell_height); // Numbers component selection
			}
			
			/* =====================
			 * PAINT DATA BACKGROUND
			 * =====================*/
			
			g.setColor(background);
			g.fillRect(numbers_column_width, y_offset, bytes_on_screen * cell_width, cell_height);

			/* ===============================
			 * PAINT DATA SELECTION BACKGROUND
			 * ===============================*/
			
			if (is_selected_y && is_selection_within_shown_fields && DataViewer.getSelectionStartX() < data.count()) {
				g.setColor(background.brighter());
				g.fillRect(numbers_column_width +  Math.max(selection_start, 0) * cell_width, y_offset, selection_screen_width * cell_width, cell_height);
			}
			
			/* ================
			 * DRAW DATA VALUES
			 * ================*/
			
			for (int x = 0; x < fields_to_render; x++) {
				
				if (x >= bytes_on_screen)
					continue;
				
				int x_value = starting_field + x;
				int x_offset = x * cell_width + numbers_column_width;
				
				int color_key = is_selected_y && x_value >= DataViewer.getSelectionStartX() && x_value < DataViewer.getSelectionEndX() ? 1 : 0;
				
				g.setColor(data_color_map[color_key]);
				g.drawString(data.getHexValueAt(x_value), x_offset + half_cell_width - char_width, char_height_offset + eighth_cell_height + y_offset);
				
				if (display_type == DisplayType.NONE)
					continue;
				
				/* =========================
				 * DRAW DISPLAY TYPE RESULTS
				 * =========================*/
				
				g.setColor(data_color_map[color_key].darker());
				
				if (display_type == DisplayType.ASCII) {
					g.drawString(getAsciiValue(data.getData()[x_value]), x_offset + half_cell_width - half_character_width, display_type_offset + eighth_cell_height + y_offset);
				}else if (display_type == DisplayType.DECIMAL) {
					String decimal_value = Byte.toUnsignedInt(data.getData()[x_value])+"";
					g.drawString(decimal_value, x_offset + half_cell_width - half_character_width * decimal_value.length(), display_type_offset + eighth_cell_height + y_offset);
				}
			}
			
			/* ===================
			 * DRAW NUMBERS VALUES
			 * ===================*/
			
			g.setColor(is_selected_y ? background_sub_frame.brighter() : background_sub_frame);
			// Draw grid on numbers component
			g.draw3DRect(0, y*cell_height+fields_row_height, numbers_column_width, cell_height-1, true);
			
			g.setColor(is_selected_y ? foreground.brighter() : foreground);
			String number = data.getNumber() + "";
			g.drawString(number, numbers_column_width - (number.length() * char_width), y_offset + char_height_offset + ((cell_height / 2)-(char_height_offset/2)));
			
		}
		
		/* =================
		 * DRAW FIELD VALUES
		 * =================*/
		
		for (int x = 0; x < fields_to_render; x++) {
			int x_value = starting_field + x;
			boolean is_selected_x = x_value >= DataViewer.getSelectionStartX() && x_value < DataViewer.getSelectionEndX();
			g.setColor(is_selected_x ? background_sub_frame.brighter() : background_sub_frame);
			// Draw grid on fields component
			g.draw3DRect(numbers_column_width + cell_width * x, 0, cell_width-1, fields_row_height-1, true);
		}

		Graphics2D g2d = (Graphics2D) g;
		g2d.rotate(Math.toRadians(-90));
		for (int x = 0; x < fields_to_render; x++) {
			int x_value = starting_field + x;
			boolean is_selected_x = x_value >= DataViewer.getSelectionStartX() && x_value < DataViewer.getSelectionEndX();
			g2d.setColor(is_selected_x ? foreground.brighter() : foreground);
			//-----------STRING--------- +Y UP DIR------------ +X RIGHT DIR
			g2d.drawString((x+starting_field)+"", -fields_row_height + 2, cell_width*x + cell_width + numbers_column_width - PADDING);
		}
		g2d.rotate(Math.toRadians(90));

		DataViewer.getScrollbarFields().setValues(starting_field, cell_count_horizontal, 0, DataViewer.getDataStore().getMaxLength()+cell_count_horizontal-1);
		DataViewer.getScrollbarNumbers().setValues(starting_num, cell_count_vertical, 0, DataViewer.getDataStore().getCount()+cell_count_vertical-1);
	}


	private void pressed(MouseEvent e) {
		requestFocus();
		if (e.getButton() == MouseEvent.BUTTON3 && !popup.isVisible()) {
			popup.show(this, e.getX(), e.getY());
		}else if (e.getButton() == MouseEvent.BUTTON1) {
			int grid_pos_x = screenPosToGridPosX(e.getX());
			int grid_pos_y = screenPosToGridPosY(e.getY());
			
			grid_pos_x = grid_pos_x < 0 ? grid_pos_x : grid_pos_x + starting_field;
			grid_pos_y = grid_pos_y < 0 ? grid_pos_y : grid_pos_y + starting_num;
			
			if (grid_pos_y < DataViewer.getDataStore().storage().size())
				DataViewer.setSelectionPoint(grid_pos_x, grid_pos_y);
			else
				DataViewer.setSelectionPoint(-1, -1);

			is_mouse_down = true;
			repaint();
		}
	}

	private void released(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			is_mouse_down = false;
		}
	}

	private void dragged(MouseEvent e) {
		if (is_mouse_down && DataViewer.hasSelection()) {
			int grid_pos_x = Math.clamp(screenPosToGridPosX(e.getX()) + starting_field, 0, DataViewer.getDataStore().getMaxLength()-1);
			int grid_pos_y = Math.clamp(screenPosToGridPosY(e.getY()) + starting_num, 0, DataViewer.getDataStore().storage().size()-1);
			
			DataViewer.setSecondSelectionPoint(grid_pos_x, grid_pos_y);
			
			repaint();
		}
	}

	private int screenPosToGridPosX(int x) {
		if (x < numbers_column_width)
			return -1;
		return (x - numbers_column_width) / cell_width;
	}

	private int screenPosToGridPosY(int y) {
		if (y < fields_row_height)
			return -1;
		return (y - fields_row_height) / cell_height;
	}

	private void calculateCellSizes(Graphics g) {
		g.setFont(font);
		char_width = g.getFontMetrics().charWidth(' ');
		cell_height = g.getFontMetrics().getHeight();
		char_height_offset = cell_height * 3 / 4;
		cell_width = char_width*3;
		if (display_type == DisplayType.ASCII || display_type == DisplayType.DECIMAL)
			cell_height *= 2;
		if (display_type == DisplayType.DECIMAL)
			cell_width = char_width*4;

		cell_count_horizontal = getWidth()/cell_width + 1;
		cell_count_vertical = getHeight()/cell_height + 1;
	}

	public static void changeDisplayType(DisplayType type) {
		if (type == display_type)
			return;
		display_type = type;
		for (HexDisplay d : instances.values()) {
			d.repaint();
		}
	}
	
	@Override
	public void updateUserSettings() {
		setFont(font);
		setForeground(foreground);
		setBackground(background_display);
		data_color_map = new Color[]{foreground, foreground.brighter(), foreground_highlighted_constant, foreground_highlighted_constant.brighter()};
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