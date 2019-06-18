package syntaxAnalyzers.utils;

public class State {
    private int id;

    public State(int id) { this.id = id; }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;

        if(getClass() != obj.getClass())
            return false;

        final State other = (State) obj;
        return id == other.getId();
    }

    @Override
    public int hashCode() {
        return 5;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return Integer.toString(id);
    }
}
