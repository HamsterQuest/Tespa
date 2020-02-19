import java.util.ArrayList;

public class Tespa {
	
	public ArrayList<Team> teams;
	
	public Tespa() {
		teams = new ArrayList<Team>();
	}
	
	public void add(Team t){
		if(teams.size() == 0){
			teams.add(t);
			return;
		}
		for(Team cur : teams){
			if(t.getAVGsr() >= cur.getAVGsr()) {
				teams.add(teams.indexOf(cur), t);
				return;
			}
		}
		teams.add(t);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(Team t : teams) {
			if(t.getMembers().size()>=6)
				sb.append(t.toString() + System.getProperty("line.separator"));
		}
		return sb.toString();
	}

}
