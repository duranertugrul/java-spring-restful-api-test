package uk.co.huntersix.spring.rest.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import uk.co.huntersix.spring.rest.model.Person;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HttpRequestTest extends HttpBaseTest{

	
	@LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    

    @Test
    public void shouldReturnPersonDetails() {
        assertThat(
            this.restTemplate.getForObject(
                getBaseURI() + "/person/smith/mary",
                String.class
            )
        ).contains("Mary");
    }
    
    @Test
    public void shouldReturnNotFound_whenPersonNotFound_givenNameAndLastName() {
    	ResponseEntity<Person> resp = this.restTemplate.getForEntity(getBaseURI()+ "/person/smith/lastname2", Person.class);
    	Assertions.assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);        
    }
    
    
    @Test
    public void shouldReturnPersonList() {
    	ResponseEntity<Person[]> resp = this.restTemplate.getForEntity(getBaseURI()+ "/person/white", Person[].class);
    	Assertions.assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
    	Assertions.assertThat(resp.getBody().length).isEqualTo(2);
    	Assertions.assertThat(resp.getBody()[0].getLastName()).isEqualTo("White");
    	Assertions.assertThat(resp.getBody()[1].getLastName()).isEqualTo("White");
    }
    
    @Test
    public void shouldReturnNotFound_whenPersonNotFound_givenLastName() throws Exception {
    	
    	ResponseEntity<Object[]> resp = this.restTemplate.getForEntity(getBaseURI()+ "/person/pink", Object[].class);
    	Assertions.assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
    
    
    @Test
    public void shouldAddThePersonToTheList_givenUniqueNameAndSurname() throws Exception {
    	JSONObject loginJSonRequest = new JSONObject();
    	loginJSonRequest.put("firstName", "firstName5");
    	loginJSonRequest.put("lastName", "lastName5");
    	
    	RequestBuilder requestBuilder = MockMvcRequestBuilders
				.post(getBaseURI() + "/person")
				.accept(MediaType.APPLICATION_JSON)
				.content(loginJSonRequest.toString())
				.contentType(MediaType.APPLICATION_JSON);
    	
    	MvcResult result = getMockMvc().perform(requestBuilder).andReturn();

    	assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.CREATED.value());
    	
    }
    
    @Test
    public void shouldReturnConflict_whenPersonAlreadyExist() throws Exception {
    	JSONObject loginJSonRequest = new JSONObject();
    	loginJSonRequest.put("firstName", "Mary");
    	loginJSonRequest.put("lastName", "Smith");
    	
    	RequestBuilder requestBuilder = MockMvcRequestBuilders
				.post(getBaseURI()+ "/person")
				.accept(MediaType.APPLICATION_JSON)
				.content(loginJSonRequest.toString())
				.contentType(MediaType.APPLICATION_JSON);
    	
    	MvcResult result = getMockMvc().perform(requestBuilder).andReturn();
    	
    	Assertions.assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
    }
    
    
    public String getBaseURI() {
		return "http://localhost:" + port;
	}
}
