package vn.khangktn.jobhunter.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.khangktn.jobhunter.domain.Job;
import vn.khangktn.jobhunter.domain.response.ResJob;
import vn.khangktn.jobhunter.domain.response.ResultPaginationDTO;
import vn.khangktn.jobhunter.service.JobService;




@Controller
@RequestMapping("api/v1/jobs")
public class JobController {
    private final JobService jobService;

    JobController(JobService jobService){
        this.jobService = jobService;
    }

    @PostMapping("")
    public ResponseEntity<ResJob> postMethodName(@Valid @RequestBody Job job) {
        ResJob newJob = jobService.createJob(job);
        return ResponseEntity.created(null).body(newJob);
    }

    @GetMapping("")
    public ResponseEntity<ResultPaginationDTO> getJobList(@Filter Specification<Job> spec, Pageable pageable) {
        return ResponseEntity.ok(jobService.getJobList(spec, pageable));
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<ResJob> getMethodName(@PathVariable("jobId") Long jobId) {
        return ResponseEntity.ok(jobService.getJobById(jobId));
    }
    
}
