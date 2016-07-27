package keychain;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Base64;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.Enumeration;

import javax.crypto.Cipher;

import javasecure.SecureUtil;

/**
 * Created by HJP on 2016/7/4 0004.
 */

public class TestActivity extends Activity {

    public final String KEYSTORE_FILE = "keychain.p12";
    public final String KEYSTORE_PASSWORD = "changeit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context mContext = getApplicationContext();
    }
}
