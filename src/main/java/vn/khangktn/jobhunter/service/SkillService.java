package vn.khangktn.jobhunter.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.khangktn.jobhunter.domain.Skill;
import vn.khangktn.jobhunter.domain.response.ResultPaginationDTO;
import vn.khangktn.jobhunter.repository.SkillRepository;

@Service
public class SkillService {
    private final SkillRepository skillReposity;

    SkillService(SkillRepository skillReposity){
        this.skillReposity = skillReposity;
    }

    public Skill createSkill(Skill skill){
        boolean existSkillName = this.skillReposity.existsByName(skill.getName());
        return existSkillName ? null : skillReposity.save(skill);
    }

    public ResultPaginationDTO getSkillList(Specification<Skill> spec, Pageable pageable){
        Page<Skill> pageSkill = skillReposity.findAll(spec, pageable);

        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageSkill.getNumber());
        meta.setPageSize(pageSkill.getSize());
        meta.setTotalPage(pageSkill.getTotalPages());
        meta.setTotalItem(pageSkill.getTotalElements());
        result.setMeta(meta);

        result.setResult(pageSkill.getContent());
        return result;
    }

    public Skill getSkillById(Long id){
        return skillReposity.findById(id).get();
    }

    public boolean existSkillByName(String name){
        return skillReposity.existsByName(name);
    }

    public Skill updateSkill(Skill skill){
        Optional<Skill> currentSkill = this.skillReposity.findById(skill.getId());
        if(currentSkill.isPresent()){
            currentSkill.get().setName(skill.getName());
        }
        return currentSkill.isPresent() ? null : skillReposity.save(currentSkill.get());
    }
}
