package com.example.my_notes.Utils.ArrayListComparator;

import com.example.my_notes.Model.Note;

import java.util.Comparator;

public class TitleSorter extends Sorter {

    @Override
    public int compare(Note o1, Note o2) {
        return o1.getTitle().compareTo(o2.getTitle());
    }
}
