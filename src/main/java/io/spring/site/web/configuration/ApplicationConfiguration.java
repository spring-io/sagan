/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.spring.site.web.configuration;

import com.google.common.cache.CacheBuilder;
import io.spring.site.domain.projects.ProjectMetadataService;
import io.spring.site.domain.projects.ProjectMetadataYamlParser;
import io.spring.site.domain.services.DateService;
import io.spring.site.web.SiteUrl;
import io.spring.site.web.blog.feed.BlogPostAtomViewer;
import liquibase.integration.spring.SpringLiquibase;
import org.cloudfoundry.runtime.env.CloudEnvironment;
import org.cloudfoundry.runtime.env.RdbmsServiceInfo;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.DispatcherServlet;
import org.tuckey.web.filters.urlrewrite.UrlRewriteFilter;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;


@EnableAutoConfiguration
@Configuration
@ComponentScan({ "io.spring.site.web", "io.spring.site.domain", "io.spring.site.search" })
@EnableCaching
public class ApplicationConfiguration {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationConfiguration.class, args);
    }

    @Configuration
    protected static class DataSourceConfiguration {

        @Autowired
        private Environment environment;

        @Bean
        public DataSource dataSource() {
            org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();

            boolean inMemory = this.environment.acceptsProfiles(this.environment
                    .getDefaultProfiles())
                    || this.environment.acceptsProfiles("acceptance");

            if (inMemory) {
                dataSource.setDriverClassName("org.hsqldb.jdbcDriver");
            } else {
                dataSource.setDriverClassName("org.postgresql.Driver");
            }

            CloudEnvironment cloudEnvironment = new CloudEnvironment();
            if (cloudEnvironment.getServiceDataByName("sagan-db") != null) {
                RdbmsServiceInfo serviceInfo = cloudEnvironment.getServiceInfo(
                        "sagan-db", RdbmsServiceInfo.class);
                dataSource.setUrl(serviceInfo.getUrl());
                dataSource.setUsername(serviceInfo.getUserName());
                dataSource.setPassword(serviceInfo.getPassword());
            } else {
                if (inMemory) {
                    dataSource.setUrl("jdbc:hsqldb:mem:sagan-db");
                    dataSource.setUsername("sa");
                    dataSource.setPassword("");
                } else {
                    dataSource.setUrl("jdbc:postgresql://localhost:5432/sagan-db");
                    dataSource.setUsername("user");
                    dataSource.setPassword("changeme");
                }
            }

            dataSource.setMaxActive(20);
            dataSource.setMaxIdle(8);
            dataSource.setMinIdle(8);
            dataSource.setTestOnBorrow(false);
            dataSource.setTestOnReturn(false);
            dataSource.setValidationQuery("SELECT 1");
            return dataSource;
        }
    }

    @Bean
    public HealthIndicator<Map<String, Object>> healthIndicator(DataSource dataSource) {
        if (dataSource instanceof org.apache.tomcat.jdbc.pool.DataSource) {
            final org.apache.tomcat.jdbc.pool.DataSource tcDataSource = (org.apache.tomcat.jdbc.pool.DataSource) dataSource;
            return new HealthIndicator<Map<String, Object>>() {
                @Override
                public Map<String, Object> health() {
                    Map<String, Object> health = new HashMap<>();
                    health.put("active", tcDataSource.getActive());
                    health.put("max_active", tcDataSource.getMaxActive());
                    health.put("idle", tcDataSource.getIdle());
                    health.put("max_idle", tcDataSource.getMaxIdle());
                    health.put("min_idle", tcDataSource.getMinIdle());
                    health.put("wait_count", tcDataSource.getWaitCount());
                    health.put("max_wait", tcDataSource.getMaxWait());
                    return health;
                }
            };
        }
        return new HealthIndicator<Map<String, Object>>() {
            @Override
            public Map<String, Object> health() {
                return Collections.emptyMap();
            }
        };
    }

    @Bean
    public BlogPostAtomViewer blogPostAtomViewer(SiteUrl siteUrl, DateService dateService) {
        return new BlogPostAtomViewer(siteUrl, dateService);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Serializer simpleXmlSerializer() {
        return new Persister();
    }

    @Bean
    public ProjectMetadataService projectMetadataService() throws IOException {
        InputStream yaml = new ClassPathResource("/project-metadata.yml", getClass())
                .getInputStream();
        return new ProjectMetadataYamlParser().createServiceFromYaml(yaml);
    }

    // http://urlrewritefilter.googlecode.com/svn/trunk/src/doc/manual/4.0/index.html#filterparams
    // Blog filter must be declared first, to ensure its rules are applied before the
    // general rules
    @Bean
    public FilterRegistrationBean blogRewriteFilterConfig() {
        FilterRegistrationBean reg = new FilterRegistrationBean();
        reg.setName("mappingsRewriteFilter");
        reg.setFilter(new UrlRewriteFilter());
        reg.addInitParameter("confPath", "urlrewrite/urlrewrite-generated.xml");
        reg.addInitParameter("confReloadCheckInterval", "-1");
        reg.addInitParameter("logLevel", "WARN");
        return reg;
    }

    @Bean
    public FilterRegistrationBean rewriteFilterConfig() {
        FilterRegistrationBean reg = new FilterRegistrationBean();
        reg.setName("rewriteFilter");
        reg.setFilter(new UrlRewriteFilter());
        reg.addInitParameter("confPath", "urlrewrite/urlrewrite.xml");
        reg.addInitParameter("confReloadCheckInterval", "-1");
        reg.addInitParameter("logLevel", "WARN");
        return reg;
    }

    @Bean
    public DispatcherServlet dispatcherServlet() {
        return new DispatcherServlet();
    }

    @Bean
    public SpringLiquibase springLiquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:liquibase/changeset.yaml");
        return liquibase;
    }

    @Bean
    public CacheManager cacheManager(@Value("${cache.network.timetolive:300}") Long cacheNetworkTimeToLive,
                                     @Value("${cache.database.timetolive:60}") Long cacheDatabaseTimeToLive) {
        SimpleCacheManager cacheManager = new SimpleCacheManager();

        List<ConcurrentMapCache> cacheList = new ArrayList<>();
        cacheList.add(createConcurrentMapCache(cacheNetworkTimeToLive, "cache.network", -1));
        cacheList.add(createConcurrentMapCache(cacheDatabaseTimeToLive, "cache.database", 50));
        cacheManager.setCaches(cacheList);
        return cacheManager;
    }

    private ConcurrentMapCache createConcurrentMapCache(Long timeToLive, String name, long cacheSize) {
        CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder()
                .expireAfterWrite(timeToLive, TimeUnit.SECONDS);

        if (cacheSize >= 0) {
            cacheBuilder.maximumSize(cacheSize);
        }
        ConcurrentMap<Object,Object> map = cacheBuilder.build().asMap();
        return new ConcurrentMapCache(name, map, false);
    }

}
