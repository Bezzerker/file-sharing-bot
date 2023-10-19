package ru.zerrbild.services.message.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.zerrbild.services.message.LinkCreatorService;
import ru.zerrbild.services.message.enums.LinkType;
import ru.zerrbild.utils.ciphering.Encoder;
import ru.zerrbild.utils.ciphering.exceptions.IncorrectKeyException;

@Slf4j
@RequiredArgsConstructor
@Service
public class LinkCreatorServiceImpl implements LinkCreatorService {
    @Value("${url_components.main_site.domain}")
    private String mainSiteDomain;
    @Value("${ciphering.key}")
    private String encodingKey;
    private final Encoder encoder;

    public String createDownloadLink(Long id, LinkType type) {
        try {
            String encodedIdUrlParam = encoder.encodeForUrl(id, encodingKey);
            return String.format("https://%s/files/%s?id=%s", mainSiteDomain, type, encodedIdUrlParam);
        } catch (IncorrectKeyException e) {
            log.error(e.getMessage());
            return "Произошла внутренняя ошибка!";
        }
    }
}