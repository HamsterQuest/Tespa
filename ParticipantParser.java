import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParticipantParser {
	
	private static boolean SEARCH = false;

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		int curPageNumber = 1;
		String curPageUrl = "https://compete.tespa.org/tournament/145/registrants?page=1";
		String teamPageUrl, curPageHtml, teamPageHtml, overBuffUrl, overBuffHtml = "";
		String teamNameSearch = "";
		boolean hasNextPage = true;
		
		if(SEARCH){
			Scanner scan = new Scanner(System.in);
			System.out.println("Paste Team Name: ");
			teamNameSearch = scan.nextLine();
		}
		
		Tespa allTeamsList = new Tespa();
		while(hasNextPage){
			curPageHtml = con(curPageUrl);
			Matcher teamLinkMatcher = reg(TConstants.TEAM_PAGE_REGEX, curPageHtml);
			while(teamLinkMatcher.find()){
				if(teamLinkMatcher.group(2).contains(teamNameSearch) || !SEARCH){
					Team t = new Team(teamLinkMatcher.group(2)); // team name
					teamPageUrl = teamLinkMatcher.group(1);
					teamPageHtml = con(teamPageUrl);
					Matcher playerNameMatcher = reg(TConstants.PLAYER_NAME_REGEX, teamPageHtml);
					if(teamPageHtml.contains("coordinator")){playerNameMatcher.find();} // skip coordinator
					while(playerNameMatcher.find()){
						Player p = new Player(playerNameMatcher.group(1));
						overBuffUrl = "https://www.overbuff.com/players/pc/" + playerNameMatcher.group(1) + "-" + playerNameMatcher.group(2)+ "?mode=competitive";
						overBuffHtml = con(overBuffUrl);
						Matcher playerSrMatcher = reg(TConstants.PLAYER_SR_REGEX, overBuffHtml);
						p.setOverbuff(overBuffUrl);
						if(playerSrMatcher.find()){
							p.setSr(Integer.parseInt(playerSrMatcher.group(1)));
						}
						else {
							p.setSr(-1);
						}
						t.add(p);
					}
					System.out.println(t.toString());
					if(SEARCH){
						System.exit(0);
					}
					allTeamsList.add(t);
				}
			}
			if(curPageHtml.contains("https://compete.tespa.org/tournament/145/registrants?page=" + ++curPageNumber)){
				curPageUrl = "https://compete.tespa.org/tournament/145/registrants?page=" + curPageNumber;
				hasNextPage = true;
			}
			else{
				hasNextPage = false;
			}
		}
		try (PrintWriter out = new PrintWriter("tespa team list.txt")) {
		    out.println(allTeamsList.toString());
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
