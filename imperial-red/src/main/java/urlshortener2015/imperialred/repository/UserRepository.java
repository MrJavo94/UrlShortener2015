package urlshortener2015.imperialred.repository;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.security.core.userdetails.User;


public interface UserRepository extends MongoRepository<User, BigInteger>  {
	
	User findByUsername(String username);
	
	User save(User u);
	
	void deleteByUsername(BigInteger id);
	
	List<User> findAll();
	
}
