package vn.khangktn.jobhunter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.khangktn.jobhunter.domain.Company;
import vn.khangktn.jobhunter.domain.Job;
import vn.khangktn.jobhunter.domain.response.ResJob;
import vn.khangktn.jobhunter.domain.response.ResultPaginationDTO;
import vn.khangktn.jobhunter.repository.CompanyRepository;
import vn.khangktn.jobhunter.repository.JobRepository;
import vn.khangktn.jobhunter.repository.SkillRepository;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;
    private final CompanyRepository companyRepository;

    JobService(JobRepository jobRepository, SkillRepository skillRepository, CompanyRepository companyRepository){
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
        this.companyRepository = companyRepository;
    }

    public ResJob createJob(Job job){
        // Check company
        if(job.getCompany() != null){
            Optional<Company> existCompany = companyRepository.findById(job.getCompany().getId());
            job.setCompany(existCompany.isPresent() ? existCompany.get() : null);
        }

        // Check list skill
        List<Long> idList = new ArrayList<>();
        if(job.getSkills() != null && job.getSkills().size() > 0) job.getSkills().forEach((skill) -> idList.add(skill.getId()));
        job.setSkills(skillRepository.findByIdIn(idList));

        Job newJob = jobRepository.save(job);
        return ResJob.convertToResJob(newJob);
    }

    public ResultPaginationDTO getJobList(Specification<Job> spec, Pageable pageable){
        Page<Job> pageJob = jobRepository.findAll(spec, pageable);

        // Set data for response
        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageJob.getNumber());
        meta.setPageSize(pageJob.getSize());
        meta.setTotalPage(pageJob.getTotalPages());
        meta.setTotalItem(pageJob.getTotalElements());
        result.setMeta(meta);

        // Convert data to DTO
        List<ResJob> resJobs = new ArrayList<>();
        pageJob.getContent().forEach((job) -> resJobs.add(ResJob.convertToResJob(job)));
        result.setResult(resJobs);
        return result;
    }

    public ResJob getJobById(Long id){
        Optional<Job> job = jobRepository.findById(id);
        ResJob resJob = null;
        if(job.isPresent()){
            resJob = new ResJob();
            resJob = ResJob.convertToResJob(job.get());
        }
        return resJob;
    }
}
