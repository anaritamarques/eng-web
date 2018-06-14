package server;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@WebServlet("/serv/user") 
public class ServletUsers extends HttpServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("cases");
		String op = request.getParameter("op");
		
		switch (op) {
			case "getuser":
				System.out.println("estesim");
				getUser(request, response);
				break;	
			case "getUserPorfile":
				getUserProfile(request, response);
				break;
			case "getplafond":
				getUserPlafond(request, response);
				break;
			case "getinvested":
				getUserInvested(request, response);
				break;
			case "getprofit":
				getUserProfit(request, response);
				break;
			case "login":
				login(request, response);
				break;
			case "getopencontracts":
				System.out.println("case");
				getOpenContracts(request, response);
				break;
			case "getopencontract":
				getClosedContract(request, response);
				break;
			case "getclosedcontracts":
				getClosedContracts(request, response);
				break;
			case "getclosedcontract":
				getClosedContract(request, response);
				break;
			default:
				break;
		}
	}
	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String op = request.getParameter("op");
		switch (op) {
			case "newuser":
				createNewUser(request, response);
				break;
			default:
				break;
		}
		
	}

	

	private void getUserProfile(HttpServletRequest request, HttpServletResponse response) {
		String token = request.getParameter("token");
	    try { 	
	    	String tokenfinal = (new String(Base64.getDecoder().decode(token), "UTF-8"));
	    	System.out.println("aqui -- " + tokenfinal);
	    	
	    	JsonElement jelement = new JsonParser().parse(tokenfinal);
		    JsonObject  jobject = jelement.getAsJsonObject();
		    
		    String sql = "SELECT * FROM config.users WHERE username=?";
		    Object [] params = {jobject.get("username").getAsString()};
		    String  result = "";
		    System.out.println("aqui1: " + tokenfinal);
		    PgConection con = PgConection.getDBCon();
			System.out.println("aqui2");
			ArrayList<Object> r = con.getObjectsSQL(sql, params);
			if(r != null){	
			 result = r.get(1)+","+r.get(2)+","+r.get(3)+","+r.get(4)+","+r.get(5)+","+r.get(6)+","+r.get(7)+","+r.get(8)+","+r.get(9);
			}else{
				result= "erro";
			}
		
			con.destroy();
			System.out.println("result: "+result);
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



	private void getUser(HttpServletRequest request, HttpServletResponse response) {
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
	
	private void createNewUser(HttpServletRequest request, HttpServletResponse response) {
		String token = request.getParameter("token");
	    
	    try {
	    	String tokenfinal = (new String(Base64.getDecoder().decode(token), "UTF-8"));
			
	    	JsonElement jelement = new JsonParser().parse(tokenfinal);
		    JsonObject  jobject = jelement.getAsJsonObject();
		    
		    String sql = "INSERT INTO config.users(email, pass_word, nome, username, contacto, valor_act) VALUES (?, ?, ?, ?, ?,50) RETURNING  user_id; ";
		    Object [] params = {jobject.get("email").getAsString(), jobject.get("pass").getAsString(), jobject.get("name").getAsString(), jobject.get("username").getAsString(), jobject.get("contacto").getAsString()};
		    
		    String  result = "";
			PgConection con = PgConection.getDBCon();
			ArrayList<Object> r = con.getObjectsSQL(sql, params);
			if(r != null){
				result= "sucesso";
				
			}else{
				result= "erro";
			}	
			con.destroy();
			
			System.out.println("ola : " + result);
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
	
	
	private void getUserPlafond(HttpServletRequest request, HttpServletResponse response){
		String token = request.getParameter("token");
	    
	    try {
	    	
	    	String tokenfinal = (new String(Base64.getDecoder().decode(token), "UTF-8"));
    	
	    	JsonElement jelement = new JsonParser().parse(tokenfinal);
		    JsonObject  jobject = jelement.getAsJsonObject();
		    
		    String username = jobject.get("username").getAsString();
		    String sql = "SELECT valor_act FROM config.users WHERE username='"+username+"';";
		    String result = "";
			PgConection con = PgConection.getDBCon();
			ArrayList<Object> r= con.getObjectsSQL(sql);
			if(r != null){
				result=String.valueOf(r.get(0));
			}else{
				result= "erro";
			}
				
			con.destroy();			
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
	
	private void getUserInvested(HttpServletRequest request, HttpServletResponse response){
		String token = request.getParameter("token");
	    
	    try {
	    	
	    	String tokenfinal = (new String(Base64.getDecoder().decode(token), "UTF-8"));
    	
	    	JsonElement jelement = new JsonParser().parse(tokenfinal);
		    JsonObject  jobject = jelement.getAsJsonObject();
		    
		    String getUserId = "SELECT user_id FROM config.users WHERE username=?";
		    Object [] userIdParams = {jobject.get("username").getAsString()};
		    int resultId = 0;
		    String result = "";
			PgConection con = PgConection.getDBCon();
			ArrayList<Object> r = con.getObjectsSQL(getUserId, userIdParams);
			if(r!=null) {
				if(r.size()==1){
					resultId=(int)r.get(0);
				    String sql = "SELECT invested FROM info.contract WHERE user="+String.valueOf(resultId)+" and closingdate is null and invested is not null";
				    float total = 0;
					PgConection con1 = PgConection.getDBCon();
					ArrayList<Object> r1= con1.getObjectsSQL(sql);
					if(r1 != null){
						for(Object o: r1){
							total+=(float)o;
						}
						result=String.valueOf(total);
					}else{
						result= "erro";
					}
					con1.destroy();
				}
			}
				
			con.destroy();			
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
	
	private void getUserProfit(HttpServletRequest request, HttpServletResponse response){
		String token = request.getParameter("token");
	    
	    try {
	    	
	    	String tokenfinal = (new String(Base64.getDecoder().decode(token), "UTF-8"));
    	
	    	JsonElement jelement = new JsonParser().parse(tokenfinal);
		    JsonObject  jobject = jelement.getAsJsonObject();
		    
		    String getUserId = "SELECT user_id FROM config.users WHERE username=?";
		    Object [] userIdParams = {jobject.get("username").getAsString()};
		    int resultId = 0;
		    String result = "";
			PgConection con = PgConection.getDBCon();
			ArrayList<Object> r = con.getObjectsSQL(getUserId, userIdParams);
			if(r!=null) {
				if(r.size()==1){
					resultId=(int)r.get(0);
				    String sql = "SELECT profitloss FROM info.contract WHERE user="+String.valueOf(resultId)+" and closingdate is null and profitloss is not null";
				    float total = 0;
					PgConection con1 = PgConection.getDBCon();
					ArrayList<Object> r1= con1.getObjectsSQL(sql);
					if(r1 != null){
						for(Object o: r1){
							total+=(float)o;
						}
						result=String.valueOf(total);
					}else{
						result= "erro";
					}
					//con1.destroy();
				}
			}
				
			con.destroy();			
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
	
	private void login(HttpServletRequest request, HttpServletResponse response) {	
		String token = request.getParameter("token");
	    
	    try { 	
	    	String tokenfinal = (new String(Base64.getDecoder().decode(token), "UTF-8"));
			
	    	
	    	JsonElement jelement = new JsonParser().parse(tokenfinal);
		    JsonObject  jobject = jelement.getAsJsonObject();
		    
		    String sql = "Select username FROM config.users WHERE email =? AND pass_word= ?";
		    Object [] params = {jobject.get("email").getAsString(), jobject.get("pass").getAsString()};
		    String  result = "";
			PgConection con = PgConection.getDBCon();
			ArrayList<Object> r = con.getObjectsSQL(sql, params);
			if(r != null){	
				result = r.get(0).toString();
			}else{
				result= "erro";
			}
				
			con.destroy();
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
	
	private void getOpenContracts(HttpServletRequest request, HttpServletResponse response){
		System.out.println("entra");
		String token = request.getParameter("token");
	    
	    try {
	    	
	    	String tokenfinal = (new String(Base64.getDecoder().decode(token), "UTF-8"));
    	
	    	JsonElement jelement = new JsonParser().parse(tokenfinal);
		    JsonObject  jobject = jelement.getAsJsonObject();
		    
		    String getUserId = "SELECT user_id FROM config.users WHERE username=?";
		    Object [] userIdParams = {jobject.get("username").getAsString()};
		    int resultId = 0;
		    String result = "";
			PgConection con = PgConection.getDBCon();
			ArrayList<Object> r = con.getObjectsSQL(getUserId, userIdParams);
			System.out.println("1");
			if(r!=null) {
				System.out.println("2");
				if(r.size()==1){
					System.out.println("3");
					resultId=(int)r.get(0);
					System.out.println(resultId);
				    String sql = "SELECT id FROM info.contract WHERE userid="+resultId+" and closingdate is null";
				    System.out.println(sql);
					PgConection con1 = PgConection.getDBCon();
					ArrayList<Object> r1= con1.getObjectsSQL(sql);
					if(r1 != null){
						for(Object o: r1){
							result=result+(String)o+" ";
						}
						System.out.println("result:"+ result);
					}else{
						result= "erro";
					}
					con1.destroy();
				}
			}
				
			con.destroy();			
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
	
	private void getOpenContract(HttpServletRequest request, HttpServletResponse response){
		String token = request.getParameter("token");
	    
	    try {
	    	
	    	String tokenfinal = (new String(Base64.getDecoder().decode(token), "UTF-8"));
    	
	    	JsonElement jelement = new JsonParser().parse(tokenfinal);
		    JsonObject  jobject = jelement.getAsJsonObject();
		    
		    String getInfo = "SELECT asset,type,openingprice, openingate, units, invested, profitloss FROM info.contract WHERE id=?";
		    Object [] infoParams = {jobject.get("id").getAsString()};
		    String result = "";
			PgConection con = PgConection.getDBCon();
			ArrayList<Object> r = con.getObjectsSQL(getInfo, infoParams);
			if(r!=null) {
				result=String.valueOf(r.get(0))+String.valueOf(r.get(1))+String.valueOf(r.get(3))+String.valueOf(r.get(4))+
						String.valueOf(r.get(5))+String.valueOf(r.get(6));
			}
				
			con.destroy();			
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
	
	private void getClosedContracts(HttpServletRequest request, HttpServletResponse response){
		System.out.println("entra");
		String token = request.getParameter("token");
	    
	    try {
	    	
	    	String tokenfinal = (new String(Base64.getDecoder().decode(token), "UTF-8"));
    	
	    	JsonElement jelement = new JsonParser().parse(tokenfinal);
		    JsonObject  jobject = jelement.getAsJsonObject();
		    
		    String getUserId = "SELECT user_id FROM config.users WHERE username=?";
		    Object [] userIdParams = {jobject.get("username").getAsString()};
		    int resultId = 0;
		    String result = "";
			PgConection con = PgConection.getDBCon();
			ArrayList<Object> r = con.getObjectsSQL(getUserId, userIdParams);
			System.out.println("1");
			if(r!=null) {
				System.out.println("2");
				if(r.size()==1){
					System.out.println("3");
					resultId=(int)r.get(0);
					System.out.println(resultId);
				    String sql = "SELECT id FROM info.contract WHERE userid="+String.valueOf(resultId)+" and closingdate is not null";
					PgConection con1 = PgConection.getDBCon();
					ArrayList<Object> r1= con1.getObjectsSQL(sql);
					if(r1 != null){
						for(Object o: r1){
							result=result+(String)o+" ";
						}
						System.out.println("result:"+ result);
					}else{
						result= "erro";
					}
					con1.destroy();
				}
			}
				
			con.destroy();			
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
	
	private void getClosedContract(HttpServletRequest request, HttpServletResponse response){
		String token = request.getParameter("token");
	    
	    try {
	    	
	    	String tokenfinal = (new String(Base64.getDecoder().decode(token), "UTF-8"));
    	
	    	JsonElement jelement = new JsonParser().parse(tokenfinal);
		    JsonObject  jobject = jelement.getAsJsonObject();
		    
		    String getInfo = "SELECT asset,type,openingprice, closingprice, openingate, closingdate, units, invested, profitloss FROM info.contract WHERE id=?";
		    Object [] infoParams = {jobject.get("id").getAsString()};
		    String result = "";
			PgConection con = PgConection.getDBCon();
			ArrayList<Object> r = con.getObjectsSQL(getInfo, infoParams);
			if(r!=null) {
				result=String.valueOf(r.get(0))+String.valueOf(r.get(1))+String.valueOf(r.get(3))+String.valueOf(r.get(4))+
						String.valueOf(r.get(5))+String.valueOf(r.get(6))+String.valueOf(r.get(7))+String.valueOf(r.get(8));
			}
				
			con.destroy();			
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
