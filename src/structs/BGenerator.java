/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package structs;

/**
 *
 * @author Hayder
 */
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Represents a binary string generator
 * @author Hayder
 */
public class BGenerator {
    /**
     * Bit field
     */
    protected boolean[] bField;
    /**
     * Bit size
     */
    protected final int size;
    /**
     * Id --> bit position
     */
    protected Map<Integer, Integer> map;

    public BGenerator(List<Integer> ids) {
        size = ids.size();
        bField = new boolean[size];
        map = new HashMap<>();
        for (int i = 0; i < size; i++) {
            map.put(ids.get(i), i);
        }
    }
    /**
     * Adds 1 to the current binary string
     */
    public void bump() {
        for (int i = size - 1; i >= 0; i--) {
            if (!bField[i]) {
                bField[i] = true;
                break;
            }
            bField[i] = false;
        }
    }
    
    public boolean contains(int id){
        return map.containsKey(id);
    }
    /**
     * Test whether the i-th bit is on
     * @param id The bit's id
     * @return True if and only if id--> bit position is on, False otherwise
     */
    public boolean isOn(int id) {
        if (!map.containsKey(id)) {
            return false;
        }
        return bField[map.get(id)];
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < size; i++) {
            out.append(bField[i] ? "1" : 0);
        }
        out.reverse();
        return out.toString();
    }
}
