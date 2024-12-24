package vn.khangktn.jobhunter.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import vn.khangktn.jobhunter.domain.Resume;
import vn.khangktn.jobhunter.util.constant.ResumeStatusConstant;

@Getter @Setter
public class ResResume {
    private Long id;
    private String email;
    private ResumeStatusConstant status;
    private UserResume user;
    private String companyName;
    private JobResume job;

    public static ResResume convertToResResume(Resume resume, String companyName){
        ResResume resResume = new ResResume();
        resResume.setId(resume.getId());
        resResume.setEmail(resume.getEmail());

        JobResume jobResume = new JobResume(resume.getJob().getId(), resume.getJob().getName());
        resResume.setCompanyName(companyName);
        resResume.setJob(jobResume);
        resResume.setStatus(resume.getStatus());
        return resResume;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class UserResume {
        private long id;
        private String name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class JobResume {
        private long id;
        private String name;
    }
}
