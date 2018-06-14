package server.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class PassGen {

	public static String genPass() {
		String passGen = null;
		char[] chars = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
		
		StringBuilder sb = new StringBuilder();
		java.util.Random random = new java.util.Random();
		
		for (int i = 0; i < 10; i++) {
			char c = chars[random.nextInt(chars.length)];
			sb.append(c);
		}
		passGen = sb.toString();
		
		return passGen;
	}
	
	public static String encryptPass(String param) {
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