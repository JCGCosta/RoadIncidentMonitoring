package main;

import utils.FieldValidator;
import utils.UserInterface;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import org.json.simple.JSONObject;

import socket.ClientInterface;

public class RepIncidenteGUI {

	static String SERVER_ADRESS;
	static int PORT;
	static String NOME;
	static String TOKEN;
	static int ID;
	static UserInterface UI; 
	static JTextField txtRua;
	static JTextField txtCidade;
	static JComboBox<String> cmbEstado;
	static JTextField txtBairro;
	static JComboBox<String> cmbTipoIncidente;
	static ClientInterface clientSocket;
	static FieldValidator fieldValidator = new FieldValidator(); 
	static String[] ufItens = {"AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO"};
	static String[] tipoIncidentesItens = {"Alagamento", "Deslizamento", "Acidente de Carro", "Obstrucao da via", "Fissura da via", "Pista em obras", "Lentidao na pista", "Animais na pista", "Nevoeiro", "Tromba de agua"};
	
	public RepIncidenteGUI(String Token, int id, String nome, String IP, int Port) {
		SERVER_ADRESS = IP;
		PORT = Port;
		NOME = nome;
		TOKEN = Token;
		ID = id;
		
		UI = new UserInterface("SMAICC - Cadastro", 300, 320);
		
		UI.add_label("Rua: ", 15, 20, 80, 25);
		txtRua = UI.add_textbox("", 100, 20, 170, 25);
		UI.add_label("Cidade: ", 15, 50, 80, 25);
		txtCidade = UI.add_textbox("", 100, 50, 170, 25);
		UI.add_label("Estado: ", 15, 80, 80, 25);
		cmbEstado = UI.add_comboBox(ufItens, 100, 80, 170, 25);
		UI.add_label("Bairro: ", 15, 110, 80, 25);
		txtBairro = UI.add_textbox("", 100, 110, 170, 25);
		UI.add_label("Tipo de Incidente: ", 15, 140, 80, 25);
		cmbTipoIncidente = UI.add_comboBox(tipoIncidentesItens, 100, 140, 170, 25);
		
		UI.add_button(actionHandler("btnAddIncidente"), "Adicionar Incidente", 15, 180, 255, 25);
		UI.add_button(actionHandler("btnVoltar"), "Voltar", 15, 210, 255, 25);
		
		UI.add_label("Todos os campos são obrigatórios.", 15, 240, 200, 25);

		UI.show();
	}
	
	public static ActionListener actionHandler(String i) {
		ActionListener onPressed = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				switch (i) {
					case "btnAddIncidente":
						if (fieldValidator.verifyEmpty(new String[] {txtRua.getText(), txtCidade.getText(), cmbEstado.getSelectedItem().toString(), txtBairro.getText()})) {
							clientSocket = new ClientInterface(SERVER_ADRESS, PORT);
							JSONObject answer;
							Date date = new Date();
							SimpleDateFormat dataFormater = new SimpleDateFormat("yyyy-MM-dd");
							SimpleDateFormat horaFormater = new SimpleDateFormat("HH:mm");
							clientSocket.sendMessage("{\"data\":\"" + dataFormater.format(date) + "\",\"hora\":\"" + horaFormater.format(date)  + "\",\"estado\":\"" + cmbEstado.getSelectedItem().toString() + "\",\"cidade\":\"" + txtCidade.getText().toUpperCase() + "\",\"bairro\":\"" + txtBairro.getText().toUpperCase() + "\",\"rua\":\"" + txtRua.getText().toUpperCase() + "\",\"tipo_incidente\":" + Integer.toString(cmbTipoIncidente.getSelectedIndex() + 1) + ",\"token\":\"" + TOKEN + "\",\"id\":" + ID + ",\"operacao\":7}");
							answer = clientSocket.waitAnswer();
							if (answer.get("status").equals("OK"))
							{
								clientSocket.close();
								UI.show_message("Incidente adicionado com sucesso.");
								new MenuGUI("Contribuidor", TOKEN, ID, NOME, SERVER_ADRESS, PORT);
								UI.frame.dispose();
							}
							else
							{
								clientSocket.close();
								UI.show_message("O servidor retornou o erro: " + answer.get("status") + ".");
							}
						}
						else {
							UI.show_message("Favor completar todos os campos!");
						}
						
						break;
					case "btnVoltar":
						new MenuGUI("Contribuidor", TOKEN, ID, NOME, SERVER_ADRESS, PORT);
						UI.frame.dispose();
						break;
				};
			}
		};
		return onPressed;
	}
	
}
