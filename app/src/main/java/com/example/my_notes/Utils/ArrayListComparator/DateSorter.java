package com.example.my_notes.Utils.ArrayListComparator;

import java.util.Comparator;

import com.example.my_notes.Model.Note;

public class DateSorter implements Comparator<Note>
{
    @Override
    public int compare(Note o1, Note o2) {
        return o1.getCreation_date().compareTo(o2.getCreation_date());
    }
}