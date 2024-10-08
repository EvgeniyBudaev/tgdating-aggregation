package com.tgdating.aggregation.service;

import com.tgdating.aggregation.dto.request.*;
import com.tgdating.aggregation.dto.response.*;
import com.tgdating.aggregation.model.*;
import com.tgdating.aggregation.repository.ProfileRepository;
import com.tgdating.aggregation.shared.exception.InternalServerException;
import com.tgdating.aggregation.shared.exception.NotFoundException;
import com.tgdating.aggregation.shared.utils.ImageConverter;
import com.tgdating.aggregation.shared.utils.Utils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProfileServiceImpl implements ProfileService {
    private final ProfileRepository profileRepository;
    private static final Path BASE_PROJECT_PATH = Paths.get(System.getProperty("user.dir"));
    private static final Integer MIN_DISTANCE = 100;
    private static final Integer MAX_COUNT_COMPLAINTS = 1;

    public ProfileServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public ResponseProfileCreateDto create(RequestProfileCreateDto requestProfileCreateDto) {
        ProfileEntity profileEntity = profileRepository.createProfile(requestProfileCreateDto);
        addImages(requestProfileCreateDto);
        addNavigator(requestProfileCreateDto);
        addFilter(requestProfileCreateDto);
        addTelegram(requestProfileCreateDto);
        return ResponseProfileCreateDto.builder()
                .sessionId(profileEntity.getSessionId())
                .build();
    }

    @Override
    public ResponseProfileUpdateDto update(RequestProfileUpdateDto requestProfileUpdateDto) {
        String sessionId = requestProfileUpdateDto.getSessionId();
        Double latitude = requestProfileUpdateDto.getLatitude();
        Double longitude = requestProfileUpdateDto.getLongitude();
        RequestProfileFilterUpdateDto requestProfileFilterUpdateDto = RequestProfileFilterUpdateDto.builder()
                .sessionId(requestProfileUpdateDto.getSessionId())
                .searchGender(requestProfileUpdateDto.getSearchGender())
                .lookingFor(requestProfileUpdateDto.getLookingFor())
                .ageFrom(requestProfileUpdateDto.getAgeFrom())
                .ageTo(requestProfileUpdateDto.getAgeTo())
                .distance(requestProfileUpdateDto.getDistance())
                .page(requestProfileUpdateDto.getPage())
                .size(requestProfileUpdateDto.getSize())
                .latitude(latitude)
                .longitude(longitude)
                .build();
        RequestProfileTelegramUpdateDto requestProfileTelegramUpdateDto = RequestProfileTelegramUpdateDto.builder()
                .sessionId(requestProfileUpdateDto.getSessionId())
                .userId(requestProfileUpdateDto.getTelegramUserId())
                .username(requestProfileUpdateDto.getTelegramUsername())
                .firstName(requestProfileUpdateDto.getTelegramFirstName())
                .lastName(requestProfileUpdateDto.getTelegramLastName())
                .languageCode(requestProfileUpdateDto.getTelegramLanguageCode())
                .allowsWriteToPm(requestProfileUpdateDto.getTelegramAllowsWriteToPm())
                .queryId(requestProfileUpdateDto.getTelegramQueryId())
                .chatId(requestProfileUpdateDto.getTelegramChatId())
                .build();
        updateLastOnline(sessionId);
        updateImages(requestProfileUpdateDto);
        updateNavigator(sessionId, latitude, longitude);
        updateFilter(requestProfileFilterUpdateDto);
        updateTelegram(requestProfileTelegramUpdateDto);
        ProfileEntity profileEntity = profileRepository.updateProfile(requestProfileUpdateDto);
        checkUserExists(profileEntity.getIsDeleted());
        List<ProfileImageEntity> profileImageListEntity = findImageListBySessionID(sessionId);
        ProfileNavigatorEntity profileNavigatorEntity = findNavigatorBySessionID(sessionId);
        ProfileFilterEntity profileFilterEntity = findFilterBySessionID(sessionId);
        ProfileTelegramEntity profileTelegramEntity = findTelegramBySessionID(sessionId);
        boolean isOnline = Utils.calculateIsOnline(profileEntity.getLastOnline());
        return ResponseProfileUpdateDto.builder()
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
                .isOnline(isOnline)
                .createdAt(profileEntity.getCreatedAt())
                .updatedAt(profileEntity.getUpdatedAt())
                .lastOnline(profileEntity.getLastOnline())
                .navigator(ResponseProfileNavigatorDto.builder()
                        .sessionId(profileNavigatorEntity.getSessionId())
                        .location(profileNavigatorEntity.getLocation())
                        .build())
                .filter(ResponseProfileFilterDto.builder()
                        .sessionId(profileFilterEntity.getSessionId())
                        .searchGender(profileFilterEntity.getSearchGender())
                        .lookingFor(profileFilterEntity.getLookingFor())
                        .ageFrom(profileFilterEntity.getAgeFrom())
                        .ageTo(profileFilterEntity.getAgeTo())
                        .distance(profileFilterEntity.getDistance())
                        .page(profileFilterEntity.getPage())
                        .size(profileFilterEntity.getSize())
                        .build())
                .telegram(ResponseProfileTelegramDto.builder()
                        .sessionId(profileTelegramEntity.getSessionId())
                        .userId(profileTelegramEntity.getUserId())
                        .username(profileTelegramEntity.getUsername())
                        .firstName(profileTelegramEntity.getFirstName())
                        .lastName(profileTelegramEntity.getLastName())
                        .languageCode(profileTelegramEntity.getLanguageCode())
                        .allowsWriteToPm(profileTelegramEntity.getAllowsWriteToPm())
                        .queryId(profileTelegramEntity.getQueryId())
                        .chatId(profileTelegramEntity.getChatId())
                        .build())
                .images(profileImageListEntity)
                .build();
    }

    @Override
    public void delete(String sessionId) {
        ProfileEntity profileEntity = findBySessionID(sessionId);
        checkUserExists(profileEntity.getIsDeleted());
        profileRepository.deleteProfile(sessionId);
        deleteFilter(sessionId);
        deleteNavigator(sessionId);
        deleteTelegram(sessionId);
        deleteImageAllBySessionID(sessionId);
    }

    public PaginationEntity<List<ResponseProfileListGetDto>> getProfileList(
            RequestProfileListGetDto requestProfileListGetDto) {
        String sessionId = requestProfileListGetDto.getSessionId();
        Double latitude = requestProfileListGetDto.getLatitude();
        Double longitude = requestProfileListGetDto.getLongitude();
        updateLastOnline(sessionId);
        updateNavigator(sessionId, latitude, longitude);
        PaginationEntity<List<ProfileListEntity>> paginationProfileListEntity =
                profileRepository.findProfileList(requestProfileListGetDto);
        List<ProfileListEntity> profileListEntity = paginationProfileListEntity.getContent();
        List<ResponseProfileListGetDto> formattedList = profileListEntity.stream().map(item -> {
            String itemSessionId = item.getSessionId();
            List<ProfileImageEntity> profileImagePublicListEntity = findImagePublicListBySessionID(itemSessionId);
            ProfileImageEntity lastImage = profileImagePublicListEntity.stream()
                    .filter(image -> image.getSessionId().equals(itemSessionId))
                    .max(Comparator.comparing(ProfileImageEntity::getCreatedAt))
                    .orElse(null);
            boolean isOnline = Utils.calculateIsOnline(item.getLastOnline());
            double distanceValue = item.getDistance();
            Integer distanceAsInt = (int) Math.floor(distanceValue < MIN_DISTANCE ? MIN_DISTANCE : distanceValue);
            return ResponseProfileListGetDto.builder()
                    .sessionId(itemSessionId)
                    .distance(distanceAsInt)
                    .url(lastImage != null ? lastImage.getUrl() : null)
                    .isOnline(isOnline)
                    .lastOnline(item.getLastOnline())
                    .build();
        }).collect(Collectors.toList());
        return PaginationEntity.<List<ResponseProfileListGetDto>>builder()
                .hasNext(paginationProfileListEntity.getHasNext())
                .hasPrevious(paginationProfileListEntity.getHasPrevious())
                .page(paginationProfileListEntity.getPage())
                .size(paginationProfileListEntity.getSize())
                .numberEntities(paginationProfileListEntity.getNumberEntities())
                .totalPages(paginationProfileListEntity.getTotalPages())
                .content(formattedList)
                .build();
    }

    public ResponseProfileBySessionIdGetDto getBySessionID(String sessionId, Double latitude, Double longitude) {
        updateLastOnline(sessionId);
        updateNavigator(sessionId, latitude, longitude);
        ProfileEntity profileEntity = findBySessionID(sessionId);
        checkUserExists(profileEntity.getIsDeleted());
        List<ProfileImageEntity> profileImageListEntity = findImageListBySessionID(sessionId);
        ProfileNavigatorEntity profileNavigatorEntity = findNavigatorBySessionID(sessionId);
        ProfileFilterEntity profileFilterEntity = findFilterBySessionID(sessionId);
        ProfileTelegramEntity profileTelegramEntity = findTelegramBySessionID(sessionId);
        boolean isOnline = Utils.calculateIsOnline(profileEntity.getLastOnline());
        return ResponseProfileBySessionIdGetDto.builder()
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
                .isOnline(isOnline)
                .createdAt(profileEntity.getCreatedAt())
                .updatedAt(profileEntity.getUpdatedAt())
                .lastOnline(profileEntity.getLastOnline())
                .navigator(ResponseProfileNavigatorDto.builder()
                        .sessionId(profileNavigatorEntity.getSessionId())
                        .location(profileNavigatorEntity.getLocation())
                        .build())
                .filter(ResponseProfileFilterDto.builder()
                        .sessionId(profileFilterEntity.getSessionId())
                        .searchGender(profileFilterEntity.getSearchGender())
                        .lookingFor(profileFilterEntity.getLookingFor())
                        .ageFrom(profileFilterEntity.getAgeFrom())
                        .ageTo(profileFilterEntity.getAgeTo())
                        .distance(profileFilterEntity.getDistance())
                        .page(profileFilterEntity.getPage())
                        .size(profileFilterEntity.getSize())
                        .build())
                .telegram(ResponseProfileTelegramDto.builder()
                        .sessionId(profileTelegramEntity.getSessionId())
                        .userId(profileTelegramEntity.getUserId())
                        .username(profileTelegramEntity.getUsername())
                        .firstName(profileTelegramEntity.getFirstName())
                        .lastName(profileTelegramEntity.getLastName())
                        .languageCode(profileTelegramEntity.getLanguageCode())
                        .allowsWriteToPm(profileTelegramEntity.getAllowsWriteToPm())
                        .queryId(profileTelegramEntity.getQueryId())
                        .chatId(profileTelegramEntity.getChatId())
                        .build())
                .images(profileImageListEntity)
                .build();
    }

    @Override
    public ResponseProfileDetailGetDto getProfileDetail(
            String sessionId, String viewedSessionId, Double latitude, Double longitude) {
        updateLastOnline(sessionId);
        updateNavigator(sessionId, latitude, longitude);
        ProfileEntity profileEntity = findBySessionID(sessionId);
        checkUserExists(profileEntity.getIsDeleted());
        ProfileNavigatorEntity profileNavigatorSessionEntity = findNavigatorBySessionID(sessionId);
        ProfileNavigatorEntity profileNavigatorViewedEntity = findNavigatorBySessionID(viewedSessionId);
        ProfileNavigatorDetailEntity profileNavigatorDetailEntity = profileRepository
                .findNavigatorBetweenSessionIDAndViewedSessionID(
                profileNavigatorSessionEntity, profileNavigatorViewedEntity);
        double distanceValue = profileNavigatorDetailEntity.getDistance();
        Integer distanceAsInt = (int) Math.floor(distanceValue < MIN_DISTANCE ? MIN_DISTANCE : distanceValue);
        List<ProfileImageEntity> profileImageListEntity = findImageListBySessionID(sessionId);
        ProfileTelegramEntity profileTelegramEntity = findTelegramBySessionID(sessionId);
        Optional<ProfileLikeEntity> profileLikeEntity =
                Optional.ofNullable(findLikeBySessionID(viewedSessionId, sessionId));
        boolean isOnline = Utils.calculateIsOnline(profileEntity.getLastOnline());
        return ResponseProfileDetailGetDto.builder()
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
                .isOnline(isOnline)
                .createdAt(profileEntity.getCreatedAt())
                .updatedAt(profileEntity.getUpdatedAt())
                .lastOnline(profileEntity.getLastOnline())
                .navigator(ResponseProfileDetailNavigatorDto.builder()
                        .distance(distanceAsInt)
                        .build())
                .telegram(ResponseProfileTelegramDto.builder()
                        .sessionId(profileTelegramEntity.getSessionId())
                        .userId(profileTelegramEntity.getUserId())
                        .username(profileTelegramEntity.getUsername())
                        .firstName(profileTelegramEntity.getFirstName())
                        .lastName(profileTelegramEntity.getLastName())
                        .languageCode(profileTelegramEntity.getLanguageCode())
                        .allowsWriteToPm(profileTelegramEntity.getAllowsWriteToPm())
                        .queryId(profileTelegramEntity.getQueryId())
                        .chatId(profileTelegramEntity.getChatId())
                        .build())
                .like(profileLikeEntity.map(likeEntity -> ResponseProfileLikeDto.builder()
                        .id(likeEntity.getId())
                        .sessionId(likeEntity.getSessionId())
                        .likedSessionId(likeEntity.getLikedSessionId())
                        .isLiked(likeEntity.getIsLiked())
                        .createdAt(likeEntity.getCreatedAt())
                        .updatedAt(likeEntity.getUpdatedAt())
                        .build()).orElse(null))
                .images(profileImageListEntity)
                .build();
    }

    @Override
    public ResponseProfileShortInfoGetDto getProfileShortInfo(String sessionId, Double latitude, Double longitude) {
        updateLastOnline(sessionId);
        updateNavigator(sessionId, latitude, longitude);
        ProfileEntity profileEntity = findBySessionID(sessionId);
        checkUserExists(profileEntity.getIsDeleted());
        List<ProfileImageEntity> profileImageListEntity = findImageListBySessionID(sessionId);
        ProfileImageEntity lastImage = profileImageListEntity.getLast();
        return ResponseProfileShortInfoGetDto.builder()
                .sessionId(profileEntity.getSessionId())
                .imageUrl(lastImage.getUrl())
                .isDeleted(profileEntity.getIsDeleted())
                .isBlocked(profileEntity.getIsBlocked())
                .build();
    }

    @Override
    public void deleteImage(Long id) {
        ProfileImageEntity profileImageEntity = profileRepository.findImageByID(id);
        if (profileImageEntity.getIsDeleted()) {
            throw new NotFoundException(
                    "Файл был удален",
                    "The file has been deleted"
            );
        }
        String filePath = BASE_PROJECT_PATH.resolve("src/main/resources/static/images")
                .resolve(profileImageEntity.getSessionId())
                .resolve(profileImageEntity.getName())
                .toString();
        deleteFileFromFileSystem(filePath);
        profileRepository.deleteImage(id);
    }

    private void deleteImageAllBySessionID(String sessionId) {
        try {
            List<ProfileImageEntity> profileImageListEntity = findImageListBySessionID(sessionId);
            for (ProfileImageEntity imageEntity : profileImageListEntity) {
                deleteImage(imageEntity.getId());
            }
            Path directoryPath = Paths.get(BASE_PROJECT_PATH.toString(), "src", "main", "resources", "static", "images", sessionId);
            Files.deleteIfExists(directoryPath);
        } catch (Exception e) {
            throw new InternalServerException(
                    "Ошибка удаления директории",
                    "Unexpected error occurred while deleting directory: " + e.getMessage()
            );
        }
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

    private void updateImages(RequestProfileUpdateDto requestProfileUpdateDto) {
        String sessionId = requestProfileUpdateDto.getSessionId();
        if (requestProfileUpdateDto.getImage() != null) {
            for (MultipartFile file : requestProfileUpdateDto.getImage()) {
                ImageConverterRecord imageConverterRecord = uploadImageToFileSystem(file, sessionId);
                addImageToDB(sessionId, imageConverterRecord);
            }
        }
    }

    private ImageConverterRecord uploadImageToFileSystem(MultipartFile file, String sessionId) {
        try {
            Path staticFolderPath = BASE_PROJECT_PATH.resolve("src/main/resources/static/images");
            String fileName = file.getOriginalFilename();
            String filePath = String.format("%s/%s/%s", staticFolderPath, sessionId, fileName);
            File directory = new File(filePath).getParentFile();
            if (!directory.exists()) directory.mkdirs(); // if (!Files.exists(staticFolderPath)) Files.createDirectories(staticFolderPath);
            file.transferTo(new File(filePath));
            return ImageConverter.convertImage(filePath, fileName);
        } catch (Exception e) {
            throw new InternalServerException(
                    "Ошибка сохранения файла",
                    "Ошибка сохранения файла на сервер: " + e.getMessage()
            );
        }
    }

    private void deleteFileFromFileSystem(String filePath) {
        try {
            File fileToDelete = new File(filePath);
            if (!fileToDelete.exists()) {
                throw new NotFoundException(
                        "Файл не найден",
                        "File does not exist: " + filePath
                );
            }
            if (!fileToDelete.delete()) {
                throw new IOException("Failed to delete file: " + filePath);
            }
        } catch (NotFoundException e) {
            throw new NotFoundException(
                    "Файл не найден",
                    "File does not exist: " + filePath
            );
        } catch (IOException e) {
            throw new InternalServerException(
                    "Ошибка удаления файла",
                    "Error occurred while deleting file: " + e.getMessage()
            );
        } catch (Exception e) {
            throw new InternalServerException(
                    "Ошибка удаления файла",
                    "Unexpected error occurred while deleting file: " + e.getMessage()
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

    public ResponseProfileNavigatorDto updateCoordinates(
            RequestProfileNavigatorUpdateDto requestProfileNavigatorUpdateDto) {
        String sessionId = requestProfileNavigatorUpdateDto.getSessionId();
        Double latitude = requestProfileNavigatorUpdateDto.getLatitude();
        Double longitude = requestProfileNavigatorUpdateDto.getLongitude();
        return ResponseProfileNavigatorDto.builder()
                .sessionId(updateNavigator(sessionId, latitude, longitude).getSessionId())
                .location(updateNavigator(requestProfileNavigatorUpdateDto.getSessionId(),
                        requestProfileNavigatorUpdateDto.getLatitude(),
                        requestProfileNavigatorUpdateDto.getLongitude()).getLocation())
                .build();
    }

    @Override
    public ResponseProfileLikeDto addLike(RequestProfileLikeAddDto requestProfileLikeAddDto) {
        ProfileLikeEntity profileLikeEntity = profileRepository.addLike(requestProfileLikeAddDto);
        return ResponseProfileLikeDto.builder()
                .id(profileLikeEntity.getId())
                .sessionId(profileLikeEntity.getSessionId())
                .likedSessionId(profileLikeEntity.getLikedSessionId())
                .isLiked(profileLikeEntity.getIsLiked())
                .createdAt(profileLikeEntity.getCreatedAt())
                .updatedAt(profileLikeEntity.getUpdatedAt())
                .build();
    }

    @Override
    public ProfileBlockEntity addBlock(RequestProfileBlockAddDto requestProfileBlockAddDto) {
        return profileRepository.addBlock(requestProfileBlockAddDto);
    }

    @Override
    public ProfileComplaintEntity addComplaint(RequestProfileComplaintAddDto requestProfileComplaintAddDto) {
        String sessionId = requestProfileComplaintAddDto.getSessionId();
        String criminalSessionId = requestProfileComplaintAddDto.getCriminalSessionId();
        RequestProfileBlockAddDto requestProfileBlockAddDto = RequestProfileBlockAddDto.builder()
                .sessionId(sessionId)
                .blockedUserSessionId(criminalSessionId)
                .build();
        profileRepository.addBlock(requestProfileBlockAddDto);
        ProfileComplaintEntity profileComplaintEntity = profileRepository.addComplaint(requestProfileComplaintAddDto);
        Integer countComplaints = profileRepository.countComplaintsByCurrentMonthAndSessionID(criminalSessionId);
        if (countComplaints > MAX_COUNT_COMPLAINTS) {
            profileRepository.blockProfile(criminalSessionId);
        }
        return profileComplaintEntity;
    }

    private ProfileNavigatorEntity updateNavigator(String sessionId, Double latitude, Double longitude) {
        if (latitude != null && longitude != null) {
            return profileRepository.updateNavigator(
                    RequestProfileNavigatorUpdateDto.builder()
                            .sessionId(sessionId)
                            .latitude(latitude)
                            .longitude(longitude)
                            .build()
            );
        }
        return null;
    }

    private ProfileNavigatorEntity deleteNavigator(String sessionId) {
        return profileRepository.deleteNavigator(sessionId);
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

    private ProfileTelegramEntity updateTelegram(RequestProfileTelegramUpdateDto requestProfileTelegramUpdateDto) {
        return profileRepository.updateTelegram(requestProfileTelegramUpdateDto);
    }

    private ProfileTelegramEntity deleteTelegram(String sessionId) {
        return profileRepository.deleteTelegram(sessionId);
    }

    public ResponseProfileFilterDto getFilterBySessionID(String sessionId, Double latitude, Double longitude) {
        updateLastOnline(sessionId);
        updateNavigator(sessionId, latitude, longitude);
        ProfileFilterEntity profileFilterEntity = profileRepository.findFilterBySessionID(sessionId);
        return getResponseProfileFilterDto(profileFilterEntity);
    }

    public ResponseProfileFilterDto updateFilter(RequestProfileFilterUpdateDto requestProfileFilterUpdateDto) {
        String sessionId = requestProfileFilterUpdateDto.getSessionId();
        Double latitude = requestProfileFilterUpdateDto.getLatitude();
        Double longitude = requestProfileFilterUpdateDto.getLongitude();
        updateLastOnline(sessionId);
        updateNavigator(sessionId, latitude, longitude);
        ProfileFilterEntity profileFilterEntity = profileRepository.updateFilter(requestProfileFilterUpdateDto);
        return getResponseProfileFilterDto(profileFilterEntity);
    }

    private ProfileFilterEntity deleteFilter(String sessionId) {
        return profileRepository.deleteFilter(sessionId);
    }

    private ResponseProfileFilterDto getResponseProfileFilterDto(ProfileFilterEntity profileFilterEntity) {
        ResponseProfileFilterDto responseProfileFilterDto = new ResponseProfileFilterDto();
        responseProfileFilterDto.setSessionId(profileFilterEntity.getSessionId());
        responseProfileFilterDto.setSearchGender(profileFilterEntity.getSearchGender());
        responseProfileFilterDto.setLookingFor(profileFilterEntity.getLookingFor());
        responseProfileFilterDto.setAgeFrom(profileFilterEntity.getAgeFrom());
        responseProfileFilterDto.setAgeTo(profileFilterEntity.getAgeTo());
        responseProfileFilterDto.setDistance(profileFilterEntity.getDistance());
        responseProfileFilterDto.setPage(profileFilterEntity.getPage());
        responseProfileFilterDto.setSize(profileFilterEntity.getSize());
        return responseProfileFilterDto;
    }

    private ProfileEntity findBySessionID(String sessionId) {
        return profileRepository.findBySessionID(sessionId);
    }

    private List<ProfileImageEntity> findImageListBySessionID(String sessionId) {
        return profileRepository.findImageListBySessionID(sessionId);
    }

    private List<ProfileImageEntity> findImagePublicListBySessionID(String sessionId) {
        return profileRepository.findImagePublicListBySessionID(sessionId);
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

    private ProfileLikeEntity findLikeBySessionID(String sessionId, String likedSessionId) {
        return profileRepository.findLikeBySessionID(sessionId, likedSessionId);
    }

    private void checkUserExists(boolean isDeleted) {
        if (isDeleted) {
            throw new NotFoundException(
                    "Пользователь был удален",
                    "User has already been deleted"
            );
        }
    }
}
