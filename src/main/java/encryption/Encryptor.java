package encryption;

import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class Encryptor {
	
    private static final String key = "A3sNcrypt1T#gr!7"; // 128 bit key
    private static final String initVector = "RandomInitVector"; // 16 bytes IV
    
	public static void decryptSystemProperties() {
		Properties props = System.getProperties();
		
		for (String key : props.stringPropertyNames()) {
			//If the key ends in ".encrypted" we know it's a value we need to decrypt and read back into memory 
			//and remove the old value since it should no longer be needed.
			if (key.endsWith(".encrypted")) {
				String value = System.getProperty(key);
				String decryptKey = key.replace(".encrypted", "");
				System.out.println("We will now decrypt '"+key+"' and read it into system properties as '"+decryptKey+"'.  '" + key + "' will then be removed from the system properties.");
				System.setProperty(decryptKey, decrypt(value));
				System.clearProperty(key);
			}
		}
	}
	
    public static String encrypt(String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());

            return Base64.encodeBase64String(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static String decrypt(String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
