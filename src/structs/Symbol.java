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
public abstract class Symbol{

    protected String id;

    public Symbol(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return id;
    }

    public String getId() {
        return id;
    }

    
    public void setId(String id) { this.id = id; }

    public boolean isTerminal() { return this instanceof Terminal; }
    public boolean isNonTerminal() { return this instanceof NonTerminal; }
}
