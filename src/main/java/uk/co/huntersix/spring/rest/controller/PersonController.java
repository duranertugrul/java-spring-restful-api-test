package uk.co.huntersix.spring.rest.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.co.huntersix.spring.rest.model.Person;
import uk.co.huntersix.spring.rest.referencedata.PersonDataService;

@RestController
public class PersonController {
    private PersonDataService personDataService;

    public PersonController(@Autowired PersonDataService personDataService) {
        this.personDataService = personDataService;
    }

    @GetMapping("/person/{lastName}/{firstName}")
    public ResponseEntity<Person> person(@PathVariable(value = "lastName") String lastName,
                                         @PathVariable(value = "firstName") String firstName) {
        return personDataService.findPerson(lastName, firstName)
                .map(person -> ResponseEntity.ok().body(person))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/person/{lastName}")
    public ResponseEntity<List<Person>> peopleBySurname(@PathVariable(value = "lastName") String lastName) {
        Optional<List<Person>> people = personDataService.findPeopleBySurname(lastName);
        if(people.isPresent() && people.get().size() > 0) {
            return ResponseEntity.ok().body(people.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/person")
    public ResponseEntity<?> addPerson(@RequestBody Person person) {
        return personDataService.findPerson(person.getLastName(), person.getFirstName())
                .map(p -> ResponseEntity.status(HttpStatus.CONFLICT).build())
                .orElseGet(() -> {
                    personDataService.addPerson(person);
                    return ResponseEntity.status(HttpStatus.CREATED).build();
                });
    
    }
}
