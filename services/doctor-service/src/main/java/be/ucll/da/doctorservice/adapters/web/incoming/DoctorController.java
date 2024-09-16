package be.ucll.da.doctorservice.adapters.web.incoming;

import be.ucll.da.doctorservice.api.DoctorApiDelegate;
import be.ucll.da.doctorservice.api.model.ApiDoctor;
import be.ucll.da.doctorservice.api.model.DoctorOnPayroll;
import be.ucll.da.doctorservice.domain.Doctor;
import be.ucll.da.doctorservice.domain.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class DoctorController implements DoctorApiDelegate {

    private final DoctorService doctorService;

    @Autowired
    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @Override
    public ResponseEntity<ApiDoctor> getDoctorById(Integer id) {
        Doctor doctor = doctorService.getDoctor(id)
                .orElseThrow(() -> new RuntimeException("Doctor does not exist"));

        ApiDoctor apiDoctor = new ApiDoctor();
        apiDoctor.id(doctor.id());
        apiDoctor.firstName(doctor.firstName());
        apiDoctor.lastName(doctor.lastName());
        apiDoctor.age(doctor.age());
        apiDoctor.address(doctor.address());
        apiDoctor.setFieldOfExpertise(doctor.fieldOfExpertise());

        return ResponseEntity.ok(apiDoctor);
    }
}
