package encryption;

import java.util.Properties;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class EncryptionTest {

	private final static String passwordE = "dSVcqNTtcDz03Q9xWyQoCSbuP/8pmAOJd4nIC1FzNpE=";
	private final static String passwordD = "MySuperSecretPassword";
	
	@Before
	public void clearAllSystemProperties() {
		//Make sure to clear out any system properties that were set from the previous test
		Properties props = System.getProperties();
		
		props.remove("javax.net.ssl.keyStore");
		props.remove("javax.net.ssl.keyStorePassword");
		props.remove("javax.net.ssl.keyStorePassword.encrypted");
		props.remove("javax.net.ssl.trustStore");
		props.remove("javax.net.ssl.trustStorePassword");
		props.remove("javax.net.ssl.trustStorePassword.encrypted");
	}
	
	@Test
	public void decodingTest() {
		Assert.assertEquals(passwordD, Encryptor.decrypt(passwordE));
	}
	
	@Test
	public void encodingTest() {
		Assert.assertEquals(passwordE, Encryptor.encrypt(passwordD));
	}
	
	/*
	 * The next two test show how you can set up your system to read and decode 
	 * encrypted values, whether they're set on server start-up (setEnv file) 
	 * or somewhere else in your code. 
	 */
	
	@Test
	public void putItAllTogetherTest1() {
		//These you would set in your setEnv file on the server
		System.setProperty("javax.net.ssl.keyStore", "Some path");
		System.setProperty("javax.net.ssl.keyStorePassword.encrypted", passwordE);
		System.setProperty("javax.net.ssl.trustStore", "Some other path");
		System.setProperty("javax.net.ssl.trustStorePassword.encrypted", passwordE);
		
		Encryptor.decryptSystemProperties();
		
		doAssertions();
	}
	
	@Test
	public void putItAllTogetherTest2() {
		//These you would set in your setEnv file on the server
		System.setProperty("javax.net.ssl.keyStore", "Some path");
		System.setProperty("javax.net.ssl.keyStorePassword", passwordD);
		System.setProperty("javax.net.ssl.trustStore", "Some other path");
		System.setProperty("javax.net.ssl.trustStorePassword", passwordD);
		
		Encryptor.decryptSystemProperties();
		
		doAssertions();
	}
	
	/*
	 * Here we make sure that anything that may have been tagged as ".encrypted" at one point in time is 
	 * now decrypted and saved in memory with it's proper value and true key name.
	 */
	private void doAssertions() {
		Assert.assertEquals("Some path", System.getProperty("javax.net.ssl.keyStore"));
		Assert.assertEquals(passwordD, System.getProperty("javax.net.ssl.keyStorePassword"));
		Assert.assertEquals("Some other path", System.getProperty("javax.net.ssl.trustStore"));
		Assert.assertEquals(passwordD, System.getProperty("javax.net.ssl.trustStorePassword"));
		
		Assert.assertNull(System.getProperty("javax.net.ssl.keyStorePassword.encrypted"));
		Assert.assertNull(System.getProperty("javax.net.ssl.trustStorePassword.encrypted"));
	}
}
