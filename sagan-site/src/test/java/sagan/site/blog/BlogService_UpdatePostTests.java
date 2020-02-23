package sagan.site.blog;

import sagan.blog.Post;
import sagan.blog.PostBuilder;
import sagan.search.service.SearchService;
import sagan.search.types.SearchEntry;
import sagan.support.DateFactory;
import sagan.support.DateTestUtils;

import java.util.Date;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BlogService_UpdatePostTests {

    private BlogService service;
    private Post post;
    private Date publishAt = DateTestUtils.getDate("2013-07-01 12:00");
    private Date now = DateTestUtils.getDate("2013-07-01 13:00");

    @Mock
    private PostRepository postRepository;

    @Mock
    private DateFactory dateFactory;

    @Mock
    private SearchService searchService;

    @Mock
    private PostFormAdapter postFormAdapter;

    @Rule
    public ExpectedException expected = ExpectedException.none();

    private PostForm postForm;

    @Before
    public void setup() {
        given(dateFactory.now()).willReturn(now);

        service = new BlogService(postRepository, postFormAdapter, dateFactory, searchService);

        post = PostBuilder.post().id(123L).publishAt(publishAt).build();

        postForm = new PostForm(post);
        service.updatePost(post, postForm);
    }

    @Test
    public void postIsUpdated() {
        verify(postFormAdapter).updatePostFromPostForm(post, postForm);
    }

    @Test
    public void postIsPersisted() {
        verify(postRepository).save(post);
    }

    @Test
    public void updatingABlogPost_addsThatPostToTheSearchIndexIfPublished() {
        verify(searchService).saveToIndex((SearchEntry) anyObject());
    }

    @Test
    public void updatingABlogPost_doesNotSaveToSearchIndexIfNotLive() throws Exception {
        reset(searchService);
        long postId = 123L;
        Post post = PostBuilder.post().id(postId).draft().build();
        service.updatePost(post, new PostForm(post));
        verifyZeroInteractions(searchService);
    }

}
