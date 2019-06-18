package syntaxAnalyzers.parsers;

import exceptions.CLRParserException;
import structs.*;
import syntaxAnalyzers.utils.Pair;
import syntaxAnalyzers.utils.State;
import syntaxAnalyzers.entries.OkEntry;
import syntaxAnalyzers.entries.ReduceEntry;
import syntaxAnalyzers.entries.ShiftEntry;
import syntaxAnalyzers.items.*;
import java.util.*;

public class CLR extends ComplexLR {
    public CLR(GLC G) { super(G); }

    public void createTables() throws CLRParserException {
        Set <ItemLR1> arr[] = getItems();

        HashMap <Set <ItemLR1>, State> mp = new HashMap<>();

        states = new ArrayList<>();
        actionTable = new HashMap<>();
        gotoTable = new HashMap<>();

        for(int i = 0; i < arr.length; i++) {
            State s = new State(i);
            states.add(s);
            mp.put(arr[i], s);
        }

        for(int i = 0; i < arr.length; i++) {
            Set <ItemLR1> s = arr[i];
            State I = mp.get(s);

            for(ItemLR1 item1 : s) {
                ItemLR0 item0 = item1.getItem();
                ProductionRule pr = item0.getPr();
                List <Symbol> right = pr.getRight();
                int pos = item0.getPos();

                if(pos < right.size() && right.get(pos).isTerminal()) {
                    Terminal a = (Terminal) right.get(pos);

                    Set <ItemLR1> s_goto = getGoto(s, a);

                    if(mp.containsKey(s_goto)) {
                        State J = mp.get(s_goto);
                        ShiftEntry shift = new ShiftEntry("S" + J.toString(), J);

                        Pair<State, Terminal> p = new Pair<>(I, a);

                        if(actionTable.containsKey(p) && !actionTable.get(p).equals(shift))
                            throw new CLRParserException("Could not create table. Grammar is not LR1");

                        actionTable.put(p, shift);
                    }
                }

                else if(pos == right.size()) {
                    if(!pr.getLeft().equals(super.getG().getStart())) {
                        int id = super.getG().getPrules().indexOf(pr);
                        ReduceEntry reduce = new ReduceEntry("R" + id, id);

                        Pair <State, Terminal> p = new Pair<>(I, item1.getTerminal());

                        if(actionTable.containsKey(p) && !actionTable.get(p).equals(reduce))
                            throw new CLRParserException("Could not create table. Grammar is not LR1");

                        actionTable.put(p, reduce);
                    }

                    else if(item1.getTerminal().equals(Terminal.DOLLAR)) {
                        Pair <State, Terminal> p = new Pair<>(I, item1.getTerminal());

                        if(actionTable.containsKey(p) && !actionTable.get(p).equals(OkEntry.getInstance()))
                            throw new CLRParserException("Could not create table. Grammar is not LR1");

                        actionTable.put(new Pair<>(I, item1.getTerminal()), OkEntry.getInstance());
                    }
                }
            }

            for(NonTerminal A : super.getG().getNterminals()) {
                Set <ItemLR1> s_goto = getGoto(s, A);

                if(mp.containsKey(s_goto)) {
                    State J = mp.get(s_goto);
                    ShiftEntry shift = new ShiftEntry("G" + J.toString(), J);
                    gotoTable.put(new Pair<>(I, A), shift);
                }
            }

            List <ProductionRule> lst = super.getG().getPrules();
            ItemLR0 item0 = new ItemLR0(lst.get(lst.size() - 1), 0); //is always in the last position, check constructor of Parser class
            ItemLR1 item1 = new ItemLR1(item0, Terminal.DOLLAR);

            if(s.contains(item1))
                start = I;
        }

        assert start != null;
    }
}
