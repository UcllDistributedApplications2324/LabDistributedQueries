package be.ucll.da.apigateway.cqrs;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDate;

@Entity
public class Appointment {

    @Id
    private Integer id;

    private LocalDate appointmentDay;

    private Integer roomId;

    private Integer accountId;

    @ManyToOne
    private Patient patient;

    @ManyToOne
    private Doctor doctor;

    protected Appointment() {}

    public Appointment(Integer id, LocalDate day, Integer roomId, Integer accountId, Patient patient, Doctor doctor) {
        this.id = id;
        this.appointmentDay = day;
        this.roomId = roomId;
        this.accountId = accountId;
        this.patient = patient;
        this.doctor = doctor;
    }

    public Integer getId() {
        return id;
    }

    public LocalDate getDay() {
        return appointmentDay;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public Patient getPatient() {
        return patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }
}
