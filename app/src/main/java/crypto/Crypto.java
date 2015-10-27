package crypto;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.pascal.securechat.MainActivity;

import org.spongycastle.util.Arrays;
import org.spongycastle.util.encoders.Base64;
import org.spongycastle.util.io.pem.PemObject;
import org.spongycastle.util.io.pem.PemWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;



public class Crypto {

    public static SharedPreferences mPreferences;
    public static SharedPreferences.Editor editor;

    private static String encryptedKey;
    private static String decryptedKey;

    public static void init(Context context) {
        mPreferences = context.getSharedPreferences("myapplab.securechat", 0);
    }

    public static void writePublicKeyToPreferences(KeyPair key) {
        StringWriter publicStringWriter = new StringWriter();

        try {
            PemWriter pemWriter = new PemWriter(publicStringWriter);
            pemWriter.writeObject(new PemObject("PUBLIC KEY", key.getPublic().getEncoded()));
            pemWriter.flush();
            pemWriter.close();

                try {
                    encryptedKey = AESHelper.encrypt(MainActivity.seedValue , publicStringWriter.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            mPreferences.edit().putString("RSA_PUBLIC_KEY", encryptedKey).commit();

        } catch (IOException e) {
            Log.e("RSA", e.getMessage());
            e.printStackTrace();
        }
    }

    public static void writePrivateKeyToPreferences(KeyPair keyPair) {
        StringWriter privateStringWriter = new StringWriter();
        try {
            PemWriter pemWriter = new PemWriter(privateStringWriter);
            pemWriter.writeObject(new PemObject("PRIVATE KEY", keyPair.getPrivate().getEncoded()));
            pemWriter.flush();
            pemWriter.close();

                try {
                    decryptedKey = AESHelper.encrypt(MainActivity.seedValue , privateStringWriter.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            mPreferences.edit().putString("RSA_PRIVATE_KEY", decryptedKey).commit();

        } catch (IOException e) {
            Log.e("RSA", e.getMessage());
            e.printStackTrace();
        }
    }

    public static PublicKey getRSAPublicKeyFromString(String publicKeyPEM) throws Exception {
        publicKeyPEM = stripPublicKeyHeaders(publicKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA", "SC");
        byte[] publicKeyBytes = Base64.decode(publicKeyPEM.getBytes("UTF-8"));
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKeyBytes);
        return keyFactory.generatePublic(x509KeySpec);
    }

    public static PrivateKey getRSAPrivateKeyFromString(String privateKeyPEM) throws Exception {
        privateKeyPEM = stripPrivateKeyHeaders(privateKeyPEM);
        KeyFactory fact = KeyFactory.getInstance("RSA", "SC");
        byte[] clear = Base64.decode(privateKeyPEM);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(clear);
        PrivateKey priv = fact.generatePrivate(keySpec);
        Arrays.fill(clear, (byte) 0);
        return priv;
    }

    public static String stripPublicKeyHeaders(String key) {
        //strip the headers from the key string
        StringBuilder strippedKey = new StringBuilder();
        String lines[] = key.split("\n");
        for (String line : lines) {
            if (!line.contains("BEGIN PUBLIC KEY") && !line.contains("END PUBLIC KEY") && !Strings.isNullOrEmpty(line.trim())) {
                strippedKey.append(line.trim());
            }
        }
        return strippedKey.toString().trim();
    }

    public static String stripPrivateKeyHeaders(String key) {
        StringBuilder strippedKey = new StringBuilder();
        String lines[] = key.split("\n");
        for (String line : lines) {
            if (!line.contains("BEGIN PRIVATE KEY") && !line.contains("END PRIVATE KEY") && !Strings.isNullOrEmpty(line.trim())) {
                strippedKey.append(line.trim());
            }
        }
        return strippedKey.toString().trim();
    }

}
