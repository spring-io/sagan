package sagan.search.service.support;

import io.searchbox.core.Search;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.facet.terms.TermsFacet;
import org.elasticsearch.search.facet.terms.TermsFacetBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.Pageable;
import sagan.search.types.SearchType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class SaganQueryBuilders {

    private static final String TODAY = "now/d";
    private static final String BOOSTED_TITLE_FIELD = "title^3";
    private static final String RAW_CONTENT_FIELD = "rawContent";
    private static final String AUTHOR_FIELD = "author";


    static Search.Builder fullTextSearch(String queryTerm, Pageable pageable, List<String> filters) {

        BoolQueryBuilder query = QueryBuilders.boolQuery()
                .must(matchTitleContentAndAuthor(queryTerm))
                .should(matchMarkedAsCurrent())
                .should(matchProjectPages())
                .should(matchPhrase(queryTerm).boost(3f));

        String search = buildSearch(query, filters, pageable);

        return new Search.Builder(search);
    }

    private static MultiMatchQueryBuilder matchTitleContentAndAuthor(String queryTerm) {
        return QueryBuilders
                .multiMatchQuery(queryTerm, BOOSTED_TITLE_FIELD, RAW_CONTENT_FIELD, AUTHOR_FIELD)
                .fuzziness(Fuzziness.ONE)
                .minimumShouldMatch("30%");
    }

    private static TermQueryBuilder matchMarkedAsCurrent() {
        return QueryBuilders.termQuery("current", true);
    }

    private static TermQueryBuilder matchProjectPages() {
        return QueryBuilders.termQuery("_type", SearchType.PROJECT_PAGE.toString());
    }

    private static MatchQueryBuilder matchPhrase(String query) {
        return QueryBuilders.matchPhraseQuery("title", query).slop(1);
    }

    static Search.Builder forEmptyQuery(Pageable pageable, List<String> filters) {
        QueryBuilder query = QueryBuilders.boolQuery().should(QueryBuilders.matchAllQuery());

        String search = buildSearch(query, filters, pageable);
        return new Search.Builder(search);
    }

    private static String buildSearch(QueryBuilder query, List<String> filters, Pageable pageable) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        FilterBuilder filterBuilder = filterUnpublishedResults();
        QueryBuilder filteredQuery = QueryBuilders.filteredQuery(query, filterBuilder);

        addQuery(filteredQuery, searchSourceBuilder);
        addFacetPathsResult(searchSourceBuilder);
        addHighlights(searchSourceBuilder);
        sort(searchSourceBuilder);
        addPagination(pageable, searchSourceBuilder);
        searchSourceBuilder.postFilter(buildFilterFacets(filters));

        return searchSourceBuilder.toString();
    }

    private static void addPagination(Pageable pageable, SearchSourceBuilder searchSourceBuilder) {
        searchSourceBuilder.from(pageable.getOffset());
        searchSourceBuilder.size(pageable.getPageSize());
    }

    private static SearchSourceBuilder addQuery(QueryBuilder query, SearchSourceBuilder searchSourceBuilder) {
        return searchSourceBuilder.query(query);
    }

    private static OrFilterBuilder filterUnpublishedResults() {
        return new OrFilterBuilder()
                .add(new RangeFilterBuilder("publishAt").lte(TODAY))
                .add(new NotFilterBuilder(new TypeFilterBuilder(SearchType.BLOG_POST.toString())));
    }

    private static OrFilterBuilder buildFilterFacets(List<String> filters) {
        OrFilterBuilder outermostFilter = new OrFilterBuilder();
        if (filters != null && !filters.isEmpty()) {

            Map<String, List<String>> splitFilters = splitFilters(filters);
            List<String> projects = splitFilters.get("projects");
            List<String> apiRef = splitFilters.get("apiRef");
            List<String> otherFacetPaths = splitFilters.get("others");

            AndFilterBuilder projectApiRefAnded = new AndFilterBuilder();
            if (apiRef.size() > 0) {
                projectApiRefAnded.add(new TermsFilterBuilder("facetPaths", apiRef).execution("or"));
            }

            if (projects.size() > 0) {
                projectApiRefAnded.add(new TermsFilterBuilder("facetPaths", projects).execution("or"));
            }

            outermostFilter.add(projectApiRefAnded);
            if (otherFacetPaths.size() > 0) {
                outermostFilter.add(new TermsFilterBuilder("facetPaths", otherFacetPaths).execution("or"));
            }
        }
        return outermostFilter;

    }

    private static Map<String, List<String>> splitFilters(List<String> filters) {
        ArrayList<String> projects = new ArrayList<>();
        ArrayList<String> apiRef = new ArrayList<>();
        ArrayList<String> others = new ArrayList<>();

        for (String filter : filters) {
            if (filter.startsWith("Projects")) {
                if (filter.equals("Projects/Api") || filter.equals("Projects/Reference")) {
                    apiRef.add(filter);
                }
                else {
                    projects.add(filter);
                }
            }
            else {
                others.add(filter);
            }
        }

        HashMap<String, List<String>> splitFilters = new HashMap<>();
        splitFilters.put("projects", projects);
        splitFilters.put("apiRef", apiRef);
        splitFilters.put("others", others);
        return splitFilters;
    }

    private static void addFacetPathsResult(SearchSourceBuilder searchSourceBuilder) {
        TermsFacetBuilder facetBuilder = new TermsFacetBuilder("facet_paths_result")
                .field("facetPaths")
                .order(TermsFacet.ComparatorType.TERM).size(100000);

        searchSourceBuilder.facet(facetBuilder);
    }

    private static void addHighlights(SearchSourceBuilder searchSourceBuilder) {
        HighlightBuilder highlightBuilder =
                new HighlightBuilder().order("score").requireFieldMatch(false).field("rawContent", 300, 1);

        searchSourceBuilder.highlight(highlightBuilder);
    }

    private static void sort(SearchSourceBuilder searchSourceBuilder) {
        searchSourceBuilder
                .sort(SortBuilders.scoreSort().order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("publishAt").order(SortOrder.DESC));
    }

    static String wrapQuery(String query) {
        return new StringBuilder().append("{\"query\":\n").append(query).append("\n}").toString();
    }

    static FilteredQueryBuilder matchUnsupportedProjectEntries(String projectId, List<String> supportedVersions) {
        QueryBuilder query = QueryBuilders.matchAllQuery();

        OrFilterBuilder supportedVersionsFilter = matchSupportedVersions(supportedVersions);
        NotFilterBuilder notSupportedVersionFilter = new NotFilterBuilder(supportedVersionsFilter);

        TermFilterBuilder projectFilter = new TermFilterBuilder("projectId", projectId);

        AndFilterBuilder filter = new AndFilterBuilder(projectFilter, notSupportedVersionFilter);

        return QueryBuilders.filteredQuery(query, filter);
    }

    private static OrFilterBuilder matchSupportedVersions(List<String> supportedVersions) {
        OrFilterBuilder orFilter = new OrFilterBuilder();
        for (String supportedVersion : supportedVersions) {
            orFilter.add(new TermFilterBuilder("version", supportedVersion));
        }
        return orFilter;
    }

}
