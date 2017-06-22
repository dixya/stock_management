/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.dl.order;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uw.ext.framework.order.ClientOrder;
import edu.uw.ext.framework.order.ClientOrderCodec;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Implements Interface for writing an encrypted order list (ciphertext) and a signature of the plaintext data to file.
 * Additionally, the shared key used to encrypt the order list will  itself be encrypted for the recipient and  included in file as well. 
 * The file shall have the structure below, the file structure is agnostic as to the plaintext form of the order data. 
 * @author dixya
 */
public class ClientOrderCodecImpl implements ClientOrderCodec{
    /** Keystore type */
    private static final String JCEKS="JCEKS";
    /** AES cipher algorithm */
    private static final String AES_ALGORITHM="AES";
    /** Key size in bits */
    private static final int AES_KEY_SIZE=128;
    /** Key size/length in bytes */
    private static final int AES_KEY_LEN=16;
    /** Root path string */
    private static final String ROOT_PATH= "/";
    /** Signing algorithm */
    private static final String SIGN_ALGORITHM="MD5withRSA";
    /** The shared encryption key, enciphered with the recipients private key */
    public byte[] encipheredSharedKey;
    /** The enciphered order data */
    public byte[] ciphertext;
    /** Signature resulting from the signing of the plaintext order data with the senders private key.*/
    public byte[] signature;
    /**
     * Constructor
     */
    public ClientOrderCodecImpl(){
        
    }

    /**
     * Writes the client order file. Key stores will be accessed as resources, i.e. on the classpath.
     * @param order the orders to be submitted by the client
     * @param orderFile the file the encrypted order list is to be stored in
     * 
     * @param senderKeyStoreName
     * @param senderKeyStorePasswd
     * @param senderKeyName
     * @param senderKeyPasswd
     * @param senderTrustStoreName
     * @param senderTrustStorePasswd
     * @param recipientCertName
     * @throws GeneralSecurityException if any cryptographic operations fail
     * @throws IOException if unable to write either of the files
     */
    @Override
    public void encipher(List<ClientOrder> order, File orderFile, String senderKeyStoreName, char[] senderKeyStorePasswd, String senderKeyName, char[] senderKeyPasswd, String senderTrustStoreName, char[] senderTrustStorePasswd, String recipientCertName) throws GeneralSecurityException, IOException {
        ObjectMapper mapper=new ObjectMapper();
        byte[] data=mapper.writeValueAsBytes(order);
        SecretKey sharedSecretKey=generateAesSecretKey();
        byte[] sharedSecretKeyBytes=sharedSecretKey.getEncoded();
        
        KeyStore senderTrustStore=loadKeyStore(senderTrustStoreName,senderTrustStorePasswd);
        PublicKey key=senderTrustStore.getCertificate(recipientCertName).getPublicKey();
        encipheredSharedKey=encipher(key,sharedSecretKeyBytes);
        ciphertext=encipher(sharedSecretKey,data);
        signature=sign(data, senderKeyStoreName,senderKeyStorePasswd,senderKeyName, senderKeyPasswd);
        writeFile(orderFile,encipheredSharedKey,ciphertext,signature);
        
    }

    
    /**
     * Read an encrypted order list and signature from file and verify the order list data. 
     * Keystores will be accessed as resources, i.e. on the classpath.
     * @param orderFile the file the encrypted order list is stored in
     * @param recipientKeyStoreName the name of the recipient's key store resource
     * @param recipientKeyStorePasswd the recipient's key store password
     * @param recipientKeyName the alias of the recipient's private key
     * @param recipientKeyPasswd the password for the recipient's private key
     * @param trustStoreName     the name of the trust store resource

     * @param trustStorePasswd the trust store password
     * @param signerCertName the name of the signer's certificate
     * @return the client order list from the file
     * @throws GeneralSecurityException if any cryptographic operations fail
     * @throws IOException if unable to write either of the files.
     */
    @Override
    public List<ClientOrder> decipher(File orderFile,
                           String recipientKeyStoreName,
                           char[] recipientKeyStorePasswd,
                           String recipientKeyName,
                           char[] recipientKeyPasswd,
                           String trustStoreName,
                           char[] trustStorePasswd,
                           String signerCertName)
                    throws GeneralSecurityException,
                           IOException { 
        /* read file from orderFile*/
        try(FileInputStream inStream=new FileInputStream(orderFile);
          DataInputStream dataIn=new DataInputStream(inStream)){
            encipheredSharedKey=readByteArray(dataIn);
            ciphertext=readByteArray(dataIn);
            signature=readByteArray(dataIn);
        } catch(IOException e){
            throw new IOException("Error reading data file");
        }
            KeyStore keyStore=loadKeyStore(recipientKeyStoreName,recipientKeyStorePasswd);
        Key skey=keyStore.getKey(recipientKeyName, recipientKeyPasswd);
        byte[] encipheredSharedKeyBytes=decipher(skey, encipheredSharedKey);
        SecretKey sharedSecretKey=keyBytesToAesSecretKey(encipheredSharedKeyBytes);
        byte[] orderData=decipher(sharedSecretKey, ciphertext);
        boolean verified=verifySignature(orderData,signature, trustStoreName, trustStorePasswd,signerCertName);
        List<ClientOrder> orders=null;
        
        if(verified){
            ObjectMapper mapper=new ObjectMapper();
            JavaType type=mapper.getTypeFactory().constructCollectionType(List.class,ClientOrder.class);
            try{
                orders=mapper.readValue(orderData, type);
            } catch(IOException e){
                throw new IOException("Error parsing order data",e);
            }
        } else{
            throw new GeneralSecurityException("Signature verification failed");
        }
        return orders;
    }
    /**
     * 
     * @param storeName
     * @param storePasswd
     * @return
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws IOException 
     */
    
