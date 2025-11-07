package com.LetMeDoWith.LetMeDoWith.application.notice.service;

import com.LetMeDoWith.LetMeDoWith.application.notice.dto.CreateNoticeCommand;
import com.LetMeDoWith.LetMeDoWith.domain.notice.model.Notice;
import com.LetMeDoWith.LetMeDoWith.domain.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateNoticeService {

    private final NoticeRepository noticeRepository;

    public void createNotice(CreateNoticeCommand command) {
        Notice notice = Notice.of(
                command.type(),
                command.title(),
                command.content(),
                command.startDateTime(),
                command.endDateTime(),
                command.thumbnailImageUrl());

        noticeRepository.save(notice);
    }
}
