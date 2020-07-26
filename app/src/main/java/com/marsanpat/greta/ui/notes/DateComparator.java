package com.marsanpat.greta.ui.notes;

import com.marsanpat.greta.Database.Element;

import java.util.Comparator;

public class DateComparator implements Comparator<Element> {
    @Override
    //Orders from oldest to newest
    public int compare(Element o1, Element o2) {
        return o1.getLastModification().compareTo(o2.getLastModification());
    }
}
