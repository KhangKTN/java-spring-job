package vn.khangktn.jobhunter.domain;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import vn.khangktn.jobhunter.util.SecurityUtil;
import vn.khangktn.jobhunter.util.constant.ResumeStatusConstant;

@Entity
@Table(name = "resumes")
@Getter @Setter
public class Resume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String url;

    @Enumerated(EnumType.STRING)
    private ResumeStatusConstant status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull(message = "User_id field is required!")
    User user;

    @ManyToOne
    @JoinColumn(name = "job_id")
    @NotNull(message = "Job field is required!")
    Job job;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @PrePersist
    public void handleCreatedBy(){
        this.setCreatedBy(SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "");
        this.setCreatedAt(Instant.now());
    }

    @PreUpdate
    public void handleUpdate(){
        this.setUpdatedAt(Instant.now());
        this.setUpdatedBy(SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "");
    }
}
