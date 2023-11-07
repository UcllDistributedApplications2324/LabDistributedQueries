package be.ucll.da.patientservice.domain;

import com.github.javafaker.Faker;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class PatientService {

    private final Faker faker = new Faker();

    public Patient validatePatient(Integer id) {
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();

        String email = firstName + "." + lastName + "@google.com";

        if (Math.random() > 0.3) {
            return new Patient(id, firstName, lastName, email, true);
        }

        return new Patient(id, firstName, lastName, email, false);
    }

    public Patient getPatient(Integer id) {
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();

        String email = firstName + "." + lastName + "@google.com";

        return new Patient(id, firstName, lastName, email, true);
    }
}
