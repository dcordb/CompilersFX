package test.tests_clr;

import java.util.*;
import structs.*;
import syntaxAnalyzers.parsers.CLR;
import syntaxAnalyzers.parsers.ComplexLR;

public class TestCLR_LRFunctions {
    public static void main(String[] args) throws Exception {
        FastScanner in = new FastScanner("src/test/tests_clr/G_parenthesis.grammar");

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

        ComplexLR clr = new CLR(G);
        clr.createTables();
        clr.printTables();
    }
}
