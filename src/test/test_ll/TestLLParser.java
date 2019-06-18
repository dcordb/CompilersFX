package test.test_ll;

import algos.Algorithms;
import structs.*;

import java.util.ArrayList;
import java.util.List;
import syntaxAnalyzers.parsers.LL;
import java.util.Scanner;

public class TestLLParser {
    public static void main(String[] args) throws Exception {
        FastScanner in = new FastScanner("src/test/test_ll/G_TestLL_1.grammar");

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

        LL ll = new LL(G);

        ll.createTable();
        ll.printTable();

        String inputString = in.nextLine();

        if(inputString == null)
            inputString = "";

        List <ProductionRule> derivations = ll.analyse(inputString);

        System.out.println("\nDerivations");

        for(ProductionRule pr : derivations)
            System.out.println(pr);
    }
}
