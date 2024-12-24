package vn.khangktn.jobhunter.domain;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.khangktn.jobhunter.util.SecurityUtil;
import vn.khangktn.jobhunter.util.constant.GenderConstant;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @NotBlank(message = "Email isn't empty!")
    private String email;

    @NotBlank(message = "Password isn't empty!")
    private String password;

    private int age;

    @Enumerated(EnumType.STRING)
    private GenderConstant gender;

    private String address;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String refreshToken;
    
    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne
    @JoinColumn(name = "role_id")
    @NotNull(message = "Role is required!")
    private Role role;
    
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Resume> resumes;
    
    // @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant createdAt;

    // @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
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
