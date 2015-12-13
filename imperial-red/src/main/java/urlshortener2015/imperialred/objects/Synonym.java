package urlshortener2015.imperialred.objects;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({ "synonyms", "word" })
public class Synonym {

	@JsonProperty("synonyms")
	private List<String> synonyms = new ArrayList<String>();
	@JsonProperty("word")
	private String word;

	/**
	 * 
	 * @return The synonyms
	 */
	@JsonProperty("synonyms")
	public List<String> getSynonyms() {
		return synonyms;
	}

	/**
	 * 
	 * @param synonyms
	 *            The synonyms
	 */
	@JsonProperty("synonyms")
	public void setSynonyms(List<String> synonyms) {
		this.synonyms = synonyms;
	}

	/**
	 * 
	 * @return The word
	 */
	@JsonProperty("word")
	public String getWord() {
		return word;
	}

	/**
	 * 
	 * @param word
	 *            The word
	 */
	@JsonProperty("word")
	public void setWord(String word) {
		this.word = word;
	}

	@Override
	public String toString() {
		return "Synonym [synonyms=" + synonyms + ", word=" + word + "]";
	}

}