/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package structs;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author Hayder
 */
public class ProductionRule {

    protected NonTerminal left;
    protected List<Symbol> right;

    public ProductionRule(NonTerminal left, List<Symbol> right) {
        this.left = left;
        this.right = right;
    }

    public NonTerminal getLeft() {
        return left;
    }

    public void setLeft(NonTerminal left) {
        this.left = left;
    }

    public List<Symbol> getRight() {
        return right;
    }

    public void setRight(List<Symbol> right) { this.right = right; }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 13 * hash + Objects.hashCode(this.left);
        hash = 13 * hash + Objects.hashCode(this.right);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;

        if(!this.getClass().equals(obj.getClass()))
            return false;

        final ProductionRule other = (ProductionRule) obj;

        return left.equals(other.getLeft()) && right.equals(other.getRight());
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append(left);
        out.append(" -> ");
        for (Symbol s : right) {
            out.append(s);
        }
//        out.append("\n");
        return out.toString();
    }
    
    public boolean isEProduction(){
        /*if(right.size() == 1 && (right.get(0) instanceof Terminal)){
            Terminal tmp = (Terminal)right.get(0);
            if(tmp.id.equals(Terminal.E))
                return true;
        }
        return false;*/

        return right.get(0).equals(Terminal.EPS); //mejor
    }
}
