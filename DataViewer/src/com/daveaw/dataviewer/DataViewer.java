package com.daveaw.dataviewer;

import java.awt.EventQueue;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;

import com.daveaw.dataviewer.display.HexDisplay;
import com.daveaw.dataviewer.frame.DataFrameStorage;
import com.daveaw.dataviewer.frame.RawDataStore;
import com.daveaw.dataviewer.io.RawFileImporter;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.BorderFactory;
import java.awt.FlowLayout;

public class DataViewer implements UserSettings{

	private JFrame application_window;
	
	private JMenuBar menu_bar;
	private JMenu menu_file;
	private HexDisplay display;
	private JMenuItem mnitem_import_raw;
	private JPanel pnl_content;
	private JButton btn_hex;
	private JButton btn_bits;
	private JLabel lbl_width;
	private JTextField txt_width;
	private JPanel pnl_toolbar;
	private static JScrollBar scrollbar_fields;
	private static JScrollBar scrollbar_numbers;
	private static DataFrameStorage data_store;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				DataViewer window = new DataViewer();
				window.application_window.setVisible(true);
			}
		});
	}
	
	/**
	 * Create the application.
	 */
	public DataViewer() {
		initialize();
	}

	public void showImportFileDialog() {
		JFileChooser filechooser = new JFileChooser();
		filechooser.setAcceptAllFileFilterUsed(true);
		filechooser.showDialog(null, "Import Raw");
		data_store = RawFileImporter.importData(filechooser.getSelectedFile());
		if (data_store instanceof RawDataStore) 
			((RawDataStore)data_store).setWidth(512);
		display.repaint();
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		application_window = new JFrame();
		application_window.setTitle("Data Viewer");
		application_window.setBounds(100, 100, 800, 600);
		application_window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		menu_bar = new JMenuBar();
		application_window.setJMenuBar(menu_bar);
		
		menu_file = new JMenu("File");
		menu_bar.add(menu_file);
		
		mnitem_import_raw = new JMenuItem("Import Raw File");
		mnitem_import_raw.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showImportFileDialog();
			}
		});
		menu_file.add(mnitem_import_raw);
		application_window.getContentPane().setLayout(new BorderLayout(0, 0));
		
		pnl_content = new JPanel();
		application_window.getContentPane().add(pnl_content, BorderLayout.CENTER);
		pnl_content.setLayout(new BorderLayout(0, 0));
		
		scrollbar_fields = new JScrollBar();
		scrollbar_fields.setOrientation(JScrollBar.HORIZONTAL);
		scrollbar_fields.setMaximum(0);
		pnl_content.add(scrollbar_fields, BorderLayout.SOUTH);
		
		scrollbar_numbers = new JScrollBar();
		scrollbar_numbers.setMaximum(0);
		pnl_content.add(scrollbar_numbers, BorderLayout.EAST);
		
		display = new HexDisplay();
		pnl_content.add(display, BorderLayout.CENTER);
		
		pnl_toolbar = new JPanel();
		pnl_content.add(pnl_toolbar, BorderLayout.NORTH);
		pnl_toolbar.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		btn_hex = new JButton("Hex");
		btn_hex.setEnabled(false);
		pnl_toolbar.add(btn_hex);
		
		btn_bits = new JButton("Bits");
		btn_bits.setEnabled(false);
		pnl_toolbar.add(btn_bits);
		
		lbl_width = new JLabel("Width:");
		pnl_toolbar.add(lbl_width);
		lbl_width.setHorizontalAlignment(SwingConstants.RIGHT);
		
		txt_width = new JTextField();
		txt_width.setEnabled(false);
		pnl_toolbar.add(txt_width);
		txt_width.setHorizontalAlignment(SwingConstants.CENTER);
		txt_width.setText("512");
		txt_width.setColumns(10);
		
		updateUserSettings();
	}
	
	public void updateUserSettings() {
		
		FontUIResource menu_font_res = new FontUIResource(menu_font);
		
		ColorUIResource foreground_res = new ColorUIResource(foreground);
		ColorUIResource foreground_highlight_res = new ColorUIResource(foreground.brighter());
		
		ColorUIResource background_res = new ColorUIResource(background);
		ColorUIResource background_highlight_res = new ColorUIResource(background.brighter());
		ColorUIResource background_shadow_res = new ColorUIResource(background.darker());
		ColorUIResource background_dark_shadow_res = new ColorUIResource(background.darker().darker());
		
		ColorUIResource background_sub_res = new ColorUIResource(background_sub_frame);
		ColorUIResource background_sub_highlight_res = new ColorUIResource(background_sub_frame.brighter());
		ColorUIResource background_sub_shadow_res = new ColorUIResource(background_sub_frame.darker());
		ColorUIResource background_sub_dark_shadow_res = new ColorUIResource(background_sub_frame.darker().darker());
		
		UIManager.getLookAndFeelDefaults().put("MenuBar.foreground", foreground_res);
		UIManager.getLookAndFeelDefaults().put("MenuBar.background", background_res);
		
		UIManager.getLookAndFeelDefaults().put("ScrollBar.gradient", null);		
		UIManager.getLookAndFeelDefaults().put("ScrollBar.background", background_res);
		UIManager.getLookAndFeelDefaults().put("ScrollBar.foreground", foreground_res);
		UIManager.getLookAndFeelDefaults().put("ScrollBar.shadow", background_shadow_res);
		UIManager.getLookAndFeelDefaults().put("ScrollBar.darkShadow", background_dark_shadow_res);
		UIManager.getLookAndFeelDefaults().put("ScrollBar.highlight", background_highlight_res);
		UIManager.getLookAndFeelDefaults().put("ScrollBar.thumb", background_sub_res);
		UIManager.getLookAndFeelDefaults().put("ScrollBar.thumbShadow", background_sub_shadow_res);
		UIManager.getLookAndFeelDefaults().put("ScrollBar.thumbDarkShadow", background_sub_dark_shadow_res);
		UIManager.getLookAndFeelDefaults().put("ScrollBar.thumbHighlight", background_sub_highlight_res);
		
		UIManager.getLookAndFeelDefaults().put("Label.foreground", foreground_res);
		UIManager.getLookAndFeelDefaults().put("Label.font", menu_font_res);
		
		UIManager.getLookAndFeelDefaults().put("Button.gradient", null);
		UIManager.getLookAndFeelDefaults().put("Button.background", background_sub_res);
		UIManager.getLookAndFeelDefaults().put("Button.select", background_sub_highlight_res);
		UIManager.getLookAndFeelDefaults().put("Button.shadow", background_sub_shadow_res);
		UIManager.getLookAndFeelDefaults().put("Button.darkShadow", background_sub_dark_shadow_res);
		UIManager.getLookAndFeelDefaults().put("Button.toolBarBorderBackground", background_sub_shadow_res);
		UIManager.getLookAndFeelDefaults().put("Button.foreground", foreground_res);
		UIManager.getLookAndFeelDefaults().put("Button.font", menu_font_res);
		
		UIManager.getLookAndFeelDefaults().put("Panel.background", background_res);
		UIManager.getLookAndFeelDefaults().put("Panel.foreground", foreground_res);
		UIManager.getLookAndFeelDefaults().put("Panel.font", menu_font_res);
		
		UIManager.getLookAndFeelDefaults().put("MenuBar.gradient", null);
		UIManager.getLookAndFeelDefaults().put("MenuBar.background", background_res);
		UIManager.getLookAndFeelDefaults().put("MenuBar.foreground", foreground_res);
		UIManager.getLookAndFeelDefaults().put("MenuBar.font", menu_font_res);
		UIManager.getLookAndFeelDefaults().put("MenuBar.Highlight", background_highlight_res);
		UIManager.getLookAndFeelDefaults().put("MenuBar.shadow", background_shadow_res);
		UIManager.getLookAndFeelDefaults().put("MenuBar.border", BorderFactory.createLineBorder(background.darker(), 2));
		
		UIManager.getLookAndFeelDefaults().put("Menu.background", background_res);
		UIManager.getLookAndFeelDefaults().put("Menu.selectionBackground", background_highlight_res);
		UIManager.getLookAndFeelDefaults().put("Menu.selectionForeground", foreground_highlight_res);
		UIManager.getLookAndFeelDefaults().put("Menu.foreground", foreground_res);
		UIManager.getLookAndFeelDefaults().put("Menu.font", menu_font_res);
		UIManager.getLookAndFeelDefaults().put("Menu.borderPainted", false);
		
		UIManager.getLookAndFeelDefaults().put("MenuItem.background", background_res);
		UIManager.getLookAndFeelDefaults().put("MenuItem.foreground", foreground_res);
		UIManager.getLookAndFeelDefaults().put("MenuItem.selectionBackground", background_highlight_res);
		UIManager.getLookAndFeelDefaults().put("MenuItem.selectionForeground", foreground_highlight_res);
		UIManager.getLookAndFeelDefaults().put("MenuItem.font", menu_font_res);
		UIManager.getLookAndFeelDefaults().put("MenuItem.borderPainted", false);
		
		UIManager.getLookAndFeelDefaults().put("PopupMenu.background", background_res);
		UIManager.getLookAndFeelDefaults().put("PopupMenu.foreground", foreground_res);
		UIManager.getLookAndFeelDefaults().put("PopupMenu.font", menu_font_res);
		UIManager.getLookAndFeelDefaults().put("PopupMenu.border", BorderFactory.createLineBorder(background.darker(), 2));
		
		UIManager.getLookAndFeelDefaults().put("TextField.font", menu_font_res);
		UIManager.getLookAndFeelDefaults().put("TextField.background", background_sub_res);
		UIManager.getLookAndFeelDefaults().put("TextField.foreground", foreground_res);
		UIManager.getLookAndFeelDefaults().put("TextField.caretForeground", foreground_res);
		UIManager.getLookAndFeelDefaults().put("TextField.border", BorderFactory.createLineBorder(background.darker(), 2));
		
		
		SwingUtilities.updateComponentTreeUI(application_window);
		display.updateUserSettings();
	}
	
	public static DataFrameStorage getDataStore() {
		return data_store;
	}
	
	public static JScrollBar getScrollbarFields() {
		return scrollbar_fields;
	}
	
	public static JScrollBar getScrollbarNumbers() {
		return scrollbar_numbers;
	}
	
}
