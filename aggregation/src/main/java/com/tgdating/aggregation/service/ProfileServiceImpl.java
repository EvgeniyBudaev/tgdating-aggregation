package com.tgdating.aggregation.service;

import com.tgdating.aggregation.dto.request.*;
import com.tgdating.aggregation.dto.response.ResponseProfileBySessionIdGetDto;
import com.tgdating.aggregation.dto.response.ResponseProfileCreateDto;
import com.tgdating.aggregation.model.*;
import com.tgdating.aggregation.repository.ProfileRepository;
import com.tgdating.aggregation.shared.exception.InternalServerException;
import com.tgdating.aggregation.shared.utils.ImageConverter;
import com.tgdating.aggregation.shared.utils.Utils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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
        return ResponseProfileCreateDto.builder()
                .sessionId(requestProfileCreateDto.getSessionId())
                .build();
    }

    private void updateLastOnline(String sessionId) {
        profileRepository.updateLastOnline(sessionId);
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
        return profileRepository.addImage(
                RequestProfileImageAddDto.builder()
                        .sessionId(sessionId)
                        .name(imageConverterRecord.name())
                        .url(imageConverterRecord.url())
                        .size(imageConverterRecord.size())
                        .isDeleted(false)
                        .isBlocked(false)
                        .isPrimary(false)
                        .isPrivate(false)
                        .createdAt(Utils.getNowUtc())
                        .updatedAt(null)
                        .build()
        );
    }

    private ProfileNavigatorEntity addNavigator(RequestProfileCreateDto requestProfileCreateDto) {
        return profileRepository.addNavigator(
                RequestProfileNavigatorAddDto.builder()
                        .sessionId(requestProfileCreateDto.getSessionId())
                        .latitude(requestProfileCreateDto.getLatitude())
                        .longitude(requestProfileCreateDto.getLongitude())
                        .build()
        );
    }

    private void updateNavigator(String sessionId, Double latitude, Double longitude) {
        if (latitude != null && longitude != null) {
            profileRepository.updateNavigator(
                    RequestProfileNavigatorAddDto.builder()
                            .sessionId(sessionId)
                            .latitude(latitude)
                            .longitude(longitude)
                            .build()
            );
        }
    }

    private ProfileFilterEntity addFilter(RequestProfileCreateDto requestProfileCreateDto) {
        return profileRepository.addFilter(
                RequestProfileFilterAddDto.builder()
                        .sessionId(requestProfileCreateDto.getSessionId())
                        .searchGender(requestProfileCreateDto.getSearchGender())
                        .lookingFor(requestProfileCreateDto.getLookingFor())
                        .ageFrom(requestProfileCreateDto.getAgeFrom())
                        .ageTo(requestProfileCreateDto.getAgeTo())
                        .distance(requestProfileCreateDto.getDistance())
                        .page(requestProfileCreateDto.getPage())
                        .size(requestProfileCreateDto.getSize())
                        .build()
        );
    }

    private ProfileTelegramEntity addTelegram(RequestProfileCreateDto requestProfileCreateDto) {
        return profileRepository.addTelegram(
                RequestProfileTelegramAddDto.builder()
                        .sessionId(requestProfileCreateDto.getSessionId())
                        .userId(requestProfileCreateDto.getTelegramUserId())
                        .username(requestProfileCreateDto.getTelegramUsername())
                        .firstName(requestProfileCreateDto.getTelegramFirstName())
                        .lastName(requestProfileCreateDto.getTelegramLastName())
                        .languageCode(requestProfileCreateDto.getTelegramLanguageCode())
                        .allowsWriteToPm(requestProfileCreateDto.getTelegramAllowsWriteToPm())
                        .queryId(requestProfileCreateDto.getTelegramQueryId())
                        .chatId(requestProfileCreateDto.getTelegramChatId())
                        .build()
        );
    }

    public ResponseProfileBySessionIdGetDto getBySessionID(String sessionId, Double latitude, Double longitude) {
        updateLastOnline(sessionId);
        updateNavigator(sessionId, latitude, longitude);
        ProfileEntity profileEntity = findBySessionID(sessionId);
        List<ProfileImageEntity> profileImageListEntity = findImageListBySessionID(sessionId);
        ProfileNavigatorEntity profileNavigatorEntity = findNavigatorBySessionID(sessionId);
        ProfileFilterEntity profileFilterEntity = findFilterBySessionID(sessionId);
        ProfileTelegramEntity profileTelegramEntity = findTelegramBySessionID(sessionId);
        return ResponseProfileBySessionIdGetDto.builder()
                .id(profileEntity.getId())
                .sessionId(profileEntity.getSessionId())
                .displayName(profileEntity.getDisplayName())
                .birthday(profileEntity.getBirthday())
                .gender(profileEntity.getGender())
                .location(profileEntity.getLocation())
                .description(profileEntity.getDescription())
                .height(profileEntity.getHeight())
                .weight(profileEntity.getWeight())
                .isDeleted(profileEntity.getIsDeleted())
                .isBlocked(profileEntity.getIsBlocked())
                .isPremium(profileEntity.getIsPremium())
                .isShowDistance(profileEntity.getIsShowDistance())
                .isInvisible(profileEntity.getIsInvisible())
                .createdAt(profileEntity.getCreatedAt())
                .updatedAt(profileEntity.getUpdatedAt())
                .lastOnline(profileEntity.getLastOnline())
                .images(profileImageListEntity)
                .navigator(profileNavigatorEntity)
                .filter(profileFilterEntity)
                .telegram(profileTelegramEntity)
                .build();
    }

    private ProfileEntity findBySessionID(String sessionId) {
        return profileRepository.findBySessionID(sessionId);
    }

    private List<ProfileImageEntity> findImageListBySessionID(String sessionId) {
        return profileRepository.findImageListBySessionID(sessionId);
    }

    private ProfileNavigatorEntity findNavigatorBySessionID(String sessionId) {
        return profileRepository.findNavigatorBySessionID(sessionId);
    }

    private ProfileFilterEntity findFilterBySessionID(String sessionId) {
        return profileRepository.findFilterBySessionID(sessionId);
    }

    private ProfileTelegramEntity findTelegramBySessionID(String sessionId) {
        return profileRepository.findTelegramBySessionID(sessionId);
    }
}
