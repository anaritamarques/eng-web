package server.utils;

import server.PgConection;

import java.lang.reflect.Constructor;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class SQLTools {
	
	public static Constructor<?> getConstructorByNumParams(String className, int numParams) throws Exception {
		
		return getConstructorByNumParams(Class.forName(className), numParams);
	}
	
	public static Constructor<?> getConstructorByNumParams(Class<?> classe, int numParams) throws Exception {
		
		Constructor<?>[] cs = classe.getConstructors();
		
		for (Constructor<?> c : cs) {
			if (c.getParameterTypes().length == numParams) {
				return c;
			}
		}
		throw new Exception(classe.getName() + " not found exception");
	}
	
	public static Constructor<?> getConstructorByNumParams(String className, Class<?>[] argTypes) throws Exception {
		return getConstructorByArgTypes(Class.forName(className), argTypes);
	}
	
	public static Constructor<?> getConstructorByArgTypes(Class<?> classe, Class<?>[] argTypes) throws Exception {
		String params = "";
		String cons = "";
		
		try {
			Constructor<?>[] cs = classe.getConstructors();
			for (Constructor<?> c : cs) {
				Class<?>[] cls = c.getParameterTypes();
				
				if (cls.length == argTypes.length) {
					boolean ok = true;
					for (int i = 0; i < cls.length; i++) {
						if (argTypes[i] != null) {
							Class<?> cl = argTypes[i];
							if (cl.equals(Class.forName("java.sql.Date"))) {
								cl = Class.forName("java.util.Date");
							}
							
							if (!cls[i].equals(cl)) {
								ok = false;
								break;
							}
						}
					} 
					if (ok) {
						return c;
					}
				}
				
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		throw new Exception("Construtor for class: " + classe.getName() + " not found exception " + params + " constructor " + cons);
	}
	
	public static Boolean checkGeometry(String wkt) throws Exception {
		
		PgConection conn = PgConection.getDBCon();
		
		String sql = "SELECT ST_IsValid(ST_Multi(ST_Transform(ST_GeomFromText(?, 900913),3763)))";
		
		ArrayList<Object> result = conn.getObjectsSQL(sql, new Object[] { wkt });
		conn.destroy();
		
		return Boolean.valueOf((result != null) && (result.size() > 0) ? ((Boolean) result.get(0)).booleanValue() : false);
	}
	
	public static String encryptPassword(String param) {
		try {
	    	MessageDigest md = MessageDigest.getInstance("MD5");
	        byte[] passBytes = param.getBytes();
	        md.reset();
	        byte[] digested = md.digest(passBytes);
	        StringBuffer sb = new StringBuffer();
	        for(int i=0;i<digested.length;i++){
	            sb.append(Integer.toHexString(0xff & digested[i]));
	        }
	        	return sb.toString();
	    } catch (NoSuchAlgorithmException ex) {
			 ex.printStackTrace();
	    }
        return null;
	}	
}