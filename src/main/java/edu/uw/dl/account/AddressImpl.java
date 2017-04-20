package edu.uw.dl.account;

import edu.uw.ext.framework.account.Address;

/**
 *
 * @author dixya
 */
public class AddressImpl implements Address {

    private static final long serialVersionUID = 1L;
    String street;
    String city;
    String state;
    String zipCode;

    /**
     * No argument constructor.
     */
    AddressImpl() {

    }

    @Override
    public String getStreetAddress() {
        return street;
    }

    @Override
    public void setStreetAddress(String streetAddress) {
        this.street = streetAddress;
    }

    @Override
    public String getCity() {
        return city;
    }

    @Override
    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String getState() {
        return state;
    }

    @Override
    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String getZipCode() {
        return zipCode;
    }

    @Override
    public void setZipCode(String zip) {
        this.zipCode = zip;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s %s", street, city, state, zipCode);
    }

}
