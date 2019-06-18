package syntaxAnalyzers.parsers;
import exceptions.LLParserException;
import structs.*;
import syntaxAnalyzers.utils.*;
import java.util.*;
import exceptions.AnalyseLLException;

public class LL extends Parser {
    private Map<Pair<NonTerminal, Terminal>, ProductionRule> table;

    public LL(GLC G) { super(G); }

    public List<ProductionRule> analyse(String w) throws AnalyseLLException {
        w = w + " $";

        StringTokenizer tk = new StringTokenizer(w);
        String arr[] = new String[tk.countTokens()];

        int p = 0;
        while(tk.hasMoreTokens())
            arr[p++] = tk.nextToken();

        List <ProductionRule> derivations = new ArrayList<>();

        Stack <Symbol> stk = new Stack<>();
        stk.add(G.getStart());

        int pos = 0;

        while(!stk.empty()) {
            Symbol cur = stk.peek();

            if(cur.isTerminal()) {
                if(cur.equals(Terminal.DOLLAR))
                    throw new AnalyseLLException("Word does not belong to generated language by G: Expecting a Terminal, found $ instead");

                stk.pop();
                pos++;
            }

            else {
                NonTerminal non_term = (NonTerminal) cur;

                Terminal term = new Terminal(arr[pos]);
                Pair <NonTerminal, Terminal> key = new Pair<>(non_term, term);

                if(!table.containsKey(key))
                    throw new AnalyseLLException("Could not get derivations. Word does not belong to generated language by G: Expecting key (" + key.getLeft() + ", " + key.getRight() + ") being in the Table");

                ProductionRule curPr = null;
                for(ProductionRule pr : G.getPrules()) {
                    if(pr.equals(table.get(key))) {
                        curPr = pr;
                        break;
                    }
                }

                if(table.get(key).equals(curPr)) {
                    stk.pop();

                    if(!curPr.isEProduction()) {
                        List<Symbol> right = curPr.getRight();

                        for (int i = right.size() - 1; i >= 0; i--) {
                            Symbol s = right.get(i);
                            stk.push(s);
                        }
                    }

                    derivations.add(curPr);
                }

                else throw new AnalyseLLException("Could not get derivations. Word does not belong to generated language by G: Table content and Production Rule dont match");
            }
        }

        return derivations;
    }

    private void addToTable(NonTerminal left, Terminal term, ProductionRule pr) throws LLParserException {
        Pair <NonTerminal, Terminal> key = new Pair<>(left, term);

        if (table.containsKey(key))
            throw new LLParserException("Could not create table: Duplicated entries in Table, it looks like Grammar is not LL");

        table.put(key, pr);
    }

    public void createTable() throws LLParserException {
        table = new HashMap<>();

        for(ProductionRule pr : G.getPrules()) {
            NonTerminal left = pr.getLeft();
            List <Symbol> right = pr.getRight();
            Set <Terminal> first = G.getFirst(right);

            for(Terminal term : first) {
                if (term.equals(Terminal.EPS)) {
                    Set <Terminal> follow = G.getFollow(left);

                    for(Terminal add_term : follow) {
                        if (!add_term.equals(Terminal.EPS))
                            addToTable(left, add_term, pr);
                    }
                }

                else addToTable(left, term, pr);
            }
        }
    }

    public void printTable() {
        System.out.println("Printing LL Table");
        for(NonTerminal non_term : G.getNterminals()) {
            for(Terminal term : G.getTerminals()) {
                Pair <NonTerminal, Terminal> key = new Pair<>(non_term, term);

                if(table.containsKey(key))
                    System.out.println(non_term + " " + term + " : " + table.get(key));
            }
        }

        System.out.println("----------------------------------");
    }

    public String[][] getTableTo2dArray() {
        List <NonTerminal> nonTerminals = G.getNterminals();
        List <Terminal> terminals = G.getTerminals();

        int n = nonTerminals.size();
        int m = terminals.size();

        String[][] arr = new String[n + 1][m + 1];

        arr[0][0] = "Non Terminals";

        for(int i = 1; i <= n; i++)
            arr[i][0] = nonTerminals.get(i - 1).toString();

        for(int i = 1; i <= m; i++)
            arr[0][i] = terminals.get(i - 1).toString();

        for(int i = 1; i <= n; i++) {
            for(int j = 1; j <= m; j++) {
                NonTerminal nonTerm = nonTerminals.get(i - 1);
                Terminal term = terminals.get(j - 1);

                Pair <NonTerminal, Terminal> key = new Pair<>(nonTerm, term);

                if(table.containsKey(key))
                    arr[i][j] = table.get(key).toString();

                else arr[i][j] = "";
            }
        }

        return arr;
    }
}
