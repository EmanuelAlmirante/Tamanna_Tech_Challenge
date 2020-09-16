package app.controller;

import app.model.candidate.CandidateAvailabilityModel;
import app.model.candidate.CandidateModel;
import app.service.candidate.CandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("tamanna/api/candidates")
public class CandidateController {
    @Autowired
    private CandidateService candidateService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CandidateModel createCandidate(@Valid @RequestBody CandidateModel candidateModel) {
        return candidateService.createCandidate(candidateModel);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CandidateModel> getAllCandidates() {
        return candidateService.getAllCandidates();
    }

    @GetMapping("/{name}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<CandidateModel> getCandidateById(@PathVariable String name) {
        return candidateService.getCandidateByName(name);
    }

    @DeleteMapping("/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCandidateById(@PathVariable String name) {
        candidateService.deleteCandidateByName(name);
    }

    @PostMapping("/availability")
    @ResponseStatus(HttpStatus.CREATED)
    public CandidateAvailabilityModel createCandidateAvailability(
            @Valid @RequestBody CandidateAvailabilityModel candidateAvailabilityModel) {
        return candidateService.createCandidateAvailability(candidateAvailabilityModel);
    }

    @GetMapping("/availability")
    @ResponseStatus(HttpStatus.OK)
    public List<CandidateAvailabilityModel> getAllCandidatesAvailability() {
        return candidateService.getAllCandidatesAvailability();
    }

    @GetMapping("/availability/{name}")
    @ResponseStatus(HttpStatus.OK)
    public CandidateAvailabilityModel getCandidateAvailabilityByName(@PathVariable String name) {
        return candidateService.getCandidateAvailabilityByName(name);
    }

    @DeleteMapping("/availability/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCandidateAvailabilityByName(@PathVariable String name) {
        candidateService.deleteCandidateAvailabilityByName(name);
    }
}
