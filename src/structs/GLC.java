/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package structs;

import java.util.*;
import exceptions.LoadingGrammarException;

import javax.naming.NameNotFoundException;

/**
 * Represents a Context-Free-Grammar
 * @author Hayder
 */
public class GLC {
    /**
     * List of non terminals symbols
     */
    protected List<NonTerminal> nterminals;
    /**
     * List of terminals symbols
     */
    protected List<Terminal> terminals;
    /**
     * List of productions rules
     */
    protected List<ProductionRule> prules;
    /**
     * Initial terminal symbol
     */
    protected NonTerminal start;

    //memoization of getFirst calculation
    private HashMap <Symbol, Set <Terminal> > memoFirst;
    private HashMap <Symbol, Set <Terminal> > memoFollow;

    public GLC deepCopy() {
        List <NonTerminal> nt = new ArrayList<>();
        List <Terminal> t = new ArrayList<>();
        List <ProductionRule> pr = new ArrayList<>();
        NonTerminal ini = new NonTerminal(start.getId());

        nt.addAll(nterminals);
        t.addAll(terminals);
        pr.addAll(prules);

        return new GLC(nt, t, pr, ini);
    }

    /*
        loads a grammar given inputTerminals, inputNonTerminals and inputProductionRules

        The format is:
            inputTerminals: String of space separated terminals (WITHOUT ~(epsilon) and $ (end of word))
            inputNonTerminals: String of space separated non terminals
            inputProdRules: String of multiple lines (separated with "\n"), each line represents a Production Rule
            in the following format LEFT -> RIGHT

        Non Terminals: start with an UPPERCASE LETTER
        Terminals: DONT start with an UPPERCASE LETTER (ie, "(", "a", "b", "dmbx", are all examples of terminals,
        note that neither "$" or "~" have to be entered in the terminals)

        All tokens need to be space separated, " " is the separator

        Start symbol is first symbol of first Production Rule.
    */

    private static List <String> tokenizeString(String in, String delim) {
        StringTokenizer tk = new StringTokenizer(in, delim);
        List <String> lst = new LinkedList<>();

        while(tk.hasMoreTokens()) {
            lst.add(tk.nextToken());
        }

        return lst;
    }

    public static GLC loadGrammar(String inputTerminals, String inputNonTerminals, String inputProdRules) throws LoadingGrammarException {
        List <String> idTerminals = tokenizeString(inputTerminals, " ");

        if(idTerminals.isEmpty())
            throw new LoadingGrammarException("Empty list of Terminals");

        List <String> idNonTerminals = tokenizeString(inputNonTerminals, " ");

        if(idNonTerminals.isEmpty())
            throw new LoadingGrammarException("Empty list of Non Terminals");

        List <Terminal> terminals = new LinkedList<>();
        List <NonTerminal> nonTerminals = new LinkedList<>();

        for(String o : idTerminals) {
            if(o.equals("$") || o.equals("~"))
                throw new LoadingGrammarException("Symbol " + o + " should not be included in terminals, ie. it is implicitly included!");

            char c = o.charAt(0);

            if(Character.isUpperCase(c))
                throw new LoadingGrammarException("Symbol " + o + " cant be a Terminal, it starts with a capital letter");

            terminals.add(new Terminal(o));
        }

        for(String o : idNonTerminals) {
            char c = o.charAt(0);

            if(!Character.isUpperCase(c))
                throw new LoadingGrammarException("Symbol " + o + " cant be a Non Terminal, it doenst start with a capital letter");

            nonTerminals.add(new NonTerminal(o));
        }

        List <String> idProdRules = tokenizeString(inputProdRules, "\n");

        List <ProductionRule> prodRules = new LinkedList<>();

        for(String pr : idProdRules) {
            List<String> line = tokenizeString(pr, " ");

            if (line.size() <= 1 || !line.get(1).equals("->"))
                throw new LoadingGrammarException("Expected '->' separator in production rules");

            NonTerminal left = null;
            List<Symbol> right = new LinkedList<>();

            boolean onLeft = true;

            for (String o : line) {
                if (o.equals("->")) {
                    onLeft = false;
                    continue;
                }

                char c = o.charAt(0);

                if (onLeft) {
                    if (!Character.isUpperCase(c))
                        throw new LoadingGrammarException("Expected Non Terminal, found symbol " + o);

                    left = new NonTerminal(o);

                    if (!nonTerminals.contains(left))
                        throw new LoadingGrammarException("Non Terminal " + o + " is not in the Non Terminals list");
                }

                else {
                    if (Character.isUpperCase(c)) {
                        NonTerminal nt = new NonTerminal(o);

                        if (!nonTerminals.contains(nt))
                            throw new LoadingGrammarException("Non Terminal " + o + " is not in the Non Terminals list");

                        right.add(nt);
                    }

                    else {
                        Terminal term = new Terminal(o);

                        if (!term.equals(Terminal.EPS) && !terminals.contains(term))
                            throw new LoadingGrammarException("Terminal " + o + " is not in the Terminals list");

                        right.add(term);
                    }
                }
            }

            prodRules.add(new ProductionRule(left, right));
        }

        if(prodRules.isEmpty())
            throw new LoadingGrammarException("There are no production rules, or are malformed");

        NonTerminal start = prodRules.get(0).getLeft(); //assumes that first production rule is the starting one
        return new GLC(nonTerminals, terminals, prodRules, start);
    }

