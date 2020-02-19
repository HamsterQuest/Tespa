
public interface TConstants {
	public static final String TEAM_PAGE_REGEX = "a href=\"(https://compete.tespa.org/tournament/145/team/\\d+)\">([\\w\\s]+)<";
	public static final String PLAYER_SR_REGEX = "<span class=\"player-skill-rating\">([0-9]+)";
	public static final String PLAYER_NAME_REGEX = "<td>([^<]+?)#([0-9]+)<";
}
