package server;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@WebServlet("/serv/login") 
public class ServletLogin extends HttpServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String token = request.getParameter("token");
		
		JsonElement jelement = new JsonParser().parse(token);
	    JsonObject  jobject = jelement.getAsJsonObject();
	    
	    try {
		    String sql = "";
		    Object [] params = {jobject.get("email"), jobject.get("tipo"), jobject.get("pass"), jobject.get("nome")};
		    String  result = "";
	    
		
			
			PgConection con = PgConection.getDBCon();
			ArrayList<Object> r = con.getObjectsSQL(sql, params);
			if(r != null){
				
				result= "sucesso";
				
			}else{
				result= "erro";
			}
			
			
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
		    OutputStream os = response.getOutputStream();
		    os.write(result.getBytes());
			os.flush();
			os.close();
			
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
