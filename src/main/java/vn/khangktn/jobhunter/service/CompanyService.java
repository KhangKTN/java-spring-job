package vn.khangktn.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.khangktn.jobhunter.domain.Company;
import vn.khangktn.jobhunter.domain.User;
import vn.khangktn.jobhunter.domain.response.ResultPaginationDTO;
import vn.khangktn.jobhunter.repository.CompanyRepository;
import vn.khangktn.jobhunter.repository.UserRepository;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    CompanyService(CompanyRepository companyRepository, UserRepository userRepository){
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public Company createCompany(Company company){
        return this.companyRepository.save(company);
    }

    public Company getCompanyById(Long id){
        Optional<Company> company = this.companyRepository.findById(id);
        return company.isPresent() ? company.get() : null;
    }

    public ResultPaginationDTO getCompanyList(Specification<Company> spec, Pageable pageable){
        Page<Company> pageCompany = this.companyRepository.findAll(spec, pageable);
        
        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageCompany.getNumber());
        meta.setPageSize(pageCompany.getSize());
        meta.setTotalPage(pageCompany.getTotalPages());
        meta.setTotalItem(pageCompany.getTotalElements());
        result.setMeta(meta);

        result.setResult(pageCompany.getContent());
        return result;
    }

    public Company updateCompany(Company company){
        Optional<Company> existCompany = this.companyRepository.findById(company.getId());
        if(existCompany.isPresent()){
            Company newCompany = new Company();
            newCompany.setName(company.getName());
            newCompany.setAddress(company.getAddress());
            newCompany.setDescription(company.getDescription());
            newCompany.setLogo(company.getLogo());
            return this.companyRepository.save(newCompany);
        }
        return null;
    }

    public Company deleteCompany(Long companyId){
        Optional<Company> existCompany = this.companyRepository.findById(companyId);
        
        // Delete all user before
        if(existCompany.isPresent()){
            List<User> usersDelete = this.userRepository.findByCompany(existCompany.get());
            this.userRepository.deleteAll(usersDelete);
            this.companyRepository.deleteById(companyId);
        }
        return existCompany.isPresent() ? existCompany.get() : null;
    }
}
