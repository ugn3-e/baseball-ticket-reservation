package ticket;

public class GameInfo {
    private String Team;
    private String opponentTeam;
    private String date;
    private String time;
    private String stadium;
    private String HomeTeam;

    public GameInfo(String Team, String opponentTeam, String date, String time, String stadium, String HomeTeam) {
        this.Team = Team;
        this.opponentTeam = opponentTeam;
        this.date = date;
        this.time = time;
        this.stadium = stadium;
        this.HomeTeam = HomeTeam;
    }

    // getter 메서드
    public String getTeam() { return Team; }
    public String getOpponentTeam() { return opponentTeam; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getStadium() { return stadium; }
    public String getHomeTeam() { return HomeTeam; }
}
