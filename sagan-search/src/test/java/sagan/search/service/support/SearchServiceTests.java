package sagan.search.service.support;

import sagan.search.service.SearchException;
import sagan.search.support.SearchEntryBuilder;
import sagan.search.types.SearchEntry;
import sagan.search.types.SearchType;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

import org.springframework.data.domain.Pageable;

import io.searchbox.action.Action;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Index;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

public class SearchServiceTests {

    private JestClient jestClient = mock(JestClient.class);
    private ElasticSearchService searchService = new ElasticSearchService(jestClient, mock(SearchResultParser.class), new ObjectMapper());
    private SearchEntry entry;

    @Before
    public void setUp() throws Exception {
        entry = SearchEntryBuilder.entry().path("/some/path")
                .title("This week in Spring").rawContent("raw content")
                .summary("Html summary").publishAt("2013-01-01 10:00")
                .blog();
    }

    @SuppressWarnings("unchecked")
    public void throwsException() throws Exception {
        given(jestClient.execute(any(GenericAction.class))).willThrow(Exception.class);
    }

    @Test(expected = SearchException.class)
    public void saveToIndexExceptionHandling() throws Exception {
        throwsException();
        searchService.saveToIndex(entry);
    }

    @Test(expected = SearchException.class)
    public void searchExceptionHandling() throws Exception {
        throwsException();
        searchService.search("foo", mock(Pageable.class), Collections.<String> emptyList());
    }

    @Test(expected = SearchException.class)
    public void removeFromIndexExceptionHandling() throws Exception {
        throwsException();
        searchService.removeFromIndex(entry);
    }

    @Test
    public void usesTheSearchEntriesType() throws Exception {
        given(jestClient.execute(any(GenericAction.class))).willReturn(mock(JestResult.class));
        searchService.saveToIndex(entry);
        verify(jestClient).execute(argThat(new ArgumentMatcher<GenericAction>() {
            @Override
            public boolean matches(Object item) {
                Index action = (Index) item;
                return action.getType().equals(SearchType.BLOG_POST.toString());
            }
        }));
    }

    @Test
    public void handlesNullFilters() throws Exception {
        given(jestClient.execute(any(GenericAction.class))).willReturn(mock(JestResult.class));
        searchService.search("foo", mock(Pageable.class), null);
    }
    
    private interface GenericAction extends Action<JestResult> {} 
}
