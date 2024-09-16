package be.ucll.da.apigateway.web;

import be.ucll.da.apigateway.client.appointment.api.AppointmentApi;
import be.ucll.da.apigateway.client.doctor.api.DoctorApi;
import be.ucll.da.apigateway.client.patient.api.PatientApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WebConfig {

    @Bean
    public AppointmentApi appointmentApi() {
        return new AppointmentApi();
    }

    @Bean
    public PatientApi patientApi() {
        return new PatientApi();
    }

    @Bean
    public DoctorApi doctorApi() {
        return new DoctorApi();
    }
}
