package sagan.blog.support;

import sagan.blog.Post;
import sagan.blog.PostBuilder;
import sagan.blog.PostNotFoundException;
import sagan.search.support.JestSearchService;
import sagan.support.DateFactory;
import sagan.support.nav.PageableFactory;
import sagan.team.MemberProfile;
import saganx.AbstractIntegrationTests;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsIn.isIn;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.initMocks;
import static sagan.blog.PostCategory.*;

public class BlogService_QueryTests extends AbstractIntegrationTests {

    private static final Pageable FIRST_TEN_POSTS = PageableFactory.forLists(1);

    @Mock
    private DateFactory dateFactory;

    @Mock
    private JestSearchService searchService;

    @Mock
    private PostFormAdapter postFormAdapter;

    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Autowired
    private PostRepository postRepository;

    private BlogService service;

    private Date yesterday;
    private Date today;
    private Date tomorrow;

    private Post published;
    private Post scheduled;
    private Post draft;

    private int px = 0;

    @Before
    public void setup() throws Exception {
        initMocks(this);
        service = new BlogService(postRepository, postFormAdapter, dateFactory, searchService);
        setupDates();
        setupFixtureData();
    }

    private void setupDates() throws Exception {
        yesterday = new SimpleDateFormat("yyyy-MM-dd").parse("2013-06-13");
        today = new SimpleDateFormat("yyyy-MM-dd").parse("2013-06-14");
        tomorrow = new SimpleDateFormat("yyyy-MM-dd").parse("2013-06-15");

        given(dateFactory.now()).willReturn(today);
    }

    private void setupFixtureData() throws Exception {
        assertThat(postRepository.findAll().size(), equalTo(0));

        scheduled = PostBuilder.post().category(ENGINEERING).title("scheduled").publishAt(tomorrow).build();
        postRepository.save(scheduled);

        published = PostBuilder.post().category(ENGINEERING).title("published").publishAt(yesterday).build();
        postRepository.save(published);

        draft = PostBuilder.post().title("draft").draft().build();
        postRepository.save(draft);
    }

    @Test
    public void postIsRetrievableById() {
        assertThat(service.getPost(published.getId()), equalTo(published));
    }

    @Test
    public void postIsRetrievableByTitleAndCreatedDate() {
        assertThat(service.getPost(published.getTitle(), published.getCreatedAt()), equalTo(published));
    }

    @Test
    public void publishedPostIsRetrievable() {
        assertThat(service.getPublishedPost(published.getPublicSlug()), equalTo(published));
    }

    @Test
    public void getPublishedDoesNotFindDrafts() {
        Post draft = PostBuilder.post().title(uniqueTitle()).draft().build();
        postRepository.save(draft);
        expected.expect(PostNotFoundException.class);
        service.getPublishedPost(draft.getPublicSlug());
    }

    @Test
    public void getPublishedDoesNotFindScheduledPost() throws ParseException {
        expected.expect(PostNotFoundException.class);
        service.getPublishedPost(scheduled.getPublicSlug());
    }

    @Test
    public void nonExistentPostThrowsException() {
        expected.expect(PostNotFoundException.class);
        service.getPost(999L);
    }

    @Test
    public void getPublishedPostsWithPagination() {
        Page<Post> publishedPosts = service.getPublishedPosts(FIRST_TEN_POSTS);

        assertThat(publishedPosts.getContent(), contains(published));
    }

    @Test
    public void getAllPublishedPosts() {
        Post anotherPost = PostBuilder.post().publishAt(yesterday).title("another post").build();
        postRepository.save(anotherPost);
        List<Post> publishedPosts = service.getAllPublishedPosts();

        assertThat(publishedPosts, containsInAnyOrder(published, anotherPost));
    }

    @Test
    public void getPublishedPostsExcludesDrafts() throws ParseException {
        List<Post> publishedPosts = service.getPublishedPosts(FIRST_TEN_POSTS).getContent();

        assertThat(draft, not(isIn(publishedPosts)));
    }

    @Test
    public void getPublishedPostsExcludesScheduledPosts() throws ParseException {
        List<Post> publishedPosts = service.getPublishedPosts(FIRST_TEN_POSTS).getContent();

        assertThat(scheduled, not(isIn(publishedPosts)));
    }

    @Test
    public void getScheduledPosts() throws ParseException {
        List<Post> scheduledPosts = service.getScheduledPosts(FIRST_TEN_POSTS).getContent();

        assertThat(scheduledPosts, contains(scheduled));
    }

