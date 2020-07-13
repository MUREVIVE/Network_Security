import java.math.BigInteger;
import java.security.MessageDigest;

public class HashFunction {

	public static String getMD5(String text1) {
		// return string text
		String result1 = "";
		
		try {
			MessageDigest md1 = MessageDigest.getInstance("MD5"); // initialized
			md1.reset(); // reset to its initialized state
			md1.update(text1.getBytes("US-ASCII")); // update the digest using the specified byte
			result1 = String.format("%032x", new BigInteger(1, md1.digest())); // digest() : completes the hash computation
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result1;
	}
	
	public static String getSHA_1(String text2) {
		// return string text
		String result2 = "";
		
		try {
			MessageDigest md2 = MessageDigest.getInstance("SHA-1"); 
			md2.reset(); 
			md2.update(text2.getBytes("US-ASCII")); 
			result2 = String.format("%040x", new BigInteger(1, md2.digest())); 
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result2;
	}
	
	public static String getSHA_256(String text3) {

		String result3 = "";
		
		try {
			MessageDigest md3 = MessageDigest.getInstance("SHA-256"); 
			md3.reset(); 
			md3.update(text3.getBytes("US-ASCII")); 
			result3 = String.format("%064x", new BigInteger(1, md3.digest())); 
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result3;
	}
	
	public static String getSHA_512(String text4) {

		String result4 = "";
		
		try {
			MessageDigest md4 = MessageDigest.getInstance("SHA-512"); 
			md4.reset(); 
			md4.update(text4.getBytes("US-ASCII")); 
			result4 = String.format("%0128x", new BigInteger(1, md4.digest())); 
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result4;
	}
	public static void main(String[] args) {
		// 가변 길이의 텍스트를 입력 받아 
		// MD5 해시값, SHA-1 해시값, SHA-256 해시값, SHA-512 해시값을 출력.
		
		//String Plain_Text = "SHA-2 is a set of cryptographic hash functions (SHA-224, SHA-256, SHA-384, SHA-512, SHA-512/224, SHA-512/256) designed by the U.S. National Security Agency (NSA) and published in 2001 by the NIST as a U.S. Federal Information Processing Standard (FIPS). SHA stands for Secure Hash Algorithm. SHA-2 includes a significant number of changes from its predecessor, SHA-1. SHA-2 currently consists of a set of six hash functions with digests that are 224, 256, 384 or 512 bits.";
		String Plain_Text = "";
		
		String md5 = getMD5(Plain_Text);
		String sha1 = getSHA_1(Plain_Text);
		String sha256 = getSHA_256(Plain_Text);
		String sha512 = getSHA_512(Plain_Text);
		
		System.out.println("MD5 : " + md5);
		System.out.println("SHA-1 : " + sha1);
		System.out.println("SHA-256 : " + sha256);
		System.out.println("SHA-512 : " + sha512);
		
		
		
	}

}
