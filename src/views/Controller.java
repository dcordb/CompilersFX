package views;
import algos.Algorithms;
import exceptions.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.*;

import java.util.*;

import javafx.util.Callback;
import structs.*;
import com.jfoenix.controls.*;
import syntaxAnalyzers.parsers.*;

public class Controller {
    public JFXButton btnAddGrammar;
    public JFXTextField txtFieldTerminals;
    public JFXTextField txtFieldNonTerminals;
    public JFXTextField txtFieldExpresion;
    public JFXTextArea txtAreaProductionRules;
    public JFXTextArea txtAreaDerivations;
    public JFXTreeView tableGrammar;
    public TableView tableParsers;

    private GLC currG; //current loaded Grammar

    private void refreshGrammarTable() {
        TreeItem <String> root = new TreeItem<>("Root");
        TreeItem <String> terminals = new TreeItem<>("Terminals");
        TreeItem <String> nonTerminals = new TreeItem<>("Non Terminals");
        TreeItem <String> prodRules = new TreeItem<>("Production Rules");

        root.getChildren().addAll(terminals, nonTerminals, prodRules);

        for(Terminal t : currG.getTerminals()) {
            TreeItem <String> node = new TreeItem<>(t.toString());
            terminals.getChildren().add(node);
        }

        for(NonTerminal nt : currG.getNterminals()) {
            TreeItem <String> node = new TreeItem<>(nt.toString());
            nonTerminals.getChildren().add(node);
        }

        for(ProductionRule pr : currG.getPrules()) {
            TreeItem <String> node = new TreeItem<>(pr.toString());
            prodRules.getChildren().add(node);
        }

        terminals.setExpanded(true);
        nonTerminals.setExpanded(true);
        prodRules.setExpanded(true);

        tableGrammar.setShowRoot(false);
        tableGrammar.setRoot(root);
    }

    //fix button clicked, download javadoc of Java FX 8
    public void menuRemoveGeneralLeftRecursion() {
        if(currG == null) {
            ErrorBox.display("The Grammar is not loaded");
            return;
        }

        currG = Algorithms.removeGeneralLRecursion(currG);
        refreshGrammarTable();
    }

    public void menuRemoveSimpleProductions() {
        if(currG == null) {
            ErrorBox.display("The Grammar is not loaded");
            return;
        }

        currG = Algorithms.delete_simple_productions(currG);
        refreshGrammarTable();
    }

    public void menuRemoveEProductions() {
        if(currG == null) {
            ErrorBox.display("The Grammar is not loaded");
            return;
        }

        currG = Algorithms.removeEProductions(currG);
        refreshGrammarTable();
    }

    public void menuRemoveUselessNonTerm() {
        if(currG == null) {
            ErrorBox.display("The Grammar is not loaded");
            return;
        }

        currG = Algorithms.algorithmA(currG);
        refreshGrammarTable();
    }

    public void menuRemoveUnreachableSymbols() {
        if(currG == null) {
            ErrorBox.display("The Grammar is not loaded");
            return;
        }

        currG = Algorithms.algorithmB(currG);
        refreshGrammarTable();
    }

    private void updateTable(String[][] arr) {
        tableParsers.getColumns().clear();

        ObservableList<String[]> data = FXCollections.observableArrayList();
        data.addAll(Arrays.asList(arr));
        data.remove(0);//remove titles from data

        for (int i = 0; i < arr[0].length; i++) {
            TableColumn tc = new TableColumn(arr[0][i]);
            final int colNo = i;

            tc.setCellValueFactory(new Callback<CellDataFeatures<String[], String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(CellDataFeatures<String[], String> p) {
                    return new SimpleStringProperty((p.getValue()[colNo]));
                }
            });

            tc.setPrefWidth(90);
            tableParsers.getColumns().add(tc);
        }

