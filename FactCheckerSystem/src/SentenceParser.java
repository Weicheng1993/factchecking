/**
 * Parse a sentence to various formats
 * @author yuweicheng
 *
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.mysql.jdbc.Connection;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import edu.stanford.nlp.ling.CoreLabel;

import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;

public class SentenceParser {
	static final String parserModel = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
	static final LexicalizedParser lexicalizedParser = LexicalizedParser.loadModel(parserModel);
	
	private String tableName = "students";
	private List<String> schema = null;
	static final List<String> stopWords = stopWordList() ;
	static final Map<String, String> logicalTranslation = logical();
	
	private String sentence;
	private String parsedSentence;
	private String [] tokenizedSentence = null;
	private Tree parseTree = null;
	private List<CoreLabel> rawWords = null;
	
	private DBConnector dbConnection = null;
		
	private QueryBuilder QB = null;
	private String query; 	//query stored inside after feedSentence
	private List<String> columnNames = new ArrayList<String>();
	
	public SentenceParser() {
		dbConnection = new JDBC();
		dbConnection.openDefaultConnection();
	}
	
	/**
	 * Give an English sentence and process the sentence and return a query built from QueryBuider
	 * @param sent
	 */
	public void feedSentence(String sent) {
		//feed sentence with querying
		sentence = sent;
		parsedSentence = new String(sent);
		parse();
	}
	
	
	/**
	 * handles input sentence and stores a parsetree in QueryBuilder
	 */
	public void parse() {
		logicalOperatorTranslation();
		tokenize();
		stopWordRemoval();
		rawWords = Sentence.toCoreLabelList(tokenizedSentence);
	    parseTree = lexicalizedParser.apply(rawWords);
	   // System.out.println(parseTree.value());
	    QB = new QueryBuilder(parseTree, tableName);
	    query = QB.buildQuery();
	    for (String i: tokenizedSentence) {
	    	System.out.print(i + " ");
	    }
	    System.out.println();
	    
	    
	}
	
	private static Map<String, String> logical() {
		Map<String, String> ret = new HashMap<String,String>();
		ret.put("greater than", ">");
		ret.put("highest", "max");
		ret.put("less than", "<");
		ret.put("lowest", "min");
		ret.put("equal to", "=");
		ret.put("maximum", "max");
		ret.put("minimun", "min");
		ret.put("mean", "avg");
		ret.put("average", "avg");
		return ret;
	}
	private static List<String> stopWordList() {
		String [] s= "the there a an about this that these those".split("\\s+");
		return Arrays.asList(s);
	}
	
	
	
	/**
	 * tokenize sentence into list of words
	 */
	public void tokenize() {
		tokenizedSentence = parsedSentence.trim().split("\\s+");
	}
	
	/**
	 * remove stopwords like the, a, an to simplify structure and query building  process
	 */
	public void stopWordRemoval() {
		List<String> ret = new ArrayList<String>();
		for (String i: tokenizedSentence) {
			if (!stopWords.contains(i)) {
				ret.add(i);
			}
		}
		tokenizedSentence = ret.toArray(new String[ret.size()]);
	}
	
	/**
	 * naively replace comparator phrases with logical signs, e.g., (more than) => (>) 
	 */
	public void logicalOperatorTranslation() {
		for (Map.Entry<String, String> e: logicalTranslation.entrySet()) {
			parsedSentence = parsedSentence.replaceAll(e.getKey(), e.getValue());
		}
	}
	
	
	/**
	 * Execute parsed query handled by feedSentence
	 * @return ResultSet if query parsed is legal otherwise return null
	 */
	public ResultSet execQuery() {
		
		return dbConnection.execQuery(query);
		
	}
	public ResultSet execQuery(String query) {
		try{
			return dbConnection.execQuery(query);
		} catch (Exception e) {
			System.out.println("cannot parse query");
			return null;
		}
	}
	
	public void printResultQuery(ResultSet rs) {
		dbConnection.printQueryResult(rs);
	}
	
	
	
	
	//getters 
	/**
	 * 
	 * @return parsetree object defined by stanford parser
	 */
	public Tree getParseTree() {
		// TODO Auto-generated method stub
		return parseTree;
	}
	
	public String getQuery(){
		return query;
	}
	
	public String getFeedSentence() {
		return sentence;
	}
	
}
