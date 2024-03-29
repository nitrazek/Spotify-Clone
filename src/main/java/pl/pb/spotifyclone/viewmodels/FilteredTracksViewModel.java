package pl.pb.spotifyclone.viewmodels;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import pl.pb.spotifyclone.ViewManager;
import pl.pb.spotifyclone.models.interfaces.ISubscriber;
import pl.pb.spotifyclone.models.musicplayer.MusicService;
import pl.pb.spotifyclone.models.playlist.Playlist;
import pl.pb.spotifyclone.models.track.Track;
import pl.pb.spotifyclone.repositories.PlaylistRepository;
import pl.pb.spotifyclone.repositories.TrackRepository;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class FilteredTracksViewModel implements Initializable, ISubscriber<List<Track>> {
    private ViewManager viewManager;
    private MusicService musicService;
    private TrackRepository trackRepository;
    private ObservableList<Track> observableTrackList;
    private Track selectedTrack;
    @FXML public TableView<Track> trackTableView;
    @FXML public TableColumn<Track,String> trackNameTableColumn;
    @FXML public TableColumn<Track,String> trackAlbumTableColumn;
    @FXML public TableColumn<Track,String> trackAuthorTableColumn;
    @FXML public TableColumn<Track,Integer> trackReleaseYearTableColumn;
    @FXML public TableColumn<Track,Boolean> trackExplicitTableColumn;

    @Setter private String keyWord;

    public FilteredTracksViewModel() {
        viewManager = ViewManager.getInstance();
        musicService = MusicService.getInstance();
        trackRepository = TrackRepository.getInstance();
        trackRepository.subscribe(this);
        observableTrackList = FXCollections.observableArrayList();
    }

    @Override
    public void initialize (URL location, ResourceBundle resources) {
        trackNameTableColumn.setCellValueFactory(new PropertyValueFactory<Track,String>("Name"));
        trackAlbumTableColumn.setCellValueFactory(new PropertyValueFactory<Track,String>("AlbumName"));
        trackAuthorTableColumn.setCellValueFactory(new PropertyValueFactory<Track,String>("AuthorName"));
        trackReleaseYearTableColumn.setCellValueFactory(new PropertyValueFactory<Track,Integer>("ReleaseYear"));
        trackExplicitTableColumn.setCellValueFactory(new PropertyValueFactory<Track,Boolean>("Explicit"));
        trackTableView.setPlaceholder(new Label("Brak utworów."));
        trackTableView.setItems(observableTrackList);
        trackTableView.getSelectionModel().selectedItemProperty().addListener((observer, oldValue, newValue) -> {
            selectedTrack = newValue;
            if(newValue == null) return;
            musicService.setSingleTrack(newValue);
        });
    }

    @Override
    public void update(List<Track> object) {
        observableTrackList.clear();
        observableTrackList.addAll(object);
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
        observableTrackList.clear();
        observableTrackList.addAll(trackRepository.getTrackContaining(this.keyWord));
    }

    public void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pl/pb/spotifyclone/home-view.fxml"));
            keyWord = "";
            viewManager.switchView(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void editTrackClicked() {
        if (selectedTrack == null) {
            Alert trackNull = new Alert(Alert.AlertType.ERROR);
            trackNull.setTitle("Brak utworu");
            trackNull.setHeaderText("Nie wybrano utworu");
            trackNull.show();
            return;
        }

        try {
            URL url = getClass().getResource("/pl/pb/spotifyclone/edit-track-dialog.fxml");
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();
            EditTrackViewModel viewModel = loader.getController();
            viewModel.setTrack(selectedTrack);
            Stage secondStage = new Stage();
            secondStage.initModality(Modality.APPLICATION_MODAL);
            secondStage.setTitle("Edytuj utwór");
            secondStage.setScene(new Scene(root));
            secondStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteTrackClicked()
    {
        if (selectedTrack == null) {
            Alert trackNull = new Alert(Alert.AlertType.ERROR);
            trackNull.setTitle("Brak utworu");
            trackNull.setHeaderText("Nie wybrano utworu");
            trackNull.show();
            return;
        }

        try {
            String trackName = selectedTrack.getName();
            if(selectedTrack.equals(musicService.getTrack())) musicService.clear();
            trackRepository.deleteTrack(selectedTrack);
            Alert trackDeleted = new Alert(Alert.AlertType.INFORMATION);
            trackDeleted.setTitle("Utwór usunięty");
            trackDeleted.setHeaderText("Pomyślnie usunięto utwór " + trackName);
            trackDeleted.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addTrackToPlaylist() {
        if (selectedTrack == null) {
            Alert trackNull = new Alert(Alert.AlertType.ERROR);
            trackNull.setTitle("Brak utworu");
            trackNull.setHeaderText("Nie wybrano utworu");
            trackNull.show();
            return;
        }

        try {
            URL url = getClass().getResource("/pl/pb/spotifyclone/add-track-to-playlist-dialog.fxml");
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();
            AddTrackToPlaylistViewModel viewModel = loader.getController();
            viewModel.setTrack(selectedTrack);
            Stage secondStage = new Stage();
            secondStage.initModality(Modality.APPLICATION_MODAL);
            secondStage.setTitle("Dodaj utwór do playlisty");
            secondStage.setScene(new Scene(root));
            secondStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
