package com.logicaldoc.core.communication;

/**
 * @author Michael Scholz
 */
public class Recipient {

    private String name = "";

    private String address = "";

    public Recipient() {
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public void setName(String nme) {
        name = nme;
    }

    public void setAddress(String addr) {
        address = addr;
    }
    
    @Override
    public boolean equals(Object arg0) {
        if (!(arg0 instanceof Recipient))
            return false;
        Recipient other = (Recipient) arg0;
        return other.getAddress().equals(address);
    }

    @Override
    public int hashCode() {
        return address.hashCode();
    }    

}
