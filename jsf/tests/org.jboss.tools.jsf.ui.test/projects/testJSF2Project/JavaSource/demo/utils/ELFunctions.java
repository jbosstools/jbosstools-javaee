package demo.utils;

import java.util.ArrayList;
import java.util.List;

public final class ELFunctions {

    private ELFunctions() {
       
    }
   
    /**
     * returns the model for lopping
     * 
     * @param size
     * @return
     */
    public static List getLoopModel(Integer size) {
        List list = new ArrayList();
        for(int i = 0; i <= size; i++)
            list.add(i);
        return list;
    }
    
    /**
     * converts string objects into an int value
     * 
     * @param size
     * @return
     */
    public static int doConvertToInteger(Object value) {
        return Integer.valueOf(value.toString());
    }
}
