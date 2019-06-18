package syntaxAnalyzers.utils;

import java.util.*;

public class Pair <X, Y> {
    private X lft;
    private Y rgt;

    public Pair(X lft, Y rgt) {
        this.lft = lft;
        this.rgt = rgt;
    }

    public X getLeft() { return lft; }
    public Y getRight() { return rgt; }

    public void setLeft(X o) { lft = o; }
    public void setRight(Y o) { rgt = o; }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;

        if(getClass() != obj.getClass())
            return false;

        final Pair <X, Y> other = (Pair <X, Y>) obj;
        return lft.equals(other.getLeft()) && rgt.equals(other.getRight());
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 13 * hash + Objects.hashCode(this.lft);
        hash = 13 * hash + Objects.hashCode(this.rgt);
        return hash;
    }
}
