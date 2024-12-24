package vn.khangktn.jobhunter.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.khangktn.jobhunter.domain.Skill;
import vn.khangktn.jobhunter.domain.response.ResultPaginationDTO;
import vn.khangktn.jobhunter.service.SkillService;
import vn.khangktn.jobhunter.util.annotation.ApiMessage;
import vn.khangktn.jobhunter.util.errorException.IdException;

@Controller
@RequestMapping("api/v1/skills")
public class SkillController {
    private final SkillService skillService;

    SkillController(SkillService skillService){
        this.skillService = skillService;
    }

    @GetMapping("")
    public ResponseEntity<ResultPaginationDTO>  getSkillList(@Filter Specification<Skill> spec, Pageable pageable) {
        ResultPaginationDTO result = skillService.getSkillList(spec, pageable);
        return ResponseEntity.ok(result);
    }

    @PostMapping("")
    @ApiMessage("Create new skill succeed!")
    public ResponseEntity<Skill> postMethodName(@Valid @RequestBody Skill skill) throws Exception {
        boolean existSkillName = skillService.existSkillByName(skill.getName());
        if(existSkillName) throw new IdException("Name cannot duplicate!");
        Skill newSkill = skillService.createSkill(skill);
        return ResponseEntity.created(null).body(newSkill);
    }

    @PutMapping("")
    @ApiMessage("Update new skill succeed!")
    public ResponseEntity<Skill> putMethodName(@Valid @RequestBody Skill skill) throws Exception {
        if(skill.getId() == null) throw new IdException("Missing required parameter!");
        Skill currentSkill = skillService.getSkillById(skill.getId());
        if(currentSkill == null) throw new IdException("Cannot find skill to update!");
        boolean existSkillName = skillService.existSkillByName(skill.getName());
        if(existSkillName) throw new IdException("Skill Name cannot duplicate!");
        Skill newSkill = skillService.createSkill(skill);
        return ResponseEntity.ok(newSkill);
    }
}
