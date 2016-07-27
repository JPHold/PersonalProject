package javasecure;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Enumeration;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by HJP on 2016/7/4 0004.
 */

public class SecureUtil {

    private static KeyStore ks;
    private static Cipher c1;

    private static String keyAlias;
    private static char[] nPassword;
    private static Cipher c2;

    private static void baseInit(Context context, String type, String certificateName, String passWord) {
        try {
            ks = KeyStore.getInstance(type);
            AssetManager assets = context.getResources().getAssets();
            int i = assets == null ? 2 : 1;
            InputStream fis = assets.open(certificateName);

            if ((passWord == null) || passWord.trim().equals("")) {
                nPassword = null;
            } else {
                nPassword = passWord.toCharArray();
            }
            ks.load(fis, nPassword);
            fis.close();
            Enumeration enuml = ks.aliases();

            if (enuml.hasMoreElements()) {
                keyAlias = (String) enuml.nextElement();
            }

        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }
    }

    private static void initC1(Context context, String type, String certificateName, String passWord) {
        //耗时
        baseInit(context, type, certificateName, passWord);

        try {
            //耗时
            c1 = Cipher.getInstance("RSA/ECB/PKCS1Padding"); // 定义算法：RSA
            Certificate cert = ks.getCertificate(keyAlias);

            PublicKey publicKey = cert.getPublicKey();
            c1.init(Cipher.ENCRYPT_MODE, publicKey);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }


    }


    private static void initC2(Context context, String type, String certificateName, String passWord) {
        //耗时
        baseInit(context, type, certificateName, passWord);

        try {
            PrivateKey privateKey = (PrivateKey) ks.getKey(keyAlias, nPassword);
            //耗时
            c2 = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            c2.init(Cipher.DECRYPT_MODE, privateKey);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

    }

    /**
     * 加密
     *
     * @param context
     * @param type
     * @param data
     * @param certificateName
     * @param passWord
     * @return
     */
    public static void encryption(final Context context, final String type, final String data,
                                  final String certificateName, final String passWord, final ResultListener resultListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String encryption = null;
                try {
                    initC1(context, type, certificateName, passWord);

                    byte[] msg = data.getBytes("ISO-8859-1"); // 待加解密的消息

                    byte[] encryptionData = c1.doFinal(msg); // 加密后的数据

                    //将加密数据转为字符串
                    encryption = new String(encryptionData, "ISO-8859-1");

                } catch (Exception e) {
                    e.printStackTrace();
                }
                encryption = encryption == null ? "" : encryption;
                resultListener.onResult(encryption);
            }
        }).start();
    }

    /**
     * 解密
     *
     * @param context
     * @param type
     * @param certificateName
     * @param passWord
     * @param data
     * @return
     */
    public static void dissection(final Context context, final String type, final String certificateName,
                                  final String passWord, final String data, final ResultListener resultListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                initC2(context, type, certificateName, passWord);

                byte[] dissectionData = new byte[0]; // 解密后的数据
                try {
                    byte[] changeData = null;

                    try {
                        changeData = data.getBytes("ISO-8859-1");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    dissectionData = c2.doFinal(changeData);
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                }
                // 打印解密字符串
                String dissection = null;
                try {
                    dissection = new String(dissectionData, "ISO-8859-1");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                dissection = dissection == null ? "" : dissection;
                resultListener.onResult(dissection);
            }
        }).start();

    }

    public interface ResultListener {
        public void onResult(String data);
    }
}
