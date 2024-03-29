package pl.pb.spotifyclone.models.playlist;

import pl.pb.spotifyclone.models.playlist.PlaylistIterator;
import pl.pb.spotifyclone.models.track.Track;

import java.util.*;
import java.util.stream.IntStream;

public final class RandomPlaylistIterator extends PlaylistIterator {
    private List<Integer> indexOrder;
    private int prevIndex = -2;
    private int currentIndex = -1;
    private int nextIndex = 0;
    private final Random random = new Random();

    private void calculateIndexes() {
        prevIndex = currentIndex - 1;
        if(looped && currentIndex == tracks.size() - 1) nextIndex = 0;
        else nextIndex = currentIndex + 1;
    }

    public RandomPlaylistIterator(List<Track> tracks) {
        super(tracks);
        indexOrder = new ArrayList<>(IntStream.range(0, tracks.size()).boxed().toList());
        Collections.shuffle(indexOrder);
    }

    @Override
    public Track next() {
        Track track = tracks.get(indexOrder.get(nextIndex));
        currentIndex = nextIndex;
        calculateIndexes();
        return track;
    }

    @Override
    public Track prev() {
        Track track = tracks.get(indexOrder.get(prevIndex));
        currentIndex = prevIndex;
        calculateIndexes();
        return track;
    }

    @Override
    public boolean hasNext() {
        return nextIndex < tracks.size();
    }

    @Override
    public boolean hasPrev() {
        return prevIndex >= 0;
    }

    @Override
    public void setLooped(boolean looped) {
        super.setLooped(looped);
        calculateIndexes();
    }
}
