package app.controller;

import app.model.interviewslots.InterviewSlotsQueryModel;
import app.model.interviewslots.InterviewSlotsReturnModel;
import app.service.interviewslots.InterviewSlotsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("tamanna/api/interview-slots")
public class InterviewSlotsController {
    @Autowired
    private InterviewSlotsService interviewSlotsService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public InterviewSlotsReturnModel getInterviewSlots(
            @Valid @RequestBody InterviewSlotsQueryModel interviewSlotsQueryModel) {
        return interviewSlotsService.getInterviewSlots(interviewSlotsQueryModel);
    }
}
