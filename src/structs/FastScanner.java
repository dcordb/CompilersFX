/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package structs;

import java.io.*;
import java.util.*;

/**
 * A class for fast input using BufferedReader
 * @author Hayder
 */
public final class FastScanner {
    
    private BufferedReader bf;
    private StringTokenizer tk;
    
    FastScanner(InputStream in){
        bf = new BufferedReader(new InputStreamReader(in), 32768);
        tk = null;
    }
    
    public FastScanner(String file){
        bf = null;
        try{
            bf = new BufferedReader(new FileReader(file));
        }catch(IOException e){
            throw new RuntimeException(e);
        }
        tk = null;
    }
    
    public String next(){
        while(tk == null || !tk.hasMoreTokens()){
            try{
                tk = new StringTokenizer(bf.readLine());
            }catch(IOException e){
                throw new RuntimeException(e);
            }
        }
        return tk.nextToken();
    }
    
    public int nextInt(){
        return Integer.parseInt(next());
    }
    
    public long nextLong(){
        return Long.parseLong(next());
    }
    
    public boolean nextBoolean(){
        return Boolean.parseBoolean(next());
    }
    
    public boolean ready(){
        try{
            return bf.ready();
        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }
    
    public String nextLine(){
        try {
            return bf.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
}
