/*
 * Copyright 2016-2017 the original author or authors.
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
package sagan.renderer.guide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Attributes;
import org.asciidoctor.OptionsBuilder;
import org.asciidoctor.SafeMode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import org.springframework.util.FileSystemUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Converts <code>org</code> and <code>repo</code> into a rendered guide. Downloads entire
 * repository from GitHub and unpacks it locally before running asciidoctor on the readme.
 * The result is the rendered HTML and table of contents.
 * 
 * @author Dave Syer
 *
 */
@RestController
public class GuideController {

    private final Asciidoctor asciidoctor;

    private GitHubClient gitHub;

    public GuideController(GitHubClient gitHub, Asciidoctor asciidoctor) {
        this.gitHub = gitHub;
        this.asciidoctor = asciidoctor;
    }

    @GetMapping("/guides/{org}/{repo}")
    public DocumentContent render(@PathVariable String org, @PathVariable String repo) {
        String path = org + "/" + repo;

        byte[] download = gitHub.sendRequestForDownload("/repos/" + path + "/zipball");

        String tempFilePrefix = path.replace("/", "-");
        try {
            // First, write the downloaded stream of bytes into a file
            File zipball = File.createTempFile(tempFilePrefix, ".zip");
            zipball.deleteOnExit();
            FileOutputStream zipOut = new FileOutputStream(zipball);
            zipOut.write(download);
            zipOut.close();

            // Open the zip file and unpack it
            File unzippedRoot;
            try (ZipFile zipFile = new ZipFile(zipball)) {
                unzippedRoot = null;
                for (Enumeration<? extends ZipEntry> e = zipFile.entries(); e
                        .hasMoreElements();) {
                    ZipEntry entry = e.nextElement();
                    if (entry.isDirectory()) {
                        File dir = new File(
                                zipball.getParent() + File.separator + entry.getName());
                        dir.mkdir();
                        if (unzippedRoot == null) {
                            unzippedRoot = dir; // first directory is the root
                        }
                    }
                    else {
                        StreamUtils.copy(zipFile.getInputStream(entry),
                                new FileOutputStream(zipball.getParent() + File.separator
                                        + entry.getName()));
                    }
                }
            }

            // Process the unzipped guide through asciidoctor, rendering HTML content
            Attributes attributes = new Attributes();
            attributes.setAllowUriRead(true);
            attributes.setSkipFrontMatter(true);
            File readmeAdocFile = new File(
                    unzippedRoot.getAbsolutePath() + File.separator + "README.adoc");
            OptionsBuilder options = OptionsBuilder.options().safe(SafeMode.SAFE)
                    .baseDir(unzippedRoot).headerFooter(true).attributes(attributes);
            StringWriter writer = new StringWriter();
            asciidoctor.convert(new FileReader(readmeAdocFile), writer, options);

            Document doc = Jsoup.parse(writer.toString());

            String htmlContent = doc.select("#content").html()
                    + "\n<!-- rendered by GuideController -->";
            String tableOfContents = findTableOfContents(doc);

            // Delete the zipball and the unpacked content
            FileSystemUtils.deleteRecursively(zipball);
            FileSystemUtils.deleteRecursively(unzippedRoot);

            return new DocumentContent(htmlContent, tableOfContents);
        }
        catch (IOException ex) {
            throw new IllegalStateException(
                    "Could not create temp file for source: " + tempFilePrefix, ex);
        }
    }

    /**
     * Extract top level table-of-content entries, and discard lower level links
     *
     * @param doc
     * @return HTML of the top tier table of content entries
     */
    private String findTableOfContents(Document doc) {
        Elements toc = doc.select("div#toc > ul.sectlevel1");

        toc.select("ul.sectlevel2").forEach(subsection -> subsection.remove());

        toc.forEach(part -> part.select("a[href]").stream()
                .filter(anchor -> doc.select(anchor.attr("href")).get(0).parent()
                        .classNames().stream()
                        .anyMatch(clazz -> clazz.startsWith("reveal")))
                .forEach(href -> href.parent().remove()));

        return toc.toString();
    }
}
