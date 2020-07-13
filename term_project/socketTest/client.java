
// ���� - ��ű��� ����� �Ǵ� ����

package socketTest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Formatter;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.io.InputStream;
import java.io.OutputStream;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class client {
	
	static final int KEY_SIZE = 512;
	static byte[] bytes;
	static String receiveMessage;
	static String sendMessage;
	static OutputStream os;
	static InputStream is;
	
	// AES IV, symmetrickey �ʱ�ȭ
	static String IV = "networksecuritys";
	static String AES256KEY = "asdfzxcvqwerfdsareqwvczsdfg";
	
	static String encryptedIV = "";
	
    public static void main(String[] args) {
        Socket socket = null;
        BufferedReader in;
        PrintWriter out;
        String data=null;
        bytes = new byte[128];
        
        try {
            socket = new Socket("127.0.0.1", 9080);//ip address, port
            
            /////////////////////////////
            // Client - Receive Public key  
         	is = socket.getInputStream();
         	int readByteCount = is.read(bytes);
         	receiveMessage = new String(bytes, 0, readByteCount, "UTF-8");
         	
         	System.out.println("Received Public Key: " + receiveMessage);
            
         
         	// Create AES256 Key + encrypted AES256 KEY
         	System.out.println("Creating AES256KEY...");
         	encryptedIV = encode(IV, receiveMessage);
         	String encryptedAES256KEY = encode(AES256KEY, receiveMessage);
         	
         	System.out.println("Encrypted IV : " + encryptedIV); 
         	System.out.println("Encrypted AES256KEY : " + encryptedAES256KEY);
         	
         	
         	// Send encrypted AES256 KEY
			os = socket.getOutputStream();
			sendMessage = encryptedIV;
			bytes = sendMessage.getBytes("UTF-8");
			
			os.write(bytes, 0, bytes.length);
			os.flush();
			
			// Send encrypted AES256 IV
			os = socket.getOutputStream();
			sendMessage = encryptedAES256KEY;
			bytes = sendMessage.getBytes("UTF-8");
						
			os.write(bytes, 0, bytes.length);
			os.flush();
			System.out.println("Data send");
			
			
			
            //�ǽð����� �����͸� �ְ� �ޱ� ���Ͽ� ������ ���
            Output output = new Output(socket);
            output.start();
            
            while(true) {
                
                try { 
                    //�������κ��� ������ �޾ƿ���
                    try {
                        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        data = in.readLine();
                  
                    }catch(Exception e) {
                        break;
                    }
                    
                    // exit ����
                    if(data == null || data.equals("exit")) {
                        socket.close();
                        output.interrupt();        //output������ ����
                     try {   
                    	output.sleep(20);
                        SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");
                        Calendar cal = Calendar.getInstance();
                        String today = null;
                        today = formatter.format(cal.getTime());
                        Timestamp ts = Timestamp.valueOf(today);
                        
                        
                        System.out.println("Client : "+ data + " " + ts);
                        String encryptData = Encrypt(data, AES256KEY);
                        System.out.println("��ȣ��: " + (encryptData));
                        
                        
                        
                    } catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                        break;
                        
                    }
                    // ������ ���� exit �Ǵ� null�� �ƴѰ��
                    else if(data!=null) {
                    	
                    	SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");
                        Calendar cal = Calendar.getInstance();
                        String today = null;
                        today = formatter.format(cal.getTime());
                        Timestamp ts = Timestamp.valueOf(today);
                        
                        
                		
                        System.out.println("Client : "+ data + " " + ts);
                        String encryptData = Encrypt(data, AES256KEY);
                		System.out.println("��ȣ��: " + (encryptData));
                		
                		
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                socket.close();
            }catch (Exception e) {
            	
            }
            
            
            System.out.println("������ ���� ����");
           
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
   
    // RSA ��ȣȭ
    static String encode(String plainData, String stringPublicKey) {
        String encryptedData = null;
        try {
            //������ ���޹��� ����Ű�� ����Ű��ü�� ����� ����
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] bytePublicKey = Base64.getDecoder().decode(stringPublicKey.getBytes());
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(bytePublicKey);
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

            //������� ����Ű��ü�� ������� ��ȣȭ���� �����ϴ� ����
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            //���� ��ȣȭ�ϴ� ����
            byte[] byteEncryptedData = cipher.doFinal(plainData.getBytes());
            encryptedData = Base64.getEncoder().encodeToString(byteEncryptedData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedData;
    }
    
    
    public static String Encrypt(String text, String key) throws Exception
    {
    	
	 	IV = key.substring(0, 16);
		byte[] keyBytes = new byte[16];
		byte[] b = key.getBytes("UTF-8");
		int len = b.length;
		if (len > keyBytes.length) {
			len = keyBytes.length;
		}
		System.arraycopy(b, 0, keyBytes, 0, len);
		SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
		
		Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
		c.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(IV.getBytes()));
		
		byte[] encrypted = c.doFinal(text.getBytes("UTF-8"));
		
		return byteArrayToHex(encrypted);
    }
    
    public static String byteArrayToHex(byte[] ba) {
        if (ba == null || ba.length == 0) {
            return null;
        }
 
        StringBuffer sb = new StringBuffer(ba.length * 2);
        String hexNumber;
        for (int x = 0; x < ba.length; x++) {
            hexNumber = "0" + Integer.toHexString(0xff & ba[x]);
 
            sb.append(hexNumber.substring(hexNumber.length() - 2));
        }
        return sb.toString();
    }
    
    public void setIV(String encryptedIV) {
		 this.encryptedIV = encryptedIV;
	 }
	 
	 public String getIV() {
		 return encryptedIV;
	 }
}
 
 
 
class Output extends Thread{
    BufferedReader in;
    PrintWriter out;
    Socket socket;
    
    static String AES256KEY = "asdfzxcvqwerfdsareqwvczsdfg";
    static server s = new server();
    Output(Socket socket){
        this.socket=socket;
        try {
            in = new BufferedReader(new InputStreamReader(System.in));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
        } catch (IOException e) {
            System.out.println("aa");
            Output.interrupted();
        }
    }
    public void run() {
        String data;
        while(true) {
            try {
                //Ű����κ��� �Է�
                data = in.readLine();
                out.println(data);                        //������ ������ ����
                out.flush();
                if(data.equals("exit") || socket.isClosed()){
                	
                	try {   
                        SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");
                        Calendar cal = Calendar.getInstance();
                        String today = null;
                        today = formatter.format(cal.getTime());
                        Timestamp ts = Timestamp.valueOf(today);
                        
                        
                        System.out.println("Client : "+ data + " " + ts);
                        String encryptData = Encrypt(data, AES256KEY);
                        
                        //System.out.print( " Timestamp : " + ts);
                        
                        System.out.println("��ȣ��: " + (encryptData));
                        
                        
                    } catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("������ ���� ���� Output ������ ����");
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static String Encrypt(String text, String key) throws Exception
    {
    	
    	String iv = s.getIV();
	 	iv = key.substring(0, 16);
		byte[] keyBytes = new byte[16];
		byte[] b = key.getBytes("UTF-8");
		int len = b.length;
		
		if (len > keyBytes.length) {
			len = keyBytes.length;
		}
		System.arraycopy(b, 0, keyBytes, 0, len);
		SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
		
		Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
		c.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));
		
		byte[] encrypted = c.doFinal(text.getBytes());
		
		
		return byteArrayToHex(encrypted);
    }
    
    public static String byteArrayToHex(byte[] ba) {
        if (ba == null || ba.length == 0) {
            return null;
        }
 
        StringBuffer sb = new StringBuffer(ba.length * 2);
        String hexNumber;
        for (int x = 0; x < ba.length; x++) {
            hexNumber = "0" + Integer.toHexString(0xff & ba[x]);
 
            sb.append(hexNumber.substring(hexNumber.length() - 2));
        }
        return sb.toString();
    }
 
}