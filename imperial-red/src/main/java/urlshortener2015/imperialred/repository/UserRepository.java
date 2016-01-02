package urlshortener2015.imperialred.repository;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import urlshortener2015.imperialred.objects.User;

public interface UserRepository extends MongoRepository<User, BigInteger>  {
	
	User findByMail(String mail);
	
	User findByNick(String nick);
	
	User findByTwitter(String twitter);
	
	User save(User u);
	
	void deleteById(BigInteger id);
	
	void deleteByMail(String mail);
	
	void deleteByNick(String nick);
	
	List<User> findAll();
	
}
