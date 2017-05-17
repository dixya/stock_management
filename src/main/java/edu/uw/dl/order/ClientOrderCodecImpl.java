/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.dl.order;

import edu.uw.ext.framework.order.ClientOrder;
import edu.uw.ext.framework.order.ClientOrderCodec;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

/**
 *
 * @author dixya
 */
public class ClientOrderCodecImpl implements ClientOrderCodec{

    @Override
    public void encipher(List<ClientOrder> list, File file, String string, char[] chars, String string1, char[] chars1, String string2, char[] chars2, String string3) throws GeneralSecurityException, IOException {
    }

    @Override
    public List<ClientOrder> decipher(File file, String string, char[] chars, String string1, char[] chars1, String string2, char[] chars2, String string3) throws GeneralSecurityException, IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
