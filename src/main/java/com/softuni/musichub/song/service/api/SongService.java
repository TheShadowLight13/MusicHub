package com.softuni.musichub.song.service.api;

import com.softuni.musichub.song.model.bindingModel.UploadSong;
import com.softuni.musichub.user.entity.User;

public interface SongService {

    void upload(UploadSong uploadSong, User user);
}