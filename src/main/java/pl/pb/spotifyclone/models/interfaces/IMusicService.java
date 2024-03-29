package pl.pb.spotifyclone.models.interfaces;

import pl.pb.spotifyclone.models.playlist.Playlist;
import pl.pb.spotifyclone.models.playlist.PlaylistIteratorType;
import pl.pb.spotifyclone.models.track.Track;

public interface IMusicService extends IMusicPlayer {
    Playlist getPlaylist();
    void setSingleTrack(Track track);
    void setPlaylist(Playlist playlist);
    void clear();
    void setTrackOrder(PlaylistIteratorType type);
    void setLooped(boolean looped);
    void setExplicitPermission(boolean explicitPermission);
    void nextTrack();
    void prevTrack();
}
