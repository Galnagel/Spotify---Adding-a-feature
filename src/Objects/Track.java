package Objects;

public class Track {

    private String id;
    private String trackName;
    private String artistName;
    private  String trackImgUrl;

    public Track(String id, String trackName, String artistName, String trackImgUrl) {
        this.id = id;
        this.trackName = trackName;
        this.artistName = artistName;
        this.trackImgUrl = trackImgUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getTrackImgUrl() {
        return trackImgUrl;
    }

    public void setTrackImgUrl(String trackImgUrl) {
        this.trackImgUrl = trackImgUrl;
    }
}
