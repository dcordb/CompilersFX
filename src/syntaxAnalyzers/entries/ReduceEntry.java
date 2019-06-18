package syntaxAnalyzers.entries;

public class ReduceEntry extends TableEntry {
    private int idPr;

    public ReduceEntry(String text, int idPr) {
        super(text);
        this.idPr = idPr;
    }

    public int getIdPr() {
        return idPr;
    }

    public void setIdPr(int idPr) {
        this.idPr = idPr;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;

        if(getClass() != obj.getClass())
            return false;

        final ReduceEntry other = (ReduceEntry) obj;
        return text.equals(other.text) && idPr == other.idPr;
    }

    @Override
    public int hashCode() {
        return 1;
    }
}
