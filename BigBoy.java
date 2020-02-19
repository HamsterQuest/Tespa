import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BigBoy {
	
	static ArrayList<Tournament> tourneys;

	public static void main(String[] args) throws Exception{
		
		//list of tourneys
		tourneys = new ArrayList<Tournament>();
		
		//search link and page
		int pageNo = 1;
		final int PAGES = 2;
		String searchURL = "https://compete.tespa.org/tournament?filter_text=&active_tab=past&filter_game%5B%5D=6&page=" + pageNo;
		String searchHTML = "";
		
		ArrayList<String> tLinks = new ArrayList<String>();
		ArrayList<String> tHtml = new ArrayList<String>();
		
		while(pageNo <= PAGES){
			// get html and set matcher
			searchHTML = con(searchURL);

			Matcher tourneyLinkMatcher = reg("https://compete.tespa.org/tournament/\\d+",searchHTML);
			//while there are tourneys
			while(tourneyLinkMatcher.find()){
				//System.out.println(tourneyLinkMatcher.group(0));
				tLinks.add(tourneyLinkMatcher.group());
			}
			//end of page
			pageNo++;
			searchURL = "https://compete.tespa.org/tournament?filter_text=&active_tab=past&filter_game%5B%5D=6&page=" + pageNo;
		}
		
		// addd all the html from those links to a big ass list
		tHtml.addAll(AsyncTest.scrapeAsync(tLinks));
			
		System.out.println("Tournaments: " + tHtml.size());
		
		for(int i = 0; i < tLinks.size(); i++){
			Tournament t = new Tournament();
			t.link = tLinks.get(i);
			System.out.println(t.link);
			//t.link = tourneyLinkMatcher.group(0);
			String TEAM_PAGE_REGEX = "a href=\"(" + t.link + "/team/\\d+)\">([\\[\\]\\w\\s]+)<";
			
			//System.out.println(tournamentPage);
			
			// tournament names
			Matcher nameMatcher = reg("tournament-name\">(.+?)<",tHtml.get(i));
			if(nameMatcher.find()){
				t.name = nameMatcher.group(1);
			}
			System.out.println(t.name);
			
			// prizes
			Matcher prizeMatcher = reg("(\\d+)\\w{2}? [Pp]lace.*?\\n?.*?\\$([\\d,]+) (?:in |per player )?(\\w+)",tHtml.get(i));
			while(prizeMatcher.find()){
				//System.out.println(prizeMatcher.group(1) + " " + prizeMatcher.group(2) + " " + t.link);
				t.addPrize(prizeMatcher.group(1), prizeMatcher.group(2), prizeMatcher.group(3));
				/*for(int i = 0; i < t.prizing.size(); i++){
					System.out.println(i + " " + t.getPrizingString(i+1));
				}*/
			}
			
			// get participants
			ParticipantGetter p = new ParticipantGetter();
			
			t.addAll(p.parse(t.link));
			System.out.println("teams " +t.teams.size());
			
			System.out.println("prize: " + t.getPrizingString(1));
			
			
			//phase page
			String phaseLink = t.link + "/phase/1";
			System.out.println("Phase: " + phaseLink);
			String phasePage = con(phaseLink);
				
			//groups finder
			//https://compete.tespa.org/tournament/125/phase/1/group/1
			Matcher groupMatcher = reg(phaseLink + "/group/\\d+", phasePage);
			System.out.println(phaseLink + "/group/\\d+");
			ArrayList<String> groupLinks = new ArrayList<String>();
			while(groupMatcher.find()){
				//System.out.println("group found: " + groupMatcher.group(0));
				groupLinks.add(groupMatcher.group(0));
			}
				//String groupPage = con(groupMatcher.group(0));
				
				// MAtCh TiMME
				//<a href="https://compete.tespa.org/tournament/125/match/179583">
			System.out.println("scraping group links..");
			ArrayList<String> groupPages = new ArrayList<String>();
			groupPages.addAll(AsyncTest.scrapeAsync(groupLinks));
			
			ArrayList<String> matchLinks = new ArrayList<String>();
			for(String groupPage: groupPages){
				Matcher matchMatcher = reg(t.link + "/match/\\d+", groupPage);
				while(matchMatcher.find()){
					matchLinks.add(matchMatcher.group(0));
					matchMatcher.find(); //skip, because there's two of each?
					
					//String matchPage = con(matchMatcher.group(0)).replaceAll("\\s+", " ");
				}
			}
			System.out.println("Matches: " + matchLinks.size());
			
			System.out.println("scraping match links..");
			ArrayList<String> matchPages = new ArrayList<String>();
			matchPages.addAll(AsyncTest.scrapeAsync(matchLinks));
			
			for(String matchPage : matchPages){
				matchPage = matchPage.replaceAll("\\s+", " ");
				Matcher teamLinkMatcher = reg(TEAM_PAGE_REGEX, matchPage);
				Match m = new Match();
				
				// find teams
				if(teamLinkMatcher.find())
					m.team1 = t.getTeam(teamLinkMatcher.group(1));
				if(teamLinkMatcher.find())
					m.team2 = t.getTeam(teamLinkMatcher.group(1));
				if(!teamLinkMatcher.find()){
					m.team2 = new Team("BYE","BYE");
					m.team1Score = "2";
				} else {
					// if map scores 
					Matcher mapScoreMatcher = reg("(\\d+) : (\\d+)", matchPage);
					Matcher forfeitMatcher = reg("(No-Show)|()", matchPage);
					Matcher mapMatcher = reg("Game \\d+ .+?> ([\\w' ]+) <.+?name\"> ([\\w ]+) ", matchPage);
					Matcher matchTimeMatcher = reg("match-detail__date\"> ([^<]+) ", matchPage);
					if(matchTimeMatcher.find()){
						m.date = matchTimeMatcher.group(1);
					}
					if(mapScoreMatcher.find()){
						m.team1Score = mapScoreMatcher.group(1);
						m.team2Score = mapScoreMatcher.group(2);
						while(mapMatcher.find()){
							MapScore map = new MapScore(mapMatcher.group(1));
							
							if(mapMatcher.group(2).equals(m.team1.getName())){
								map.team1Score = "1";
								map.winnerName = m.team1.getName();
							} else {
								map.team2Score = "2";
								map.winnerName = m.team2.getName();
							}
							m.mapScores.add(map);
						}
					} else if(forfeitMatcher.find()){// no map scores, there was at least one forfeit of no show
						if(matchPage.indexOf("No-Show") > matchPage.indexOf(m.team2.getTeampage())){
							m.team2Score = "F";
						} else {
							m.team1Score = "F";
						}
					}
				} // not a bye
				System.out.println(m.toString());
				t.matches.add(m);
			}
			tourneys.add(t);
		}// tournament
		try (PrintWriter out = new PrintWriter("please work.txt")) {
		    out.println(tourneys.toString());
		} catch(Exception e) {
			System.out.println("uh oh");
		}
	}

    private static Matcher reg(String regex, String text) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);
        return m;
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
