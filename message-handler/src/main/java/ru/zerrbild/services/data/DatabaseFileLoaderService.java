package ru.zerrbild.services.data;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.zerrbild.entities.DocumentEntity;
import ru.zerrbild.entities.ImageEntity;

public interface DatabaseFileLoaderService {
    DocumentEntity loadDocument(Update update);
    ImageEntity loadImage(Update update);
}
