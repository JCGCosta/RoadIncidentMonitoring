package socket;
import java.util.ArrayList;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.io.IOException;
import java.lang.Thread;
import javax.swing.JTable;
import utils.UserInterface;

public class SocketsListener extends Thread {
	
	public ArrayList<ServerInterface> serverSockets;
	private UserInterface UI;
	private JTable tblCon;
	private int PORT;
	private ServerSocket listener;
	private int currentID;
	private String BDPath;
	
	public SocketsListener(String BDPath, UserInterface UI, JTable tblCon, int PORT) {
		this.serverSockets = new ArrayList<ServerInterface>();
		this.currentID = 0;
		this.UI = UI;
		this.tblCon = tblCon; 
		this.PORT = PORT;
		this.BDPath = BDPath;
		try {
			InetAddress enderecoIP = InetAddress.getByName("0.0.0.0");
			this.listener = new ServerSocket(this.PORT, 0, enderecoIP);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		System.out.println("Server (SocketsListener): A thread " + getName() +  " está escutando na porta " + this.PORT + ".");
		while (true) {
			try {
				ServerInterface client = new ServerInterface(this.listener.accept(), UI, tblCon, currentID, BDPath);
				client.start();
				this.serverSockets.add(client);
				UI.add_row_table(tblCon, new String[] {client.clientSocket.getInetAddress().getHostName(), "", "", Boolean.toString(client.Connection)});
				currentID++;
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
	}
}
