/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algos;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import structs.BGenerator;
import structs.GLC;
import structs.NonTerminal;
import structs.ProductionRule;
import structs.Symbol;
import structs.Terminal;

/**
 *
 * @author Hayder
 */
public final class Algorithms {

    /**
     * Removes simple productions from Grammar
     *
     * @param Grammar The target Grammar
     * @return A GLC simple-production-free
     */
    public static GLC delete_simple_productions(GLC Grammar) {

        GLC G = removeEProductions(Grammar);
        ArrayList<TreeSet<NonTerminal>> ni = new ArrayList<>();

        for (int i = 0; i < G.getNterminals().size(); i++) {

            TreeSet<NonTerminal> before;
            TreeSet<NonTerminal> after = new TreeSet<>();

            after.add(G.getNterminals().get(i));
            do {
                before = (TreeSet<NonTerminal>) after.clone();
                after = new TreeSet<>();
                for (NonTerminal nn : before) {
                    for (ProductionRule pr : G.getPrules()) {
                        if (pr.getLeft().equals(nn) && pr.getRight().size() == 1
                                && (pr.getRight().get(0) instanceof NonTerminal)) {
                            after.add((NonTerminal) pr.getRight().get(0));
                        }
                    }
                }

                after.addAll(before);

            } while (!before.equals(after));
            ni.add(after);
        }

        ArrayList<ProductionRule> pprime = new ArrayList<>();
        for (int i = 0; i < G.getNterminals().size(); i++) {
            TreeSet<NonTerminal> current = ni.get(i);
            for (NonTerminal b : current) {
                for (ProductionRule pr : G.getPrules()) {
                    if (b.equals(pr.getLeft()) && ((pr.getRight().size() > 1)
                            || (pr.getRight().get(0) instanceof Terminal))) {
                        pprime.add(new ProductionRule(G.getNterminals().get(i),
                                pr.getRight()));
                    }
                }
            }
        }

        GLC newone = new GLC(G.getNterminals(), G.getTerminals(), pprime, G.getStart());
        return newone;
    }

    /**
     * Removes e-production from Grammar
     *
     * @param Grammar The target Grammar
     * @return A grammar e-production-free
     */
    public static GLC removeEProductions(GLC Grammar) {

        if (!eProductions(Grammar)) {
            return Grammar;
        }

        final Terminal E = new Terminal(Terminal.E);
        TreeSet<NonTerminal> before;
        TreeSet<NonTerminal> after = new TreeSet<>();

        for (ProductionRule pr : Grammar.getPrules()) {
            if (pr.getRight().size() == 1) {
                if (pr.getRight().get(0) instanceof Terminal) {
                    Terminal tmp = (Terminal) pr.getRight().get(0);
                    if (tmp.equals(E)) {
                        after.add(pr.getLeft());
                    }
                }
            }
        }


        do {
            before = (TreeSet<NonTerminal>) after.clone();
            after = new TreeSet<>();
            for (ProductionRule pr : Grammar.getPrules()) {
                boolean belongs = true;
                for (Symbol s : pr.getRight()) {
                    if (s instanceof NonTerminal) {
                        NonTerminal tmp = (NonTerminal) s;
                        if (!before.contains(tmp)) {
                            belongs = false;
                            break;
                        }
                    }

                    else {
                        belongs = false;
                        break;
                    }
                }

                if (belongs) {
                    after.add(pr.getLeft());
                }
            }
            after.addAll(before);
        } while (!before.equals(after));

        TreeSet<NonTerminal> NE = after;
        ArrayList<ProductionRule> pprime = new ArrayList<>();

        for (ProductionRule pr : Grammar.getPrules()) {
            if (pr.isEProduction()) {
                continue;
            }
            //Betas
            List<Integer> ids = new LinkedList<>();
            for (int i = 0; i < pr.getRight().size(); i++) {
                if (pr.getRight().get(i) instanceof NonTerminal) {
                    NonTerminal tmp = (NonTerminal) pr.getRight().get(i);
                    if (NE.contains(tmp)) {
                        ids.add(i);
                    }
                }
            }
            if (ids.isEmpty() && !pr.isEProduction()) {
                pprime.add(pr);
            } else {

                BGenerator bg = new BGenerator(ids);
                int max = pow(2, ids.size());
                NonTerminal l = new NonTerminal(pr.getLeft().getId());
                for (int i = 0; i < max; i++) {
                    List<Symbol> r = new LinkedList<>();

                    for (int j = 0; j < pr.getRight().size(); j++) {
                        if (!bg.contains(j) || (bg.contains(j) && bg.isOn(j))) {
                            r.add(pr.getRight().get(j));
                        }
                    }
                    bg.bump();
                    if(!r.isEmpty()){
                        ProductionRule tmp = new ProductionRule(l, r);
                        pprime.add(tmp);
                    }
                }
            }
        }

        NonTerminal start = Grammar.getStart();
        List<NonTerminal> newNt = new LinkedList<>();

        for (NonTerminal nonTerminal : Grammar.getNterminals()) {
            newNt.add(nonTerminal);
        }

        if (NE.contains(Grammar.getStart())) {
            NonTerminal l = new NonTerminal(start.getId() + "'");
            List<Symbol> s1 = new LinkedList<>();
            List<Symbol> s2 = new LinkedList<>();
            s1.add(start);
            s2.add(E);

            ProductionRule pr1 = new ProductionRule(l, s1);
            ProductionRule pr2 = new ProductionRule(l, s2);
            pprime.add(pr1);
            pprime.add(pr2);
            start = l;
            newNt.add(l);
        }

        return new GLC(newNt, Grammar.getTerminals(), pprime, start);
    }

