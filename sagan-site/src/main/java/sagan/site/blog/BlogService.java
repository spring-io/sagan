package sagan.site.blog;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import sagan.blog.Post;
import sagan.blog.PostCategory;
import sagan.blog.PostMovedException;
import sagan.blog.PostNotFoundException;
import sagan.search.service.SearchService;
import sagan.support.DateFactory;
import sagan.team.MemberProfile;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sagan.blog.Post;
import sagan.blog.PostCategory;
import sagan.blog.PostMovedException;
import sagan.blog.PostNotFoundException;
import sagan.search.support.JestSearchService;
import sagan.support.DateFactory;
import sagan.team.MemberProfile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * Service providing high-level, selectively cached data access and other {@link Post}
 * -related operations.
 */
@Service
public class BlogService {

    private static final Log logger = LogFactory.getLog(BlogService.class);

    private final PostFormAdapter postFormAdapter;
    private final PostRepository postRepository;
    private final SearchService searchService;
    private final DateFactory dateFactory;
    private final PostSearchEntryMapper mapper = new PostSearchEntryMapper();

    @Value("${disqus_shortname}")
    private String disqusShortname;

    @Autowired
    public BlogService(PostRepository postRepository, PostFormAdapter postFormAdapter, DateFactory dateFactory,
                       SearchService searchService) {
        this.postRepository = postRepository;
        this.postFormAdapter = postFormAdapter;
        this.dateFactory = dateFactory;
        this.searchService = searchService;
    }

    // Property methods

    public String getDisqusShortname() {
        return disqusShortname;
    }

    // Query methods

    public Post getPost(Long postId) {
        Post post = postRepository.findOne(postId);
        if (post == null) {
            throw new PostNotFoundException(postId);
        }
        return post;
    }

    public Post getPost(String title, Date createdAt) {
        return postRepository.findByTitleAndCreatedAt(title, createdAt);
    }

    public Page<Post> getDraftPosts(Pageable pageRequest) {
        return postRepository.findByDraftTrue(pageRequest);
    }

    public Page<Post> getScheduledPosts(Pageable pageRequest) {
        return postRepository.findByDraftFalseAndPublishAtAfter(dateFactory.now(), pageRequest);
    }

    public Page<Post> getPublishedPosts(Pageable pageRequest) {
        return postRepository.findByDraftFalseAndPublishAtBeforeOrderByPublishAtDesc(dateFactory.now(), pageRequest);
    }

    public Post getPublishedPost(String publicSlug) {
        Date now = dateFactory.now();
        Post post = postRepository.findByPublicSlugAndDraftFalseAndPublishAtBefore(publicSlug, now);
        if (post == null) {
            post = postRepository.findByPublicSlugAliasesInAndDraftFalseAndPublishAtBefore(
                    Collections.singleton(publicSlug), now);
            if (post != null) {
                throw new PostMovedException(post.getPublicSlug());
            }
            throw new PostNotFoundException(publicSlug);
        }
        return post;
    }

    public List<Post> getAllPublishedPosts() {
        return postRepository.findByDraftFalseAndPublishAtBeforeOrderByPublishAtDesc(dateFactory.now());
    }

    public Page<Post> getPublishedPostsByDate(int year, int month, int day, Pageable pageRequest) {
        return postRepository.findByDate(year, month, day, pageRequest);
    }

    public Page<Post> getPublishedPostsByDate(int year, int month, Pageable pageRequest) {
        return postRepository.findByDate(year, month, pageRequest);
    }

    public Page<Post> getPublishedPostsByDate(int year, Pageable pageRequest) {
        return postRepository.findByDate(year, pageRequest);
    }

    public Page<Post> getPublishedPosts(PostCategory category, Pageable pageRequest) {
        return postRepository.findByCategoryAndDraftFalseAndPublishAtBefore(category, dateFactory.now(), pageRequest);
    }

    public Page<Post> getPublishedBroadcastPosts(Pageable pageRequest) {
        return postRepository.findByBroadcastAndDraftFalseAndPublishAtBefore(true, dateFactory.now(), pageRequest);
    }

    public Page<Post> getPublishedPostsForMember(MemberProfile profile, Pageable pageRequest) {
        return postRepository.findByDraftFalseAndAuthorAndPublishAtBeforeOrderByPublishAtDesc(profile, dateFactory.now(), pageRequest);
    }

    public Page<Post> getAllPosts(Pageable pageRequest) {
        return postRepository.findAll(pageRequest);
    }

    public Post addPost(PostForm postForm, String username) {
        Post post = postFormAdapter.createPostFromPostForm(postForm, username);
        postRepository.save(post);
        saveToIndex(post);
        return post;
    }

    public void updatePost(Post post, PostForm postForm) {
        postFormAdapter.updatePostFromPostForm(post, postForm);
        postRepository.save(post);
        saveToIndex(post);
    }

    public void deletePost(Post post) {
        postRepository.delete(post);
    }

    private void saveToIndex(Post post) {
        if (post.isLiveOn(dateFactory.now())) {
            try {
                searchService.saveToIndex(mapper.map(post));
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    public void resummarizeAllPosts() {
        List<Post> posts = postRepository.findAll();
        for (Post post : posts) {
            postFormAdapter.summarize(post);
            postRepository.save(post);
        }
    }

    public Page<Post> refreshPosts(int page, int size) {
        PageRequest pageRequest = new PageRequest(page, size, Sort.Direction.DESC, "id");
        Page<Post> posts = postRepository.findAll(pageRequest);
        for (Post post : posts) {
            postFormAdapter.refreshPost(post);
            postRepository.save(post);
        }
        return posts;
    }
}
