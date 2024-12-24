package vn.khangktn.jobhunter.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import vn.khangktn.jobhunter.domain.Skill;

public interface SkillRepository extends JpaRepository<Skill, Long>, JpaSpecificationExecutor<Skill> {
    boolean existsByName(String name);
    List<Skill> findByIdIn(List<Long> idList);
}
