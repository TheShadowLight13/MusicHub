package com.softuni.musichub.song.comment.services;

import com.softuni.musichub.song.comment.models.viewModels.CommentView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentExtractionService {

    Page<CommentView> findPendingComments(Pageable pageable);
}
