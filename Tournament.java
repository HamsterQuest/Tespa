import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Tournament {
	public Map<String, Team> teams;
	public String name;
	public String reg;
	public String duration;
	public ArrayList<Match> matches;
	public Map<Integer, String> prizing;
	public String link;
	
	public Tournament() {
		teams = new HashMap<String, Team>();
		prizing = new HashMap<Integer, String>();
		matches = new ArrayList<Match>();
	}
	public Tournament(String link){
		this();
		this.link = link;
	}
	
	public void add(Team t){
		this.teams.put(t.getTeampage(),t);
	}
	public void addAll(ArrayList<Team> ts){
		for(Team t: ts){
			teams.put(t.getTeampage(), t);
		}
	}
	public Team getTeam(String link){
		return teams.get(link);
	}
	public int getPrizing(int placement){
		if(prizing.isEmpty()){
			return 0;
		}
		if(placement <= prizing.size() && placement > 0){
			return Integer.parseInt(prizing.get(placement).split(" ")[0]);
		}
		return 0;
	}
	public String getPrizingString(int placement){
		if(placement <= prizing.size() && placement > 0){
			return prizing.get(placement);
		}
		return "0";
	}
	public void addPrize(String place, String amount, String currency){
		int placement = Integer.parseInt(place);
		for(int i = prizing.size()+1; i <= placement; i++){
			prizing.put(i, amount.replaceAll(",", "") + " " +currency);
		}
	}
	@Override
	public String toString() {
		String ret = "Tournament [name=" + name + 
				 ", prizing=" + prizing.toString() + ", link=" + link + "]" + "\n";
		for(Match m : matches){
			ret += m.toString() + "/n";
		}
		return ret;
	}
}
