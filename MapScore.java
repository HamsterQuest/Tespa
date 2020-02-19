
public class MapScore {
	String mapName;
	String team1Score;
	String team2Score;
	String winnerName = "draw";
	
	public MapScore(){
		
	}
	public MapScore(String mapName){
		this.mapName = mapName;
		team1Score = "0";
		team2Score = "0";
	}
	public MapScore(String mapName, String team1Score, String team2Score) {
		this.mapName = mapName;
		this.team1Score = team1Score;
		this.team2Score = team2Score;
	}
	
	public String toString() {
		return mapName + ": " + winnerName;
	}
}
