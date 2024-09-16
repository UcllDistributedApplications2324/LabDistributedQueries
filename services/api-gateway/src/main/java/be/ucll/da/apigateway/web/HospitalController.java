package be.ucll.da.apigateway.web;

import be.ucll.da.apigateway.api.HospitalApiDelegate;

import be.ucll.da.apigateway.api.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@Component
public class HospitalController implements HospitalApiDelegate {

    @Override
    public ResponseEntity<Void> apiV1AppointmentConfirmationPost(ApiAppointmentConfirmation apiAppointmentConfirmation) {
        throw new RuntimeException("Implement me!!");
    }

    @Override
    public ResponseEntity<ApiAppointmentRequestResponse> apiV1AppointmentRequestPost(ApiAppointmentRequest apiAppointmentRequest) {
        throw new RuntimeException("Implement me!!");
    }

    @Override
    public ResponseEntity<ApiAppointmentOverview> apiV1AppointmentDayGet(String dayString, Boolean useCqrs) {
        LocalDate day = LocalDate.parse(dayString, DateTimeFormatter.ISO_DATE);

        if (useCqrs) {
            return getUsingCqrs(day);
        } else {
            return getUsingApiComposition(day);
        }
    }

    // --- Code For Composition ---

    private ResponseEntity<ApiAppointmentOverview> getUsingApiComposition(LocalDate day) {
        throw new RuntimeException("Implement me!!");
    }

    // --- Code For CQRS

    private ResponseEntity<ApiAppointmentOverview> getUsingCqrs(LocalDate day) {
        throw new RuntimeException("Implement me!!");
    }
}
