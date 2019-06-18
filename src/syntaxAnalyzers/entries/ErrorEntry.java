package syntaxAnalyzers.entries;

//singleton class
public class ErrorEntry extends TableEntry {
    private static ErrorEntry obj;

    private ErrorEntry(String text) {
        super(text);
    }

    public static ErrorEntry getInstance() {
        if(obj == null)
            obj = new ErrorEntry("E");

        return obj;
    }
}
