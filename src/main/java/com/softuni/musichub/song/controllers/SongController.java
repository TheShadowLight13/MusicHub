package com.softuni.musichub.song.controllers;

import com.softuni.musichub.admin.category.models.views.CategoryView;
import com.softuni.musichub.admin.category.services.CategoryExtractionService;
import com.softuni.musichub.admin.category.staticData.CategoryConstants;
import com.softuni.musichub.controller.BaseController;
import com.softuni.musichub.home.staticData.HomeConstants;
import com.softuni.musichub.song.comment.staticData.CommentConstants;
import com.softuni.musichub.song.exceptions.SongNotFoundException;
import com.softuni.musichub.song.models.bindingModels.EditSong;
import com.softuni.musichub.song.models.bindingModels.UploadSong;
import com.softuni.musichub.song.models.viewModels.SongDetailsView;
import com.softuni.musichub.song.models.viewModels.SongView;
import com.softuni.musichub.song.services.SongExtractionService;
import com.softuni.musichub.song.services.SongManipulationService;
import com.softuni.musichub.song.staticData.SongConstants;
import com.softuni.musichub.staticData.Constants;
import com.softuni.musichub.user.entities.User;
import com.softuni.musichub.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/songs")
public class SongController extends BaseController {

    private final CategoryExtractionService categoryExtractionService;

    private final SongManipulationService songManipulationService;

    private final SongExtractionService songExtractionService;

    private final FileUtil fileUtil;

    @Autowired
    public SongController(CategoryExtractionService categoryExtractionService,
                          SongManipulationService songManipulationService,
                          SongExtractionService songExtractionService,
                          FileUtil fileUtil) {
        this.categoryExtractionService = categoryExtractionService;
        this.songManipulationService = songManipulationService;
        this.songExtractionService = songExtractionService;
        this.fileUtil = fileUtil;
    }

    private void saveSongInFileSystem(UploadSong uploadSong, FileUtil fileUtil)
            throws IOException {
        MultipartFile songTempFile = uploadSong.getFile();
        byte[] songTempFileBytes = songTempFile.getBytes();
        String songTempFileName = songTempFile.getOriginalFilename();
        File songPersistedFile = fileUtil.createFile(songTempFileBytes, songTempFileName);
        uploadSong.setPersistedFile(songPersistedFile);
    }

    @ExceptionHandler(SongNotFoundException.class)
    public ModelAndView handleSongNotFoundException() {
        return this.redirect(HomeConstants.INDEX_ROUTE);
    }

    @GetMapping("/upload")
    public ModelAndView getUploadSongPage(Model model) {
        Map<String, Object> objectByKey = new HashMap<>();
        if (!model.asMap().containsKey(SongConstants.UPLOAD_SONG)) {
            UploadSong uploadSong = new UploadSong();
            objectByKey.put(SongConstants.UPLOAD_SONG, uploadSong);
        }

        List<CategoryView> categories = this.categoryExtractionService.findAll();
        objectByKey.put(SongConstants.VALIDATE_UPLOAD_SONG_JS_ENABLED, "");
        objectByKey.put(CategoryConstants.CATEGORIES, categories);
        return this.view(SongConstants.UPLOAD_SONG_TITLE,
                SongConstants.UPLOAD_SONG_VIEW, objectByKey);
    }

    @PostMapping("/upload")
    public ModelAndView uploadSong(@Valid @ModelAttribute UploadSong uploadSong,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes,
                                   @AuthenticationPrincipal User user) throws IOException {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(SongConstants.UPLOAD_SONG, uploadSong);
            String bindingResultKey = Constants.BINDING_RESULT_PACKAGE
                    + SongConstants.UPLOAD_SONG;
            redirectAttributes.addFlashAttribute(bindingResultKey, bindingResult);
            return this.redirect(SongConstants.SONG_UPLOAD_ROUTE);
        }

        // Create persisted file to upload in CDN
        // and then set this file as field in uploadSong
        this.saveSongInFileSystem(uploadSong, fileUtil);
        this.songManipulationService.upload(uploadSong, user);
        redirectAttributes.addFlashAttribute(Constants.INFO, SongConstants.UPLOAD_SONG_SOON);
        return this.redirect(SongConstants.SONG_UPLOAD_ROUTE);
    }

    @GetMapping("/details/{id}")
    public ModelAndView getSongDetailsPage(@PathVariable Long id) throws Exception {
        SongDetailsView songDetailsView = this.songExtractionService.getDetailsById(id);
        Map<String, Object> objectByKey = new HashMap<>();
        objectByKey.put(SongConstants.AUDIO_JS_STYLE_ENABLED, "");
        objectByKey.put(CommentConstants.POST_COMMENTS_JS_ENABLED, "");
        objectByKey.put(SongConstants.SONG_DETAILS, songDetailsView);
        return this.view(SongConstants.SONG_DETAILS_TITLE,
                SongConstants.SONG_DETAILS_VIEW, objectByKey);
    }

    @GetMapping("/delete/{id}")
    public ModelAndView getDeleteSongPage(@PathVariable Long id) {
        SongView songView = this.songExtractionService.findById(id);
        Map<String, Object> objectByKey = new HashMap<>();
        objectByKey.put(SongConstants.DELETE_SONG, songView);
        return this.view(SongConstants.DELETE_SONG_TITLE,
                SongConstants.DELETE_SONG_VIEW, objectByKey);
    }

    @PostMapping("/delete/{id}")
    public ModelAndView deleteSong(@PathVariable Long id) throws Exception {
        this.songManipulationService.deleteById(id);
        return this.redirect(HomeConstants.INDEX_ROUTE);
    }

    @GetMapping("/edit/{id}")
    public ModelAndView getEditSongPage(@PathVariable Long id, Model model) {
        EditSong editSong;
        if (!model.asMap().containsKey(SongConstants.EDIT_SONG)) {
            editSong = this.songExtractionService.getEditSongById(id);
        } else {
            editSong = (EditSong) model.asMap().get(SongConstants.EDIT_SONG);
        }

        Map<String, Object> objectByKey = new HashMap<>();
        objectByKey.put(SongConstants.EDIT_SONG, editSong);
        List<CategoryView> categories = this.categoryExtractionService.findAll();
        objectByKey.put(CategoryConstants.CATEGORIES, categories);
        return this.view(SongConstants.EDIT_SONG_TITLE,
                SongConstants.EDIT_SONG_VIEW, objectByKey);
    }

    @PostMapping("/edit/{id}")
    public ModelAndView editSong(@PathVariable Long id,
                                 @Valid @ModelAttribute EditSong editSong,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(SongConstants.EDIT_SONG, editSong);
            String bindingResultKey = Constants.BINDING_RESULT_PACKAGE
                    + SongConstants.EDIT_SONG;
            redirectAttributes.addFlashAttribute(bindingResultKey, bindingResult);
            return this.redirect(SongConstants.EDIT_SONG_BASE_ROUTE + id);
        }

        this.songManipulationService.edit(editSong, id);
        redirectAttributes.addFlashAttribute(Constants.INFO, SongConstants.SONG_EDITED_MESSAGE);
        return this.redirect(SongConstants.EDIT_SONG_BASE_ROUTE + id);
    }
}
