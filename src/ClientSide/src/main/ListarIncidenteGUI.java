package main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import socket.ClientInterface;
import utils.FieldValidator;
import utils.UserInterface;

public class ListarIncidenteGUI {
	
	static String SERVER_ADRESS;
	static int PORT;
	static String NOME;
	static String TOKEN;
	static int ID;
	static String TIPOLISTAGEM;
	static UserInterface UI;
	static JTextField txtData;
	static JComboBox<String> cmbEstado;
	static JTextField txtCidade;
	public static JTable tblIncidentes;
	static FieldValidator fieldValidator = new FieldValidator(); 
	static ClientInterface clientSocket;
	static String[] Head = new String[] {"ID", "Data", "Hora", "Estado", "Cidade", "Bairro", "Rua", "Tipo_incidente"};
	static String[] ufItens = {"AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO"};
	static String[] tipoIncidentesItens = {"Alagamento", "Deslizamento", "Acidente de Carro", "Obstrucao da via", "Fissura da via", "Pista em obras", "Lentidao na pista", "Animais na pista", "Nevoeiro", "Tromba de agua"};
	
	public ListarIncidenteGUI(String TipoListagem, String Token, int id, String nome, String IP, int Port) {
		
		SERVER_ADRESS = IP;
		PORT = Port;
		TOKEN = Token;
		ID = id;
		NOME = nome;
		TIPOLISTAGEM = TipoListagem;
		
		UI = new UserInterface("SMAICC - " + TIPOLISTAGEM, 800, 570);
		
		if (TIPOLISTAGEM.equals("Meus Incidentes")) {
			tblIncidentes = UI.add_table(Head, 15, 20, 750, 400);
			UI.add_button(actionHandler("btnVoltar"), "Voltar", 15, 475, 150, 25);
			UI.add_button(actionHandler("btnExcluir"), "Excluir Incidentes Selecionados", 550, 475, 215, 25);

			clientSocket = new ClientInterface(SERVER_ADRESS, PORT);
			JSONObject answer;
			clientSocket.sendMessage("{\"token\":\"" + TOKEN + "\",\"id\":" + ID + ",\"operacao\":5}");
			answer = clientSocket.waitAnswer();
			if (answer.get("status").equals("OK"))
			{
				JSONArray listaIncidentes = (JSONArray) answer.get("incidentes");
				Iterator<?> i = listaIncidentes.iterator();
				while (i.hasNext())
				{
					JSONObject currentObj = (JSONObject) i.next();
					Long tI = (Long) currentObj.get("tipo_incidente") - 1;
					UI.add_row_table(tblIncidentes, new String[] {((Long) currentObj.get("id_incidente")).toString(), (String) currentObj.get("data"), (String) currentObj.get("hora"), (String) currentObj.get("estado"), (String) currentObj.get("cidade"), (String) currentObj.get("bairro"), (String) currentObj.get("rua"), tipoIncidentesItens[Integer.parseInt(tI.toString())]});
				}
				clientSocket.close();
			}
			else
			{
				clientSocket.close();
				UI.show_message("O servidor retornou o erro: " + answer.get("status") + ".");
				new MenuGUI("Contribuidor", TOKEN, ID, NOME, SERVER_ADRESS, PORT);
				UI.frame.dispose();
			}
		}
		if (TIPOLISTAGEM.equals("Listagem Geral dos Incidentes")) {
			tblIncidentes = UI.add_table(Head, 15, 60, 750, 400);
			UI.add_button(actionHandler("btnVoltar"), "Voltar", 15, 475, 150, 25);
			UI.add_label("Data: ", 15, 20, 80, 25);
			Date date = new Date();
			SimpleDateFormat dataFormater = new SimpleDateFormat("yyyy-MM-dd");
			txtData = UI.add_textbox(dataFormater.format(date), 55, 20, 135, 25);
			UI.add_label("Estado: ", 200, 20, 80, 25);
			cmbEstado = UI.add_comboBox(ufItens, 250, 20, 135, 25);
			UI.add_label("Cidade: ", 400, 20, 80, 25);
			txtCidade = UI.add_textbox("", 450, 20, 135, 25);
			UI.add_button(actionHandler("btnListar"), "Listar Incidentes", 600, 20, 165, 25);
			UI.add_label("Todos os campos são obrigatórios.", 15, 505, 200, 25);
		}
		
		UI.show();
	}

