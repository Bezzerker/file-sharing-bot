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
    @Value("${link.protocol}")
    private String protocol;
    @Value("${link.address}")
    private String address;
    @Value("${link.port.rest_service}")
    private String restServicePort;
    @Value("${link.encryption_key}")
    private String key;
    private final Encoder encoder;

    public String createDownloadLink(Long id, LinkType type) {
        try {
            String encodedIdUrlParam = encoder.encodeForUrl(id, key);
            return String.format("%s://%s:%s/file/%s?id=%s", protocol, address, restServicePort, type, encodedIdUrlParam);
        } catch (IncorrectKeyException e) {
            log.error(e.getMessage());
            return "Произошла внутренняя ошибка!";
        }
    }
}