package urlshortener2015.imperialred.repository;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import urlshortener2015.imperialred.objects.User;

@Repository
public interface UserRepository extends MongoRepository<User, BigInteger>  {
	
	User findByMail(String mail);
	
	User findByProvider(Serializable provider);
	
	User save(User u);
	
	void deleteById(BigInteger id);
	
	void deleteByMail(String mail);
	
	List<User> findAll();
	
}