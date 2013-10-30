package sagan.guides.search;

import sagan.guides.Tutorial;
import sagan.guides.support.Tutorials;
import sagan.search.service.SearchService;
import sagan.util.index.Indexer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TutorialIndexer implements Indexer<Tutorial> {

    private TutorialMapper mapper = new TutorialMapper();

    private final SearchService searchService;
    private final Tutorials tutorials;

    @Autowired
    public TutorialIndexer(SearchService searchService, Tutorials tutorials) {
        this.searchService = searchService;
        this.tutorials = tutorials;
    }

    @Override
    public Iterable<Tutorial> indexableItems() {
        return tutorials.findAll();
    }

    @Override
    public void indexItem(Tutorial tutorial) {
        searchService.saveToIndex(mapper.map(tutorial));
    }

    @Override
    public String counterName() {
        return "tutorials";
    }

    @Override
    public String getId(Tutorial tutorial) {
        return tutorial.getGuideId();
    }
}
