package vn.khangktn.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.turkraft.springfilter.converter.FilterSpecification;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.node.FilterNode;

import vn.khangktn.jobhunter.domain.Job;
import vn.khangktn.jobhunter.domain.Resume;
import vn.khangktn.jobhunter.domain.User;
import vn.khangktn.jobhunter.domain.response.ResResume;
import vn.khangktn.jobhunter.domain.response.ResultPaginationDTO;
import vn.khangktn.jobhunter.repository.JobRepository;
import vn.khangktn.jobhunter.repository.ResumeRepository;
import vn.khangktn.jobhunter.repository.UserRepository;
import vn.khangktn.jobhunter.util.SecurityUtil;
import vn.khangktn.jobhunter.util.constant.ResumeStatusConstant;

@Service
public class ResumeService {
    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    @Autowired
    FilterParser filterParser;

    @Autowired
    FilterSpecificationConverter filterSpecificationConverter;

    ResumeService(ResumeRepository resumeRepository, UserRepository userRepository, JobRepository jobRepository){
        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }

    public ResResume createResume(Resume resume){
        // Check exist username
        Optional<User> userOptional = userRepository.findById(resume.getUser().getId());
        if(!userOptional.isPresent()) return null;
        resume.setUser(userOptional.get());
        resume.setEmail(userOptional.get().getEmail());

        // Check exist job
        Optional<Job> jobOptional = jobRepository.findById(resume.getJob().getId());
        if(!jobOptional.isPresent()) return null;
        resume.setJob(jobOptional.get());

        resume.setStatus(ResumeStatusConstant.PENDING);
        Optional<Job> job = jobRepository.findById(resume.getJob().getId());
        return ResResume.convertToResResume(resumeRepository.save(resume), job.get().getCompany().getName());
    }

    public ResResume getResumeById(Long id){
        Optional<Resume> resume = resumeRepository.findById(id);
        if(resume.isPresent()){
            Optional<Job> job = jobRepository.findById(resume.get().getJob().getId());
            return ResResume.convertToResResume(resume.get(), job.get().getCompany().getName());
        }
        return null;
    }

    public ResultPaginationDTO getResumeByUser(Pageable pageable){
        // Get email user from 
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        FilterNode node = filterParser.parse("email='" + email + "'");
        FilterSpecification<Resume> spec = filterSpecificationConverter.convert(node);

        Page<Resume> pageResume = resumeRepository.findAll(spec, pageable);
        
        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageResume.getNumber());
        meta.setPageSize(pageResume.getSize());
        meta.setTotalPage(pageResume.getTotalPages());
        meta.setTotalItem(pageResume.getTotalElements());
        result.setMeta(meta);

        List<ResResume> listResume = pageResume.stream()
            .map(resume -> {
                Optional<Job> job = jobRepository.findById(resume.getJob().getId());
                return ResResume.convertToResResume(resume, job.get().getCompany().getName());
            })
            .collect(Collectors.toList());
        
        result.setResult(listResume);
        return result;
    }

    public ResResume updateResume(Resume resume){
        Optional<Resume> resumeOptional = resumeRepository.findById(resume.getId());
        if(!resumeOptional.isPresent()) return null;

        Resume resumeUpdate = resumeOptional.get();
        resumeUpdate.setStatus(resume.getStatus());

        Optional<User> userOptional = userRepository.findById(resumeUpdate.getUser().getId());
        if(userOptional.isPresent()) resumeUpdate.setEmail(userOptional.get().getEmail());
        Optional<Job> job = jobRepository.findById(resume.getJob().getId());
        resumeRepository.save(resumeUpdate);
        return ResResume.convertToResResume(resumeUpdate, job.get().getCompany().getName());
    }

    public ResultPaginationDTO getResumeList(Specification<Resume> spec, Pageable pageable){
        Page<Resume> pageResume = resumeRepository.findAll(spec, pageable);
        
        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageResume.getNumber());
        meta.setPageSize(pageResume.getSize());
        meta.setTotalPage(pageResume.getTotalPages());
        meta.setTotalItem(pageResume.getTotalElements());
        result.setMeta(meta);

        List<ResResume> listResume = pageResume.stream()
            .map(resume -> {
                Optional<Job> job = jobRepository.findById(resume.getJob().getId());
                return ResResume.convertToResResume(resume, job.get().getCompany().getName());
            })
            .collect(Collectors.toList());
        
        result.setResult(listResume);

        return result;
    }
}
