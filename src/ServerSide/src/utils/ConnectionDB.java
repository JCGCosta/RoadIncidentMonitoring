package utils;
import java.sql.*;
import java.util.UUID;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ConnectionDB {
	
	private Connection conn;
	private String fPath;
	
	public ConnectionDB(String dbFilePath) throws SQLException {
		this.fPath = dbFilePath;
		this.conn = DriverManager.getConnection("jdbc:sqlite:" + dbFilePath);
	}
	
	public void close() {
		try {
			this.conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void connection_check(){
		try {
			this.conn.createStatement().executeUpdate("DELETE FROM Current;");
			System.out.println("Server (ConnectionDB): Conectado com - " + fPath);
		} catch (SQLException e) {
			System.out.println("Server (ConnectionDB): Erro ao conectar com - " + fPath);
			e.printStackTrace();
		}
	}
	
	public String[] cadastraUsuario(String nome, String email, String senha){
		ResultSet rs;
		try {
			rs = this.conn.createStatement().executeQuery("SELECT * FROM Usuarios WHERE email_user='" + email + "';");
			if (!rs.next()) {
				this.conn.createStatement().executeUpdate("INSERT INTO Usuarios (nome_user, email_user, senha_user) VALUES ('" + nome + "', '" + email + "', '" + senha + "');");
				String[] result = { "OK" };
				return result;
			}
			else {
				String[] result = { "EMAIL JA CADASTRADO." };
				return result;
			}
		} catch (SQLException e) {
			System.out.println("Server (ConnectionDB): Problema com o banco de dados (SQL Exception).");
			String[] result = { "ERRO NO BANCO DE DADOS." };
			return result;
		}
	}
	
	public String[] verificaLogin(String email, String senha){
		ResultSet rs;
		try {
			rs = this.conn.createStatement().executeQuery("SELECT nome_user, id_user FROM Usuarios WHERE email_user='" + email + "' AND senha_user='" + senha + "';");
			if (rs.next()) {
				UUID uuid = UUID.randomUUID();
		        String token = uuid.toString().replace("-", "").substring(0, 20);
				this.conn.createStatement().executeUpdate("INSERT INTO Current (id_user, token_current) VALUES (" + rs.getInt("id_user") + ", '" + token + "');");
				// TOKEN, NOME, ID
				String[] result = { "OK", token, rs.getString(1), Integer.toString(rs.getInt(2)) }; // token, name, id
				return result;
			}
			else {
				String[] result = { "EMAIL OU SENHA NAO CADASTRADO." };
				return result;
			}
		} catch (SQLException e) {
			System.out.println("Server (ConnectionDB): Problema com o banco de dados (SQL Exception).");
			String[] result = { "ERRO NO BANCO DE DADOS." };
			return result;
		}

	}
	
	public String[] atualizacaoCadastro(Long id, String token, String nome, String email, String senha){
		ResultSet rs, rs1;
		try {
			rs = this.conn.createStatement().executeQuery("SELECT nome_user, id_user FROM Usuarios WHERE id_user='" + id.toString() + "';");
			if (rs.next()) {
				rs1 = this.conn.createStatement().executeQuery("SELECT * FROM Current WHERE id_user='" + id.toString() + "' AND token_current='" + token + "';");
				if (rs1.next())
				{
					this.conn.createStatement().executeUpdate("DELETE FROM Current WHERE id_user='" + id.toString() + "' AND token_current='" + token + "';");
					this.conn.createStatement().executeUpdate("UPDATE Usuarios SET nome_user='" + nome + "', email_user='" + email + "', senha_user='" + senha + "' WHERE id_user='" + id.toString() + "';");
					String[] result = { "OK" };
					return result;
				}
				else
				{
					String[] result = { "TOKEN OU ID INVALIDO." };
					return result;
				}
			}
			else {
				String[] result = { "NAO FOI POSSIVEL ENCONTRAR O ID INFORMADO." };
				return result;
			}
		} catch (SQLException e) {
			System.out.println("Server (ConnectionDB): Problema com o banco de dados (SQL Exception).");
			String[] result = { "ERRO NO BANCO DE DADOS." };
			return result;
		}
	}
	
	public String[] reportarIncidente(String data, String hora, String estado, String cidade, String bairro, String rua, Long tipo_incidente, String token, Long id){
		ResultSet rs;
		try {
			rs = this.conn.createStatement().executeQuery("SELECT * FROM Current WHERE id_user='" + id.toString() + "' AND token_current='" + token + "';");
			if (rs.next())
			{
				this.conn.createStatement().executeUpdate("INSERT INTO Incidentes (data_incidente, hora_incidente, estado_incidente, cidade_incidente, bairro_incidente, rua_incidente, tipo_incidente, id_user) VALUES ('" + data + "','" + hora +"','" + estado + "','" + cidade + "','" + bairro + "','" + rua + "'," + tipo_incidente.toString() + "," + id.toString() + ");");
				String[] result = { "OK" };
				return result;
			}
			else
			{
				String[] result = { "TOKEN OU ID INVALIDO." };
				return result;
			}
		} catch (SQLException e) {
			System.out.println("Server (ConnectionDB): Problema com o banco de dados (SQL Exception).");
			String[] result = { "ERRO NO BANCO DE DADOS." };
			return result;
		}
	}
	
	public String[] removerUsuario(String senha, String token, Long id){
		ResultSet rs, rs1;
		try {
			rs = this.conn.createStatement().executeQuery("SELECT id_user FROM Usuarios WHERE id_user='" + id.toString() + "' AND senha_user='" + senha + "';");
			if (rs.next()) {
				rs1 = this.conn.createStatement().executeQuery("SELECT * FROM Current WHERE id_user='" + id.toString() + "' AND token_current='" + token + "';");
				if (rs1.next())
				{
					this.conn.createStatement().executeUpdate("DELETE FROM Current WHERE id_user='" + id.toString() + "' AND token_current='" + token + "';");
					this.conn.createStatement().executeUpdate("DELETE FROM Usuarios WHERE id_user='" + id.toString() + "';");
					String[] result = { "OK" };
					return result;
				}
				else
				{
					String[] result = { "TOKEN OU ID INVALIDO." };
					return result;
				}
			}
			else {
				String[] result = { "SENHA INCORRETA" };
				return result;
			}
		} catch (SQLException e) {
			System.out.println("Server (ConnectionDB): Problema com o banco de dados (SQL Exception).");
			String[] result = { "ERRO NO BANCO DE DADOS." };
			return result;
		}
	}
	
	public String[] removerIncidenteUsuario(Long id_incidente, String token, Long id){
		ResultSet rs;
		try {
			rs = this.conn.createStatement().executeQuery("SELECT * FROM Current WHERE id_user='" + id + "' AND token_current='" + token + "';");
			if (rs.next()) {
				this.conn.createStatement().executeUpdate("DELETE FROM Incidentes WHERE id_incidente='" + id_incidente + "';");
				String[] result = { "OK" };
				return result;
			}
			else {
				String[] result = { "TOKEN OU ID INVALIDO." };
				return result;
			}
		} catch (SQLException e) {
			System.out.println("Server (ConnectionDB): Problema com o banco de dados (SQL Exception).");
			String[] result = { "ERRO NO BANCO DE DADOS." };
			return result;
		}
	}
	
	@SuppressWarnings("unchecked")
	public String[] listarIncidentesClient(Long id, String token){
		ResultSet rs, rs1;
		try {
			rs1 = this.conn.createStatement().executeQuery("SELECT * FROM Current WHERE id_user='" + id + "' AND token_current='" + token + "';");
			if (rs1.next()) {
				JSONArray result = new JSONArray();
				rs = this.conn.createStatement().executeQuery("SELECT * FROM Incidentes WHERE id_user='" + id.toString() + "' ORDER BY hora_incidente;");
				while (rs.next()) {
					JSONObject current = new JSONObject();
					current.put("data", rs.getString("data_incidente"));
					current.put("hora", rs.getString("hora_incidente"));
					current.put("estado", rs.getString("estado_incidente"));
					current.put("cidade", rs.getString("cidade_incidente"));
					current.put("bairro", rs.getString("bairro_incidente"));
					current.put("rua", rs.getString("rua_incidente"));
					current.put("tipo_incidente", rs.getInt("tipo_incidente"));
					current.put("id_incidente", rs.getInt("id_incidente"));
					result.add(current);
				}
				String[] results = { "OK", result.toJSONString() };
				return results;
			}
			else {
				String[] result = { "TOKEN OU ID INVALIDO." };
				return result;
			}
		} catch (SQLException e) {
			System.out.println("Server (ConnectionDB): Problema com o banco de dados (SQL Exception).");
			String[] result = { "ERRO NO BANCO DE DADOS." };
			return result;
		}
	}
	
	@SuppressWarnings("unchecked")
	public String[] listarIncidentesGeral(String data, String estado, String cidade){
		ResultSet rs;
		try {
			JSONArray result = new JSONArray();
			rs = this.conn.createStatement().executeQuery("SELECT * FROM Incidentes WHERE data_incidente='" + data + "' AND estado_incidente='" + estado + "' AND cidade_incidente='" + cidade + "' ORDER BY hora_incidente;");
			while (rs.next()) {
				JSONObject current = new JSONObject();
				current.put("data", rs.getString("data_incidente"));
				current.put("hora", rs.getString("hora_incidente"));
				current.put("estado", rs.getString("estado_incidente"));
				current.put("cidade", rs.getString("cidade_incidente"));
				current.put("bairro", rs.getString("bairro_incidente"));
				current.put("rua", rs.getString("rua_incidente"));
				current.put("tipo_incidente", rs.getInt("tipo_incidente"));
				current.put("id_incidente", rs.getInt("id_incidente"));
				result.add(current);
			}
			String[] results = { "OK", result.toJSONString() };
			return results;
		} catch (SQLException e) {
			System.out.println("Server (ConnectionDB): Problema com o banco de dados (SQL Exception).");
			String[] result = { "ERRO NO BANCO DE DADOS." };
			return result;
		}
	}
	
	public String[] desloga(String token, String id){
		ResultSet rs;
		try {
			rs = this.conn.createStatement().executeQuery("SELECT * FROM Current WHERE id_user='" + id + "' AND token_current='" + token + "';");
			if (rs.next()) {
				this.conn.createStatement().executeUpdate("DELETE FROM Current WHERE id_user='" + id + "' AND token_current='" + token + "';");
				String[] result = { "OK" };
				return result;
			}
			else {
				String[] result = { "TOKEN OU ID INVALIDO." };
				return result;
			}
		} catch (SQLException e) {
			System.out.println("Server (ConnectionDB): Problema com o banco de dados (SQL Exception).");
			String[] result = { "ERRO NO BANCO DE DADOS." };
			return result;
		}
	}
}
