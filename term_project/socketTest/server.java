
// ���� - ��ű��� ����� �Ǵ� ����

package socketTest;

import java.lang.Thread;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class server {
	
	 static final int KEY_SIZE = 512;
	 static byte[] bytes;
	 static OutputStream os;
	 static InputStream is;
	 static String receiveMessage;
	 static String sendMessage;
	 public static String AESKEY;
	 

	 static String IV = "";
	 static String encryptedAES256KEY = "";
	
	public static void main(String[] args) {
		
		bytes = new byte[128];
		try {
			ServerSocket server = new ServerSocket(9080); // port num 9080

			System.out.println("waiting...");
			while (true) {
				
				Socket socket = server.accept(); // Ŭ���̾�Ʈ ���� ���
				System.out.println("Client is connected.");
				
				
				// RSA publicKey, privateKey ����
				HashMap<String, String> rsaKeyPair = createKeypairAsString();
				String publicKey = rsaKeyPair.get("publicKey");
				String privateKey = rsaKeyPair.get("privateKey");
				
				System.out.println("Creating RSA key pair..");
				System.out.println("public key:" + publicKey);
				System.out.println("private key:" + privateKey);
				
				///////////////////////////
				// ������ ������ - ����Ű
				os = socket.getOutputStream();
				sendMessage = publicKey.toString();
				bytes = sendMessage.getBytes("UTF-8");
				
				
				
				os.write(bytes, 0, bytes.length);
				os.flush();
				System.out.println("Data send");
				
				
				///////////////////////////
				// IV, AES256KEY �ޱ�
	         	is = socket.getInputStream();
	         	int readByteCount = is.read(bytes);
	         	receiveMessage = new String(bytes, 0, readByteCount, "UTF-8");
	         	System.out.println("Received Encrypted IV : " + receiveMessage);
	         	IV = sendMessage;
	         	
	         	//System.out.println("����");
	         	is = socket.getInputStream();
	         	readByteCount = is.read(bytes);
	         	receiveMessage = new String(bytes, 0, readByteCount, "UTF-8");
	         	System.out.println("Received Encrypted AES256KEY : " + receiveMessage);
	         	encryptedAES256KEY = receiveMessage;
	         	
	         	
	         	// ��ȣȭ�� EncryptedAES256KEY�� ����Ű�� ��ȣȭ
				AESKEY = decode(receiveMessage, privateKey);
				System.out.println("Decrypted AESKEY : " + AESKEY);
				
				
				Input input = new Input(socket); // Input ������ ����
				input.start();
				Output output = new Output(socket); // Output ������ ����
				output.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 public void setKey(String AESKEY) {
		 this.AESKEY = AESKEY;
	 }
	 
	 public String getKey() {
		 return AESKEY;
	 }
	 
	 public void setIV(String IV) {
		 this.IV = IV;
	 }
	 
	 public String getIV() {
		 return IV;
	 }
	 
	/**
	 * RSA Keypair 
	 */
	static HashMap<String, String> createKeypairAsString() {
		HashMap<String, String> stringKeypair = new HashMap<>();
		try {
			SecureRandom secureRandom = new SecureRandom();
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(KEY_SIZE, secureRandom);
			KeyPair keyPair = keyPairGenerator.genKeyPair();

			PublicKey publicKey = keyPair.getPublic();
			PrivateKey privateKey = keyPair.getPrivate();

			String stringPublicKey = Base64.getEncoder().encodeToString(publicKey.getEncoded());
			String stringPrivateKey = Base64.getEncoder().encodeToString(privateKey.getEncoded());

			stringKeypair.put("publicKey", stringPublicKey);
			stringKeypair.put("privateKey", stringPrivateKey);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringKeypair;
	}

	 
	 public static String decode(String encryptedData, String stringPrivateKey) throws Exception {
	        String decryptedData = null;
	        
	        byte[] byteEncryptedData = null;
	        byte[] byteDecryptedData = null;
	        
	        try {
	            //������ ���޹��� ����Ű�� ����Ű��ü�� ����� ����
	            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	            byte[] bytePrivateKey = Base64.getDecoder().decode(stringPrivateKey.getBytes());
	            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(bytePrivateKey);
	            PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
	            
	            
	            //������� ����Ű��ü�� ������� ��ȣȭ���� �����ϴ� ����
	            Cipher cipher = Cipher.getInstance("RSA");
	            cipher.init(Cipher.PRIVATE_KEY, privateKey);

	            //��ȣ���� ��ȭ�ϴ� ����
	            byteEncryptedData = Base64.getMimeDecoder().decode(encryptedData.getBytes());
	            byteDecryptedData = cipher.doFinal(byteEncryptedData);
	            decryptedData = new String(byteDecryptedData);
	            
		        
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        
	        return decryptedData;
	    }
	 
	 
}

class Input extends Thread {
	BufferedReader in;
	Socket socket;
	String data = null;
	
	static server s = new server();
	Input(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		while (true) {
			try {
				// �������� ������ �޾ƿ���
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				data = in.readLine();
				

				if (data == null || data.equals("exit")) {
					try {
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
						Calendar cal = Calendar.getInstance();
						String today = null;
						today = formatter.format(cal.getTime());
						Timestamp ts = Timestamp.valueOf(today);

						//System.out.println("����");
						System.out.println("Server : " + "exit" + " " + ts); // �޾ƿ� ������ ���
						String encryptedData = Encrypt("exit", s.getKey());
						System.out.println("��ȣ�� : " + encryptedData);

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				} else if (data != null){
					
					try {
					
					SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");
					Calendar cal = Calendar.getInstance();
					String today = null;
					today = formatter.format(cal.getTime());
					Timestamp ts = Timestamp.valueOf(today);
					
					System.out.println("Server : " + data + " " + ts); // �޾ƿ� ������ ���
					String encryptedData = Encrypt(data, s.getKey());
					System.out.println("��ȣ�� : " + encryptedData);
					
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} catch (IOException e) {
				break;
			} 
		}
		
		
		System.out.println("Ŭ���̾�Ʈ�� ���� ���� Input ������ ����");
		try {
			in.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	 public static String Encrypt(String text, String key) throws Exception{
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
			
			byte[] encrypted = c.doFinal(text.getBytes("UTF-8"));
			//String encrStr = new String(Base64.encodeBase64(encrypted));
			
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



