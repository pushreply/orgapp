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

public class MyHttpClient extends DefaultHttpClient {

	final Context context;
	final private String KEYSTORE_PW = "AndroidSS2014";

	public MyHttpClient(Context context) {
		this.context = context;
	}

	@Override
	protected ClientConnectionManager createClientConnectionManager() {
		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		
		// Register port 443 for the SSLSocketFactory with 
		// the keystore to the ConnectionManager
		registry.register(new Scheme("https", newSslSocketFactory(), 443));
		return new SingleClientConnManager(getParams(), registry);
	}

	private SSLSocketFactory newSslSocketFactory() {
		try {
			// Get an instance of the Bouncy Castle Keystore format
			KeyStore trusted = KeyStore.getInstance("BKS");

			// Get the keystore from resource raw folder. The keystore is the
			// trusted certificate.
			InputStream in = context.getResources().openRawResource(R.raw.customtruststore);
			try {
				// Initialize keystore with the provided trusted cert and 
				// the keystore pass
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
