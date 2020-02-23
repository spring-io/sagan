package sagan.search.service;

import sagan.search.types.SearchEntry;

public interface SearchEntryMapper<T> {

    <R extends SearchEntry> R map(T item);

}
