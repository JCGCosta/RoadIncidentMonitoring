package main;

import utils.CesarCrypto;
import utils.UserInterface;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.json.simple.JSONObject;

import socket.ClientInterface;

public class MenuGUI {

	static String SERVER_ADRESS;
	static int PORT;
	static String TOKEN;
	static int ID;
	static String NOME;
	static UserInterface UI; 
	static ClientInterface clientSocket;
	
	public MenuGUI(String userType, String Token, int id, String nome, String IP, int Port) {
		
		TOKEN = Token;
		ID = id;
		SERVER_ADRESS = IP;
		PORT = Port;
		NOME = nome;
		
		UI = new UserInterface("SMAICC - Menu", 300, 280);
		
		UI.add_button(actionHandler("btnListagemIncidentes"), "Listar Incidentes", 15, 50, 255, 25);
		
		if (userType.equals("Contribuidor")) {
			UI.add_label("Seja bem vindo " + NOME + " (" + userType + ").", 15, 10, 270, 25);
			UI.add_button(actionHandler("btnMeusIncidentes"), "Meus Incidentes", 15, 80, 255, 25);
			UI.add_button(actionHandler("btnRepIncidente"), "Reportar Incidente", 15, 110, 255, 25);
			UI.add_button(actionHandler("btnAttCadastro"), "Atualizar Cadastro", 15, 140, 255, 25);
			UI.add_button(actionHandler("btnRemoverCadastro"), "Remover Cadastro", 15, 170, 255, 25);
			UI.add_button(actionHandler("btnLogout"), "Fazer Logout", 15, 200, 255, 25);
		}
		
		if (userType.equals("Não Contribuidor")) {
			UI.add_label("Seja bem vindo (" + userType + ").", 15, 10, 270, 25);
			UI.add_button(actionHandler("btnLogar"), "Logar", 15, 80, 255, 25);
			UI.add_button(actionHandler("btnCadastrar"), "Cadastrar", 15, 110, 255, 25);
			UI.add_button(actionHandler("btnSair"), "Sair", 15, 140, 255, 25);
		}

		UI.show();
	}
	
	public static ActionListener actionHandler(String i) {
		ActionListener onPressed = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				switch (i) {
					case "btnListagemIncidentes":
						UI.frame.dispose();
						new ListarIncidenteGUI("Listagem Geral dos Incidentes", TOKEN, ID, NOME, SERVER_ADRESS, PORT);
						break;
					case "btnMeusIncidentes":
						UI.frame.dispose();
						new ListarIncidenteGUI("Meus Incidentes", TOKEN, ID, NOME, SERVER_ADRESS, PORT);
						break;
					case "btnRepIncidente":
						UI.frame.dispose();
						new RepIncidenteGUI(TOKEN, ID, NOME, SERVER_ADRESS, PORT);
						break;
					case "btnAttCadastro":
						UI.frame.dispose();
						new AttCadastroGUI(TOKEN, ID, NOME, SERVER_ADRESS, PORT);
						break;
					case "btnLogar":
						UI.frame.dispose();
						new LoginGUI(SERVER_ADRESS, PORT);
						break;
					case "btnCadastrar":
						UI.frame.dispose();
						new CadastroGUI(SERVER_ADRESS, PORT);
						break;
					case "btnRemoverCadastro":
						if (UI.show_confirmation("Deseja realmente remover seu cadastro?") == 0)
						{
							String senha = UI.show_inputDialog("Informe sua senha.");
							if (senha != null)
							{
								clientSocket = new ClientInterface(SERVER_ADRESS, PORT);
								JSONObject answer;
								clientSocket.sendMessage("{\"senha\":\"" + CesarCrypto.encriptar(2, senha) + "\",\"token\":\"" + TOKEN + "\",\"id\":" + ID + ",\"operacao\":8}");
								answer = clientSocket.waitAnswer();
								if (answer.get("status").equals("OK"))
								{
									UI.show_message("Cadastro removido com sucesso.");
									clientSocket.close();
									UI.frame.dispose();
									new MenuGUI("Não Contribuidor", "", 0, "", SERVER_ADRESS, PORT);
								}
								else
								{
									clientSocket.close();
									UI.show_message("O servidor retornou o erro: " + answer.get("status") + ".");
								}
							}
						}
						break;
					case "btnLogout":
						clientSocket = new ClientInterface(SERVER_ADRESS, PORT);
						JSONObject answer;
						clientSocket.sendMessage("{\"token\":\"" + TOKEN + "\",\"id\":" + ID + ",\"operacao\":9}");
						answer = clientSocket.waitAnswer();
						if (answer.get("status").equals("OK"))
						{
							clientSocket.close();
							UI.frame.dispose();
							new MenuGUI("Não Contribuidor", "", 0, "", SERVER_ADRESS, PORT);
						}
						else
						{
							clientSocket.close();
							UI.show_message("O servidor retornou o erro: " + answer.get("status") + ".");
						}
						break;
					case "btnSair":
						UI.frame.dispose();
						break;
				};
			}
		};
		return onPressed;
	}
}
