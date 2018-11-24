package com.sheena.playground.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sheena.playground.api.ElementTO;
import com.sheena.playground.logic.entity.ElementEntity;
import com.sheena.playground.api.Location;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ElementRestControllerTest {

    @LocalServerPort
    private int port;

    private String url;

    private RestTemplate restTemplate;

    private ObjectMapper jsonMapper;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ElementTO dummyElement;
    private ElementTO dummy2;
    private ElementTO dummy3;


    @PostConstruct
    public void init() {
        this.restTemplate = new RestTemplate();
        this.url = "http://localhost:" + port;
        System.err.println(this.url);

        // Jackson init
        this.jsonMapper = new ObjectMapper();
    }

    @Before
    public void setup() {
        Map<String, Object> att = new HashMap<>();
        att.put("attribute1", new HashMap());
        this.dummyElement = new ElementTO("sheena", "123", new Location(13.0, 25.0), "Pen", new Date("20/11/18"),
                new Date("19/11/19"), "tool", att, "sheena", "123@gmail.com");

        this.dummy2 = new ElementTO("playground", "456", this.dummyElement.getLocation(), this.dummyElement.getName(),
                this.dummyElement.getCreationDate(), this.dummyElement.getExpirationDate(), this.dummyElement.getType(),
                this.dummyElement.getAttributes(), this.dummyElement.getCreatorPlayground(),
                this.dummyElement.getCreatorEmail());
        this.dummy3 = new ElementTO("playground", "789", new Location(4.0, 24.9), this.dummyElement.getName(),
                this.dummyElement.getCreationDate(), this.dummyElement.getExpirationDate(), this.dummyElement.getType(),
                this.dummyElement.getAttributes(), this.dummyElement.getCreatorPlayground(),
                this.dummyElement.getCreatorEmail());

    }

    @Test
    public void addNewElementSuccessfully() {

        String targetUrl = String.format("%s/playground/elements/%s/%s", this.url,
                this.dummyElement.getCreatorPlayground(), this.dummyElement.getCreatorEmail());

        ElementTO actualElement = this.restTemplate.postForObject(targetUrl, this.dummyElement, ElementTO.class);
        assertThat(actualElement).isNotNull().isEqualTo(this.dummyElement);
    }

    @Test
    public void updateElementSuccessfully() {
        String postTargetUrl = String.format("%s/playground/elements/%s/%s", this.url,
                this.dummyElement.getCreatorPlayground(), this.dummyElement.getCreatorEmail());

        // Given
        this.dummyElement.setLocation(new Location(26.0, 24.9));
        this.restTemplate.postForObject(postTargetUrl, this.dummyElement, ElementTO.class);

        // When
        ElementEntity elementEntity = this.dummyElement.toEntity();
        String putTargetUrl = String.format("%s/playground/elements/%s/%s/%s/%s", this.url,
                elementEntity.getCreatorPlayground(), elementEntity.getCreatorEmail(), elementEntity.getPlayground(),
                elementEntity.getId());

        this.restTemplate.put(putTargetUrl, this.dummyElement);

        // Then
        assertThat(elementEntity.getLocation()).isEqualTo(new Location(26.0, 24.9));
    }

    @Test
    public void getElementByItsIDSuccessfully() {
        String postTargetUrl = String.format("%s/playground/elements/%s/%s", this.url,
                this.dummyElement.getCreatorPlayground(), this.dummyElement.getCreatorEmail());

        // Given
        this.restTemplate.postForObject(postTargetUrl, this.dummyElement, ElementTO.class);

        // When
        String getTargetUrl = String.format("%s/playground/elements/%s/%s/%s/%s", this.url,
                this.dummyElement.getCreatorPlayground(), this.dummyElement.getCreatorEmail(),
                this.dummyElement.getPlayground(), this.dummyElement.getId());
        ElementTO actualElementTO = this.restTemplate.getForObject(getTargetUrl, ElementTO.class);

        // Then
        assertThat(actualElementTO).isEqualTo(this.dummyElement);
    }

    @Test
    public void getElementByItsIDWhenIDDoesNotExistShouldHttpServerErrorException() throws HttpServerErrorException {

        thrown.expect(HttpServerErrorException.class);
        thrown.expectMessage("500 null");

        // When
        String getTargetUrl = String.format("%s/playground/elements/%s/%s/%s/%s", this.url,
                this.dummyElement.getCreatorPlayground(), this.dummyElement.getCreatorEmail(),
                this.dummyElement.getPlayground(), 1);
        this.restTemplate.getForObject(getTargetUrl, ElementTO.class);

    }

    @Test
    public void getAllElementsSuccesfully() {
        String postTargetUrl = String.format("%s/playground/elements/%s/%s", this.url,
                this.dummyElement.getCreatorPlayground(), this.dummyElement.getCreatorEmail());

        // Given
        this.restTemplate.postForObject(postTargetUrl, this.dummyElement, ElementTO.class);
        this.restTemplate.postForObject(postTargetUrl, this.dummy2, ElementTO.class);

        // When
        String getAllUrl = String.format("%s/playground/elements/%s/%s/all", this.url,
                this.dummyElement.getCreatorPlayground(), this.dummyElement.getCreatorEmail());
        ElementTO[] elements = this.restTemplate.getForObject(getAllUrl, ElementTO[].class);

        // Then
        ElementTO[] expected = { this.dummyElement, dummy2 };
        assertThat(elements).isNotNull().hasSize(2).isEqualTo(expected);

    }

    @Test
    public void getElementByCoordinatesShouldReturnMatch() {
        String postTargetUrl = String.format("%s/playground/elements/%s/%s", this.url,
                this.dummyElement.getCreatorPlayground(), this.dummyElement.getCreatorEmail());

        // Given
        this.restTemplate.postForObject(postTargetUrl, this.dummyElement, ElementTO.class);
        this.restTemplate.postForObject(postTargetUrl, this.dummy3, ElementTO.class);
        this.restTemplate.postForObject(postTargetUrl, this.dummy2, ElementTO.class);


        // When
        String getAllUrl = String.format("%s/playground/elements/%s/%s/near/%d/%d/%d", this.url,
                this.dummyElement.getCreatorPlayground(), this.dummyElement.getCreatorEmail(), 10, 20, 5);
        ElementTO[] elements = this.restTemplate.getForObject(getAllUrl, ElementTO[].class);

        ElementTO[] expected = { this.dummyElement, dummy2 };
        assertThat(elements).isNotNull().hasSize(2).isEqualTo(expected);

    }

    @Test
    public void getElementsThatShareAttributeSuccesfully() {
        String postTargetUrl = String.format("%s/playground/elements/%s/%s", this.url,
                this.dummyElement.getCreatorPlayground(), this.dummyElement.getCreatorEmail());

        // Given
        this.restTemplate.postForObject(postTargetUrl, this.dummyElement, ElementTO.class);
        this.restTemplate.postForObject(postTargetUrl, this.dummy2, ElementTO.class);
        this.restTemplate.postForObject(postTargetUrl, this.dummy3, ElementTO.class);

        // When
        String getAllUrl = String.format("%s/playground/elements/%s/%s/search/%s/%s", this.url,
                this.dummyElement.getCreatorPlayground(), this.dummyElement.getCreatorEmail(), "attribute1", "{}");
        ElementTO[] elements = this.restTemplate.getForObject(getAllUrl, ElementTO[].class);


        ElementTO[] expected = { this.dummyElement, this.dummy2 , this.dummy3};
        assertThat(elements).isNotNull().hasSize(3).isEqualTo(expected);
    }
}