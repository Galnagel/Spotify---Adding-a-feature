package Objects;

public class Track {

    private String trackName;
    private String artistName;
    private  String trackImgUrl;

    public Track(String trackName, String artistName, String trackImgUrl) {
        this.trackName = trackName;
        this.artistName = artistName;
        this.trackImgUrl = trackImgUrl;
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


    @Override
    public String toString() {
        return "Track{" +
                "trackName='" + trackName + '\'' +
                ", artistName='" + artistName + '\'' +
                ", trackImgUrl='" + trackImgUrl + '\'' +
                '}';
    }
}
