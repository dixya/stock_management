/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.dl.dao;

import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author dixya
 */
public class AccountSer {

    private static final String NULL_STR = "<null>";

    public AccountSer() {

    }

    /**
     *
     * @param dos
     * @param fullName
     * @throws IOException
     */
    private static void writeString(DataOutputStream dos, String fullName) throws IOException {
        dos.writeUTF(fullName == null ? NULL_STR : fullName);
    }

    /**
     * Writes the byte array.
     *
     * @param dos
     * @param passwordHash
     * @throws IOException
     */
    private static void writeByteArray(DataOutputStream dos, byte[] passwordHash) throws IOException {
        final int len = (passwordHash == null) ? -1 : passwordHash.length;
        dos.writeInt(len);
        if (len > 0) {
            dos.write(passwordHash);
        }
    }

    /**
     * Reads the string.
     *
     * @param dos
     * @return
     * @throws IOException
     */
    private static String readString(DataInputStream dos) throws IOException {
        String s = dos.readUTF();
        //dos.writeUTF(fullName==null ? NULL_STR:fullName);
        return NULL_STR.equals(s) ? null : s;
    }

    /**
     * Reads byte array
     *
     * @param in
     * @return
     * @throws IOException
     */
    private static byte[] readByteArray(final DataInputStream in) throws IOException {
        byte[] bytes = null;
        final int len = in.readInt();
        if (len >= 0) {
            bytes = new byte[len];
            in.readFully(bytes);
        }
        return bytes;
    }

    /**
     *
     * @param in
     * @return
     */
    static Account read(FileInputStream in) throws IOException, AccountException {
        DataInputStream din = new DataInputStream(in);
        try (ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("context.xml")) {
            final Account acct = appContext.getBean(Account.class);
            try {
                acct.setName(din.readUTF());
            } catch (AccountException ex) {
                throw new AccountException("Error while reading UTF value");
            }
            acct.setPasswordHash(readByteArray(din));
            acct.setBalance(din.readInt());
            acct.setFullName(readString(din));
            acct.setPhone(readString(din));
            acct.setEmail(readString(din));
            return acct;
        } catch (final BeansException ex) {
            throw new AccountException("Unable to create account instance" + ex);
        }

    }

    /**
     * Writes an account object to an output stream.
     *
     * @param out the output stream to write to
     * @param account the account object to write.
     */
    static void write(FileOutputStream out, Account account) throws AccountException {
        try {
            final DataOutputStream dos = new DataOutputStream(out);
            dos.writeUTF(account.getName());

            writeByteArray(dos, account.getPasswordHash());
            dos.writeInt(account.getBalance());
            writeString(dos, account.getFullName());
            writeString(dos, account.getPhone());
            writeString(dos, account.getEmail());
            dos.flush();

        } catch (final IOException ex) {
            throw new AccountException("Failed to write account data" + ex);
        }
    }

}
