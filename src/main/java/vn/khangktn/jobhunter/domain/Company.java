package vn.khangktn.jobhunter.domain;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import vn.khangktn.jobhunter.util.SecurityUtil;

@Entity
@Table(name = "companies")
@Getter @Setter
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Name is required!")
    private String name;
    
    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;
    private String address;
    private String logo;

    // @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant createdAt;
    // @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @OneToMany(mappedBy = "company")
    @JsonIgnore
    private List<User> users;

    @OneToMany(mappedBy = "company")
    @JsonIgnore
    private List<Job> jobs;

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