    /**
     * Tests whether Grammar is e-production-free
     *
     * @param Grammar The target Grammar
     * @return True if and only if Grammar is e-production-free, False otherwise
     */
    public static boolean eProductions(GLC Grammar) {

        boolean foundE = false;
        boolean starterE = false;
        boolean eContained = false;
        final Terminal E = new Terminal(Terminal.E);

        for (ProductionRule pr : Grammar.getPrules()) {
            for (Symbol s : pr.getRight()) {
                if (s instanceof Terminal) {
                    Terminal tmp = (Terminal) s;
                    if (tmp.equals(E)) {
                        foundE = true;
                    }
                    if (pr.getLeft().equals(Grammar.getStart())) {
                        starterE = true;
                    }
                } else {
                    NonTerminal tmp = (NonTerminal) s;
                    if (tmp.equals(Grammar.getStart())) {
                        eContained = true;
                    }
                }
            }
        }
        if (!foundE || (starterE && !eContained)) {
            return false;
        }
        return true;

    }

    public static GLC removeUselessSymbols(GLC Grammar) {
        GLC G = algorithmA(Grammar);
        G = algorithmB(G);
        return G;
    }

    /**
     * Removes useless non-terminals from Grammar
     *
     * @param Grammar The targer grammar
     * @return A grammar useless-non-terminals-free or null if Grammar.start is
     * useless
     */
    public static GLC algorithmA(GLC Grammar) {
        Set<NonTerminal> usef = new HashSet<>();

        int szBefore = Integer.MIN_VALUE;
        int szAfter = usef.size();

        while (szBefore != szAfter) {
            szBefore = szAfter;
            Set<NonTerminal> current = new HashSet<>();
            //Wanna know if pr.getLeft() is useful
            for (ProductionRule pr : Grammar.getPrules()) {
                boolean accepted = true;
                for (Symbol symbol : pr.getRight()) {
                    accepted &= Grammar.getTerminals().contains(symbol)
                            || usef.contains(symbol);

                }
                if (accepted) {
                    current.add(pr.getLeft());
                }
            }
            usef.addAll(current);
            szAfter = usef.size();
        }
        if (!usef.contains(Grammar.getStart())) {
            return null;
        }

        List<NonTerminal> newNonTerminals = new LinkedList<>(usef);
        List<ProductionRule> newPr = new LinkedList<>();

        for (ProductionRule pr : Grammar.getPrules()) {
            boolean accepted = newNonTerminals.contains(pr.getLeft());
            for (int i = 0; i < pr.getRight().size() && accepted; i++) {
                if (pr.getRight().get(i) instanceof Terminal) {
                    continue;
                }
                accepted &= newNonTerminals.contains(pr.getRight().get(i));
            }
            if (accepted) {
                newPr.add(pr);
            }
        }
        return new GLC(newNonTerminals, Grammar.getTerminals(), newPr, Grammar.getStart());
    }

