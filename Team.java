import java.util.ArrayList;

public class Team {
	private ArrayList<Player> members;
	private Player Captain;
	private Player Coordinator;
	private String teampage;
	private String name;
	private String matchScore;
	private String mapDif;
	public School school;

	public Team() {
		members = new ArrayList<Player>();
		name = "Bye";
	}

	public Team(String n) {
		name = n;
		members = new ArrayList<Player>();
	}
	public Team(String n, String link) {
		name = n;
		teampage = link;
		members = new ArrayList<Player>();
	}

	// adds sorted
	public void add(Player p) {
		if (members.size() == 0) {
			members.add(p);
			return;
		}
		for(Player cur : members) {
			if (p.getSr() >= cur.getSr()) {
				members.add(members.indexOf(cur), p);
				return;
			}
		}
		members.add(p);
	}

	public ArrayList<Player> getMembers() {
		return members;
	}

	public Player getCaptain() {
		return Captain;
	}

	public void setCaptain(Player captain) {
		Captain = captain;
	}

	public Player getCoordinator() {
		return Coordinator;
	}

	public void setCoordinator(Player coordinator) {
		Coordinator = coordinator;
	}

	public int getAVGsr() {
		int total = 0;
		int count = 0;
		for (Player p : members) {
			if(count == 6){
				break;
			}
			if (p.getSr() > 0) { // some srs won't be found
				total += p.getSr();
				count++;
			}
		}
		if(count == 0) {
			return 0;
		}
		return total / count;
	}

	public String getTeampage() {
		return teampage;
	}

	public void setTeampage(String teampage) {
		this.teampage = teampage;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMatchScore() {
		return matchScore;
	}

	public void setMatchScore(String matchScore) {
		this.matchScore = matchScore;
	}

	public String getMapDif() {
		return mapDif;
	}

	public void setMapDif(String mapDif) {
		this.mapDif = mapDif;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%1$-30s ", name) + "\t" + "Matches: " + matchScore + " " + "Games: " + mapDif
				+ System.getProperty("line.separator"));
		sb.append(String.format("%24s ", "------TEAM AVERAGE------") + "\t" + getAVGsr() + System.getProperty("line.separator"));
		for (int i = 0; i < members.size() && i < 6; i++) {
			sb.append(members.get(i).toString());
		}
		return sb.toString();
	}
}
