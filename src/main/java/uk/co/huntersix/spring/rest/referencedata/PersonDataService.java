package uk.co.huntersix.spring.rest.referencedata;


import static java.util.Optional.of;

import org.springframework.stereotype.Service;
import uk.co.huntersix.spring.rest.model.Person;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PersonDataService {
    public static final List<Person> PERSON_DATA = new ArrayList<>(Arrays.asList(
            new Person("Mary", "Smith"),
            new Person("Brian", "Archer"),
            new Person("Collin", "Brown"),
            new Person("James", "White"),
            new Person("Alice", "White")
    ));

    public Optional<Person> findPerson(String lastName, String firstName) {
        return PERSON_DATA.stream()
                .filter(p -> p.getFirstName().equalsIgnoreCase(firstName)
                        && p.getLastName().equalsIgnoreCase(lastName))
                .findFirst();
    }

    public Optional<List<Person>> findPeopleBySurname(String lastName) {
        return of(PERSON_DATA.stream().filter(person -> person.getLastName().equalsIgnoreCase(lastName)).collect(Collectors.toList()));

    }

    public void addPerson(Person person) {
        PERSON_DATA.add(person);
    }
	
	/**
	 * 
	 * @param person
	 * @return true if a person already exit for the given person
	 */
	public boolean isPersonExist(Person person) {
		if(person == null)
			return false;
		return isPersonExist(person.getFirstName(),person.getLastName());
	}
	
	/**
	 * 
	 * @param firstName
	 * @param lastName
	 * @return true if a person already exit for the given firstName and lastName
	 */
	public boolean isPersonExist(String firstName, String lastName) {
		
		Optional<Person> result = findPerson(lastName,firstName);
		return result.isPresent();
	}	
}
