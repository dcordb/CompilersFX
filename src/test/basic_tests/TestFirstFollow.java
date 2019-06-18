package test.basic_tests;

import algos.Algorithms;
import structs.*;
import java.util.*;

public class TestFirstFollow {
    public static void main(String[] args) {
        FastScanner in = new FastScanner("src/test/basic_tests/G_FirstFollow.grammar");

        List <NonTerminal> nterm = new ArrayList <> ();
        List <Terminal> term = new ArrayList<>();
        List <ProductionRule> prules = new ArrayList<>();

        int n = in.nextInt();

        for(int i = 0; i < n; i++) {
            String s = in.next();
            nterm.add(new NonTerminal(s));
        }

        int m = in.nextInt();

        for(int i = 0; i < m; i++) {
            String s = in.next();
            term.add(new Terminal(s));
        }

        int k = in.nextInt();
        for(int i = 0; i < k; i++) {
            int cnt = in.nextInt();

            NonTerminal left = new NonTerminal(in.next());
            List <Symbol> right = new ArrayList<>();

            for(int j = 0; j < cnt - 1; j++) {
                String id = in.next();
                char c = id.charAt(0);

                if(Character.isLetter(c) && Character.isUpperCase(c))
                    right.add(new NonTerminal(id));

                else right.add(new Terminal(id));
            }

            assert(!right.isEmpty());

            prules.add(new ProductionRule(left, right));
        }

        String id = in.next();
        NonTerminal start = new NonTerminal(id);
        GLC G = new GLC(nterm, term, prules, start);

        System.out.println(G);

        //G = Algorithms.removeGeneralLRecursion(G);

        System.out.println("First Calculation");
        for(NonTerminal o : nterm) {
            System.out.println(o + " " + G.getFirst(o));
        }

        System.out.println();

        System.out.println("Follow Calculation");
        for(NonTerminal o : nterm) {
            System.out.println(o + " " + G.getFollow(o));
        }
    }
}
