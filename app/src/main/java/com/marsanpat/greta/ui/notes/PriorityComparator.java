package com.marsanpat.greta.ui.notes;

import com.marsanpat.greta.Database.Element;

import java.util.Comparator;

public class PriorityComparator implements Comparator<Element> {
    @Override
    public int compare(Element o1, Element o2) {
        if(o1.getPriority()<o2.getPriority()){
            return -1;
        }else if(o1.getPriority()>o2.getPriority()){
            return 1;
        }else{
            //We still get into account the date in case of tie
            return new DateComparator().compare(o1,o2);
        }
    }
}
