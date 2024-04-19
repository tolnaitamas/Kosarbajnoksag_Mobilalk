package com.example.basketball_app;

public class MatchResult {
    private String id;
    private String home;
    private String away;
    private String homePts;
    private String awayPts;
    private  int homeImg;
    private  int awayImg;
    private String date;

    public MatchResult() {
    }

    public MatchResult(String home, String away, String homePts, String awayPts, int homeImg, int awayImg, String date) {
        this.home = home;
        this.away = away;
        this.homePts = homePts;
        this.awayPts = awayPts;
        this.homeImg = homeImg;
        this.awayImg = awayImg;
        this.date = date;

    }

    public String getHome() {
        return home;
    }

    public String getAway() {
        return away;
    }

    public String getHomePts() {
        return homePts;
    }

    public String getAwayPts() {
        return awayPts;
    }

    public int getHomeImg() {
        return homeImg;
    }

    public int getAwayImg() {
        return awayImg;
    }

    public String getDate() {
        return date;
    }

    public String _getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
}
