package com.skyle.foodbuildings;

import java.util.ArrayList;
import java.util.List;

public class RecentSearchesData {
    public static List<String> data = new ArrayList<String>();
    public static String[] getArray() {
        String[] a = new String[RecentSearchesData.data.size()];
        for (int i=0; i<RecentSearchesData.data.size(); ++i)
            a[i] = RecentSearchesData.data.get(i);
        return a;
    }
}
