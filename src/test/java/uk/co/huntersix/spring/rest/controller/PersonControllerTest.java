package uk.co.huntersix.spring.rest.controller;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.huntersix.spring.rest.model.Person;
import uk.co.huntersix.spring.rest.referencedata.PersonDataService;

@RunWith(SpringRunner.class)
@WebMvcTest(PersonController.class)
public class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonDataService personDataService;

    @Test
    public void shouldReturnPersonFromService() throws Exception {
        when(personDataService.findPerson(any(), any())).thenReturn(of(new Person("Mary", "Smith")));
        this.mockMvc.perform(get("/person/smith/mary"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("firstName").value("Mary"))
                .andExpect(jsonPath("lastName").value("Smith"));
    }

    @Test
    public void shouldReturnNotFoundWhenPersonDoesNotExist() throws Exception {
        when(personDataService.findPerson(any(), any())).thenReturn(empty());
        this.mockMvc.perform(get("/person/lastname/mary"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnListOfPeopleWithGivenSurname() throws Exception {
        List<Person> people = new ArrayList<Person>();
        people.add(new Person("Mary", "Smith"));
        people.add(new Person("James", "Smith"));

        when(personDataService.findPeopleBySurname(any())).thenReturn(of(people));
        this.mockMvc.perform(get("/person/smith"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Mary"))
                .andExpect(jsonPath("$[0].lastName").value("Smith"));

    }

    @Test
    public void shouldReturnNotFoundWhenNoPeopleWithGivenSurname() throws Exception {
        when(personDataService.findPeopleBySurname(any())).thenReturn(empty());
        this.mockMvc.perform(get("/person/lastname2"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldAddPerson() throws Exception {
        when(personDataService.findPerson(any(), any())).thenReturn(empty());
        Person person = new Person("Jack", "White");
        this.mockMvc.perform(
                post("/person")
                        .contentType(APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(person)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldReturnConflictWhenPersonExist() throws Exception {
        when(personDataService.findPerson(any(), any())).thenReturn(of(new Person("Mary", "Smith")));
        Person person = new Person("Mary", "Smith");
        this.mockMvc.perform(
                post("/person")
                        .contentType(APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(person)))
                .andDo(print())
                .andExpect(status().isConflict());
    }
}
