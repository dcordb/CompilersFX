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
public class NonTerminal extends Symbol implements Comparable<NonTerminal>{

    public static final NonTerminal START = new NonTerminal("START");

    public NonTerminal(String id) {
        super(id.toUpperCase());
    }
    
    @Override
    public int compareTo(NonTerminal other){
        return id.compareTo(other.id);
    }

    @Override
    public boolean equals(Object o) {
        if(o == null)
            return false;
        if(!this.getClass().equals(o.getClass()))
            return false;
        if( !(o instanceof NonTerminal))
            return false;
        NonTerminal tmp = (NonTerminal)o;
        return id.equals(tmp.id);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        return hash;
    }
    
    
}
