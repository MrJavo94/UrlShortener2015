package urlshortener2015.imperialred.repository;

import java.math.BigInteger;

import org.springframework.data.mongodb.repository.MongoRepository;

import urlshortener2015.imperialred.objects.User;

public interface UserRepository extends MongoRepository<User, BigInteger>  {
	
	User findByMail(String mail);
	
	User save(User u);
	
	void delete(BigInteger id);
	
	void delete(String mail);
	
}
