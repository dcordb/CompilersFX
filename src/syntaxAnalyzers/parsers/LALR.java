package syntaxAnalyzers.parsers;

import structs.*;
import syntaxAnalyzers.utils.Pair;
import syntaxAnalyzers.utils.State;
import syntaxAnalyzers.entries.*;
import syntaxAnalyzers.items.*;
import java.util.*;
import exceptions.LALRParserException;

public class LALR extends ComplexLR {
    public LALR(GLC G) { super(G); }

    public void createTables() throws LALRParserException {
        Set <ItemLR1> items[] = getItems();

        HashMap <Set <ItemLR0>, Set <ItemLR1> > union = new HashMap<>();
        HashMap <Set <ItemLR0>, Integer> ids = new HashMap<>();
        HashMap <Set <ItemLR0>, ArrayList <Integer> > whoPut = new HashMap<>();

        Set <Set <ItemLR0> > s_heart = new HashSet<>();

        int t = 0;

        for(int i = 0; i < items.length; i++) {
            Set <ItemLR0> heart = new HashSet<>();

            for(ItemLR1 item1 : items[i])
                heart.add(item1.getItem());

            if(!union.containsKey(heart)) {
                union.put(heart, new HashSet<>());
                whoPut.put(heart, new ArrayList <>());
                ids.put(heart, t++);
            }

            union.get(heart).addAll(items[i]);
            whoPut.get(heart).add(i);

            s_heart.add(heart);
        }

        Set <ItemLR1> arr[] = new Set[union.size()];
        ArrayList <Integer> who[] = new ArrayList[union.size()];

        for(Set <ItemLR0> heart : s_heart) {
            int pos = ids.get(heart);
            arr[pos] = union.get(heart);
            who[pos] = whoPut.get(heart);
        }

        HashMap <Set <ItemLR1>, State> mp = new HashMap<>();

        states = new ArrayList<>();
        actionTable = new HashMap<>();
        gotoTable = new HashMap<>();

        int inv[] = new int[items.length];

        for(int i = 0; i < arr.length; i++) {
            State s = new State(i);
            states.add(s);
            mp.put(arr[i], s);

            for(int o : who[i])
                inv[o] = i;
        }

        for(int i = 0; i < arr.length; i++) {
            Set <ItemLR1> s = arr[i];
            State I = states.get(i);

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
                            throw new LALRParserException("Could not create table. Grammar is not LALR1");

                        actionTable.put(p, shift);
                    }

                    else {
                        for(int pt = 0; pt < items.length; pt++) {
                            Set <ItemLR1> sP = items[pt];

                            if(s_goto.equals(sP)) {
                                State J = states.get(inv[pt]);

                                ShiftEntry shift = new ShiftEntry("S" + J.toString(), J);

                                Pair <State, Terminal> p = new Pair<>(I, a);

                                if(actionTable.containsKey(p) && !actionTable.get(p).equals(shift))
                                    throw new LALRParserException("Could not create table. Grammar is not LALR1");

                                actionTable.put(p, shift);

                                break;
                            }
                        }
                    }
                }

                else if(pos == right.size()) {
                    if(!pr.getLeft().equals(super.getG().getStart())) {
                        int id = super.getG().getPrules().indexOf(pr);
                        ReduceEntry reduce = new ReduceEntry("R" + id, id);

                        Pair <State, Terminal> p = new Pair<>(I, item1.getTerminal());

                        if(actionTable.containsKey(p) && !actionTable.get(p).equals(reduce))
                            throw new LALRParserException("Could not create table. Grammar is not LALR1");

                        actionTable.put(p, reduce);
                    }

                    else if(item1.getTerminal().equals(Terminal.DOLLAR)) {
                        Pair <State, Terminal> p = new Pair<>(I, item1.getTerminal());

                        if(actionTable.containsKey(p) && !actionTable.get(p).equals(OkEntry.getInstance()))
                            throw new LALRParserException("Could not create table. Grammar is not LALR1");

                        actionTable.put(new Pair<>(I, item1.getTerminal()), OkEntry.getInstance());
                    }
                }
            }

            for(NonTerminal A : super.getG().getNterminals()) {
                Set <ItemLR1> s_goto = getGoto(items[who[i].get(0)], A);

                Set <ItemLR0> heart = new HashSet<>();

                for(ItemLR1 item1 : s_goto)
                    heart.add(item1.getItem());

                if(!union.containsKey(heart))
                    continue;

                Set <ItemLR1> to = union.get(heart);

                int enter = 0;
                for(int j = 0; j < arr.length; j++) {
                    if(to.equals(arr[j])) {
                        State J = states.get(j);
                        ShiftEntry shift = new ShiftEntry("G" + J.toString(), J);
                        gotoTable.put(new Pair<>(I, A), shift);
                        enter++;
                    }
                }

                assert enter <= 1;
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