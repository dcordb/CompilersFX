package test.tests_clr;

import java.util.*;

import algos.Algorithms;
import structs.*;
import syntaxAnalyzers.items.ItemLR0;
import syntaxAnalyzers.parsers.CLR;
import syntaxAnalyzers.parsers.ComplexLR;

public class TestCLRAritmeticExpression {
    public static void main(String[] args) throws Exception {
        FastScanner in = new FastScanner("src/test/tests_clr/G_TestLR_10.grammar");

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

//        G = Algorithms.removeGeneralLRecursion(G);
//
//        System.out.println(G);

        ComplexLR clr = new CLR(G);
        clr.createTables();
        clr.printTables();

        String inputString = in.nextLine();

        if(inputString == null)
            inputString = "";

        List <ProductionRule> derivations = clr.analyse(inputString);

        System.out.println("\nDerivations");

        for(ProductionRule pr : derivations)
            System.out.println(pr);
    }
}
