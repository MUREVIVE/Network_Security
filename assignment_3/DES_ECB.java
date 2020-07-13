import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class DES_ECB {

      public static SecretKey key;
      public static String iv;
      
      public static void main(String[] args) throws IndexOutOfBoundsException {
         
    	 byte[] k = {0x01, 0x23, 0x45, 0x67, (byte)0x89, (byte)0xAB,(byte)0xCD,(byte)0xEF};
         key = new SecretKeySpec(k, 0, k.length, "DES");
         
         String plainText = "zzasdfaszzasdfas";  
         
         // byte배열로 선언 후 plaintext의 값을 받는다.
         byte[] PlainText = null;
 		 try {
 			 
 			 if(plainText.length() == 0) // 평문에 문자열이 존재하지 않는 경우
 			 {
 				System.out.println("문자열이이 존재하지 않습니다.");
 				System.exit(0);
 			 }
 			 PlainText = plainText.getBytes("US-ASCII");
 			 
 			 byte[] zeroPadding = null;
 			 // plainText의 길이가 8의 배수이면
 			 int padding_length;
 			 
 			 // 문자열의 길이가 8의 배수인 경우
 			 if(PlainText.length % 8 == 0)
 			 {
 				 padding_length = 8;
 				zeroPadding = new byte[PlainText.length]; // zero padding의 경우 padding이 없다.
 			 }
 			 else
 			 {
 				padding_length = 8 - (plainText.length() % 8); // 패딩해야 할 길이 찾기
 				zeroPadding = new byte[PlainText.length + padding_length]; // 8의 배수 비트 길이 맞추기
 			 }
 			
 	 		byte[] ANSIX9_23 = new byte[PlainText.length + padding_length];
 	 		byte[] PKCS5_7 = new byte[PlainText.length + padding_length];
 	 		
 	 		for(int i=0; i<plainText.length(); i++)
 	 		{
 	 			zeroPadding[i] = PlainText[i];
 	 			ANSIX9_23[i] = PlainText[i];
 	 			PKCS5_7[i] = PlainText[i];
 	 		}
 	 		
 	 		
 	 		int last = plainText.length() + padding_length;
 	 		if(last % 8 != 0) // 문자열 길이 + 패딩 길이가 8의 배수가 아닌 경우
 	 		{
 	 			for(int i=plainText.length(); i < last; i++)
 	 			{
 	 				zeroPadding[i] = (byte) 0; // 나머지 바이트를 0으로 채움.
 	 				ANSIX9_23[i] = (byte) 0;   // 우선 나머지 바이트를 0으로 채움.
 	 				PKCS5_7[i] = (byte) padding_length; // 나머지 바이트를 padding_length로 채움.
 	 			}
 	 		}
 	 		else
 	 		{
 	 			for(int i=plainText.length(); i < last; i++)
 	 			{
 	 				ANSIX9_23[i] = (byte) 0;   // 우선 나머지 바이트를 0으로 채움.
 	 				PKCS5_7[i] = (byte) padding_length; // 나머지 바이트를 padding_length로 채움.
 	 			}
 	 		}
 		
 	 		ANSIX9_23[last - 1] = (byte) padding_length; // 마지막 바이트는 Padding length로 나타낸다.

 	 		byte[] result1= DES_encrypt(zeroPadding);
 	 		byte[] result2= DES_encrypt(ANSIX9_23);
 	 		byte[] result3= DES_encrypt(PKCS5_7);
 	 		
 	 		//
 	 		System.out.println("ZeroPadding : " + getHexString(result1));
 	 		System.out.println("ANSI X9.23  : " + getHexString(result2));
 	 		System.out.println("PKCS5_7     : " + getHexString(result3));
 	 		
 	 		
         } catch (Exception e) {
            e.printStackTrace();
         }
         
      }
      
      public static byte[] DES_encrypt(byte[] data) throws Exception { 
           if (data == null || data.length == 0) 
               return null; 

           Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");  // DES 객체 생성
           cipher.init(Cipher.ENCRYPT_MODE, key); // 암호문 만들기
    	   
           int Count = data.length / 8; // 예를 들어 평문의 길이가 24인 경우 3번의 count를 반복.
           byte[] result = new byte[Count * 8]; 
           
           for(int i=0; i < Count; i++)
           {
        	   // 24의 경우 0 ~ 7 / 8 ~ 15 / 16 ~ 23을  encryptedByte에 복사.
        	   byte[] encryptedByte = cipher.doFinal(Arrays.copyOfRange(data, i * 8, (i + 1)*8)); 
        		   
        	   for(int j=0; j<8; j++)
        		   result[i*8 + j] = encryptedByte[j];
        	   
           }
           return result;
      }
      
      // byte를 16진수로 바꿔주는 함수
      public static String getHexString(byte[] buf) { 
    	  
    	  StringBuilder builder = new StringBuilder(buf.length);
    	  
    	  for (int i = 0; i<buf.length; i++) 
    		  builder.append(String.format("%02X", buf[i]));
    	  
    	  //return builder.toString();
    	  return new String(builder);
      }
      
}