        tableParsers.setItems(data);
    }

    public void menuApplyLLParser() {
        if(currG == null) {
            ErrorBox.display("The Grammar is not loaded");
            return;
        }

        String word = txtFieldExpresion.getText();

        LL llParser = new LL(currG);

        try {
            llParser.createTable();
        } catch (Exception e) {
            ErrorBox.display(e.getMessage());
            return;
        }

        String[][] arr = llParser.getTableTo2dArray();
        updateTable(arr);

        llParser.printTable(); //debugging

        List <ProductionRule> derivations = null;

        try {
            derivations = llParser.analyse(word);
        } catch(Exception e) {
            ErrorBox.display(e.getMessage());
            return;
        }

        StringBuilder prodRules = new StringBuilder();

        for(ProductionRule pr : derivations) {
            prodRules.append(pr.toString());
            prodRules.append("\n");
        }

        txtAreaDerivations.setText(prodRules.toString());
    }

    public void menuApplyCLRParser() {
        if(currG == null) {
            ErrorBox.display("The Grammar is not loaded");
            return;
        }

        String word = txtFieldExpresion.getText();

        CLR clr = new CLR(currG);

        try {
            clr.createTables();
        } catch (CLRParserException e) {
            ErrorBox.display(e.getMessage());
            return;
        }

        String[][] arr = clr.getTablesTo2dArray();
        updateTable(arr);

        clr.printTables(); //debugging

        List <ProductionRule> derivations = null;

        try {
            derivations = clr.analyse(word);
        } catch (AnalyseLRException e) {
            ErrorBox.display(e.getMessage());
            return;
        }

        StringBuilder prodRules = new StringBuilder();

        for(ProductionRule pr : derivations) {
            prodRules.append(pr.toString());
            prodRules.append("\n");
        }

        txtAreaDerivations.setText(prodRules.toString());
    }

    public void menuApplyLALRParser() {
        if(currG == null) {
            ErrorBox.display("The Grammar is not loaded");
            return;
        }

        String word = txtFieldExpresion.getText();

        LALR lalr = new LALR(currG);

        try {
            lalr.createTables();
        } catch (LALRParserException e) {
            ErrorBox.display(e.getMessage());
            return;
        }

        String[][] arr = lalr.getTablesTo2dArray();
        updateTable(arr);

        lalr.printTables(); //debugging

        List <ProductionRule> derivations = null;

        try {
            derivations = lalr.analyse(word);
        } catch (AnalyseLRException e) {
            ErrorBox.display(e.getMessage());
            return;
        }

        StringBuilder prodRules = new StringBuilder();

        for(ProductionRule pr : derivations) {
            prodRules.append(pr.toString());
            prodRules.append("\n");
        }

        txtAreaDerivations.setText(prodRules.toString());
    }

    public void menuHowToUseGrammar() {
        InfoBox.display("Grammar View Help", "This part of the app handles grammar operations. The format to enter the grammar is the following:\n" +
                "a non terminal must start with a uppercase letter, else is a terminal, you should not add epsilon or dollar sign as terminals\n" +
                "they are added by default! Each production rule should be on a separate line.");
    }

    public void menuHowToUseParsers() {
        InfoBox.display("Parser View Help", "This is the parser view, this part will analyse a given word and output the tables of the applied parser.");
    }

    public void handleAddGrammar() {
        parseGrammar();

        if(currG == null) //there was some error and grammar is not valid
            return;

        refreshGrammarTable();
    }

    private List <String> tokenizeString(String in, String delim) {
        StringTokenizer tk = new StringTokenizer(in, delim);
        List <String> lst = new LinkedList<>();

        while(tk.hasMoreTokens()) {
            lst.add(tk.nextToken());
        }

        return lst;
    }

    //also updates current grammar
    private void parseGrammar() {
        String inputTerminals = txtFieldTerminals.getText();
        String inputNonTerminals = txtFieldNonTerminals.getText();
        String inputProdRules = txtAreaProductionRules.getText();

        try {
            currG = GLC.loadGrammar(inputTerminals, inputNonTerminals, inputProdRules);
        } catch(LoadingGrammarException e) {
            ErrorBox.display(e.getMessage());
            return;
        }

        System.out.println(currG); //debugging
    }
}
