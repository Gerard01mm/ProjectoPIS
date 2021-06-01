package com.example.my_notes.Utils.ArrayListComparator;

import com.example.my_notes.Model.Note;

public class ModifyDateSorter extends Sorter {

    @Override
    public int compare(Note o1, Note o2) {
        return o2.getModify_date().compareTo(o1.getModify_date());
    }
}
