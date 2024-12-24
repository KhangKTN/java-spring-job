package vn.khangktn.jobhunter.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;

import jakarta.validation.Valid;
import vn.khangktn.jobhunter.domain.Company;
import vn.khangktn.jobhunter.domain.Job;
import vn.khangktn.jobhunter.domain.Resume;
import vn.khangktn.jobhunter.domain.User;
import vn.khangktn.jobhunter.domain.response.ResResume;
import vn.khangktn.jobhunter.domain.response.ResultPaginationDTO;
import vn.khangktn.jobhunter.service.ResumeService;
import vn.khangktn.jobhunter.service.UserService;
import vn.khangktn.jobhunter.util.SecurityUtil;
import vn.khangktn.jobhunter.util.constant.ResumeStatusConstant;
import vn.khangktn.jobhunter.util.errorException.IdException;




@RestController
@RequestMapping("api/v1/resumes")
public class ResumeController {
    private final ResumeService resumeService;
    private final UserService userService;

    @Autowired
    private FilterSpecificationConverter filterSpecificationConverter;
    @Autowired
    private FilterBuilder filterBuilder;

    ResumeController(ResumeService resumeService, UserService userService){
        this.resumeService = resumeService;
        this.userService = userService;
    }

    @PostMapping("")
    public ResponseEntity<ResResume> createResume(@Valid @RequestBody Resume resume) throws IdException {
        ResResume resResume = resumeService.createResume(resume);
        if(resResume == null) throw new IdException("Cannot create Resume!");
        return ResponseEntity.created(null).body(resResume);
    }

    @GetMapping("/by-user")
    public ResponseEntity<ResultPaginationDTO> getResumeUser(Pageable pageable) {
        ResultPaginationDTO result = resumeService.getResumeByUser(pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("")
    public ResponseEntity<ResultPaginationDTO> getResumeList(@Filter Specification<Resume> spec, Pageable pageable) {
        List<Long> jobIdList = null;
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        User currentUser = userService.getUserByEmail(email);
        if(currentUser != null){
            Company userCompany = currentUser.getCompany();
            if(userCompany != null){
                List<Job> jobList = userCompany.getJobs();
                if(jobList != null && jobList.size() > 0){
                    jobIdList = jobList.stream().map(x -> x.getId()).collect(Collectors.toList());
                }
            }
        }
        Specification<Resume> jobInSpec = filterSpecificationConverter.convert(filterBuilder.field("job")
            .in(filterBuilder.input(jobIdList)).get());
        return ResponseEntity.ok(resumeService.getResumeList(jobInSpec.and(spec), pageable));
    }

    @PutMapping("")
    public ResponseEntity<ResResume> updateResume(@Valid @RequestBody Resume resume) throws IdException {
        // Check for id null
        if(resume.getId() == null) throw new IdException("Misssing required parameter!");

        // Check status is valid
        if(resume.getStatus().equals(ResumeStatusConstant.PENDING) || resume.getStatus().equals(ResumeStatusConstant.REJECTED))
            throw new IdException("Cannot update resume because status isn't valid!");

        ResResume resResume = resumeService.updateResume(resume);
        if(resResume == null) throw new IdException("Resume not found. Cannot update Resume!");
        return ResponseEntity.ok(resResume);
    }
}