    private static KeyStore loadKeyStore(final String storeName, final char[] storePasswd) throws KeyStoreException,
      NoSuchAlgorithmException, CertificateException, IOException{
        try(InputStream stream=ClientOrderCodec.class.getResourceAsStream(ROOT_PATH+storeName)){
            if(stream ==null){
                throw new KeyStoreException("Unable to locate keystore resource" +storeName);
            }
            KeyStore keyStore=KeyStore.getInstance(JCEKS);
            keyStore.load(stream,storePasswd);
            return keyStore;
        }
        
    }

    /**
     * Generate an AES secret key
     * @return the newly generated key
     */
    private SecretKey generateAesSecretKey() throws NoSuchAlgorithmException {
        KeyGenerator generator=KeyGenerator.getInstance(AES_ALGORITHM);
        generator.init(AES_KEY_SIZE);
        SecretKey key=generator.generateKey();
        return key;
    }
    /**
     * 
     * @param orderFile
     * @param encipheredSharedKey
     * @param ciphertext
     * @param signature 
     */
    private void writeFile(File orderFile, byte[] encipheredSharedKey, byte[] ciphertext, byte[] signature) throws IOException {
        try(FileOutputStream fout= new FileOutputStream(orderFile);
          DataOutputStream dataOut=new DataOutputStream(fout)){
           writeByteArray(dataOut,encipheredSharedKey);
           writeByteArray(dataOut,ciphertext);
           writeByteArray(dataOut,signature);
           dataOut.flush();
        } catch(IOException e){
            throw new IOException("Error attempting write orders file",e);
        }
    
          
    }
    
    /**
     * Encipher the provided data with the provided key.
     * @param cipherKey key to be used to encipher data
     * @param plaintext the data to encipher
     * @return the enciphered data
     * @throws GeneralSecurityException
     * @throws IOException 
     */
    private static byte[] encipher(final Key cipherKey, final byte[] plaintext) throws GeneralSecurityException,IOException{
        Cipher cipher=Cipher.getInstance(cipherKey.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, cipherKey);
            byte[] ciphertext=cipher.doFinal(plaintext);
            return ciphertext;
    }
    
    private static byte[] sign(final byte[] data, final String signerKeyStoreName, final char[] signerStorePasswd,final String signerName, final char[] signerPasswd) throws GeneralSecurityException, IOException{
        byte[] signature;
        try{
            KeyStore clientKeyStore=loadKeyStore(signerKeyStoreName, signerStorePasswd);
            PrivateKey privateKey=(PrivateKey) clientKeyStore.getKey(signerName, signerPasswd);
            if(privateKey==null){
                throw new GeneralSecurityException("No key exists named" + signerName);
            }
            Signature signer=Signature.getInstance(SIGN_ALGORITHM);
            signer.initSign(privateKey);
            signer.update(data);
            signature=signer.sign();
            return signature;
        } catch(KeyStoreException | UnrecoverableKeyException | InvalidKeyException | CertificateException | SignatureException e){
            throw new GeneralSecurityException("Error signing order data" ,e);
        }
    }
    private static void writeByteArray(final DataOutputStream out, final byte[] b) throws IOException{
        final int len=(b==null)? -1:b.length;
        out.writeInt(len);
        if(len>0){
            out.write(b);
        }
    }
    
   
    
    private static byte[] readByteArray(final DataInputStream in) throws IOException{
        byte[] bytes=null;
        final int len=in.readInt();
        if(len>=0){
            bytes=new byte[len];
            in.readFully(bytes);
        }
        return bytes;
    }

    private byte[] decipher(final Key skey, final byte[] ciphertext) throws GeneralSecurityException, IOException{
        try{
            Cipher cipher=Cipher.getInstance(skey.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, skey);
            byte[] plaintext=cipher.doFinal(ciphertext);
            return plaintext;
        } catch(InvalidKeyException | NoSuchAlgorithmException |NoSuchPaddingException e){
            throw new GeneralSecurityException("Error encrypting data", e);
        }
    }
    /**
     * Reconstitutes an AES secret key from the keys bytes
     * @param keyBytes the keys bytes in encoded form
     * @return 
     */

    private SecretKey keyBytesToAesSecretKey(byte[] keyBytes) throws NoSuchAlgorithmException{
        KeyGenerator generator=KeyGenerator.getInstance(AES_ALGORITHM);
        generator.init(AES_KEY_SIZE);
        SecretKey secKey=new SecretKeySpec(keyBytes,0,AES_KEY_LEN,AES_ALGORITHM);
        return secKey;
    }

    /**
     * 
     * @param orderData
     * @param signature
     * @param trustStoreName
     * @param trustStorePasswd
     * @param signerCertName
     * @return
     * @throws GeneralSecurityException
     * @throws IOException 
     */
    private boolean verifySignature(byte[] orderData, byte[] signature, String trustStoreName, char[] trustStorePasswd, String signerCertName) throws GeneralSecurityException, IOException{
        try{
            KeyStore clientTrustStore=loadKeyStore(trustStoreName,trustStorePasswd);
            Signature verifier=Signature.getInstance(SIGN_ALGORITHM);
            Certificate cert=clientTrustStore.getCertificate(signerCertName);
            PublicKey publicKey=cert.getPublicKey();
            verifier.initVerify(publicKey);
            verifier.update(orderData);
            return verifier.verify(signature);
        } catch(KeyStoreException | CertificateException e){
            throw new GeneralSecurityException("Unable to retrieve signing key",e);
        } catch(NoSuchAlgorithmException | InvalidKeyException | SignatureException e){
            throw new GeneralSecurityException("Invalid signing key",e);
        }
    }
    
    
    
}
