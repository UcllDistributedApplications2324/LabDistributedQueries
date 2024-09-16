package be.ucll.da.patientservice.adapters.web.incoming;

import be.ucll.da.patientservice.api.PatientApiDelegate;
import be.ucll.da.patientservice.api.model.ApiPatient;
import be.ucll.da.patientservice.domain.Patient;
import be.ucll.da.patientservice.domain.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class PatientController implements PatientApiDelegate {

    private final PatientService patientService;

    @Autowired
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @Override
    public ResponseEntity<ApiPatient> getPatientById(Integer id) {
        Patient patient = patientService.getPatient(id);

        ApiPatient apiPatient = new ApiPatient();
        apiPatient.setPatientId(patient.id());
        apiPatient.setFirstName(patient.firstName());
        apiPatient.setLastName(patient.lastName());
        apiPatient.setEmail(patient.email());

        return ResponseEntity.ok(apiPatient);
    }
}
