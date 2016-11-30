import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.process.DocumentPreprocessor;

/**
 * Parse document into sentences.
 * @author yuweicheng
 *
 */
public class DocumentParser {
	private String sentences;
	
	public DocumentParser(String sentences) {
		this.sentences = sentences;
	}
	
	
	public List<String> tokenize(String paragraph) {
		Reader reader = new StringReader(paragraph);
		DocumentPreprocessor dp = new DocumentPreprocessor(reader);
		List<String> sentenceList = new ArrayList<String>();

		for (List<HasWord> sentence : dp) {
		   String sentenceString = Sentence.listToString(sentence);
		   sentenceList.add(sentenceString);
		}

		return sentenceList;
	}

        

	
	public List<String> getTokenized() {
		return null;
	}
}
