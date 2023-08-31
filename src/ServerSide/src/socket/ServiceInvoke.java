package socket;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import utils.ConnectionDB;

public class ServiceInvoke {
	public JSONObject message;
	public JSONObject answer;
	private String BDPath;
	
	public ServiceInvoke(String in, String BDPath) {
		this.BDPath = BDPath;
		try {
			this.message = (JSONObject) new JSONParser().parse(in);
			this.answer = this.executeService(this.message);
		} catch (ParseException e) {
			System.out.println("Server (ServiceInvoke): Erro ao tentar transformar a mensagem em JSON. Reveja a mensagem enviada.");
		} catch (SQLException e) {
			System.out.println("Server (ServiceInvoke): Erro com o banco de dados.");
		}
	}
	
	public JSONObject executeService(JSONObject in) throws ParseException, SQLException
	{
		Integer op;
		ConnectionDB conn = new ConnectionDB(BDPath);
		String status = "OK";
		String answerJson = "";
		String[] result;
		
		try {
			op = ((Long) in.get("operacao")).intValue();
		}
		catch (Exception ex)
		{
			return (JSONObject) new JSONParser().parse("{\"operacao\":-1,\"status\":\"OPERACAO EM FORMATO ERRADO OU AUSENCIA DA OPERACAO.\"}");
		}
		
		try {
			if (!validaEntrada(op, in))
			{
				return (JSONObject) new JSONParser().parse("{\"operacao\":" + op + ",\"status\":\"ERRO DE VALIDACAO DE ENTRADA.\"}");
			}
		}
		catch (Exception ex){
			return (JSONObject) new JSONParser().parse("{\"operacao\":" + op + ",\"status\":\"ALGUM CAMPO ESTA AUSENTE.\"}");
		}
		
		try {
			switch (op) {
				case 1:
					result = conn.cadastraUsuario((String) in.get("nome"), (String) in.get("email"), (String) in.get("senha"));
					if (!result[0].equals("OK")) status = result[0]; 
					break;
				case 2:
					result = conn.verificaLogin((String) in.get("email"), (String) in.get("senha"));
					if (!result[0].equals("OK")) status = result[0]; 
					else answerJson = "\"token\":\"" + result[1] + "\",\"nome\":\"" + result[2]  + "\",\"id\":" + result[3];
					break;
				case 3:
					result = conn.atualizacaoCadastro((Long) in.get("id"), (String) in.get("token"), (String) in.get("nome"), (String) in.get("email"), (String) in.get("senha"));
					if (!result[0].equals("OK")) status = result[0];
					break;
				case 4:
					result = conn.listarIncidentesGeral((String) in.get("data"), (String) in.get("estado"), (String) in.get("cidade"));
					if (!result[0].equals("OK")) status = result[0]; 
					else answerJson = "\"incidentes\":" + result[1];
					break;
				case 5:
					result = conn.listarIncidentesClient((Long) in.get("id"), (String) in.get("token"));
					if (!result[0].equals("OK")) status = result[0]; 
					else answerJson = "\"incidentes\":" + result[1];
					break;
				case 6:
					result = conn.removerIncidenteUsuario((Long) in.get("id_incidente"), (String) in.get("token"), (Long) in.get("id"));
					if (!result[0].equals("OK")) status = result[0]; 
					break;
				case 7:
					result = conn.reportarIncidente((String) in.get("data"), (String) in.get("hora"), (String) in.get("estado"), (String) in.get("cidade"), (String) in.get("bairro"), (String) in.get("rua"), (Long) in.get("tipo_incidente"), (String) in.get("token"), (Long) in.get("id"));
					if (!result[0].equals("OK")) status = result[0]; 
					break;
				case 8:
					result = conn.removerUsuario((String) in.get("senha"), (String) in.get("token"), (Long) in.get("id"));
					if (!result[0].equals("OK")) status = result[0]; 
					break;
				case 9:
					result = conn.desloga((String) in.get("token"), ((Long) in.get("id")).toString());
					if (!result[0].equals("OK")) status = result[0]; 
					break;	
				default:
					status = "OPERACAO NAO CADASTRADA.";
			}
		} catch (Exception ex) {
			return (JSONObject) new JSONParser().parse("{\"operacao\":" + op + ",\"status\":\"ALGUM CAMPO ESTA AUSENTE.\"}");
		}
		conn.close();
		return (JSONObject) new JSONParser().parse("{\"operacao\":" + op + ",\"status\":\"" + status + "\"," + answerJson + "}");
		
	}
	
