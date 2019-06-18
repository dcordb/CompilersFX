package syntaxAnalyzers.entries;

//singleton class
public class OkEntry extends TableEntry {
    private static OkEntry obj;

    private OkEntry(String text) {
        super(text);
    }

    public static OkEntry getInstance() {
        if(obj == null)
            obj = new OkEntry("OK");

        return obj;
    }
}
