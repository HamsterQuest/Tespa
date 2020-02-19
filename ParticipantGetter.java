import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParticipantGetter{
	public ArrayList<Team> parse(String url) throws Exception{
		String TEAM_PAGE_REGEX = "a href=\"(" + url + "/team/\\d+)\">(.+?)<";
		String startPageUrl = url+"/registrants?page=1";
		ArrayList<String> teamUrls = new ArrayList<String>();
		ArrayList<String> teamNames = new ArrayList<String>();
		
		ArrayList<Team> allTeamsList = new ArrayList<Team>();
		// go through participant pages
		
		String startPageHtml = con(startPageUrl);
		Matcher maxMatcher = reg("/registrants\\?page=(\\d+)",startPageHtml);
		int max = 1; // by default will run once
		String lastUrl = "";
		// find the last page
		while(maxMatcher.find()){
			if(Integer.parseInt(maxMatcher.group(1)) > max){
				max = Integer.parseInt(maxMatcher.group(1));
				lastUrl = url + maxMatcher.group(0);
			}
			//System.out.println("last url" + lastUrl);
		}
		// create list of links for async
		ArrayList<String> pLinks = new ArrayList<String>();
		for(int i = 1; i <= max; i++){
			pLinks.add(url+"/registrants?page=" + i);
		}
		
		System.out.println("participants list size " + pLinks.size());
		
		// get htmls
		ArrayList<String> pageHtmls = new ArrayList<String>();
		System.out.println("Scraping participants...");
		pageHtmls.addAll(AsyncTest.scrapeAsync(pLinks));
		System.out.println("page's size: " + pageHtmls.size());
		
		//for each html
		for(String curPageHtml: pageHtmls){
			
			//find each team page
			Matcher teamLinkMatcher = reg(TEAM_PAGE_REGEX, curPageHtml);
			while(teamLinkMatcher.find()){
				// create list of links for async
				teamUrls.add(teamLinkMatcher.group(1));
				teamNames.add(teamLinkMatcher.group(2));
			}
		}
		System.out.println("team size: " + teamUrls.size());
		//System.out.println(teamNames.toString());
		
		// get htmls
		ArrayList<String> teamPages = new ArrayList<String>();
		System.out.println("Scraping team pages...");
		teamPages.addAll(AsyncTest.scrapeAsync(teamUrls));
		System.out.println("scraped!");
		for(int i = 0; i < teamUrls.size(); i++){
			Team t = new Team(teamNames.get(i)); // team name
			t.setTeampage(teamUrls.get(i));
			//find each player
			Matcher playerNameMatcher = reg(TConstants.PLAYER_NAME_REGEX, teamPages.get(i));
			if(teamPages.get(i).contains("coordinator") && playerNameMatcher.find()){
				Player p = new Player(playerNameMatcher.group(1));
				p.setRole("Coordinator");
				t.add(p);
			}
			int players = 0;
			while(playerNameMatcher.find()){
				Player p = new Player(playerNameMatcher.group(1));
				if(players == 0){
					p.setRole("Captain");
				}
				t.add(p);
			}
			//System.out.println(t.toString());
			allTeamsList.add(t);
		}
		System.out.println(allTeamsList.size());
		return allTeamsList;
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
