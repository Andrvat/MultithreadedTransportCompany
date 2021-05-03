import lombok.Builder;

@Builder
public class StationsRailwayTrack {
    private final String trackId;

    private boolean isTrackOccupied;

    public void occupyTrack() {
        isTrackOccupied = true;
    }

    public void freeTrack() {
        isTrackOccupied = false;
    }

    public boolean isTrackOccupied() {
        return isTrackOccupied;
    }

    public String getTrackId() {
        return trackId;
    }
}
