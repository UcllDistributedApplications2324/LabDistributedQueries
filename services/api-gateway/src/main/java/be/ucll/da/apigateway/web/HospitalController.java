package be.ucll.da.apigateway.web;

import be.ucll.da.apigateway.api.HospitalApiDelegate;
import be.ucll.da.apigateway.api.model.*;
import be.ucll.da.apigateway.client.appointment.api.AppointmentApi;
import be.ucll.da.apigateway.client.doctor.api.DoctorApi;
import be.ucll.da.apigateway.client.doctor.model.ApiDoctor;
import be.ucll.da.apigateway.client.patient.api.PatientApi;
import be.ucll.da.apigateway.client.patient.model.ApiPatient;
import be.ucll.da.apigateway.cqrs.Appointment;
import be.ucll.da.apigateway.cqrs.AppointmentRepository;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Component
public class HospitalController implements HospitalApiDelegate {

    @Override
    public ResponseEntity<Void> apiV1AppointmentConfirmationPost(ApiAppointmentConfirmation apiAppointmentConfirmation) {
        appointmentApi.getApiClient().setBasePath(discoveryClient.getNextServerFromEureka("appointment-service", false).getHomePageUrl());

        var confirmation = new be.ucll.da.apigateway.client.appointment.model.ApiAppointmentConfirmation();
        confirmation.appointmentRequestNumber(apiAppointmentConfirmation.getAppointmentRequestNumber());
        confirmation.setAcceptProposedAppointment(apiAppointmentConfirmation.getAcceptProposedAppointment());

        return circuitBreakerFactory.create("appointmentApi")
                .run(() -> appointmentApi.apiV1AppointmentConfirmationPostWithHttpInfo(confirmation));
    }

    @Override
    public ResponseEntity<ApiAppointmentRequestResponse> apiV1AppointmentRequestPost(ApiAppointmentRequest apiAppointmentRequest) {
        appointmentApi.getApiClient().setBasePath(discoveryClient.getNextServerFromEureka("appointment-service", false).getHomePageUrl());

        var request = new be.ucll.da.apigateway.client.appointment.model.ApiAppointmentRequest();
        request.patientId(apiAppointmentRequest.getPatientId());
        request.neededExpertise(apiAppointmentRequest.getNeededExpertise());
        request.preferredDay(apiAppointmentRequest.getPreferredDay());

        var appointmentApi1 = circuitBreakerFactory.create("appointmentApi")
                .run(() -> appointmentApi.apiV1AppointmentRequestPostWithHttpInfo(request));

        var response = new ApiAppointmentRequestResponse();
        response.appointmentRequestNumber(Objects.requireNonNull(appointmentApi1.getBody()).getAppointmentRequestNumber());

        return ResponseEntity.ok(response);
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

    @Autowired
    private AppointmentApi appointmentApi;

    @Autowired
    private PatientApi patientApi;

    @Autowired
    private DoctorApi doctorApi;

    @Autowired
    private CircuitBreakerFactory circuitBreakerFactory;

    @Autowired
    private EurekaClient discoveryClient;

    private ResponseEntity<ApiAppointmentOverview> getUsingApiComposition(LocalDate day) {
        ApiAppointmentOverview overview = new ApiAppointmentOverview();

        appointmentApi.getApiClient().setBasePath(discoveryClient.getNextServerFromEureka("appointment-service", false).getHomePageUrl());
        patientApi.getApiClient().setBasePath(discoveryClient.getNextServerFromEureka("patient-service", false).getHomePageUrl());
        doctorApi.getApiClient().setBasePath(discoveryClient.getNextServerFromEureka("doctor-service", false).getHomePageUrl());


        be.ucll.da.apigateway.client.appointment.model.ApiAppointmentOverview appointments = circuitBreakerFactory.create("appointmentApi")
                .run(() -> appointmentApi.apiV1AppointmentDayGet(day.format(DateTimeFormatter.ISO_DATE)));

        overview.setDay(day);

        if (appointments.getAppointments() == null || appointments.getAppointments().isEmpty()) {
            return ResponseEntity.ok(overview);
        }

        for (be.ucll.da.apigateway.client.appointment.model.ApiAppointment appointment : appointments.getAppointments()) {
            ApiAppointment apiAppointment = new ApiAppointment();

            apiAppointment.accountId(appointment.getAccountId());
            apiAppointment.roomId(appointment.getRoomId());

            ApiPatient patient = circuitBreakerFactory.create("patientApi")
                    .run(() -> patientApi.getPatientById(appointment.getPatientId()));

            ApiAppointmentPatient apiPatient = new ApiAppointmentPatient();
            apiPatient.setEmail(patient.getEmail());
            apiPatient.setId(patient.getPatientId());
            apiPatient.setFirstName(patient.getFirstName());
            apiPatient.setLastName(patient.getLastName());

            apiAppointment.setPatient(apiPatient);

            ApiDoctor doctor = circuitBreakerFactory.create("doctorApi")
                    .run(() -> doctorApi.getDoctorById(appointment.getDoctorId()));

            ApiAppointmentDoctor apiDoctor = new ApiAppointmentDoctor();
            apiDoctor.setId(doctor.getId());
            apiDoctor.setFirstName(doctor.getFirstName());
            apiDoctor.setLastName(doctor.getLastName());
            apiDoctor.setAddress(doctor.getAddress());
            apiDoctor.setAge(doctor.getAge());

            apiAppointment.setDoctor(apiDoctor);
            overview.addAppointmentsItem(apiAppointment);
        }

        return ResponseEntity.ok(overview);
    }

    // --- Code For CQRS

    @Autowired
    private AppointmentRepository appointmentRepository;

    private ResponseEntity<ApiAppointmentOverview> getUsingCqrs(LocalDate day) {
        ApiAppointmentOverview overview = new ApiAppointmentOverview();
        overview.setDay(day);

        List<Appointment> appointments = appointmentRepository.getAppointmentsByAppointmentDay(day);

        if (appointments == null || appointments.isEmpty()) {
            return ResponseEntity.ok(overview);
        }

        for (Appointment appointment : appointments) {
            ApiAppointment apiAppointment = new ApiAppointment();

            apiAppointment.accountId(appointment.getAccountId());
            apiAppointment.roomId(appointment.getRoomId());

            ApiAppointmentPatient apiPatient = new ApiAppointmentPatient();
            apiPatient.setEmail(appointment.getPatient().getEmail());
            apiPatient.setId(appointment.getPatient().getId());
            apiPatient.setFirstName(appointment.getPatient().getFirstName());
            apiPatient.setLastName(appointment.getPatient().getLastName());
            apiAppointment.setPatient(apiPatient);

            ApiAppointmentDoctor apiDoctor = new ApiAppointmentDoctor();
            apiDoctor.setId(appointment.getDoctor().getId());
            apiDoctor.setFirstName(appointment.getDoctor().getFirstName());
            apiDoctor.setLastName(appointment.getDoctor().getLastName());
            apiDoctor.setAddress(appointment.getDoctor().getAddress());
            apiDoctor.setAge(appointment.getDoctor().getAge());
            apiAppointment.setDoctor(apiDoctor);

            overview.addAppointmentsItem(apiAppointment);
        }

        return ResponseEntity.ok(overview);
    }
}
