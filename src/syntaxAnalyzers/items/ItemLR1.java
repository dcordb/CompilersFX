package syntaxAnalyzers.items;

import structs.*;

import java.util.Objects;

public class ItemLR1 {
    private ItemLR0 item;
    private Terminal terminal;

    public ItemLR1(ItemLR0 item, Terminal terminal) {
        this.setItem(item);
        this.setTerminal(terminal);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;
        if (!getClass().equals(obj.getClass()))
            return false;

        final ItemLR1 other = (ItemLR1) obj;
        return item.equals(other.getItem()) && terminal.equals(other.getTerminal());
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 13 * hash + Objects.hashCode(this.item);
        hash = 13 * hash + Objects.hashCode(this.terminal);
        return hash;
    }

    public ItemLR0 getItem() {
        return item;
    }

    public void setItem(ItemLR0 item) {
        this.item = item;
    }

    public Terminal getTerminal() {
        return terminal;
    }

    public void setTerminal(Terminal terminal) {
        this.terminal = terminal;
    }

    @Override
    public String toString() {
        return "[ " + item.toString() + ", " + terminal.toString() + " ]";
    }
}
