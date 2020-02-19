import java.util.ArrayList;

public class Match {
	public Team team1 = new Team();
	public Team team2 = new Team();
	public ArrayList<MapScore> mapScores;
	public String team1Score = "0";
	public String team2Score = "0";
	public String date;
	public String round;
	
	public Match(){
		mapScores = new ArrayList<MapScore>();
		team1 = new Team();
		team2 = new Team();
		date = "";
		round ="";
	}
	
	public Match(Team t1, Team t2){
		this();
		team1 = t1;
		team2 = t2;
	}
	public void addMap(String map, String t1s, String t2s){
		mapScores.add(new MapScore(map, t1s, t2s));
	}
	
	public String toString(){
		String maps = "";
		for(int i = 0; i < mapScores.size(); i++){
			maps += mapScores.get(i).toString() + "\n";
		}
		return date + "\n" + 
				team1.getName() + " vs " + team2.getName() + ": " + team1Score + "-" + team2Score + "\n" +
				maps;
	}
}
