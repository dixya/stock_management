/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.ext.framework.account;

import java.io.Serializable;

/**
 * Interface for a pure JavaBean implementation of an address. The implementing class must provide a no argument constructor.
 * @author dixya
 */
public interface Address extends Serializable{
    /**
     * Gets the street address.

     * @return the street address.
     */
    String getStreetAddress();
    /**
     * Sets the street address.

     * @param streetAddress the street address.
     */
    void setStreetAddress(String streetAddress);
    /**
     * Gets the city.
     * @return the city
     */
    String getCity();
    /**
     * Sets the city.

     * @param city the city
     */
    void setCity(String city);
    /**
     * Gets the state.

     * @return the state.
     */
    String getState();
    /**
     * Sets the state.
     * @param state the state.
     */
    void setState(String state);
    /**
     * Gets the ZIP code.
     * @return the ZIP code.
     */
    String getZipCode();
    /**
     * Sets the ZIP code.
     * @param zip the ZIP code.
     */
    void setZipCode(String zip);










    
}
