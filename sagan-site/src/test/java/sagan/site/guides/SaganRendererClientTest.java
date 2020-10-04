package sagan.site.guides;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import sagan.site.renderer.SaganRendererClient;
import sagan.support.ResourceNotFoundException;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RunWith(SpringRunner.class)
@RestClientTest(SaganRendererClient.class)
@TestPropertySource(properties = "sagan.site.renderer.service-url=https://example.com/")
public class SaganRendererClientTest {

    @Autowired
    private SaganRendererClient client;

    @Autowired
    private MockRestServiceServer server;

    @Test(expected = ResourceNotFoundException.class)
    public void findByNameShouldReturn404IfObjectMissing(){
        this.server
                .expect(requestTo("https://example.com/"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));
        this.client.fetchGettingStartedGuide("not-a-guide");
    }

}
