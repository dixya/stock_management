/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.dl.dao;

import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.Address;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;
/**
 *
 * @author dixya
 */
public class AddressSer {
    private static final String STREET_ADDRESS_PROP_NAME="streetAddress";
    private static final String STREET_CITY_PROP_NAME="city";
    private static final String STREET_STATE_PROP_NAME="state";
    private static final String STREET_ZIP_CODE_PROP_NAME="zipCode";

    static Address read(FileInputStream in) throws AccountException, IOException {
        final Properties props=new Properties();
        try(ClassPathXmlApplicationContext appContext=new ClassPathXmlApplicationContext("context.xml")){
            props.load(in);
            final Address addr=appContext.getBean(Address.class);
            addr.setStreetAddress(props.getProperty(STREET_ADDRESS_PROP_NAME));
            addr.setCity(props.getProperty(STREET_CITY_PROP_NAME));
            addr.setState(props.getProperty(STREET_STATE_PROP_NAME));
            addr.setZipCode(props.getProperty(STREET_ZIP_CODE_PROP_NAME));
            return addr;
        } catch(final BeansException ex){
            throw new AccountException("Unable to create address instance"+ex);
        }
    }

    static void write(FileOutputStream out, Address address) throws AccountException {
        final Properties props=new Properties();
        if(address!=null){
            String tmp;
            tmp=address.getStreetAddress();
            if(tmp!=null){
                props.put(STREET_ADDRESS_PROP_NAME,tmp);
            }
            tmp=address.getCity();
            if(tmp!=null){
                props.put(STREET_CITY_PROP_NAME, tmp);
            }
            tmp=address.getState();
            if(tmp!=null){
                props.put(STREET_STATE_PROP_NAME, tmp);
            }
            tmp=address.getZipCode();
            if(tmp!=null){
                props.put(STREET_ZIP_CODE_PROP_NAME, tmp);
            }
        }
        try{
            props.store(out, "Address data");
        } catch(final IOException ex){
            throw new AccountException("Failed to write address data"+ex);
        }
    }
    
}
