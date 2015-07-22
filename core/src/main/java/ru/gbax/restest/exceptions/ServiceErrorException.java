 package ru.gbax.restest.exceptions;

/**
 * Ошибка на сервере
 * Created by GBAX on 22.07.2015.
 */
public class ServiceErrorException extends Exception {

    public ServiceErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceErrorException(String message) {
        super(message);
    }

}
