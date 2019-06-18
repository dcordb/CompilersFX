package test.basic_tests;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Hayder
 */
import algos.Algorithms;
import structs.*;
import java.util.*;
import java.io.*;

public class Main {
    
    public static void main(String[] args)throws Exception{
        
        Scanner in = new Scanner(new FileReader("src/test/basic_tests/G1.grammar"));
        
        List<NonTerminal> nt = new LinkedList<>();
        List<Terminal> t = new LinkedList<>();
        List<ProductionRule> pr = new LinkedList<>();
        NonTerminal start;
        
        int lenNT = in.nextInt();
        
        in.nextLine();
        while(lenNT-->0){
            nt.add(new NonTerminal(in.next()));
        }
        in.nextLine();
        
        int lenT = in.nextInt();
        in.nextLine();
        while(lenT-->0){
            t.add(new Terminal(in.next()));
        }
        in.nextLine();
        
        int lenP = in.nextInt();
        in.nextLine();
        while(lenP-->0){
            String[] line = in.nextLine().split(" ");
            
            NonTerminal left = new NonTerminal(line[0]);
            List<Symbol> right = new LinkedList<>();
            for(int i = 1; i < line.length; i++){
                if(line[i].equals(Terminal.E)){
                    right.add(new Terminal(Terminal.E));
                    continue;
                }
                char c = line[i].charAt(0);
                if(Character.isLetter(c) && Character.isUpperCase(c)){
                    right.add(new NonTerminal(c+""));
                }else
                    right.add(new Terminal(c+""));
            }
            ProductionRule tmp = new ProductionRule(left, right);
            pr.add(tmp);
        }
        
        start = new NonTerminal(in.next());
        GLC G = new GLC(nt, t, pr, start);
        System.out.println(Algorithms.delete_simple_productions(G));
    }
}
