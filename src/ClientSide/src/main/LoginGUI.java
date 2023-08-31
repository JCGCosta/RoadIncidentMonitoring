package main;

import utils.UserInterface;
import utils.CesarCrypto;
import utils.FieldValidator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;

import org.json.simple.JSONObject;

import socket.ClientInterface;

import javax.swing.JPasswordField;

public class LoginGUI {

	static String SERVER_ADRESS;
	static int PORT;
	static UserInterface UI;
	static JTextField txtEmail;
	static JPasswordField txtSenha;
	static ClientInterface clientSocket;
	static FieldValidator fieldValidator = new FieldValidator(); 
	
	public LoginGUI(String IP, int Port) {
		
		SERVER_ADRESS = IP;
		PORT = Port;
		
		UI = new UserInterface("SMAICC - Login", 300, 220);

		UI.add_label("Email: ", 15, 20, 80, 25);
		txtEmail = UI.add_textbox("", 100, 20, 170, 25);
		UI.add_label("Senha: ", 15, 50, 80, 25);
		txtSenha = UI.add_passbox("", 100, 50, 170, 25);
		UI.add_button(actionHandler("btnLogar"), "Logar", 15, 90, 255, 25);
		UI.add_button(actionHandler("btnVoltar"), "Voltar", 15, 120, 255, 25);
		UI.add_label("Todos os campos são obrigatórios.", 15, 150, 200, 25);
		UI.show();
	
	}
	
	public static ActionListener actionHandler(String i) {
		ActionListener aL = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				switch (i) {
					case "btnLogar":
						if (fieldValidator.verifyEmpty(new String[] {txtEmail.getText(), txtSenha.getText()}))
						{
							if (fieldValidator.emailValidation(txtEmail.getText())) {
								clientSocket = new ClientInterface(SERVER_ADRESS, PORT);
								JSONObject answer;
								clientSocket.sendMessage("{\"email\":\"" + txtEmail.getText() + "\",\"senha\":\"" + CesarCrypto.encriptar(2, txtSenha.getText()) + "\",\"operacao\":2}");
								answer = clientSocket.waitAnswer();
								if (answer.get("status").equals("OK"))
								{
									clientSocket.close();
									new MenuGUI("Contribuidor", (String) answer.get("token"), ((Long) answer.get("id")).intValue(), (String) answer.get("nome"), SERVER_ADRESS, PORT);
									clear();
									UI.frame.dispose();
								}
								else
								{
									clientSocket.close();
									UI.show_message("O servidor retornou o erro: " + answer.get("status") + ".");
								}	
							}
							else {
								UI.show_message("Email fora do padrão.");
							}
						}
						else {
							UI.show_message("Favor completar todos os campos!");
						}
						break;
					case "btnVoltar":
						new MenuGUI("Não Contribuidor", "", 0, "", SERVER_ADRESS, PORT);
						UI.frame.dispose();
						break;	
				};
			}
		};
		return aL;
	}
	
	public static void clear() {
		txtEmail.setText("");
		txtSenha.setText("");
	}

}
