package syntaxAnalyzers.parsers;

import java.util.*;

import exceptions.*;
import structs.*;
import syntaxAnalyzers.utils.Pair;
import syntaxAnalyzers.utils.State;
import syntaxAnalyzers.entries.*;
import syntaxAnalyzers.items.*;

public abstract class ComplexLR extends Parser {
    protected Map <Pair<State, Terminal>, TableEntry> actionTable;
    protected Map <Pair <State, NonTerminal>, TableEntry> gotoTable;

    protected List <State> states;

    protected State start;

    public ComplexLR(GLC G) {
        super(G);
    }

    public Set <ItemLR1> getClousure(Set <ItemLR1> I) {
        Set <ItemLR1> prv = new HashSet<>();
        prv.addAll(I);

        while(true) {
            Set <ItemLR1> curr = new HashSet<>();
            curr.addAll(prv);

            for(ItemLR1 o : prv) {
                ItemLR0 item0 = o.getItem();

                ProductionRule pr = item0.getPr();
                List <Symbol> right = pr.getRight();

                LinkedList <Symbol> suffix = new LinkedList<>();

                for(int i = right.size() - 1; i >= 0; i--) {
                    Symbol s = right.get(i);

                    if(item0.getPos() == i && s.isNonTerminal()) {
                        suffix.addLast(o.getTerminal());

                        Set <Terminal> tmp = super.getG().getFirst(suffix);

                        NonTerminal B = (NonTerminal) s;

                        for(ProductionRule currPR : super.getG().getPrules()) {
                            if(!currPR.getLeft().equals(B))
                                continue;

                            for (Terminal b : tmp) {
                                ItemLR0 item = new ItemLR0(currPR, 0);

                                if(currPR.isEProduction())
                                    item.setPos(1);

                                curr.add(new ItemLR1(item, b));
                            }
                        }

                        suffix.pollLast();
                    }

                    suffix.addFirst(s);
                }
            }

            if(curr.equals(prv))
                break;

            prv.addAll(curr);
        }

        return prv;
    }

    public Set <ItemLR1> getGoto(Set <ItemLR1> I, Symbol s) {
        Set <ItemLR1> J = new HashSet<>();

        for(ItemLR1 item1 : I) {
            ItemLR0 item0 = item1.getItem();
            List <Symbol> right = item0.getPr().getRight();

            int pos = item0.getPos();

            if(pos < right.size() && right.get(pos).equals(s)) {
                ItemLR0 newItem0 = new ItemLR0(item0.getPr(), pos + 1);
                ItemLR1 newItem1 = new ItemLR1(newItem0, item1.getTerminal());
                J.add(newItem1);
            }
        }

        return getClousure(J);
    }

    public Set <ItemLR1> [] getItems() {
        List <ProductionRule> lst = super.getG().getPrules();
        ItemLR0 item0 = new ItemLR0(lst.get(lst.size() - 1), 0); //is always in the last position, check constructor of Parser class
        ItemLR1 item1 = new ItemLR1(item0, Terminal.DOLLAR);

        Set <ItemLR1> ini = new HashSet<>();
        ini.add(item1);

        HashMap <Set <ItemLR1>, Integer> ids = new HashMap<>();

        Set <Set <ItemLR1> > prv = new HashSet<>();
        Set <ItemLR1> o = getClousure(ini);
        prv.add(o);

        int t = 0;
        ids.put(o, t++);

        while(true) {
            Set <Set <ItemLR1> > curr = new HashSet<>();
            curr.addAll(prv);

            for(Set <ItemLR1> I : prv) {
                for(Symbol s : super.getG().getSymbols()) {
                    Set <ItemLR1> tmp = getGoto(I, s);

                    if(!tmp.isEmpty()) {
                        if(!curr.contains(tmp)) {
                            curr.add(tmp);
                            ids.put(tmp, t++);
                        }
                    }
                }
            }

            if(curr.equals(prv))
                break;

            prv.addAll(curr);
        }

        Set <ItemLR1> arr[] = new Set[prv.size()];

        for(Set <ItemLR1> st : prv)
            arr[ids.get(st)] = st;

        return arr;
    }

    abstract public void createTables() throws LALRParserException, CLRParserException;

