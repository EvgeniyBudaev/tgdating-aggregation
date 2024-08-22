package com.tgdating.aggregation.service;

import com.tgdating.aggregation.dto.request.*;
import com.tgdating.aggregation.dto.response.ResponseProfileCreateDto;
import com.tgdating.aggregation.model.*;
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

    public ResponseProfileCreateDto create(RequestProfileCreateDto requestProfileCreateDto) {
        profileRepository.create(requestProfileCreateDto);
        addImages(requestProfileCreateDto);
        addNavigator(requestProfileCreateDto);
        addFilter(requestProfileCreateDto);
        addTelegram(requestProfileCreateDto);
        ResponseProfileCreateDto responseProfileCreateDto = new ResponseProfileCreateDto();
        responseProfileCreateDto.setSessionId(requestProfileCreateDto.getSessionId());
        return responseProfileCreateDto;
    }

    private void addImages(RequestProfileCreateDto requestProfileCreateDto) {
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

    private ProfileNavigatorEntity addNavigator(RequestProfileCreateDto requestProfileCreateDto) {
        RequestProfileNavigatorAddDto requestProfileNavigatorAddDto = new RequestProfileNavigatorAddDto();
        requestProfileNavigatorAddDto.setSessionId(requestProfileCreateDto.getSessionId());
        requestProfileNavigatorAddDto.setLatitude(requestProfileCreateDto.getLatitude());
        requestProfileNavigatorAddDto.setLongitude(requestProfileCreateDto.getLongitude());
        return profileRepository.addNavigator(requestProfileNavigatorAddDto);
    }

    private ProfileFilterEntity addFilter(RequestProfileCreateDto requestProfileCreateDto) {
        RequestProfileFilterAddDto requestProfileFilterAddDto = new RequestProfileFilterAddDto();
        requestProfileFilterAddDto.setSessionId(requestProfileCreateDto.getSessionId());
        requestProfileFilterAddDto.setSearchGender(requestProfileCreateDto.getSearchGender());
        requestProfileFilterAddDto.setLookingFor(requestProfileCreateDto.getLookingFor());
        requestProfileFilterAddDto.setAgeFrom(requestProfileCreateDto.getAgeFrom());
        requestProfileFilterAddDto.setAgeTo(requestProfileCreateDto.getAgeTo());
        requestProfileFilterAddDto.setDistance(requestProfileCreateDto.getDistance());
        requestProfileFilterAddDto.setPage(requestProfileCreateDto.getPage());
        requestProfileFilterAddDto.setSize(requestProfileCreateDto.getSize());
        return profileRepository.addFilter(requestProfileFilterAddDto);
    }

    private ProfileTelegramEntity addTelegram(RequestProfileCreateDto requestProfileCreateDto) {
        RequestProfileTelegramAddDto requestProfileTelegramAddDto = new RequestProfileTelegramAddDto();
        requestProfileTelegramAddDto.setSessionId(requestProfileCreateDto.getSessionId());
        requestProfileTelegramAddDto.setUserId(requestProfileCreateDto.getTelegramUserId());
        requestProfileTelegramAddDto.setUsername(requestProfileCreateDto.getTelegramUsername());
        requestProfileTelegramAddDto.setFirstName(requestProfileCreateDto.getTelegramFirstName());
        requestProfileTelegramAddDto.setLastName(requestProfileCreateDto.getTelegramLastName());
        requestProfileTelegramAddDto.setLanguageCode(requestProfileCreateDto.getTelegramLanguageCode());
        requestProfileTelegramAddDto.setAllowsWriteToPm(requestProfileCreateDto.getTelegramAllowsWriteToPm());
        requestProfileTelegramAddDto.setQueryId(requestProfileCreateDto.getTelegramQueryId());
        requestProfileTelegramAddDto.setChatId(requestProfileCreateDto.getTelegramChatId());
        ProfileTelegramEntity p = profileRepository.addTelegram(requestProfileTelegramAddDto);
        System.out.println("Telegram: " + p);
        return p;
    }
}
