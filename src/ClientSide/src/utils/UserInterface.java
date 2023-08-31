package utils;

import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JFileChooser;

public class UserInterface {

	public JFrame frame;
	public JPanel panel;

	public UserInterface(String windowName, int windowW, int windowH) {
		this.frame = new JFrame(windowName);
		this.panel = new JPanel();
		this.setup_frame(windowW, windowH);
	}
	
	public void setup_frame(int w, int h) {
		this.frame.pack();
		this.frame.setResizable(false);
		this.frame.setSize(w, h);
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setLocationRelativeTo(null);
		this.panel.setLayout(null);
	}
	
	public void show() {
		this.frame.setVisible(true);
		this.frame.add(panel);
	}
	
	public JButton add_button(ActionListener action, String text, int x, int y, int w, int h) {	
		JButton btn = new JButton(text);
		btn.setBounds(x, y, w, h);
		btn.addActionListener(action);
		this.panel.add(btn);
		return btn;
	}
	
	public JTable add_table(String[] Head, int x, int y, int w, int h) {
		DefaultTableModel model = new DefaultTableModel(new Object[][] {{}}, Head);
		model.removeRow(0);
		JTable table = new JTable(model);
		JScrollPane sPane = new JScrollPane(table);
		sPane.setBounds(x, y, w, h);
		this.panel.add(sPane);
		return table;
	}
	
	public JComboBox<String> add_comboBox(String[] itens, int x, int y, int w, int h) {
		JComboBox<String> comboBox = new JComboBox<>(itens);
		comboBox.setBounds(x, y, w, h);
		this.panel.add(comboBox);
		return comboBox;
	}
	
	public void add_row_table(JTable table, Object[] row) {
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		model.addRow(row);
	}
	
	public void clear_rows(JTable table) {
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		model.setRowCount(0);
	}
	
	public void remove_row(JTable table, int r) {
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		try {
			model.removeRow(r);
		}
		catch (Exception ex) {
			model.setRowCount(0);
		}
		
	}
	
	public void update_table(Boolean status, JTable table, int id, String recieve, String answer) {
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		model.setValueAt(recieve, id, 1);
		model.setValueAt(answer, id, 2);
		model.setValueAt(status, id, 3);
		model.fireTableDataChanged();
	}
	
	public JFileChooser add_fileChooser(FileNameExtensionFilter filter) {
		JFileChooser fC = new JFileChooser();
		fC.setFileFilter(filter);
		return fC;
	}
	
	public JLabel add_label(String text, int x, int y, int w, int h) {
		JLabel label = new JLabel(text);
		label.setBounds(x, y, w, h);
		this.panel.add(label);
		return label;
	}
	
	public JTextField add_textbox(String text, int x, int y, int w, int h) {
		JTextField txt = new JTextField(text);
		txt.setBounds(x, y, w, h);
		panel.add(txt);
		return txt;
	}
	
	public JPasswordField add_passbox(String text, int x, int y, int w, int h) {
		JPasswordField txtPass = new JPasswordField();
		txtPass.setBounds(x, y, w, h);
		panel.add(txtPass);
		return txtPass;
	}
	
	public void show_message(String text) {
		JOptionPane.showMessageDialog(null, text);
	}
	
	public int show_confirmation(String text) {
		return JOptionPane.showConfirmDialog(null, text);
	}
	
	public String show_inputDialog(String text) {
		JPasswordField pf = new JPasswordField();
		int okCxl = JOptionPane.showConfirmDialog(null, pf, text, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (okCxl == JOptionPane.OK_OPTION) {
		  return new String(pf.getPassword());
		}
		return null;
	}

}
