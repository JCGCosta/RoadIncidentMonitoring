package main;
import utils.UserInterface;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JTextField;

public class ConnectionSocketGUI {

	static UserInterface UI;
	static JTextField txtIP;
	static JTextField txtPort;
	
	public static void main(String[] args) {
		new ConnectionSocketGUI();
	}
	
	public ConnectionSocketGUI() {
		
		UI = new UserInterface("SMAICC - Server Address", 300, 220);

		UI.add_label("IP: ", 15, 20, 80, 25);
		txtIP = UI.add_textbox("127.0.0.1", 100, 20, 170, 25);
		UI.add_label("Porta: ", 15, 50, 80, 25);
		txtPort = UI.add_textbox("20000", 100, 50, 170, 25);
		UI.add_button(actionHandler("btnProseguir"), "Proseguir", 15, 90, 255, 25);
		UI.add_button(actionHandler("btnSair"), "Sair", 15, 120, 255, 25);
		UI.add_label("Todos os campos são obrigatórios.", 15, 150, 200, 25);
		UI.show();
	}
	
	public static ActionListener actionHandler(String i) {
		ActionListener onPressed = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				switch (i) {
					case "btnProseguir":
						UI.frame.dispose();
						new MenuGUI("Não Contribuidor", "", 0, "", txtIP.getText(), Integer.valueOf(txtPort.getText()));
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