    @Test
    public void getScheduledPostsExcludesScheduledDrafts() throws ParseException {
        Post scheduledDraft = PostBuilder.post().title(uniqueTitle()).draft().publishAt(tomorrow).build();
        postRepository.save(scheduledDraft);

        List<Post> scheduledPosts = service.getScheduledPosts(FIRST_TEN_POSTS).getContent();

        assertThat(scheduledDraft, not(isIn(scheduledPosts)));
    }

    @Test
    public void getScheduledPostsExcludesPublishedPosts() throws ParseException {
        List<Post> scheduledPosts = service.getScheduledPosts(FIRST_TEN_POSTS).getContent();

        assertThat(published, not(isIn(scheduledPosts)));
    }

    @Test
    public void listPostsForCategory() {
        Post newsPost = PostBuilder.post().title(uniqueTitle()).publishAt(yesterday).category(NEWS_AND_EVENTS).build();
        postRepository.save(newsPost);

        Page<Post> publishedPosts = service.getPublishedPosts(NEWS_AND_EVENTS, FIRST_TEN_POSTS);

        assertThat(publishedPosts.getContent(), contains(newsPost));
    }

    @Test
    public void listPostsForCategoryExcludesScheduledPosts() throws ParseException {
        Post publishedRelease = PostBuilder.post().title(uniqueTitle()).publishAt(yesterday).category(RELEASES).build();
        postRepository.save(publishedRelease);
        Post scheduledRelease = PostBuilder.post().title(uniqueTitle()).publishAt(tomorrow).category(RELEASES).build();
        postRepository.save(scheduledRelease);

        List<Post> releases = service.getPublishedPosts(RELEASES, FIRST_TEN_POSTS).getContent();

        assertThat(scheduledRelease, not(isIn(releases)));
    }

    @Test
    public void paginationInfoBasedOnCurrentPageAndTotalPosts() {
        postRepository.deleteAll();

        List<Post> posts = new ArrayList<>();
        long itemCount = 11;
        for (int i = 0; i < itemCount; ++i) {
            posts.add(PostBuilder.post().title(uniqueTitle()).build());
        }
        postRepository.save(posts);

        PageRequest pageRequest = new PageRequest(0, 10);
        Page<Post> result = service.getAllPosts(pageRequest);
        assertThat(result.getTotalElements(), is(itemCount));
    }

    @Test
    public void listBroadcasts() {
        Post broadcast = PostBuilder.post().isBroadcast().title(uniqueTitle()).publishAt(yesterday).build();
        postRepository.save(broadcast);

        List<Post> broadcasts = service.getPublishedBroadcastPosts(FIRST_TEN_POSTS).getContent();

        assertThat(broadcasts, contains(broadcast));
    }

    @Test
    public void listBroadcastsExcludesScheduledPosts() throws ParseException {
        Post publishedBroadcast = PostBuilder.post().title(uniqueTitle()).isBroadcast().publishAt(yesterday).build();
        postRepository.save(publishedBroadcast);
        Post scheduledBroadcast = PostBuilder.post().title(uniqueTitle()).isBroadcast().publishAt(tomorrow).build();
        postRepository.save(scheduledBroadcast);

        List<Post> broadcasts = service.getPublishedBroadcastPosts(FIRST_TEN_POSTS).getContent();

        assertThat(scheduledBroadcast, not(isIn(broadcasts)));
    }

    @Test
    public void allPosts() {
        Page<Post> allPosts = service.getAllPosts(FIRST_TEN_POSTS);

        assertThat(allPosts.getContent(), containsInAnyOrder(published, scheduled, draft));
    }

    @Test
    public void getPublishedPostsForAuthorOnlyShowsAuthorsPosts() {
        MemberProfile profile = new MemberProfile();
        profile.setUsername("myauthor");

        Post post = PostBuilder.post().author(profile).title(uniqueTitle()).publishAt(yesterday).build();

        postRepository.save(post);
        postRepository.save(PostBuilder.post().title(uniqueTitle()).build());

        Page<Post> publishedPosts = service.getPublishedPostsForMember(profile, FIRST_TEN_POSTS);
        assertThat(publishedPosts.getContent(), contains(post));
    }

    @Test
    public void getPublishedPostsForAuthorOnlyShowsPublishedPosts() {
        MemberProfile profile = new MemberProfile();
        profile.setUsername("myauthor");

        Post post = PostBuilder.post().author(profile).title(uniqueTitle()).publishAt(yesterday).build();

        postRepository.save(post);
        postRepository.save(PostBuilder.post().title(uniqueTitle()).author(profile).draft().build());

        Page<Post> publishedPosts = service.getPublishedPostsForMember(profile, FIRST_TEN_POSTS);
        assertThat(publishedPosts.getContent(), contains(post));
    }

    private String uniqueTitle() {
        return "post" + ++px;
    }

}
