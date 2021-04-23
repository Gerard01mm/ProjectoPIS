package com.example.my_notes.ArrayListComparator;

import java.util.Comparator;

import Notes.Note;

public class DateSorter implements Comparator<Note>
{
    @Override
    public int compare(Note o1, Note o2) {
        return o2.getCreation_date().compareTo(o1.getCreation_date());
    }
}