    public String[][] getTablesTo2dArray() {
        List <NonTerminal> nonTerminals = G.getNterminals();
        List <Terminal> terminals = G.getTerminals();

        int n = states.size();
        int m = nonTerminals.size() + terminals.size();

        String[][] arr = new String[n + 1][m + 2];

        for(int i = 0; i <= n; i++)
            for(int j = 0; j <= m + 1; j++)
                arr[i][j] = "";

        for(int i = 1; i <= n; i++) {
            for(int j = 1; j <= terminals.size(); j++) {
                Pair <State, Terminal> key = new Pair<>(new State(i - 1), terminals.get(j - 1));

                if(!actionTable.containsKey(key))
                    arr[i][j] = ErrorEntry.getInstance().toString();

                else arr[i][j] = actionTable.get(key).toString();
            }

            for(int j = 1; j <= nonTerminals.size(); j++) {
                Pair <State, NonTerminal> key = new Pair<>(new State(i - 1), nonTerminals.get(j - 1));

                if(!gotoTable.containsKey(key))
                    arr[i][j + terminals.size() + 1] = ErrorEntry.getInstance().toString();

                else arr[i][j + terminals.size() + 1] = gotoTable.get(key).toString();
            }
        }

        for(int i = 1; i <= terminals.size(); i++)
            arr[0][i] = terminals.get(i - 1).toString();

        for(int i = 1; i <= nonTerminals.size(); i++)
            arr[0][i + terminals.size() + 1] = nonTerminals.get(i - 1).toString();

        for(int i = 1; i <= n; i++)
            arr[i][0] = Integer.toString(i - 1);

        arr[0][0] = "Action Table";
        arr[0][terminals.size() + 1] = "Goto Table";

        return arr;
    }

    public List <ProductionRule> analyse(String w) throws AnalyseLRException { //se requiere que cada simbolo este separado por espacios
        w = w + " $";

        StringTokenizer tk = new StringTokenizer(w);
        String arr[] = new String[tk.countTokens()];

        int p = 0;
        while (tk.hasMoreTokens())
            arr[p++] = tk.nextToken();

        List <ProductionRule> derivations = new ArrayList<>();

        Stack <Object> stk = new Stack<>();
        stk.add(start);

        int pos = 0;

        while(true) {
            State s = (State) stk.peek();
            String token = arr[pos];

            Terminal a = new Terminal(token);

            if(!super.getG().exists(a))
                throw new AnalyseLRException("Could not get derivations. Word does not belong to generated language by G");

            Pair <State, Terminal> who = new Pair<>(s, a);

            if(!actionTable.containsKey(who))
                throw new AnalyseLRException("Could not get derivations. Word does not belong to generated language by G");

            TableEntry entry = actionTable.get(who);

            if(entry instanceof ShiftEntry) {
                stk.push(a);
                stk.push(((ShiftEntry) entry).getState());
                pos++;
            }

            else if(entry instanceof ReduceEntry) {
                int idpr = ((ReduceEntry) entry).getIdPr();
                ProductionRule pr = super.getG().getPrules().get(idpr);

                int cnt = 2 * pr.getRight().size();

                if(pr.isEProduction())
                    cnt = 0;

                while(cnt-- > 0) {
                    stk.pop();
                }

                State top = (State) stk.peek();

                if(!gotoTable.containsKey(new Pair<>(top, pr.getLeft())))
                    throw new AnalyseLRException("Could not get derivations. Word does not belong to generated language by G");

                stk.push(pr.getLeft());

                ShiftEntry shift = (ShiftEntry) gotoTable.get(new Pair<>(top, pr.getLeft()));
                stk.push(shift.getState());

                derivations.add(pr);
            }

            else {
                break; //Ok!
            }
        }

        return derivations;
    }

    public void printTables() {
        System.out.println("Number of states: " + states.size());
        System.out.println("Initial state: " + start);
        System.out.println();

        System.out.println("Action Table");

        StringBuilder s = new StringBuilder();

        for(Terminal o : super.getG().getTerminals()) {
            s.append('\t');
            s.append(o.toString());
        }

        System.out.println(s);

        for(State i : states) {
            System.out.print(i);

            for(Terminal o : super.getG().getTerminals()) {
                Pair <State, Terminal> p = new Pair<>(i, o);

                System.out.print('\t');

                if(actionTable.containsKey(p))
                    System.out.print(actionTable.get(p));

                else System.out.print(ErrorEntry.getInstance());
            }

            System.out.println();
        }

        System.out.println("\nGoto Table");

        s = new StringBuilder();
        for(NonTerminal o : super.getG().getNterminals()) {
            s.append('\t');
            s.append(o.toString());
        }

        System.out.println(s);

        for(State i : states) {
            System.out.print(i);

            for(NonTerminal o : super.getG().getNterminals()) {
                Pair <State, NonTerminal> p = new Pair<>(i, o);

                System.out.print('\t');

                if(gotoTable.containsKey(p))
                    System.out.print(gotoTable.get(p));

                else System.out.print(ErrorEntry.getInstance());
            }

            System.out.println();
        }
    }

    public Map<Pair<State, Terminal>, TableEntry> getActionTable() {
        return actionTable;
    }

    public void setActionTable(Map<Pair<State, Terminal>, TableEntry> actionTable) {
        this.actionTable = actionTable;
    }

    public Map<Pair<State, NonTerminal>, TableEntry> getGotoTable() {
        return gotoTable;
    }

    public void setGotoTable(Map<Pair<State, NonTerminal>, TableEntry> gotoTable) {
        this.gotoTable = gotoTable;
    }

    public List<State> getStates() {
        return states;
    }

    public void setStates(List<State> states) { this.states = states; }

    public State getStart() { return start; }

    public void setStart(State start) {
        this.start = start;
    }
}
