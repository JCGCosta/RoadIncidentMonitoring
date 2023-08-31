package socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ClientInterface {
	
	private String SERVER_ADRESS;
	private int PORT;
	private Socket clientSocket;
	private PrintWriter out;
	private BufferedReader in;
	public Integer Result;
	
	public ClientInterface(String IP, int Port) {
		try {
			this.SERVER_ADRESS = IP;
			this.PORT = Port;
			this.clientSocket = new Socket(this.SERVER_ADRESS, this.PORT);
			this.out = new PrintWriter(clientSocket.getOutputStream(), true);
			this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			System.out.println("Cliente (ClientInterface): Nova conexão com o servidor " + this.SERVER_ADRESS + ":" + Integer.toString(this.PORT) + " através da porta " + this.clientSocket.getLocalPort() + ".");
		} catch (IOException e) {
			System.out.println("Cliente (ClientInterface): Não foi possivel se conectar ao Servidor " + this.SERVER_ADRESS + ":" + Integer.toString(this.PORT) + ".");
		} 
	}
	
	public JSONObject waitAnswer() {
		String input;
		JSONObject serverAnswer = new JSONObject();
		try {
			while ((input = in.readLine()) != null)
			{
				System.out.println("Cliente (ClientInterface): Server - " + input);
				serverAnswer = (JSONObject) new JSONParser().parse(input);
				break;
			}
			return serverAnswer;
		} catch (Exception e) {
			JSONObject ans = new JSONObject();
			ans.put("status", "ERRO GERAL NA MENSAGEM DO SERVIDOR.");
			System.out.println("Cliente (ClientInterface): Não foi possivel ler a mensagem.");
			return ans;
		}	
	}
	
	public void sendMessage(String Msg) {
		try {
			out.println(Msg);
			out.flush();
			System.out.println("Cliente (ClientInterface): Enviando a mensagem - " + Msg + ".");
		} catch (NullPointerException e) {
			System.out.println("Cliente (ClientInterface): Não existe uma conexão para enviar uma mensagem.");
		}
	}
	
	public void close() {
		try {
			int out_port = clientSocket.getLocalPort();
			clientSocket.close();
			System.out.println("Cliente (ClientInterface): Fechando conexão com " + this.SERVER_ADRESS + ":" + Integer.toString(this.PORT) + " através da porta " + Integer.toString(out_port) + ".");
		} catch (IOException e) {
			System.out.println("Cliente (ClientInterface): Não existe uma conexão a ser fechada.");
		} catch (NullPointerException e) {
			System.out.println("Cliente (ClientInterface): Não existe uma conexão a ser fechada.");
		}
		
	}
}
