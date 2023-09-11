package ru.zerrbild.services.message;

import ru.zerrbild.services.message.enums.LinkType;

public interface LinkCreatorService {
    String createDownloadLink(Long id, LinkType type);
}
