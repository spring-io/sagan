package sagan.search.support;

import sagan.search.SearchException;
import sagan.search.service.SearchFacet;
import sagan.search.service.SearchResult;
import sagan.search.service.SearchResults;
import sagan.search.service.SearchService;
import sagan.support.nav.PageableFactory;
import sagan.support.nav.PaginationInfo;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Controller that handles search requests at /search.
 */
@Controller
@RequestMapping("/search")
class SearchController {

    private final SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @RequestMapping(method = { GET, HEAD, POST })
    public String search(SearchForm searchForm, @RequestParam(defaultValue = "1") int page, Model model) {
        Pageable pageable = PageableFactory.forSearch(page);
        SearchResults searchResults = searchService.search(searchForm.getQ(), pageable, searchForm.getFilters());
        Page<SearchResult> entries = searchResults.getPage();
        model.addAttribute("totalResultsCount", entries.getTotalElements());
        model.addAttribute("results", entries.getContent());
        model.addAttribute("paginationInfo", new PaginationInfo(entries));
        SearchFacet root = new SpringFacetsBuilder(searchResults.getFacets()).build();
        model.addAttribute("rootFacet", root);
        model.addAttribute("searchForm", searchForm);
        return "search/results";
    }

    @ExceptionHandler(SearchException.class)
    public String handleSearchException(SearchException exception, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return "search/searcherror";
    }
}
