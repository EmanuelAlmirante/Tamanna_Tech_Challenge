package app.controller;

import app.model.interviewer.InterviewerModel;
import app.service.interviewer.InterviewerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("tamanna/api/interviewers")
public class InterviewerController {
    @Autowired
    private InterviewerService interviewerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InterviewerModel createInterviewer(@Valid @RequestBody InterviewerModel interviewerModel) {
        return interviewerService.createInterviewer(interviewerModel);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InterviewerModel> getAllInterviewers() {
        return interviewerService.getAllInterviewers();
    }

    @GetMapping("/{name}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<InterviewerModel> getInterviewerById(@PathVariable String name) {
        return interviewerService.getInterviewerByName(name);
    }

    @DeleteMapping("/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteInterviewerById(@PathVariable String name) {
        interviewerService.deleteInterviewerByName(name);
    }
}
