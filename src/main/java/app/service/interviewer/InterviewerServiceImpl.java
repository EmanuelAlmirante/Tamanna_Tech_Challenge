package app.service.interviewer;

import app.exception.BusinessException;
import app.model.interviewer.InterviewerModel;
import app.repository.interviewer.InterviewerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InterviewerServiceImpl implements InterviewerService {
    private final InterviewerRepository interviewerRepository;

    @Autowired
    public InterviewerServiceImpl(InterviewerRepository interviewerRepository) {
        this.interviewerRepository = interviewerRepository;
    }

    @Override
    public InterviewerModel createInterviewer(InterviewerModel interviewerModel) {
        verifyValidityOfInterviewer(interviewerModel);

        return interviewerRepository.save(interviewerModel);
    }

    @Override
    public List<InterviewerModel> getAllInterviewers() {
        return interviewerRepository.findAll();
    }

    @Override
    public Optional<InterviewerModel> getInterviewerByName(String name) {
        return interviewerRepository.findById(name);
    }

    @Override
    public void deleteInterviewerByName(String name) {
        interviewerRepository.deleteById(name);
    }

    private void verifyValidityOfInterviewer(InterviewerModel interviewerModel) {
        verifyNameIsFilled(interviewerModel);
        verifyUniqueName(interviewerModel);
    }

    private void verifyNameIsFilled(InterviewerModel interviewerModel) {
        if (interviewerModel.getName() == null || interviewerModel.getName().isBlank()) {
            throw new BusinessException("You must provide a name!",
                                        interviewerModel.getName() != null ? interviewerModel.getName() : null);
        }
    }

    private void verifyUniqueName(InterviewerModel interviewerModel) {
        List<String> existingNames = interviewerRepository.getAllNames();

        if (existingNames.contains(interviewerModel.getName())) {
            throw new BusinessException("Name already exists!", interviewerModel.getName());
        }
    }
}
