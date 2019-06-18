package syntaxAnalyzers.items;

import structs.*;

import java.util.*;

public class ItemLR0 {
    private ProductionRule pr;
    private int pos;

    public ItemLR0(ProductionRule pr, int pos) {
        this.setPr(pr);
        this.setPos(pos);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;

        if(!getClass().equals(obj.getClass()))
            return false;

        final ItemLR0 other = (ItemLR0) obj;
        return pos == other.getPos() && pr.equals(other.getPr());
    }

    public int hashCode() {
        int hash = 5;
        hash = 13 * hash + Objects.hashCode(this.pr);
        hash = 13 * hash + Objects.hashCode(this.pos);
        return hash;
    }

    public ProductionRule getPr() {
        return pr;
    }

    public void setPr(ProductionRule pr) {
        this.pr = pr;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        if(pos >= 0 && pos <= pr.getRight().size())
            this.pos = pos;

        else {
            throw new RuntimeException("Pos should be >= 0 and <= len, pos = " + pos + " len = " + pr.getRight().size());
        }
    }

    @Override
    public String toString() {
        NonTerminal left = pr.getLeft();
        List <Symbol> right = pr.getRight();

        StringBuilder s = new StringBuilder(left.toString() + " -> ");

        for(int i = 0; i < right.size(); i++) {
            if(i == pos)
                s.append(".");

            s.append(right.get(i).toString());
        }

        if(right.size() == pos)
            s.append(".");

        return s.toString();
    }
}