    /**
     * Removes unreachable symbols from Grammar
     *
     * @param Grammar The target Grammar
     * @return A grammar unreachable-symbols free
     */
    public static GLC algorithmB(GLC Grammar) {

        Set<Symbol> reachable = new HashSet<>();
        reachable.add(Grammar.getStart());
        int szBefore = -1;
        int szAfter = reachable.size();

        while (szBefore != szAfter) {
            szBefore = szAfter;
            Set<Symbol> current = new HashSet<>();
            for (ProductionRule pr : Grammar.getPrules()) {
                if (reachable.contains(pr.getLeft())) {
                    for (Symbol symbol : pr.getRight()) {
                        current.add(symbol);
                    }
                }
            }
            current.addAll(reachable);
            reachable = current;
            szAfter = reachable.size();
        }

        List<Terminal> newTerminals = new LinkedList<>();
        List<NonTerminal> newNonTerminals = new LinkedList<>();
        List<ProductionRule> newPr = new LinkedList<>();

        for (Symbol symbol : reachable) {
            if (symbol instanceof Terminal) {
                Terminal tmp = (Terminal) symbol;
                newTerminals.add(tmp);
            } else {
                NonTerminal tmp = (NonTerminal) symbol;
                newNonTerminals.add(tmp);
            }
        }

        for (ProductionRule pr : Grammar.getPrules()) {
            boolean contained = reachable.contains(pr.getLeft());
            for (int i = 0; i < pr.getRight().size() && contained; i++) {
                contained = reachable.contains(pr.getRight().get(i));
            }
            if (contained) {
                newPr.add(pr);
            }
        }

        return new GLC(newNonTerminals, newTerminals, newPr, Grammar.getStart());
    }

    /**
     * Removes General Left Recursion
     *
     * @param Grammar The target grammar
     * @return A grammar left-recursion-free
     */
    public static GLC removeGeneralLRecursion(GLC Grammar) {
        /*Making it proper*/
        GLC G = removeEProductions(Grammar);
        G = removeUselessSymbols(G);
        G = delete_simple_productions(G);

        List<NonTerminal> newN = new ArrayList<>(G.getNterminals());
        List<ProductionRule> newP = new ArrayList<>();

        for (int i = 0; i < G.getNterminals().size(); i++) {
            List<ProductionRule> currentA = new ArrayList<>();
            NonTerminal ni = G.getNterminals().get(i);
            //Searching for Ai productions
            for (ProductionRule pr : G.getPrules()) {
                if (pr.getLeft().equals(ni)) {
                    currentA.add(pr);
                }
            }
            for (int j = 0; j < i; j++) {
                List<ProductionRule> subsA = new ArrayList<>();
                NonTerminal nj = G.getNterminals().get(j);
                for (ProductionRule pr : currentA) {
                    if (pr.getRight().get(0).equals(nj)) {
                        //Searching for Aj productions
                        for (ProductionRule pr2 : G.getPrules()) {
                            if (pr2.getLeft().equals(nj)) {
                                List<Symbol> right = new ArrayList(pr2.getRight());
                                List<Symbol> alpha = pr.getRight().subList(1, pr.getRight().size());
                                right.addAll(alpha);
                                subsA.add(new ProductionRule(ni, right));
                            }
                        }
                    } else {
                        subsA.add(pr);
                    }
                }
                currentA = subsA;
            }

            boolean foundRecursion = false;
            for (ProductionRule pr : currentA) {
                if (pr.getRight().get(0).equals(ni)) {
                    foundRecursion = true;
                    break;
                }
            }
            if (foundRecursion) {
                NonTerminal aPrime = new NonTerminal(ni.getId() + "'");
                newN.add(aPrime);
                for (ProductionRule pr : currentA) {
                    if (!pr.getRight().get(0).equals(ni)) {
                        List<Symbol> right = new ArrayList<>(pr.getRight());
                        right.add(aPrime);
                        newP.add(new ProductionRule(ni, right));
                    } else {
                        List<Symbol> alpha = new ArrayList<>(pr.getRight().subList(1, pr.getRight().size()));
                        alpha.add(aPrime);
                        newP.add(new ProductionRule(aPrime, alpha));
                    }
                }
                List<Symbol> right = new ArrayList<>();
                right.add(new Terminal(Terminal.E));
                newP.add(new ProductionRule(aPrime, right));

            } else {
                newP.addAll(currentA);
            }
        }
        return new GLC(newN, G.getTerminals(), newP, G.getStart());
    }

    /**
     * Performs binary exponentiation
     *
     * @param b Base
     * @param e Exponent
     * @return b^e
     */
    public static int pow(int b, int e) {
        if (e == 0) {
            return 1;
        }
        if (e == 1) {
            return b;
        }
        int mid = pow(b, e >> 1);
        if (e % 2 == 0) {
            return mid * mid;
        }
        return mid * mid * b;
    }

}
