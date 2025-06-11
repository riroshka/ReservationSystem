package ru.mivlgu.ReservationSystem.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.io.ByteArrayResource;
import ru.mivlgu.ReservationSystem.Entities.EventTemplate;
import ru.mivlgu.ReservationSystem.dto.EventCardDto;

import java.io.IOException;
import java.util.Base64;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class VKService {

    @Value("${vk.api.token}")  // Токен доступа для использования API
    private String serviceKey;

    @Value("${vk.api.group.id}")  // ID группы ВКонтакте
    private String groupId;

    private static final String VK_API_URL = "https://api.vk.com/method/wall.post";
    private static final String VK_PHOTO_UPLOAD_URL = "https://api.vk.com/method/photos.getWallUploadServer";
    private static final String VK_PHOTO_SAVE_URL = "https://api.vk.com/method/photos.saveWallPhoto";

    // Метод для отправки поста в группу ВКонтакте
    public void postToVK(EventCardDto eventCardDto) {
        String title = eventCardDto.getTitle();
        String date = eventCardDto.getDate();
        String time = eventCardDto.getTime();
        String description = eventCardDto.getDescription();
        String photoBase64 = eventCardDto.getPhotoBase64();

        // Формируем тело поста
        String postContent = String.format(
                "Доборогие студенты и преподаватели!\nПриглашаем вас на мероприятие: %s\n%s в %s\n%s",
                title, date, time, description
        );

        // Если фото имеется, загружаем его в ВКонтакте
        String photoAttachment = null;
        if (photoBase64 != null) {
            String photoId = uploadPhotoToVK(photoBase64);  // Загружаем фото на сервер ВКонтакте
            if (photoId != null) {
                photoAttachment = "photo" + photoId;  // Получаем ID фотографии для использования в посте
            }
        }

        // Подготовка параметров для API ВКонтакте
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("owner_id", "-" + groupId);  // Указываем ID группы с минусом для стен
        body.add("from_group", "1");  // Указываем, что пост от группы
        body.add("message", postContent);  // Текст поста
        if (photoAttachment != null) {
            body.add("attachments", photoAttachment);  // Прикрепляем фото
        }
        body.add("access_token", serviceKey);  // Ваш токен доступа
        body.add("v", "5.131");  // Версия API ВКонтакте

        // Настройки запроса
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        // Создаем экземпляр RestTemplate для отправки запроса
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(VK_API_URL, HttpMethod.POST, request, String.class);

        // Обработка ответа от VK
        System.out.println("Response from VK: " + response.getBody());
    }

    // Метод для загрузки фотографии в ВКонтакте и получения ID
    private String uploadPhotoToVK(String photoBase64) {
        try {
            // 1. Получение URL для загрузки (используем параметры в запросе)
            String uploadServerUrl = VK_PHOTO_UPLOAD_URL +
                    "?group_id=" + groupId +  // Используем правильный group_id
                    "&access_token=" + serviceKey +
                    "&v=5.131";

            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(uploadServerUrl, String.class);
            System.out.println("GetWallUploadServer response: " + response); // Логирование

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            // Проверка на ошибку VK
            if (root.has("error")) {
                String errorMsg = root.path("error").path("error_msg").asText();
                System.err.println("VK API Error: " + errorMsg);
                return null;
            }

            // Извлекаем URL для загрузки
            String uploadUrl = root.path("response").path("upload_url").asText();
            System.out.println("Upload URL: " + uploadUrl); // Логирование

            // 2. Подготовка бинарных данных
            if (photoBase64.contains(",")) {
                photoBase64 = photoBase64.split(",")[1];
            }
            byte[] imageData = Base64.getDecoder().decode(photoBase64);

            // 3. Загрузка файла на сервер ВК
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            ByteArrayResource fileResource = new ByteArrayResource(imageData) {
                @Override
                public String getFilename() {
                    return "event_photo.jpg";
                }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("photo", fileResource);

            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> uploadResponse = restTemplate.postForEntity(uploadUrl, request, String.class);
            System.out.println("Upload response: " + uploadResponse.getBody()); // Логирование

            // 4. Обработка ответа от сервера загрузки
            JsonNode uploadResult = mapper.readTree(uploadResponse.getBody());
            String server = uploadResult.path("server").asText();
            String photo = uploadResult.path("photo").asText();
            String hash = uploadResult.path("hash").asText();

            // 5. Сохранение фото
            return savePhotoToWall(server, photo, hash);

        } catch (Exception e) {
            System.err.println("Error uploading photo to VK: ");
            e.printStackTrace();
            return null;
        }
    }

    // Метод для сохранения фото на стене ВКонтакте
    private String savePhotoToWall(String server, String photo, String hash) {
        try {
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("group_id", groupId);
            body.add("server", server);
            body.add("photo", photo);
            body.add("hash", hash);
            body.add("access_token", serviceKey);
            body.add("v", "5.131");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(VK_PHOTO_SAVE_URL, request, String.class);
            System.out.println("SaveWallPhoto response: " + response.getBody()); // Логирование

            // Обработка ответа
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            // Проверка на ошибку
            if (root.has("error")) {
                String errorMsg = root.path("error").path("error_msg").asText();
                System.err.println("VK API Error: " + errorMsg);
                return null;
            }

            JsonNode photoArray = root.path("response");
            if (photoArray.isArray() && photoArray.size() > 0) {
                JsonNode photoNode = photoArray.get(0);
                String photoId = photoNode.path("id").asText();
                String ownerId = photoNode.path("owner_id").asText();
                return ownerId + "_" + photoId;
            }
        } catch (Exception e) {
            System.err.println("Error saving photo to wall: ");
            e.printStackTrace();
        }
        return null;
    }
}
