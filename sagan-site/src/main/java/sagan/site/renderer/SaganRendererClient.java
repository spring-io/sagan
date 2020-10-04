package sagan.site.renderer;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;
import sagan.SiteProperties;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.client.Hop;
import org.springframework.hateoas.client.Traverson;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import sagan.support.ResourceNotFoundException;

@Component
public class SaganRendererClient {

	private static final ParameterizedTypeReference<Resources<GuideMetadata>> guidesResourceRef =
			new ParameterizedTypeReference<Resources<GuideMetadata>>() {};

	private static final MediaType TEXT_ASCIIDOC = MediaType.parseMediaType("text/asciidoc");

	private final RestTemplate template;

	private final Traverson traverson;


	public SaganRendererClient(RestTemplateBuilder builder, SiteProperties properties) {
		this.template = builder
				.messageConverters(Traverson.getDefaultMessageConverters(MediaTypes.HAL_JSON))
				.build();
		this.traverson = new Traverson(URI.create(properties.getRenderer().getServiceUrl()), MediaTypes.HAL_JSON);
		this.traverson.setRestOperations(this.template);
	}

	public GuideMetadata[] fetchAllGuides() {
		return this.traverson.follow("guides").toObject(guidesResourceRef).getContent()
				.toArray(new GuideMetadata[]{});
	}

	public GuideMetadata[] fetchGettingStartedGuides() {
		return Arrays.stream(fetchAllGuides())
				.filter(guide -> GuideType.GETTING_STARTED.equals(guide.getType()))
				.toArray(GuideMetadata[]::new);
	}

	public GuideMetadata fetchGettingStartedGuide(String name) {
		try{
			return this.traverson.follow("guides")
					.follow(Hop.rel("getting-started").withParameter("guide", name))
					.toObject(GuideMetadata.class);
		}catch(HttpStatusCodeException ex){
			if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
				throw new ResourceNotFoundException(name + " not found");
			}
			throw ex;
		}
	}

	public GuideContent fetchGettingStartedGuideContent(String name) {
		try {
			return this.traverson.follow("guides")
					.follow(Hop.rel("getting-started").withParameter("guide", name))
					.follow("content")
					.toObject(GuideContent.class);
		} catch (HttpStatusCodeException ex) {
			if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
				throw new ResourceNotFoundException(name + " not found");
			}
			throw ex;
		}
	}

	public GuideMetadata[] fetchTutorialGuides() {
		return Arrays.stream(fetchAllGuides())
				.filter(guide -> GuideType.TUTORIAL.equals(guide.getType()))
				.toArray(GuideMetadata[]::new);
	}

	public GuideMetadata fetchTutorialGuide(String name) {
		try {
			return this.traverson.follow("guides")
					.follow(Hop.rel("tutorial").withParameter("guide", name))
					.toObject(GuideMetadata.class);
		} catch (HttpStatusCodeException ex) {
			if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
				throw new ResourceNotFoundException(name + " not found");
			}
			throw ex;
		}
	}

	public GuideContent fetchTutorialGuideContent(String name) {
		try {
			return this.traverson.follow("guides")
					.follow(Hop.rel("tutorial").withParameter("guide", name))
					.follow("content")
					.toObject(GuideContent.class);
		} catch (HttpStatusCodeException ex) {
			if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
				throw new ResourceNotFoundException(name + " not found");
			}
			throw ex;
		}
	}

	public GuideMetadata[] fetchTopicalGuides() {
		return Arrays.stream(fetchAllGuides())
				.filter(guide -> GuideType.TOPICAL.equals(guide.getType()))
				.toArray(GuideMetadata[]::new);
	}

	public GuideMetadata fetchTopicalGuide(String name) {
		try {
			return this.traverson.follow("guides")
					.follow(Hop.rel("topical").withParameter("guide", name))
					.toObject(GuideMetadata.class);
		} catch (HttpStatusCodeException ex) {
			if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
				throw new ResourceNotFoundException(name + " not found");
			}
			throw ex;
		}
	}

	public GuideContent fetchTopicalGuideContent(String name) {
		try {
			return this.traverson.follow("guides")
					.follow(Hop.rel("topical").withParameter("guide", name))
					.follow("content")
					.toObject(GuideContent.class);
		} catch (HttpStatusCodeException ex) {
			if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
				throw new ResourceNotFoundException(name + " not found");
			}
			throw ex;

		}
	}

	public String renderMarkdown(String markup) {
		return renderMarkup(markup, MediaType.TEXT_MARKDOWN);
	}

	public String renderAsciidoc(String markup) {
		return renderMarkup(markup, TEXT_ASCIIDOC);
	}

	private String renderMarkup(String markup, MediaType contentType) {
		Link renderLink = this.traverson.follow("markup").asLink();
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.TEXT_HTML));
		headers.setContentType(contentType);
		HttpEntity<String> request = new HttpEntity<>(markup, headers);
		return this.template.postForObject(renderLink.getHref(), request, String.class);
	}

}
