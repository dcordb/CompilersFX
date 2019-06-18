package syntaxAnalyzers.entries;

import syntaxAnalyzers.utils.State;

public class ShiftEntry extends TableEntry {
    private State state;

    public ShiftEntry(String text, State state) {
        super(text);
        this.state = state;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;

        if(getClass() != obj.getClass())
            return false;

        final ShiftEntry other = (ShiftEntry) obj;
        return text.equals(other.text) && state.equals(other.state);
    }

    @Override
    public int hashCode() {
        return 1;
    }
}
