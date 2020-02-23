package sagan.search.service.support;

import sagan.search.service.SearchResult;
import sagan.search.service.SearchResults;
import sagan.search.service.support.SearchResultParser;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import io.searchbox.client.JestResult;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class SearchResultParser_WithoutHighlightTests {

    private static String RESULT_STRING =
            "{\n"
                    +
                    "  \"took\": 8,\n"
                    +
                    "  \"timed_out\": false,\n"
                    +
                    "  \"_shards\": {\n"
                    +
                    "    \"total\": 5,\n"
                    +
                    "    \"successful\": 5,\n"
                    +
                    "    \"failed\": 0\n"
                    +
                    "  },\n"
                    +
                    "  \"hits\": {\n"
                    +
                    "    \"total\": 237,\n"
                    +
                    "    \"max_score\": 6.519557,\n"
                    +
                    "    \"hits\": [{\n"
                    +
                    "      \"_index\": \"site\",\n"
                    +
                    "      \"_type\": \"site\",\n"
                    +
                    "      \"_id\": \"aHR0cDovL3N0YXRpYy5zcHJpbmdzb3VyY2Uub3JnL3NwcmluZy9kb2NzLzMuMS40LnJlbGVhc2UvamF2YWRvYy1hcGkvb3JnL3NwcmluZ2ZyYW1ld29yay9jb250ZXh0L2FwcGxpY2F0aW9uY29udGV4dC5odG1s\",\n"
                    +
                    "      \"_score\": 6.519557,\n"
                    +
                    "      \"_source\": {\n"
                    +
                    "        \"path\": \"http://docs.spring.io/spring/docs/3.1.4.RELEASE/javadoc-api/org/springframework/context/ApplicationContext.html\",\n"
                    +
                    "        \"summary\": \"org.springframework.context Interface ApplicationContext All Superinterfaces: ApplicationEventPublisher, BeanFactory, EnvironmentCapable, HierarchicalBeanFactory, ListableBeanFactory, MessageSource, ResourceLoader, ResourcePatternResolver All Known Subinterfaces: ConfigurableApplicationContext, ConfigurablePortletApplicationContext, ConfigurableWebApplicationContext, WebApplicationContext All Known Implementing Classes: AbstractApplicationContext, AbstractRefreshableApplicationContext, AbstractR\",\n"
                    +
                    "        \"rawContent\": \"org.springframework.context Interface ApplicationContext All Superinterfaces: ApplicationEventPublisher, BeanFactory, EnvironmentCapable, HierarchicalBeanFactory, ListableBeanFactory, MessageSource, ResourceLoader, ResourcePatternResolver All Known Subinterfaces: ConfigurableApplicationContext, ConfigurablePortletApplicationContext, ConfigurableWebApplicationContext, WebApplicationContext All Known Implementing Classes: AbstractApplicationContext, AbstractRefreshableApplicationContext, AbstractRefreshableConfigApplicationContext, AbstractRefreshablePortletApplicationContext, AbstractRefreshableWebApplicationContext, AbstractXmlApplicationContext, AnnotationConfigApplicationContext, AnnotationConfigWebApplicationContext, ClassPathXmlApplicationContext, FileSystemXmlApplicationContext, GenericApplicationContext, GenericWebApplicationContext, GenericXmlApplicationContext, ResourceAdapterApplicationContext, StaticApplicationContext, StaticPortletApplicationContext, StaticWebApplicationContext, XmlPortletApplicationContext, XmlWebApplicationContext public interface ApplicationContext extends EnvironmentCapable, ListableBeanFactory, HierarchicalBeanFactory, MessageSource, ApplicationEventPublisher, ResourcePatternResolver Central interface to provide config for an application. This is read-only while the application is running, but may be reloaded if the implementation supports this. An ApplicationContext provides: Bean factory methods for accessing application components. Inherited from ListableBeanFactory. The ability to load file resources in a generic fashion. Inherited from the ResourceLoader interface. The ability to publish events to registered listeners. Inherited from the ApplicationEventPublisher interface. The ability to resolve messages, supporting internationalization. Inherited from the MessageSource interface. Inheritance from a parent context. Definitions in a descendant context will always take priority. This means, for example, that a single parent context can be used by an entire web application, while each servlet has its own child context that is independent of that of any other servlet. In addition to standard BeanFactory lifecycle capabilities, ApplicationContext implementations detect and invoke ApplicationContextAware beans as well as ResourceLoaderAware, ApplicationEventPublisherAware and MessageSourceAware beans. Author: Rod Johnson, Juergen Hoeller See Also: ConfigurableApplicationContext, BeanFactory, ResourceLoader Field Summary  Fields inherited from interface org.springframework.beans.factory.BeanFactory FACTORY_BEAN_PREFIX  Fields inherited from interface org.springframework.core.io.support.ResourcePatternResolver CLASSPATH_ALL_URL_PREFIX  Fields inherited from interface org.springframework.core.io.ResourceLoader CLASSPATH_URL_PREFIX  Method Summary  AutowireCapableBeanFactory getAutowireCapableBeanFactory()           Expose AutowireCapableBeanFactory functionality for this context.  String getDisplayName()           Return a friendly name for this context.  String getId()           Return the unique id of this application context.  ApplicationContext getParent()           Return the parent context, or null if there is no parent and this is the root of the context hierarchy.  long getStartupDate()           Return the timestamp when this context was first loaded.  Methods inherited from interface org.springframework.core.env.EnvironmentCapable getEnvironment  Methods inherited from interface org.springframework.beans.factory.ListableBeanFactory containsBeanDefinition, findAnnotationOnBean, getBeanDefinitionCount, getBeanDefinitionNames, getBeanNamesForType, getBeanNamesForType, getBeansOfType, getBeansOfType, getBeansWithAnnotation  Methods inherited from interface org.springframework.beans.factory.HierarchicalBeanFactory containsLocalBean, getParentBeanFactory  Methods inherited from interface org.springframework.beans.factory.BeanFactory containsBean, getAliases, getBean, getBean, getBean, getBean, getType, isPrototype, isSingleton, isTypeMatch  Methods inherited from interface org.springframework.context.MessageSource getMessage, getMessage, getMessage  Methods inherited from interface org.springframework.context.ApplicationEventPublisher publishEvent  Methods inherited from interface org.springframework.core.io.support.ResourcePatternResolver getResources  Methods inherited from interface org.springframework.core.io.ResourceLoader getClassLoader, getResource  Method Detail getId String getId() Return the unique id of this application context. Returns: the unique id of the context, or null if none getDisplayName String getDisplayName() Return a friendly name for this context. Returns: a display name for this context (never null) getStartupDate long getStartupDate() Return the timestamp when this context was first loaded. Returns: the timestamp (ms) when this context was first loaded getParent ApplicationContext getParent() Return the parent context, or null if there is no parent and this is the root of the context hierarchy. Returns: the parent context, or null if there is no parent getAutowireCapableBeanFactory AutowireCapableBeanFactory getAutowireCapableBeanFactory()                                                         throws IllegalStateException Expose AutowireCapableBeanFactory functionality for this context. This is not typically used by application code, except for the purpose of initializing bean instances that live outside the application context, applying the Spring bean lifecycle (fully or partly) to them. Alternatively, the internal BeanFactory exposed by the ConfigurableApplicationContext interface offers access to the AutowireCapableBeanFactory interface too. The present method mainly serves as convenient, specific facility on the ApplicationContext interface itself. Returns: the AutowireCapableBeanFactory for this context Throws: IllegalStateException - if the context does not support the AutowireCapableBeanFactory interface or does not hold an autowire-capable bean factory yet (usually if refresh() has never been called) See Also: ConfigurableApplicationContext.refresh(), ConfigurableApplicationContext.getBeanFactory()\",\n"
                    +
                    "        \"title\": \"ApplicationContext\",\n" +
                    "        \"publishAt\": \"Jan 1, 1970 1:00:00 AM\",\n" +
                    "        \"subTitle\": \"My Subtitle\"\n" +
                    "      }\n" +
                    "    } ]\n" +
                    "  }\n" +
                    "}\n";

    private Gson gson = new Gson();
    private SearchResultParser searchResultParser;
    private SearchResults searchResults;
    private List<SearchResult> content;

    @Before
    public void setup() {
        JsonParser jsonParser = new JsonParser();
        searchResultParser = new SearchResultParser();
        JestResult jestResult = new JestResult(gson);
        jestResult.setJsonObject(jsonParser.parse(RESULT_STRING).getAsJsonObject());

        Pageable pageable = new PageRequest(3, 12);
        searchResults = searchResultParser.parseResults(jestResult, pageable, "search term");
        content = searchResults.getPage().getContent();
    }

    @Test
    public void returnsAPageOfResults() {
        Page<SearchResult> page = searchResults.getPage();
        assertThat(page.getNumber(), equalTo(3));
        assertThat(page.getSize(), equalTo(12));
        assertThat(page.getTotalElements(), equalTo(237L));
    }

    @Test
    public void returnsAResultForEveryHit() {
        assertThat(content.size(), equalTo(1));
    }

    @Test
    public void title() {
        assertThat(content.get(0).getTitle(), equalTo("ApplicationContext"));
    }

    @Test
    public void useSourceSummary() {
        assertThat(
                content.get(0).getSummary(),
                equalTo("org.springframework.context Interface ApplicationContext All Superinterfaces: ApplicationEventPublisher, BeanFactory, EnvironmentCapable, HierarchicalBeanFactory, ListableBeanFactory, MessageSource, ResourceLoader, ResourcePatternResolver All Known Subinterfaces: ConfigurableApplicationContext, ConfigurablePortletApplicationContext, ConfigurableWebApplicationContext, WebApplicationContext All Known Implementing Classes: AbstractApplicationContext, AbstractRefreshableApplicationContext, AbstractR"));
    }

    @Test
    public void url() {
        assertThat(
                content.get(0).getPath(),
                equalTo("http://docs.spring.io/spring/docs/3.1.4.RELEASE/javadoc-api/org/springframework/context/ApplicationContext.html"));
    }

    @Test
    public void type() {
        assertThat(content.get(0).getType(), equalTo("site"));
    }

    @Test
    public void subTitle() {
        assertThat(content.get(0).getSubTitle(), equalTo("My Subtitle"));
    }

    @Test
    public void originalSearchTerm() {
        assertThat(content.get(0).getOriginalSearchTerm(), equalTo("search term"));
    }
}
