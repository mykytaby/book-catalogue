package com.university.library;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Каталог публікацій.
 * Підтримує додавання, видалення, пошук, сортування та серіалізацію.
 */
public class Catalogue {
    private List<Publication> publications;

    public Catalogue() {
        publications = new ArrayList<>();
    }

    /** Додає публікацію до каталогу. */
    public void addPublication(Publication p) {
        publications.add(p);
    }

    /**
     * Видаляє публікацію за назвою.
     * @throws BookNotFoundException якщо публікацію не знайдено
     */
    public void removePublicationByTitle(String title) throws BookNotFoundException {
        // findPublicationByTitle або поверне об'єкт, або кине виняток — null перевірка зайва
        Publication toRemove = findPublicationByTitle(title);
        publications.remove(toRemove);
    }

    /**
     * Знаходить публікацію за точною назвою (без урахування регістру).
     * @throws BookNotFoundException якщо публікацію не знайдено
     */
    public Publication findPublicationByTitle(String title) throws BookNotFoundException {
        for (Publication p : publications) {
            if (p.getTitle().equalsIgnoreCase(title.trim())) {
                return p;
            }
        }
        throw new BookNotFoundException("Публікацію з назвою '" + title + "' не знайдено!");
    }

    /**
     * Повертає захисну копію списку публікацій,
     * щоб зовнішній код не міг змінити внутрішню колекцію напряму.
     */
    public List<Publication> getAllPublications() {
        return new ArrayList<>(publications);
    }

    /** Повертає кількість публікацій у каталозі. */
    public int getCount() {
        return publications.size();
    }

    /**
     * Повертає список публікацій, відсортованих за назвою (А→Я).
     */
    public List<Publication> getSortedByTitle() {
        List<Publication> sorted = new ArrayList<>(publications);
        sorted.sort(Comparator.comparing(p -> p.getTitle().toLowerCase()));
        return sorted;
    }

    /**
     * Повертає список публікацій, відсортованих за роком видання (старіші спочатку).
     */
    public List<Publication> getSortedByYear() {
        List<Publication> sorted = new ArrayList<>(publications);
        sorted.sort(Comparator.comparingInt(Publication::getYear));
        return sorted;
    }

    /**
     * Повертає список публікацій, у яких назва або автор містить рядок query.
     * Пошук регістронезалежний.
     */
    public List<Publication> search(String query) {
        String q = query.toLowerCase().trim();
        List<Publication> result = new ArrayList<>();
        for (Publication p : publications) {
            boolean titleMatch = p.getTitle().toLowerCase().contains(q);
            boolean authorMatch = (p instanceof Book)
                    && ((Book) p).getAuthor().toLowerCase().contains(q);
            if (titleMatch || authorMatch) {
                result.add(p);
            }
        }
        return result;
    }

    // ── Серіалізація ─────────────────────────────────────────────────────────

    /** Зберігає каталог у бінарний файл. */
    public void saveToFile(String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(publications);
        }
    }

    /** Завантажує каталог із бінарного файлу. */
    @SuppressWarnings("unchecked")
    public void loadFromFile(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            publications = (List<Publication>) ois.readObject();
        }
    }
}
