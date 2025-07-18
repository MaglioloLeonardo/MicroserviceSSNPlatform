package tassproject.anagraficaservice;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tassproject.anagraficaservice.CitizenResponse;
import tassproject.anagraficaservice.Citizen;
import tassproject.anagraficaservice.CitizenRepository;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class AnagraficaApplicationService {

    private final CitizenRepository citizens;

    public AnagraficaApplicationService(CitizenRepository citizens) {
        this.citizens = citizens;
    }

    public CitizenResponse getCitizen(UUID id) {
        Citizen c = citizens.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Citizen not found"));
        return CitizenResponse.from(c);
    }

    public List<CitizenResponse> listCitizens(List<UUID> ids) {
        return citizens.findAllById(ids).stream()
                .map(CitizenResponse::from)
                .toList();
    }
}
