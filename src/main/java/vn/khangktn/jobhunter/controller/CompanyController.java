package vn.khangktn.jobhunter.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.khangktn.jobhunter.domain.Company;
import vn.khangktn.jobhunter.domain.response.ResultPaginationDTO;
import vn.khangktn.jobhunter.service.CompanyService;
import vn.khangktn.jobhunter.util.annotation.ApiMessage;
import vn.khangktn.jobhunter.util.errorException.IdException;


@RestController
@RequestMapping("api/v1/companies")
public class CompanyController {
    private final CompanyService companyService;

    CompanyController(CompanyService companyService){
        this.companyService = companyService;
    }

    @PostMapping("")
    public ResponseEntity<Company> createCompany(@Valid @RequestBody Company company){
        Company newCompany = this.companyService.createCompany(company);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCompany);
    }

    @GetMapping("")
    @ApiMessage("Fetch companies data successfully!")
    public ResponseEntity<ResultPaginationDTO> getCompanyList(
        @Filter Specification<Company> spec,
        Pageable pageable
    ){
        /* String pageString = page.isPresent() ? page.get() : "0";
        String pageSizeString = pageSize.isPresent() ? pageSize.get() : "5";
        Pageable pageable = PageRequest.of(Integer.parseInt(pageString) - 1, Integer.parseInt(pageSizeString)); */
        
        return ResponseEntity.ok(this.companyService.getCompanyList(spec, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Company> getCompanyById(@PathVariable("id") Long id) {
        Company company = this.companyService.getCompanyById(id);
        return ResponseEntity.ok(company);
    }
    
    @PutMapping("")
    public ResponseEntity<Company> updateCompany(@RequestBody Company company){
        return ResponseEntity.ok(this.companyService.updateCompany(company));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Company> deleteCompany(@PathVariable("id") String id) throws IdException{
        try {
            long companyId = Long.parseLong(id);
            return ResponseEntity.ok(this.companyService.deleteCompany(companyId));
        } catch (Exception e) {
            throw new IdException("Id must is numberic!");
        }
    } 
}
