package com.mysterionnh.util;

import java.util.LinkedList;
import java.util.List;

public class Collections {
    public static List toList(Object[] objects) {
        LinkedList list = new LinkedList();
        for (Object o : objects) {
            list.add(o);
        }
        return list;
    }
}