	public static ActionListener actionHandler(String i) {
		ActionListener onPressed = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				switch (i) {
					case "btnExcluir":
						if (tblIncidentes.getSelectedRowCount() != 0)
						{
							if (UI.show_confirmation("Deseja realmente remover os incidentes selecionados?") == 0)
							{
								int[] Rows = tblIncidentes.getSelectedRows();
								for (int r : Rows)
								{
									clientSocket = new ClientInterface(SERVER_ADRESS, PORT);
									JSONObject answer;
									clientSocket.sendMessage("{\"id\":" + ID + ",\"token\":\"" + TOKEN + "\",\"id_incidente\":" + tblIncidentes.getValueAt(r, 0) + ",\"operacao\":6}");
									answer = clientSocket.waitAnswer();
									if (!answer.get("status").equals("OK"))
									{
										clientSocket.close();
										UI.show_message("O servidor retornou o erro: " + answer.get("status") + ".");
										new MenuGUI("Contribuidor", TOKEN, ID, NOME, SERVER_ADRESS, PORT);
										UI.frame.dispose();
									}
									else
									{
										clientSocket.close();
									}
								}
								UI.show_message("Exclusão realizada com sucesso.");
								UI.frame.dispose();
								new ListarIncidenteGUI("Meus Incidentes", TOKEN, ID, NOME, SERVER_ADRESS, PORT);
							}
						}
						else
						{
							UI.show_message("Favor selecionar os incidentes que deseja excluir.");
						}
					break;
					case "btnListar":
						if (fieldValidator.verifyEmpty(new String[] {txtData.getText(), txtCidade.getText(), cmbEstado.getSelectedItem().toString()}))
						{
							UI.clear_rows(tblIncidentes);
							clientSocket = new ClientInterface(SERVER_ADRESS, PORT);
							JSONObject answer;
							clientSocket.sendMessage("{\"data\":\"" + txtData.getText() + "\",\"estado\":\"" + cmbEstado.getSelectedItem().toString() + "\",\"cidade\":\"" + txtCidade.getText().toUpperCase() + "\",\"operacao\":4}");
							answer = clientSocket.waitAnswer();
							if (answer.get("status").equals("OK"))
							{
								JSONArray listaIncidentes = (JSONArray) answer.get("incidentes");
								Iterator<?> i = listaIncidentes.iterator();
								while (i.hasNext())
								{
									JSONObject currentObj = (JSONObject) i.next();
									Long tI = (Long) currentObj.get("tipo_incidente") - 1;
									UI.add_row_table(tblIncidentes, new String[] {((Long) currentObj.get("id_incidente")).toString(), (String) currentObj.get("data"), (String) currentObj.get("hora"), (String) currentObj.get("estado"), (String) currentObj.get("cidade"), (String) currentObj.get("bairro"), (String) currentObj.get("rua"), tipoIncidentesItens[Integer.parseInt(tI.toString())] });
								}
								clientSocket.close();
							}
							else
							{
								clientSocket.close();
								UI.show_message("O servidor retornou o erro: " + answer.get("status") + ".");
								new MenuGUI("Contribuidor", TOKEN, ID, NOME, SERVER_ADRESS, PORT);
								UI.frame.dispose();
							}
						}
						else {
							UI.show_message("Favor completar todos os campos!");
						}
						break;
					case "btnVoltar":
						if (TOKEN.isEmpty()) {
							new MenuGUI("Não Contribuidor", "", 0, "", SERVER_ADRESS, PORT);
						}
						else
						{
							new MenuGUI("Contribuidor", TOKEN, ID, NOME, SERVER_ADRESS, PORT);
						}
						UI.frame.dispose();
						break;
				};
			}
		};
		return onPressed;
	}
}