    public GLC(List<NonTerminal> nterminals, List<Terminal> terminals, List<ProductionRule> prules, NonTerminal start) {
        setNterminals(nterminals);
        setTerminals(terminals);
        setPrules(prules);
        setStart(start);

        memoFirst = new HashMap<>();
        memoFollow = new HashMap<>();
    }

    public List <Symbol> getSymbols() {
        List <Symbol> s = new ArrayList<>();

        for(NonTerminal o : nterminals)
            s.add(o);

        for(Terminal o : terminals)
            s.add(o);

        return s;
    }

    public List<NonTerminal> getNterminals() {
        return nterminals;
    }

    public void setNterminals(List<NonTerminal> nterminals) {
        Set <NonTerminal> set = new HashSet<>(nterminals);
        this.nterminals = new LinkedList<>(set);
    }

    public List<Terminal> getTerminals() {
        return terminals;
    }

    public void setTerminals(List<Terminal> terminals) {
        Set <Terminal> set = new HashSet<>(terminals);

        set.add(Terminal.DOLLAR);
        set.add(Terminal.EPS);

        this.terminals = new LinkedList<>(set);
    }

    public List<ProductionRule> getPrules() {
        return prules;
    }

    public void setPrules(List<ProductionRule> prules) {
        this.prules = prules;
    }

    public NonTerminal getStart() {
        return start;
    }

