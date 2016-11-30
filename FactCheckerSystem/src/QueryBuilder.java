
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.trees.Tree;

/**
 * QueryBuilder builds query out of parsed string
 * *
 *
 */

public class QueryBuilder {
	//stop word removal?
	private String proj = "count(*) ";
	private String tableName = null;
	private List<String> identities = new ArrayList<String>();
	private List<String> conditions = new ArrayList<String>();
	private boolean negate = false;
	private static Map<String, String> negationMap = getNegationMap();
	private List<String> columnNames;
	private Tree parseTree = null;
	private boolean displayAll = false;
	
	public QueryBuilder(Tree parseTree, String tableName) {
		this.tableName = tableName;
		this.parseTree = parseTree;
	}
	
	private static Map<String, String> getNegationMap() {
		Map<String, String> ret = new HashMap<String, String>();
		ret.put("<", ">=");
		ret.put(">", "<=");
		ret.put("=", "!=");
		ret.put("!=", "=");
		ret.put("<=", ">");
		ret.put(">=", "<");
		return ret;
	}
	
	/**
	 * decides what tree structure it is and build query based on it
	 * handles np + vp structure now
	 */
	public void determineStructure() {
		
		if ("NP".equals(parseTree.firstChild().value())){
			displayAll = true;
			buildNP(parseTree.firstChild());
			return;
		}
		
		Tree S = parseTree.firstChild();
		for (Tree t: S.getChildrenAsList()) {
			//System.out.println(t.value());
			if ("NP".equals(t.value()))
				buildNP(t);
			if ("VP".equals(t.value()))
				buildVP(t);
		}
		
		
	}
	
	public void buildNP(Tree tree) {
		
		List<Tree> leaves = tree.getLeaves();
		
		if (leaves.size() == 2) {
			
			String a = leaves.get(0).value();
			String b = leaves.get(1).value();
			String [] agg = {"max", "min", "avg"};
			for (String i: agg){
				if (i.equals(a)){
					proj =  i + "(" + b +") ";
					return;
				}
				else if (i.equals(b)) {
					proj = i + "(" + a + ") ";
					return;
				}
			}
			
			if (displayAll){
				proj = "* ";
				displayAll = false;
			}
			handlePair(leaves);
		}
		else if (leaves.size() == 3) {
			handleTriple(leaves);
		}
			
	}
		
		
	
	
	public void buildVP(Tree tree) {
		List<Tree> leaves = tree.getLeaves();
		if (leaves.size() == 2){
			handlePair(leaves);
			return;
		}
		else if (leaves.size() == 3) {
			if (negationMap.containsKey(leaves.get(1).value())){
				handleTriple(leaves);
				return;
			}
		}
		List<Tree> children = tree.getChildrenAsList();
		
		for (Tree t: children) {
			String temp = t.value();
			if ("RB".equals(temp)){
				buildRB(t);
			}
			else if ("NP".equals(temp)){
				buildNP(t);
			}
			else if ("VP".equals(temp)){
				buildVP(t);
				
			}
		}
	}
	
	public void buildRB(Tree tree) {
		negate = true;
	}
	
	public String formatConditions() { 
		
		String ret = "";
		for (int i = 0; i < conditions.size(); ++i) {
			if (i == conditions.size() - 1) {
				ret += conditions.get(i);
				break;
			}
			
			ret += conditions.get(i) + " and ";
		}
		
		
		return ret;
	}
	
	public void handlePair(List<Tree> leaves) {
		
		try {
			double val = Double.valueOf(leaves.get(1).value());
			if (!negate)
				conditions.add(leaves.get(0).value() + "=" +val);
			else{
				conditions.add(leaves.get(0).value() + "!=" +val);
				negate = false;
			}
		} catch (NumberFormatException e){
			String val = leaves.get(1).value();
			if (!negate)
				conditions.add(leaves.get(0).value() + "=" + "\"" +val+"\"");
			else{
				conditions.add(leaves.get(0).value() + "!=" + "\"" +val+"\"");
				negate = false;
			}
		} catch(Exception e) {
			System.out.println("could not parse sentence query");
		}
	}
	
	public void handleTriple(List<Tree> leaves) {
		try{
			double val = Double.valueOf(leaves.get(2).value());
			if (!negate){
				conditions.add(leaves.get(0).value() + leaves.get(1).value()+val);
			}
			else {
				conditions.add(leaves.get(0).value() + negationMap.getOrDefault(leaves.get(1).value(), "")+val);
			} 
		}catch (NumberFormatException e){
				String val = leaves.get(1).value();
				if (!negate)
					conditions.add(leaves.get(0).value() + leaves.get(1).value() + "\"" +val+"\"");
				else{
					conditions.add(leaves.get(0).value() + negationMap.getOrDefault(leaves.get(1).value(), "") + "\"" +val+"\"");
					negate = false;
				}
			} catch(Exception e) {
				System.out.println("could not parse sentence query");
			}
		}

	public String buildQuery() {
		determineStructure();
		if (conditions.size() > 0)
			return "select " + proj + "from " + tableName + " where " + formatConditions();
		return "select " + proj + "from " + tableName;
	}
	
}
