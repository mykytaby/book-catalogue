package com.university.library;

/**
 * Виняток, що виникає коли публікацію з вказаною назвою не знайдено у каталозі.
 */
public class BookNotFoundException extends Exception {
    public BookNotFoundException(String message) {
        super(message);
    }
}