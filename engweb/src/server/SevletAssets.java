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

@WebServlet("/serv/assets")
public class SevletAssets extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String op = request.getParameter("op");
		
		switch (op) {
		/*case "sellcontract":
			createSellContract(request, response);
			break;	*/
		default:
			break;
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String op = request.getParameter("op");
		switch (op) {
		case "createcontract":
			createContract(request, response);
			break;
		case "newasset":
			newasset(request, response);
			break;
		default:
			break;
		}
	}
	
	private void createContract(HttpServletRequest request, HttpServletResponse response) {
		String token = request.getParameter("token");
			    
	    try {
	    	String tokenfinal = (new String(Base64.getDecoder().decode(token), "UTF-8"));
	    	System.out.println(tokenfinal);
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
					
					String getAssetId = "SELECT id FROM info.asset WHERE name=?";
					Object [] assetIdParams = {jobject.get("assetname").getAsString()};
				    int resultAssetId = 0;
					PgConection con1 = PgConection.getDBCon();
					ArrayList<Object> r1 = con1.getObjectsSQL(getAssetId, assetIdParams);
					if(r1!=null){
						if(r1.size()==1){
							con1.destroy();
							resultAssetId=(int)r1.get(0);
							
							String getUserPlafond = "SELECT valor_act FROM config.users WHERE user_id=?";
							String [] userPlafondParams = {String.valueOf(resultId)};
							float resultPlafond = 0;
							PgConection con2 = PgConection.getDBCon();
							ArrayList<Object> r2 = con2.getObjectsSQL(getUserPlafond, userPlafondParams);
							if(r2!=null){
								if(r2.size()==1) {
									con2.destroy();
									resultPlafond = (float)r2.get(0);
									float invested = jobject.get("invested").getAsFloat();
									System.out.println(resultPlafond);
									System.out.println(invested);
									float finalPlafond = resultPlafond-(invested);
									System.out.println(finalPlafond);
									if(finalPlafond<0){
										result = "Not enough plafond!";
									}
									else{
										String updatePlafond = "UPDATE config.users SET valor_act="+finalPlafond+" returning user_id";
										PgConection con3 = PgConection.getDBCon();
										ArrayList<Object> r3 =con3.getObjectsSQL(updatePlafond);
										if(r3!=null){
											con3.destroy();
											String newContract = "INSERT INTO info.contract (userid, asset, type, openingprice, invested, units) VALUES (?, ?, ?, ?, ?, ?) returning id";
											String type = jobject.get("type").getAsString();
											String openingprice = jobject.get("openingprice").getAsString();
											String units = jobject.get("units").getAsString();
											
											Object [] contractParams = {resultId, resultAssetId, type, openingprice, invested, units};

											PgConection con4 = PgConection.getDBCon();
											ArrayList<Object> r4 =con4.getObjectsSQL(newContract, contractParams);
											if(r4!=null){
												con4.destroy();
												result="sucesso";
											}
											else{
												result="erro";
											}
										}
										else{
											result = "erro";
										}
									}
								}
								else{
									result = "erro";
								}
							}
							else{
								result="erro";
							}
						}
						else{
							result="erro";
						}
					}else{
						result= "erro";
					}
					
				}else{
					result= "erro";
				}
			}
			else{
				result="erro";
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
	
	private void newasset(HttpServletRequest request, HttpServletResponse response) {
		String token = request.getParameter("token");
	    
	    try {
	    	String tokenfinal = (new String(Base64.getDecoder().decode(token), "UTF-8"));
	    	JsonElement jelement = new JsonParser().parse(tokenfinal);
		    JsonObject  jobject = jelement.getAsJsonObject();
		    
		    String sql = "INSERT INTO info.assets(symbol, change) VALUES (?, ?); ";
		    Object [] params = {jobject.get("symbol"), jobject.get("change")};
		    
		    PgConection con = PgConection.getDBCon();
			con.getObjectsSQL(sql, params);
			
			
			con.destroy();
			String result= "sucesso";
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
		    OutputStream os = response.getOutputStream();
		    os.write(result.getBytes());
			os.flush();
			os.close();
			
	    }catch (Exception e) {
			// TODO: handle exception
		}
		
	}
}
