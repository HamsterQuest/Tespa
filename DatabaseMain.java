import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.*;

public class DatabaseMain {

	public static final String REGIONAL = "125";
	public static final String NATIONAL = "124";

	public static void main(String[] args) throws IOException {
		
		Tespa teams = new Tespa();
		
		// connect to regional or national page
		String tourney = REGIONAL;
		String homePage = con("https://compete.tespa.org/tournament/" + tourney + "/phase/1");

		// regex for dropped teams
		String regexDropped = "<td>(([0-9][0-9]?)|(dropped))</td>";
		Pattern pDrop = Pattern.compile(regexDropped);
		Matcher mDrop = pDrop.matcher(homePage);
		
		// regex for finding team names
		String regexTeamName = "(team-name)..([^<]*?)</span>";
		Pattern pTeamName = Pattern.compile(regexTeamName);
		Matcher mTeamName = pTeamName.matcher(homePage);

		// regex for finding team match scores
		String regexScores = "<td>([0-9]+ - [0-9]+)</td>";
		Pattern pScores = Pattern.compile(regexScores);
		Matcher mScores = pScores.matcher(homePage);

		// regex for finding team URLs
		String combineTest = "";
		//String combineTest = ".*\\s*?[>\\s]([^<]*?)[<\\s]";
		String regexTeam = "(https://compete.tespa.org/tournament/" + tourney + "/team/[0-9]+)" + combineTest;
		Pattern pTeam = Pattern.compile(regexTeam);
		Matcher mTeam = pTeam.matcher(homePage);

		// regex for finding bnets from team page (Matcher must be initialized
		// inside loop)
		String regexUserName = "<td>([^<]*?)#([0-9]*)<";
		Pattern pUserName = Pattern.compile(regexUserName);

		// regex for finding player sr
		String regexUserSR = "<span class=\"player-skill-rating\">([0-9]+)";
		Pattern pUserSR = Pattern.compile(regexUserSR);

		// for each team found
		while (mTeamName.find()) {
			
			// make a new team (with team name)
			Team t = new Team(mTeamName.group(2)); // changed
			//System.out.println(t.getName() + " found");
			
			if(mDrop.find()){
				System.out.println(mDrop.group(1));
				while(mDrop.group(1).equals("dropped")){
					mDrop.find();
					mScores.find();
					mTeam.find();
				}
			}
			// find their scores
			if (mScores.find()) {
				t.setMatchScore(mScores.group(1));
			} else {
				System.out.println("no score found???");
			}
			if (mScores.find()) {
				t.setMapDif(mScores.group(1));
			} else {
				System.out.println("no game found???");
			}

			// find their team page
			if (mTeam.find()) { //from teamURL

				// set and conecc
				t.setTeampage(mTeam.group(1)); // changing to group 1
				//System.out.println(t.getTeampage() + " found");
				String teamPage = con(t.getTeampage());

				// make matcher for the teamPage
				Matcher mUserName = pUserName.matcher(teamPage);

				// find user names on team page
				while (mUserName.find()) {

					// make player, set name
					Player p = new Player(mUserName.group(1) + "#" + mUserName.group(2));
					//System.out.println(p.getBnet());

					// make and connect to player url overbuff
					String pUrl = "https://www.overbuff.com/players/pc/" + mUserName.group(1) + "-" + mUserName.group(2)
							+ "?mode=competitive";
					p.setOverbuff(pUrl);
					String playerPage = con(pUrl);

					// make matcher for the overbuff page
					Matcher mUserSR = pUserSR.matcher(playerPage);

					// find the sr
					if (mUserSR.find()) {
						p.setSr(Integer.parseInt(mUserSR.group(1)));
					} else {
						// sr not found (eternal private profile)
						p.setSr(-1);
					}
					t.add(p);
				}
			}//here for comment
			System.out.println(t.toString());
			teams.add(t);
			
			//System.out.println(t.getName() + " ADDED");
		}
		try (PrintWriter out = new PrintWriter("tespa team list.txt")) {
		    out.println(teams.toString());
		} catch(Exception e) {
			System.out.println("uh oh");
		}
	}

	private static String con(String u) throws IOException {   
		
		String encodedURL = URI.create(u).toASCIIString();
		URL url = new URL(encodedURL);

		BufferedReader in = null;
		try {
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
			conn.connect();
			InputStream is = conn.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			in = new BufferedReader(isr);
		} catch (Exception e) {
			return "No page";
		}
		BufferedReader bufferedReader = new BufferedReader(in);
		StringBuilder inputBuilder = new StringBuilder();
		try {
			inputBuilder = new StringBuilder();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				inputBuilder.append(line);
			}
			bufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return inputBuilder.toString();
	}

}