	private Boolean validaEntrada(Integer op, JSONObject in) {
		switch (op) {
		case 1:
			if (validaQtdCaracteres(in, "nome", 2, 51) && validaQtdCaracteres(in, "senha", 4, 11) && validaQtdCaracteres(in, "email", 6, 61) && validaEmail((String) in.get("email")) && validaQtdEmail((String) in.get("email"), 2, 51, 11)) return true;
			else return false;
		case 2:
			if (validaQtdCaracteres(in, "email", 6, 61) && validaQtdCaracteres(in, "senha", 4, 11) && validaEmail((String) in.get("email")) && validaQtdEmail((String) in.get("email"), 2, 51, 11)) return true;
			else return false;
		case 3:
			if (validaQtdCaracteres(in, "nome", 2, 51) && validaQtdCaracteres(in, "senha", 4, 11) && validaQtdCaracteres(in, "email", 6, 61) && validaEmail((String) in.get("email")) && validaQtdEmail((String) in.get("email"), 2, 51, 11)) return true;
			else return false;
		case 4:
			if (validaData((String) in.get("data")) && validaQtdCaracteres(in, "estado", 1, 3) && validaQtdCaracteres(in, "cidade", 1, 51)) return true;
			else return false;
		case 5:
			if (validaQtdCaracteres(in, "token", 1, 21)) return true;
			else return false;
		case 6:
			if (validaQtdCaracteres(in, "token", 1, 21)) return true;
			else return false;
		case 7:
			if (validaData((String) in.get("data")) && validaHora((String) in.get("hora")) && validaQtdCaracteres(in, "estado", 1, 3) && validaQtdCaracteres(in, "cidade", 1, 51) && validaQtdCaracteres(in, "bairro", 1, 51) && validaQtdCaracteres(in, "rua", 1, 51)) return true;
			else return false;
		case 8:
			if (validaQtdCaracteres(in, "token", 1, 21)) return true;
			else return false;
		case 9:
			if (validaQtdCaracteres(in, "token", 1, 21)) return true;
			else return false;
		default:	
			return true;
		}
	}
	
	private Boolean validaQtdCaracteres(JSONObject in, String field, int lowerBound, int upperBound) {
		if (lowerBound < ((String) in.get(field)).length() && ((String) in.get(field)).length() < upperBound) return true;
		else return false;
	}
	
	public Boolean validaEmail(String email) {
		Pattern p = Pattern.compile("^(.+)@(.+)$");
		Matcher m = p.matcher(email);
		return m.matches();
	}
	
	public Boolean validaQtdEmail(String email, int min, int maxl, int maxr) {
		String[] split = email.split("@");
		if (split.length == 2)
		{
			if (split[0].length() > min && split[0].length() > min && split[0].length() < maxl && split[1].length() < maxr) return true;
		}
		return false;
	}
	
	public Boolean validaData(String data) {
		String[] split = data.split("-");
		if (split.length == 3)
		{
			if (split[0].length() == 4 && split[1].length() == 2 && split[2].length() == 2) return true;
		}
		return false;
	}
	
	public Boolean validaHora(String hora) {
		String[] split = hora.split(":");
		if (split.length == 2)
		{
			if (split[0].length() == 2 && split[1].length() == 2) return true;
		}
		return false;
	}
}
