package test.tests_lalr;

import java.util.*;

import algos.Algorithms;
import structs.*;
import syntaxAnalyzers.parsers.ComplexLR;
import syntaxAnalyzers.parsers.LALR;

public class TestLALR {
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

        G = Algorithms.removeGeneralLRecursion(G);

        System.out.println(G);

        ComplexLR lalr = new LALR(G);
        lalr.createTables();
        lalr.printTables();

        String inputString = in.nextLine();

        if(inputString == null)
            inputString = "";

        List <ProductionRule> derivations = lalr.analyse(inputString);

        System.out.println("\nDerivations");

        for(ProductionRule pr : derivations)
            System.out.println(pr);
    }
}
