package com.mikhaylova.lms.service.impl;

import com.mikhaylova.lms.domain.AvatarImage;
import com.mikhaylova.lms.domain.User;
import com.mikhaylova.lms.exception.NotFoundException;
import com.mikhaylova.lms.repository.AvatarImageRepository;
import com.mikhaylova.lms.service.AvatarStorageService;
import com.mikhaylova.lms.service.UserService;
import com.mikhaylova.lms.util.FileUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

@Service
public class AvatarStorageServiceImpl implements AvatarStorageService {

    private final AvatarImageRepository avatarImageRepository;
    private final UserService userService;
    private final FileUtilService fileUtilService;

    @Autowired
    public AvatarStorageServiceImpl(AvatarImageRepository avatarImageRepository,
                                    UserService userService,
                                    FileUtilService fileUtilService) {
        this.avatarImageRepository = avatarImageRepository;
        this.userService = userService;
        this.fileUtilService = fileUtilService;
    }

    @Transactional
    @Override
    public void save(Long id, String contentType, InputStream is) {
        Optional<AvatarImage> opt = avatarImageRepository.findByUserId(id);
        AvatarImage avatarImage;
        String filename;
        if (opt.isEmpty()) {
            filename = UUID.randomUUID().toString();
            User user = userService.findUserById(id);
            avatarImage = new AvatarImage(null, contentType, filename, user);
        } else {
            avatarImage = opt.get();
            filename = avatarImage.getFilename();
            avatarImage.setContentType(contentType);
        }
        avatarImageRepository.save(avatarImage);
        fileUtilService.saveFile(is, filename);
    }

    @Override
    public String getContentTypeByUserId(Long id) {
        return avatarImageRepository.findByUserId(id)
                .map(AvatarImage::getContentType)
                .orElseThrow(() -> new NotFoundException(AvatarImage.class.getSimpleName(), id));
    }

    @Override
    public Optional<byte[]> getAvatarImageByUserId(Long id) {
        return avatarImageRepository.findByUserId(id)
                .map(AvatarImage::getFilename)
                .map(fileUtilService::readFile);
    }
}
