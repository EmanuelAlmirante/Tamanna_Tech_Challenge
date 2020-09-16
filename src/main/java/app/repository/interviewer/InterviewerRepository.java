package app.repository.interviewer;

import app.model.interviewer.InterviewerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewerRepository extends JpaRepository<InterviewerModel, String> {
    @Query("select i.name from InterviewerModel i")
    List<String> getAllNames();
}
