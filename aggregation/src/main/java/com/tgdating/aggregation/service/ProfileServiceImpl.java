package com.tgdating.aggregation.service;

import com.tgdating.aggregation.dto.request.RequestProfileCreateDto;
import com.tgdating.aggregation.dto.request.RequestProfileImageAddDto;
import com.tgdating.aggregation.model.ImageConverterRecord;
import com.tgdating.aggregation.model.ProfileEntity;
import com.tgdating.aggregation.model.ProfileImageEntity;
import com.tgdating.aggregation.repository.ProfileRepository;
import com.tgdating.aggregation.shared.exception.InternalServerException;
import com.tgdating.aggregation.shared.utils.ImageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Service
public class ProfileServiceImpl implements ProfileService {
    private final ProfileRepository profileRepository;
    private static final Path BASE_PROJECT_PATH = Paths.get(System.getProperty("user.dir"));

    public ProfileServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public ProfileEntity create(RequestProfileCreateDto requestProfileCreateDto) {
        ProfileEntity profileEntity = profileRepository.create(requestProfileCreateDto);
        uploadImages(requestProfileCreateDto, profileEntity.getId());
        return profileEntity;
    }

    private void uploadImages(RequestProfileCreateDto requestProfileCreateDto, Long profileId) {
        String sessionId = requestProfileCreateDto.getSessionId();
        for (MultipartFile file : requestProfileCreateDto.getImage()) {
            ImageConverterRecord imageConverterRecord = uploadImageToFileSystem(file, sessionId);
            addImageToDB(sessionId, imageConverterRecord);
        }
    }

    private ImageConverterRecord uploadImageToFileSystem(MultipartFile file, String sessionId) {
        try {
            Path staticFolderPath = BASE_PROJECT_PATH.resolve("src/main/resources/static");
            String fileName = file.getOriginalFilename();
            String filePath = String.format("%s/%s/%s", staticFolderPath, sessionId, fileName);
            File directory = new File(filePath).getParentFile();
            if (!directory.exists()) directory.mkdirs();
            file.transferTo(new File(filePath));
            return ImageConverter.convertImage(filePath, fileName);
        } catch (Exception e) {
            throw new InternalServerException(
                    "Ошибка сохранения файла",
                    "Ошибка сохранения файла на сервер: " + e.getMessage()
            );
        }
    }

    private ProfileImageEntity addImageToDB(String sessionId, ImageConverterRecord imageConverterRecord) {
        RequestProfileImageAddDto requestProfileImageAddDto = new RequestProfileImageAddDto();
        requestProfileImageAddDto.setSessionId(sessionId);
        requestProfileImageAddDto.setName(imageConverterRecord.name());
        requestProfileImageAddDto.setUrl(imageConverterRecord.url());
        requestProfileImageAddDto.setSize(imageConverterRecord.size());
        requestProfileImageAddDto.setIsDeleted(false);
        requestProfileImageAddDto.setIsBlocked(false);
        requestProfileImageAddDto.setIsPrimary(false);
        requestProfileImageAddDto.setIsPrivate(false);
        requestProfileImageAddDto.setCreatedAt(LocalDateTime.now());
        requestProfileImageAddDto.setUpdatedAt(null);
        return profileRepository.addImage(requestProfileImageAddDto);
    }
}
