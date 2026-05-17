package com.university.library;

/**
 * Утилітний клас для попереднього заповнення каталогу тестовими даними.
 * Запускається один раз перед демонстрацією програми.
 */
public class PreloadData {
    public static void main(String[] args) throws Exception {
        Catalogue catalogue = new Catalogue();

        catalogue.addPublication(new Book("Кобзар",                    1840, "Тарас Шевченко",                  "Знання",                              "Поезія"));
        catalogue.addPublication(new Book("Тіні забутих предків",       1911, "Михайло Коцюбинський",            "Дніпро",                              "Повість"));
        catalogue.addPublication(new Book("Місто",                      1928, "Валер'ян Підмогильний",           "Фоліо",                               "Роман"));
        catalogue.addPublication(new Book("Лісова пісня",               1911, "Леся Українка",                   "Школа",                               "Драма"));
        catalogue.addPublication(new Book("Захар Беркут",               1882, "Іван Франко",                     "Видавництво Львівської політехніки",  "Історична проза"));
        catalogue.addPublication(new Book("Майстер і Маргарита",        1967, "Михайло Булгаков",                "Фоліо",                               "Роман"));
        catalogue.addPublication(new Book("1984",                       1949, "Джордж Орвелл",                   "Видавництво Жупанського",             "Антиутопія"));
        catalogue.addPublication(new Book("Маленький принц",            1943, "Антуан де Сент-Екзюпері",         "Рідна мова",                          "Казка"));

        catalogue.saveToFile("catalogue.dat");
        System.out.println("Готово! Збережено " + catalogue.getCount() + " книг у catalogue.dat");
    }
}
