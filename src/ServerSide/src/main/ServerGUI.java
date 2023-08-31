package main;

import utils.UserInterface;

import utils.ConnectionDB;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.SQLException;
import java.awt.*;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import socket.SocketsListener;

public class ServerGUI {
	
	public static UserInterface UI;
	static JTextField txtBD;
	static JTextField txtPorta;
	static JTextField txtCaminho;
	static JButton btnStartServer;
	static JButton btnTestConn;
	static JButton btnSearchDB;
	public static JTable tblCon;
	static JFileChooser fCDataBase;
	static String[] Head = new String[] {"Client", "Last_Recieve", "Last_Answer", "Status"};
	
	public static void main(String[] args) {
		new ServerGUI();
	}
	
	public ServerGUI() {
		
		UI = new UserInterface("SMAICC - Server", 800, 600);
		
		btnSearchDB = UI.add_button(actionHandler("btnFileChoose"), "Procurar BD", 15, 20, 130, 25);
		txtCaminho = UI.add_textbox("C:\\Users\\1513 MXTI\\eclipse-workspace\\ServerSide\\src\\db\\dbSMAICC.db", 170, 20, 550, 25);
		txtCaminho.setEditable(false);
		fCDataBase = UI.add_fileChooser(new FileNameExtensionFilter("DataBase Files", "db", "db"));
		
		btnTestConn = UI.add_button(actionHandler("btnTestConn"), "Testar Conexão", 15, 50, 130, 25);
		//btnTestConn.setEnabled(false);
		txtBD = UI.add_textbox("", 170, 50, 120, 25);
		txtBD.setEditable(false);
		
		UI.add_label("Porta: ", 15, 80, 80, 25);
		txtPorta = UI.add_textbox("", 60, 80, 115, 25);
		txtPorta.setEditable(false);
		btnStartServer = UI.add_button(actionHandler("btnSocket"), "Iniciar Servidor", 190, 80, 120, 25);
		btnStartServer.setEnabled(false);
		
		UI.add_label("Histórico de troca de Mensagens: ", 15, 110, 300, 25);
		tblCon = UI.add_table(Head, 15, 140, 750, 400);

		UI.show();
		
	}
	
	public static ActionListener actionHandler(String i) {
		ActionListener aL = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				switch (i) {
					case "btnFileChoose":
						fCDataBase.showOpenDialog(null);
						File f = fCDataBase.getSelectedFile();
						if (f != null) {
							txtCaminho.setText(f.getAbsolutePath());
							btnTestConn.setEnabled(true);
						}
						break;
					case "btnTestConn":
						try {
							ConnectionDB conn = new ConnectionDB(txtCaminho.getText());
							conn.connection_check();
							conn.close();
							txtBD.setForeground(new Color(0, 153, 0));
							txtBD.setText("Conectado!");
							btnTestConn.setEnabled(false);
							btnSearchDB.setEnabled(false);
							txtPorta.setEditable(true);
							btnStartServer.setEnabled(true);
						} catch (SQLException e1) {
							txtBD.setForeground(new Color(204, 0, 0));
							txtBD.setText("Não Conectado!");
							e1.printStackTrace();
						}
						break;
					case "btnSocket":
						txtPorta.setEditable(false);
						btnStartServer.setEnabled(false);
						SocketsListener list = new SocketsListener(txtCaminho.getText(), UI, tblCon, Integer.valueOf(txtPorta.getText()));
						list.start();	
				};
			}
		};
		return aL;
	}
}
