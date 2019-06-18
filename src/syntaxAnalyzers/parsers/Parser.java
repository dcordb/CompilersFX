package syntaxAnalyzers.parsers;
import structs.*;

import java.util.LinkedList;
import java.util.List;

public abstract class Parser {
    protected GLC G;
    public Parser(GLC G) {
        this.setG(G); //augmented grammar
    }

    public GLC getG() {
        return G;
    }

    public void setG(GLC g) {
        G = new GLC(g.getNterminals(), g.getTerminals(), g.getPrules(), g.getStart());

        List<Symbol> right = new LinkedList<>();
        right.add(G.getStart());

        ProductionRule prod = new ProductionRule(NonTerminal.START, right);
        G.getPrules().add(prod);
        G.getNterminals().add(NonTerminal.START);
        G.setStart(NonTerminal.START);
    }
}
