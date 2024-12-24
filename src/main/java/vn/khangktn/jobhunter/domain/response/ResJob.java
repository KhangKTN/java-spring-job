package vn.khangktn.jobhunter.domain.response;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.khangktn.jobhunter.domain.Job;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ResJob {
    private long id;
    private String name;
    private double salary;
    private int quantity;
    private Instant startDate;
    private Instant endDate;
    private boolean active = true;
    String company;
    private List<String> skills;

    public static ResJob convertToResJob(Job job){
        ResJob resJob = new ResJob();

        // Get field name of Skill and Set list
        if(job.getSkills() != null){
            List<String> jobsName = new ArrayList<>();
            job.getSkills().forEach((skill) -> jobsName.add(skill.getName()));
            resJob.setSkills(jobsName);
        }

        // Set other Job props
        resJob.setId(job.getId());
        resJob.setName(job.getName());
        resJob.setSalary(job.getSalary());
        resJob.setQuantity(job.getQuantity());
        resJob.setStartDate(job.getStartDate());
        resJob.setEndDate(job.getEndDate());
        resJob.setCompany(job.getCompany().getName());

        return resJob;
    }
}
