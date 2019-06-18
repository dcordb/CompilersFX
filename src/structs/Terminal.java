/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package structs;

/**
 *
 * @author Hayder
 */
public class Terminal extends Symbol implements Comparable<Terminal> {
    
    /*Represents empty string*/
    public static final String E = "~";
    public static final Terminal EPS = new Terminal("~");
    public static final Terminal DOLLAR = new Terminal("$");

    public Terminal(String id) {
        super(id.toLowerCase());
    }
    
    @Override
    public int compareTo(Terminal other){
        return id.compareTo(other.id);
    }
    
    @Override
    public boolean equals(Object o){
        if(o == null)
            return false;
        if(!this.getClass().equals(o.getClass()))
            return false;
        if( !(o instanceof Terminal))
            return false;
        Terminal tmp = (Terminal)o;
        return id.equals(tmp.id);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }

}
