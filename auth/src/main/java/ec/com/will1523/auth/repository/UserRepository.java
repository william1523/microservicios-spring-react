package ec.com.will1523.auth.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import ec.com.will1523.auth.entities.User;

public interface UserRepository extends MongoRepository<User,Integer> {
    
    
}
