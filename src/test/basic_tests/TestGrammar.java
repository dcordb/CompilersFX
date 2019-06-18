package test.basic_tests;

import algos.Algorithms;
import structs.*;

public class TestGrammar {
    public static void main(String[] args) throws Exception {
        FastScanner in = new FastScanner("src/test/tests_clr/G_EPS_5.grammar");

        String terminals = in.nextLine();
        String nonTerminals = in.nextLine();

        int cntProdRules = in.nextInt();

        StringBuilder prodRules = new StringBuilder();

        for(int i = 0; i < cntProdRules; i++) {
            prodRules.append(in.nextLine());
            prodRules.append("\n");
        }

        GLC G = GLC.loadGrammar(terminals, nonTerminals, prodRules.toString());

        System.out.println(G);

        System.out.println(Algorithms.algorithmA(G));
    }
}
