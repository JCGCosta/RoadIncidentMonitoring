package main;

import utils.CesarCrypto;
import utils.FieldValidator;
import utils.UserInterface;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.json.simple.JSONObject;

import socket.ClientInterface;

public class CadastroGUI {

	static String SERVER_ADRESS;
	static int PORT;
	static UserInterface UI; 
	static JTextField txtNome;
	static JTextField txtEmail;
	static JPasswordField txtSenha;
	static ClientInterface clientSocket;
	static FieldValidator fieldValidator = new FieldValidator(); 
	
	public CadastroGUI(String IP, int Port) {
		
		SERVER_ADRESS = IP;
		PORT = Port;
		
		UI = new UserInterface("SMAICC - Cadastro", 300, 250);
		
		UI.add_label("Nome: ", 15, 20, 80, 25);
		txtNome = UI.add_textbox("", 100, 20, 170, 25);
		UI.add_label("E-mail: ", 15, 50, 80, 25);
		txtEmail = UI.add_textbox("", 100, 50, 170, 25);
		UI.add_label("Senha: ", 15, 80, 80, 25);
		txtSenha = UI.add_passbox("", 100, 80, 170, 25);
		
		UI.add_button(actionHandler("btnCadastrar"), "Realizar Cadastro", 15, 120, 255, 25);
		UI.add_button(actionHandler("btnVoltar"), "Voltar", 15, 150, 255, 25);
		
		UI.add_label("Todos os campos são obrigatórios.", 15, 180, 200, 25);

		UI.show();
	}
	
	public static ActionListener actionHandler(String i) {
		ActionListener onPressed = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				switch (i) {
					case "btnCadastrar":
						if (fieldValidator.verifyEmpty(new String[] {txtEmail.getText(), txtNome.getText(), txtSenha.getText()})) {
							if (fieldValidator.emailValidation(txtEmail.getText()))
							{
								clientSocket = new ClientInterface(SERVER_ADRESS, PORT);
								JSONObject answer;
								clientSocket.sendMessage("{\"nome\":\"" + txtNome.getText() + "\",\"senha\":\"" + CesarCrypto.encriptar(2, txtSenha.getText())  + "\",\"email\":\"" + txtEmail.getText() + "\",\"operacao\":1}");
								answer = clientSocket.waitAnswer();
								if (answer.get("status").equals("OK"))
								{
									clientSocket.close();
									UI.show_message("Cadastro realizado com sucesso.");
									new LoginGUI(SERVER_ADRESS, PORT);
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
		return onPressed;
	}
	
}
