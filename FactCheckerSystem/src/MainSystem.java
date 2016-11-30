import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.BreakIterator;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.process.DocumentPreprocessor;







public class MainSystem {
	
	public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		// TODO Auto-generated method stub
//		

		String text = "this is a.   this is b. OK ? ";
		
		tokenize(text);
		
		
	}

	public static void tokenize(String paragraph) {
		Reader reader = new StringReader(paragraph);
		DocumentPreprocessor dp = new DocumentPreprocessor(reader);
		List<String> sentenceList = new ArrayList<String>();

		for (List<HasWord> sentence : dp) {
		   String sentenceString = Sentence.listToString(sentence);
		   sentenceList.add(sentenceString);
		}

		for (String sentence : sentenceList) {
		   System.out.println(sentence);
		}
	}

}	


