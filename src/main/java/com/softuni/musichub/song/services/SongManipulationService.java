package com.softuni.musichub.song.services;

import com.softuni.musichub.song.exceptions.SongNotFoundException;
import com.softuni.musichub.song.models.bindingModels.EditSong;
import com.softuni.musichub.song.models.bindingModels.UploadSong;
import com.softuni.musichub.song.models.viewModels.SongView;
import com.softuni.musichub.user.entities.User;
import java.io.IOException;

public interface SongManipulationService {

    void upload(UploadSong uploadSong, User user) throws IOException;

    void deleteById(Long songId) throws Exception;

    void edit(EditSong editSong, Long songId) throws SongNotFoundException;
}
