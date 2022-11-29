package ru.practicum.exploreWithMe.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class StatsServerClient {
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate = new RestTemplate();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatsServerClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void setStat(HttpServletRequest request) {
        log.info(String.format("отправка статистики по url %s", request.getRequestURI()));
        restTemplate.postForEntity(getUrl("/hit"),
                objectMapper.createObjectNode()
                        .put("app", "ewm-service")
                        .put("uri", request.getRequestURI())
                        .put("ip", request.getRemoteAddr())
                        .put("timestamp", LocalDateTime.now().format(formatter))
                        .put("hits", 1), String.class);
    }

    private URI getUrl(String path) {
        try {
            return new URI("http://stats-server:9090" + path);
        } catch (Exception e) {
            log.error(String.format("can't create request with path %s. Error: %s", path, e.getMessage()));
            throw new RuntimeException("can't create request");
        }
    }
}
