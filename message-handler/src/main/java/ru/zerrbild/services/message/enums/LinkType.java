package ru.zerrbild.services.message.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum LinkType {
    DOC("document", "документа"),
    IMAGE("image", "изображения");

    private final String linkEnding;
    @Getter
    private final String replyInfo;

    @Override
    public String toString() {
        return linkEnding;
    }
}
