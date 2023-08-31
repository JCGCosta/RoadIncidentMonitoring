package socket;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import utils.UserInterface;
import javax.swing.JTable;
import java.lang.Thread;

public class ServerInterface extends Thread {
	
	private UserInterface UI;
	private JTable tblCon;
	private String BDPath;
	public Socket clientSocket;
	private PrintWriter out;
	private BufferedReader in;
	public Boolean Connection;
	private String clientName;
	private int id;
	
	public ServerInterface(Socket clientSocket, UserInterface UI, JTable tblCon, int id, String BDPath) throws IOException {
		this.id = id;
		this.clientSocket = clientSocket;
		this.clientName = clientSocket.getInetAddress().getHostName();
		this.out = new PrintWriter(clientSocket.getOutputStream(), true);
		this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		this.Connection = true;
		this.UI = UI;
		this.tblCon = tblCon;
		this.BDPath = BDPath;
	}
	
	public void run() {
		System.out.println("Server (ServerInterface): Criada a thread " + getName() + " para o cliente " + this.clientName + ".");
		ServiceInvoke service = null;
		String input;
		String lastMessage = "";
		try {
			while ((input = in.readLine()) != null)
			{
				lastMessage = input;
				System.out.println("Server (ServerInterface): Cliente " + this.clientName + " - " + input);
				service = new ServiceInvoke(input, BDPath);
				if (service != null) {
					sendMessage(service.answer.toJSONString());
					this.UI.update_table(true, this.tblCon, id, lastMessage, service.answer.toJSONString());
				}
			}
			System.out.println("Server (ServerInterface): O cliente " + this.clientName + " cortou a conexão fechando a thread " + getName() + ".");
			this.Connection = false;
			if (service != null) this.UI.update_table(false, this.tblCon, id, lastMessage, service.answer.toJSONString());
			else this.UI.update_table(false, this.tblCon, id, "*----CONNECTION-CLOSED----*", "*----CONNECTION-CLOSED----*");
		} catch (IOException | NullPointerException e) {
			System.out.println("Server (ServerInterface): Conexão perdida com o cliente fechando a thread " + getName() + ".");
			this.UI.update_table(false, this.tblCon, id, "*----CONNECTION-CLOSED----*", "*----CONNECTION-CLOSED----*");
		}
	}
	
	public void sendMessage(String Msg) {
		try {
			out.println(Msg);
			out.flush();
			System.out.println("Server (ServerInterface): Enviando para " + this.clientName + " - " + Msg);
		} catch (NullPointerException e) {
			System.out.println("Server (ServerInterface): Não existe uma conexão para enviar uma mensagem.");
		}
	}
}
