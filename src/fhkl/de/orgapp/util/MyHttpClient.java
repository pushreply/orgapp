package fhkl.de.orgapp.util;

import java.io.InputStream;
import java.security.KeyStore;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;

import fhkl.de.orgapp.R;

import android.content.Context;

/**
 * MyHttpClient - Manage the self-signed certificate (/res/raw/customtruststore146.bks)
 * to access HTTPS through SSL connection. 
 * It extends the DefaultHttpRequest.
 * 
 * @author ronaldo.hasiholan
 *
 */
public class MyHttpClient extends DefaultHttpClient {
	
	// Prepare a context of any given activity 
	final Context context;
	
	// Certificate store password 
	final private String KEYSTORE_PW = "AndroidSS2014";

	// Constructor with given context as parameter
	public MyHttpClient(Context context) {
		this.context = context;
	}

	// Define the connection manager for client
	@Override
	protected ClientConnectionManager createClientConnectionManager() {
		
		// Need a registry scheme to encapsulate the connection request to a specific protocol.
		SchemeRegistry registry = new SchemeRegistry();
		
		// set the plain HTTP scheme port 80 to registry scheme
		registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		
		// Register port 443 for the SSLSocketFactory with 
		// the keystore to the ConnectionManager
		registry.register(new Scheme("https", newSslSocketFactory(), 443));
		
		return new SingleClientConnManager(getParams(), registry);
	}

	// The SSL Socket, validates the identity of HTTPS Server against the provided certificate
	// and authenticates the HTTPS server using the private key (KEYSTORE_PW)
	private SSLSocketFactory newSslSocketFactory() {
		try {
			// Get an instance of the Bouncy Castle Keystore format
			KeyStore trusted = KeyStore.getInstance("BKS");

			// Get the keystore from resource raw folder. The keystore is the
			// trusted certificate.
			InputStream in = context.getResources().openRawResource(R.raw.customtruststore146);
			try {
				// Initialize keystore with the provided trusted cert and 
				// the keystore password
				trusted.load(in, KEYSTORE_PW.toCharArray());
			} finally {
				in.close();
			}

			// Pass the keystore to the SSLSocketFactory. 
			// The SSLSocketFactory is responsible for 
			// server certificate verification.
			SSLSocketFactory sf = new SSLSocketFactory(trusted);

			// Verify hostname of the cert.
			sf.setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
			
			return sf;
		} catch (Exception e) {
			throw new AssertionError(e);
		}
	}
}
