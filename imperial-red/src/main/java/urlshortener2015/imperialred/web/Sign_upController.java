package urlshortener2015.imperialred.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class Sign_upController {

	
	@RequestMapping(value = "/sign_up", method = RequestMethod.GET,produces = "text/html")
	public String goTo() {
		System.out.println("sign_up");
		return "sign_up";
	}

}
