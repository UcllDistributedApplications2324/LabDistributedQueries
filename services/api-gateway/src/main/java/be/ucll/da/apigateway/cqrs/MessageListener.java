package be.ucll.da.apigateway.cqrs;

import be.ucll.da.apigateway.client.appointment.model.AppointmentFinalizedEvent;
import be.ucll.da.apigateway.client.doctor.model.DoctorOnPayroll;
import be.ucll.da.apigateway.client.doctor.model.DoctorsOnPayrollEvent;
import be.ucll.da.apigateway.client.patient.model.PatientValidatedEvent;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Transactional
public class MessageListener {

    private final static Logger LOGGER = LoggerFactory.getLogger(MessageListener.class);

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;

    @Autowired
    public MessageListener(PatientRepository patientRepository, DoctorRepository doctorRepository, AppointmentRepository appointmentRepository) {
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @RabbitListener(queues = {"q.patient-validated.api-gateway"})
    public void onPatientValidated(PatientValidatedEvent event) {
        LOGGER.info("Receiving event: " + event);

        Patient patient = new Patient(event.getPatientId(), event.getFirstName(), event.getLastName(), event.getLastName());
        patientRepository.save(patient);
    }

    @RabbitListener(queues = {"q.doctors-employed.api-gateway"})
    public void onDoctorsEmployed(DoctorsOnPayrollEvent event) {
        LOGGER.info("Receiving event: " + event);

        if (event.getDoctors() == null || event.getDoctors().isEmpty()) {
            return;
        }

        for (DoctorOnPayroll dop : event.getDoctors()) {
            Doctor doctor = new Doctor(dop.getId(), dop.getFirstName(), dop.getLastName(), dop.getAge(), dop.getAddress());
            doctorRepository.save(doctor);
        }
    }

    @RabbitListener(queues = {"q.appointment-finalized.api-gateway"})
    public void onPatientValidated(AppointmentFinalizedEvent event) {
        LOGGER.info("Receiving event: " + event);

        if (event.getAccepted()) {
            Patient patient = patientRepository.findById(event.getPatientId()).orElseThrow(() -> new RuntimeException("Patient should have been added through prior event"));
            Doctor doctor = doctorRepository.findById(event.getDoctorId()).orElseThrow(() -> new RuntimeException("Doctor should have been added through prior event"));
            Appointment appointment = new Appointment(Integer.valueOf(event.getAppointmentRequestNumber()), event.getDay(), event.getRoomId(), event.getAccountId(), patient, doctor);
            appointmentRepository.save(appointment);
        }
    }
}