    public void setStart(NonTerminal start) {
        this.start = start;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + Objects.hashCode(this.nterminals);
        hash = 41 * hash + Objects.hashCode(this.terminals);
        hash = 41 * hash + Objects.hashCode(this.prules);
        hash = 41 * hash + Objects.hashCode(this.start);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GLC other = (GLC) obj;
        if (!Objects.equals(this.nterminals, other.nterminals)) {
            return false;
        }
        if (!Objects.equals(this.terminals, other.terminals)) {
            return false;
        }
        if (!Objects.equals(this.prules, other.prules)) {
            return false;
        }
        if (!Objects.equals(this.start, other.start)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("N: ");
        nterminals.forEach( nt -> {
            out.append(nt).append(" ");
        });
        out.append("\n");
        out.append("T: ");
        terminals.forEach( t ->{
            out.append((t)).append(" ");
        });
        out.append("\n");
        out.append("P:");
        prules.forEach( pr ->{
            out.append("\n\t");
            out.append(pr);
        });
        out.append("\n");
        out.append("So: ");
        out.append(start);
        out.append("\n");
        return out.toString();
    }

    public boolean exists(Symbol s) {
        if(s instanceof Terminal) {
            Terminal t = (Terminal) s;

            if(t.equals(Terminal.EPS))
                return true;
        }

        for (Symbol o : nterminals)
            if (o.equals(s))
                return true;

        for (Symbol o : terminals)
            if (o.equals(s))
                return true;

        return false;
    }

    //THIS HAS A BUG, if grammar is cyclic can cycle for ever! ... Palmface :(
    public Set <Terminal> getFirst(Symbol s) {
        if(!exists(s))
            throw new RuntimeException("Symbol " + s + " doesn't exist!");

        if(memoFirst.containsKey(s))
            return memoFirst.get(s);

        Set <Terminal> res = new HashSet<>();

        if(s.isTerminal()) {
            res.add((Terminal) s);
            memoFirst.put(s, res);
            return res;
        }

        for(ProductionRule pr : prules) {
            if(pr.getLeft().equals(s)) {
                if(pr.isEProduction()) {
                    res.add(Terminal.EPS);
                    continue;
                }

                boolean enter = false;
                for(Symbol right : pr.getRight()) {
                    Set <Terminal> tmp = getFirst(right);

                    for(Terminal t : tmp)
                        if(!t.equals(Terminal.EPS))
                            res.add(t);

                    if(!tmp.contains(Terminal.EPS)) {
                        enter = true;
                        break;
                    }
                }

                if(!enter)
                    res.add(Terminal.EPS);
            }
        }

        memoFirst.put(s, res);
        return res;
    }

    public Set <Terminal> getFirst(List <Symbol> list) {
        Set <Terminal> res = new HashSet<>();

        boolean enter = false;
        for(Symbol s : list) {
            Set <Terminal> tmp = getFirst(s);

            for(Terminal t : tmp)
                if(!t.equals(Terminal.EPS))
                    res.add(t);

            if(!tmp.contains(Terminal.EPS)) {
                enter = true;
                break;
            }
        }

        if(!enter)
            res.add(Terminal.EPS);

        return res;
    }

    private void calcFollow() {
        for(NonTerminal o : nterminals)
            memoFollow.put(o, new HashSet<>());

        memoFollow.get(start).add(Terminal.DOLLAR);

        while(true) {
            HashMap <NonTerminal, Set <Terminal> > currMemoFollow = (HashMap <NonTerminal, Set <Terminal> >) memoFollow.clone();

            boolean enter = false;

            for(ProductionRule pr : prules) {
                List <Symbol> right = pr.getRight();
                LinkedList <Symbol> list = new LinkedList<>();

                for(int i = right.size() - 1; i >= 0; i--) {
                    Symbol B = right.get(i);

                    if(B.isNonTerminal() && !list.isEmpty()) {
                        Set <Terminal> tmp = getFirst(list);

                        for(Terminal t : tmp)
                            if (!t.equals(Terminal.EPS))
                                currMemoFollow.get((NonTerminal) B).add(t);
                    }

                    list.addFirst(B);
                }
            }

            for(ProductionRule pr : prules) {
                List <Symbol> right = pr.getRight();
                LinkedList <Symbol> list = new LinkedList<>();

                for(int i = right.size() - 1; i >= 0; i--) {
                    Symbol B = right.get(i);

                    if(B.isNonTerminal()) {
                        Set <Terminal> v = currMemoFollow.get((NonTerminal) pr.getLeft());

                        if (list.isEmpty()) {
                            currMemoFollow.get((NonTerminal) B).addAll(v);
                        }

                        else {
                            Set <Terminal> tmp = getFirst(list);

                            if(tmp.contains(Terminal.EPS))
                                currMemoFollow.get((NonTerminal) B).addAll(v);
                        }
                    }

                    list.addFirst(B);
                }
            }

            if(currMemoFollow.equals(memoFollow))
                break;
        }
    }
    
    public Set <Terminal> getFollow(NonTerminal v) {
        if(memoFollow.isEmpty())
            calcFollow();

        return memoFollow.get(v);
    }
}
