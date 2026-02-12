package ec.com.will1523.auth.controllers;


import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import ec.com.will1523.auth.entities.User;
import ec.com.will1523.auth.repository.UserRepository;
import ec.com.will1523.auth.services.SequenceGeneratorService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;




@RestController
public class UserController {
    
    @Autowired
	private SequenceGeneratorService service;

    @Autowired
    private UserRepository usrRepo;
    
    @GetMapping(path = "hello")
    public String sayHello(){
        return "Hello";
    }

    @PostMapping("register")
    public User singUp(@RequestBody User entity) {
        entity.setCreationDate(new Date());
        entity.setId(service.getSequenceNumber("usuario"));
        
        return usrRepo.save(entity);
       
    }

    @PostMapping("update/{id}")
    
    public User update(@PathVariable("id") Integer id, @RequestBody User entity) {
        User entityToChange=usrRepo.findById(id).get();
        if(entityToChange!=null){
            entityToChange.setModificationDate(new Date());
        }
        
        return usrRepo.save(entity);
       
    }

    @GetMapping(path = "list")
    public List<User> getAll(){
        return usrRepo.findAll();
    }
    
}
