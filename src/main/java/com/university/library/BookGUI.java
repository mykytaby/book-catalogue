package com.university.library;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;

/**
 * Головне вікно програми «Каталог книг».
 * Реалізує сучасний темний інтерфейс з JTable, сортуванням,
 * статистикою та підтвердженням видалення.
 */
public class BookGUI extends JFrame implements ActionListener {

    // ── Кольорова схема (темна тема) ────────────────────────────────────────
    private static final Color BG_DARK      = new Color(18,  18,  28);
    private static final Color BG_PANEL     = new Color(28,  28,  42);
    private static final Color BG_FIELD     = new Color(38,  38,  56);
    private static final Color ACCENT       = new Color(99,  102, 241); // indigo
    private static final Color TEXT_PRIMARY = new Color(236, 236, 255);
    private static final Color TEXT_MUTED   = new Color(140, 140, 180);
    private static final Color BORDER_COLOR = new Color(55,  55,  80);
    private static final Color ROW_ALT      = new Color(32,  32,  50);
    private static final Color ROW_SELECT   = new Color(60,  63,  120);
    private static final Color BTN_DANGER   = new Color(220, 60,  80);
    private static final Color BTN_SUCCESS  = new Color(34,  197, 94);

    private static final Font  FONT_TITLE   = new Font("Segoe UI", Font.BOLD,  22);
    private static final Font  FONT_LABEL   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font  FONT_FIELD   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font  FONT_BTN     = new Font("Segoe UI", Font.BOLD,  12);
    private static final Font  FONT_TABLE   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font  FONT_STATS   = new Font("Segoe UI", Font.PLAIN, 12);

    // ── Дані ─────────────────────────────────────────────────────────────────
    private final Catalogue catalogue = new Catalogue();
    private final String    FILE_NAME = "catalogue.dat";

    // ── Поля вводу ───────────────────────────────────────────────────────────
    private JTextField titleField, authorField, publisherField, genreField, yearField, searchField;

    // ── Таблиця ──────────────────────────────────────────────────────────────
    private JTable          table;
    private DefaultTableModel tableModel;

    // ── Кнопки ───────────────────────────────────────────────────────────────
    private JButton btnAdd, btnDelete, btnUpdate, btnSave, btnLoad, btnClear;
    private JComboBox<String> sortCombo;

    // ── Статус-рядок ─────────────────────────────────────────────────────────
    private JLabel statusLabel;
    private JLabel countLabel;

    // ════════════════════════════════════════════════════════════════════════
    public BookGUI() {
        setTitle("Каталог книг  ·  Виконав: П'ятаченко В.Ю.");
        setSize(1050, 680);
        setMinimumSize(new Dimension(860, 540));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout(0, 0));

        add(buildHeader(),    BorderLayout.NORTH);
        add(buildCenter(),    BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        refreshTable(catalogue.getAllPublications());
        setStatus("Готово. Каталог порожній.");
    }

    // ── Хедер ────────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_PANEL);
        header.setBorder(new MatteBorder(0, 0, 1, 0, BORDER_COLOR));

        JLabel title = new JLabel("  📚  Каталог книг");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_PRIMARY);
        title.setBorder(new EmptyBorder(14, 16, 14, 0));

        countLabel = new JLabel("0 записів  ");
        countLabel.setFont(FONT_STATS);
        countLabel.setForeground(TEXT_MUTED);
        countLabel.setBorder(new EmptyBorder(0, 0, 0, 20));
        countLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        header.add(title,      BorderLayout.WEST);
        header.add(countLabel, BorderLayout.EAST);
        return header;
    }

    // ── Центральна частина ───────────────────────────────────────────────────
    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(12, 12));
        center.setBackground(BG_DARK);
        center.setBorder(new EmptyBorder(14, 14, 6, 14));

        center.add(buildFormPanel(),  BorderLayout.WEST);
        center.add(buildTablePanel(), BorderLayout.CENTER);
        return center;
    }

    // ── Панель форми ─────────────────────────────────────────────────────────
    private JPanel buildFormPanel() {
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(BG_PANEL);
        form.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(16, 16, 16, 16)
        ));
        form.setPreferredSize(new Dimension(260, 0));

        // ─ Поля вводу ─
        titleField     = styledField("Назва книги *");
        authorField    = styledField("Автор *");
        publisherField = styledField("Видавництво");
        genreField     = styledField("Жанр");
        yearField      = styledField("Рік видання *");

        form.add(formRow("Назва",       titleField));
        form.add(Box.createVerticalStrut(8));
        form.add(formRow("Автор",       authorField));
        form.add(Box.createVerticalStrut(8));
        form.add(formRow("Видавництво", publisherField));
        form.add(Box.createVerticalStrut(8));
        form.add(formRow("Жанр",        genreField));
        form.add(Box.createVerticalStrut(8));
        form.add(formRow("Рік",         yearField));
        form.add(Box.createVerticalStrut(18));

        // ─ Кнопки дій ─
        btnAdd    = accentButton("＋  Додати книгу",    ACCENT);
        btnUpdate = accentButton("✎  Оновити книгу",   new Color(234, 179, 8));
        btnDelete = accentButton("✕  Видалити книгу",  BTN_DANGER);
        btnClear  = accentButton("↺  Очистити поля",   new Color(75, 85, 99));

        form.add(btnAdd);    form.add(Box.createVerticalStrut(8));
        form.add(btnUpdate); form.add(Box.createVerticalStrut(8));
        form.add(btnDelete); form.add(Box.createVerticalStrut(8));
        form.add(btnClear);  form.add(Box.createVerticalStrut(18));

        // ─ Файлові операції ─
        JLabel fileLabel = new JLabel("Файл");
        fileLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        fileLabel.setForeground(TEXT_MUTED);
        fileLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(fileLabel);
        form.add(Box.createVerticalStrut(6));

        btnSave = accentButton("💾  Зберегти у файл",       BTN_SUCCESS);
        btnLoad = accentButton("📂  Завантажити з файлу",   new Color(14, 165, 233));
        form.add(btnSave); form.add(Box.createVerticalStrut(8));
        form.add(btnLoad);

        // Реєструємо слухачів
        for (JButton btn : new JButton[]{btnAdd, btnDelete, btnUpdate, btnSave, btnLoad, btnClear}) {
            btn.addActionListener(this);
        }

        return form;
    }

    // ── Панель таблиці ───────────────────────────────────────────────────────
    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(BG_DARK);

        // ─ Пошук + сортування ─
        JPanel topBar = new JPanel(new BorderLayout(8, 0));
        topBar.setBackground(BG_DARK);

        searchField = new JTextField();
        searchField.setFont(FONT_FIELD);
        searchField.setBackground(BG_FIELD);
        searchField.setForeground(TEXT_PRIMARY);
        searchField.setCaretColor(TEXT_PRIMARY);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(6, 10, 6, 10)
        ));
        // Пошук у реальному часі під час введення
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { liveSearch(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { liveSearch(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { liveSearch(); }
        });

        // Заголовок пошуку
        JLabel searchIcon = new JLabel("🔍  Пошук:");
        searchIcon.setForeground(TEXT_MUTED);
        searchIcon.setFont(FONT_LABEL);

        // Сортування
        sortCombo = new JComboBox<>(new String[]{"Без сортування", "За назвою А→Я", "За роком ↑"});
        sortCombo.setFont(FONT_LABEL);
        sortCombo.setBackground(BG_FIELD);
        sortCombo.setForeground(TEXT_PRIMARY);
        sortCombo.setPreferredSize(new Dimension(180, 32));
        sortCombo.addActionListener(e -> applySort());

        JPanel searchRow = new JPanel(new BorderLayout(6, 0));
        searchRow.setBackground(BG_DARK);
        searchRow.add(searchIcon,  BorderLayout.WEST);
        searchRow.add(searchField, BorderLayout.CENTER);
        searchRow.add(sortCombo,   BorderLayout.EAST);

        topBar.add(searchRow, BorderLayout.CENTER);
        panel.add(topBar, BorderLayout.NORTH);

        // ─ Таблиця ─
        String[] cols = {"Назва", "Автор", "Видавництво", "Жанр", "Рік"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(FONT_TABLE);
        table.setForeground(TEXT_PRIMARY);
        table.setBackground(BG_PANEL);
        table.setSelectionBackground(ROW_SELECT);
        table.setSelectionForeground(Color.WHITE);
        table.setRowHeight(30);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(BORDER_COLOR);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFillsViewportHeight(true);

        // Чергування рядків
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setFont(FONT_TABLE);
                if (sel) {
                    setBackground(ROW_SELECT);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(row % 2 == 0 ? BG_PANEL : ROW_ALT);
                    setForeground(TEXT_PRIMARY);
                }
                setBorder(new EmptyBorder(0, 10, 0, 10));
                return this;
            }
        });

        // Заголовок таблиці
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(40, 40, 62));
        header.setForeground(TEXT_MUTED);
        header.setBorder(new MatteBorder(0, 0, 1, 0, BORDER_COLOR));
        header.setPreferredSize(new Dimension(header.getWidth(), 36));

        // Ширини колонок
        int[] widths = {220, 160, 140, 110, 60};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Клік на рядок → заповнює форму
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillFormFromSelectedRow();
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(BG_PANEL);
        scroll.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        scroll.setBackground(BG_DARK);

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // ── Статус-рядок ─────────────────────────────────────────────────────────
    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BG_PANEL);
        bar.setBorder(new MatteBorder(1, 0, 0, 0, BORDER_COLOR));

        statusLabel = new JLabel("  Готово");
        statusLabel.setFont(FONT_STATS);
        statusLabel.setForeground(TEXT_MUTED);
        statusLabel.setBorder(new EmptyBorder(5, 10, 5, 0));

        JLabel author = new JLabel("П'ятаченко В.Ю.  ");
        author.setFont(FONT_STATS);
        author.setForeground(TEXT_MUTED);

        bar.add(statusLabel, BorderLayout.WEST);
        bar.add(author,      BorderLayout.EAST);
        return bar;
    }

    // ════════════════════════════════════════════════════════════════════════
    // ActionListener
    // ════════════════════════════════════════════════════════════════════════
    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        try {
            if      (src == btnAdd)    addBook();
            else if (src == btnDelete) deleteBook();
            else if (src == btnUpdate) updateBook();
            else if (src == btnSave)   saveData();
            else if (src == btnLoad)   loadData();
            else if (src == btnClear)  clearAndReset();
        } catch (NumberFormatException ex) {
            showError("Рік видання має бути цілим числом!");
        } catch (BookNotFoundException ex) {
            showWarn(ex.getMessage());
        } catch (Exception ex) {
            showError("Непередбачена помилка: " + ex.getMessage());
        }
    }

    // ── Логіка кнопок ────────────────────────────────────────────────────────

    private void addBook() {
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String yearTxt = yearField.getText().trim();

        if (title.isEmpty())   { showWarn("Введіть назву книги!"); return; }
        if (author.isEmpty())  { showWarn("Введіть автора!"); return; }
        if (yearTxt.isEmpty()) { showWarn("Введіть рік видання!"); return; }

        int year = Integer.parseInt(yearTxt);
        if (year < 1 || year > 2100) { showWarn("Вкажіть реальний рік (1–2100)."); return; }

        Book book = new Book(title, year, author,
                publisherField.getText().trim(),
                genreField.getText().trim());
        catalogue.addPublication(book);
        applySort();
        clearFields();
        setStatus("Додано книгу: «" + title + "»");
    }

    private void deleteBook() throws BookNotFoundException {
        String title = titleField.getText().trim();
        if (title.isEmpty()) { showWarn("Введіть назву книги для видалення."); return; }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Видалити книгу «" + title + "»?",
            "Підтвердження видалення",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        catalogue.removePublicationByTitle(title);
        applySort();
        clearFields();
        setStatus("Видалено книгу: «" + title + "»");
    }

    private void updateBook() throws BookNotFoundException {
        String title = titleField.getText().trim();
        if (title.isEmpty()) { showWarn("Введіть назву книги для оновлення."); return; }

        Publication p = catalogue.findPublicationByTitle(title);
        if (!(p instanceof Book)) { showWarn("Запис не є книгою."); return; }
        Book book = (Book) p;

        String yearTxt = yearField.getText().trim();
        if (!yearTxt.isEmpty()) {
            int year = Integer.parseInt(yearTxt);
            if (year < 1 || year > 2100) { showWarn("Вкажіть реальний рік."); return; }
            book.setYear(year);
        }
        if (!authorField.getText().trim().isEmpty())
            book.setAuthor(authorField.getText().trim());
        if (!publisherField.getText().trim().isEmpty())
            book.setPublisher(publisherField.getText().trim());
        if (!genreField.getText().trim().isEmpty())
            book.setGenre(genreField.getText().trim());

        applySort();
        setStatus("Оновлено книгу: «" + title + "»");
    }

    private void saveData() throws IOException {
        catalogue.saveToFile(FILE_NAME);
        setStatus("Каталог збережено у файл «" + FILE_NAME + "».");
        JOptionPane.showMessageDialog(this, "Дані збережено у " + FILE_NAME, "Збережено", JOptionPane.INFORMATION_MESSAGE);
    }

    private void loadData() {
        try {
            catalogue.loadFromFile(FILE_NAME);
            applySort();
            setStatus("Завантажено " + catalogue.getCount() + " записів з файлу.");
            JOptionPane.showMessageDialog(this, "Дані завантажено успішно.", "Завантажено", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException | ClassNotFoundException ex) {
            showError("Помилка завантаження (файл може бути відсутній): " + ex.getMessage());
        }
    }

    private void clearAndReset() {
        clearFields();
        searchField.setText("");
        refreshTable(catalogue.getAllPublications());
        setStatus("Поля очищено.");
    }

    // ── Пошук у реальному часі ───────────────────────────────────────────────
    private void liveSearch() {
        String q = searchField.getText().trim();
        if (q.isEmpty()) {
            applySort();
        } else {
            refreshTable(catalogue.search(q));
        }
    }

    // ── Сортування ───────────────────────────────────────────────────────────
    private void applySort() {
        int idx = sortCombo.getSelectedIndex();
        List<Publication> list;
        if (idx == 1)      list = catalogue.getSortedByTitle();
        else if (idx == 2) list = catalogue.getSortedByYear();
        else               list = catalogue.getAllPublications();

        String q = searchField.getText().trim();
        if (!q.isEmpty()) {
            list.retainAll(catalogue.search(q));
        }
        refreshTable(list);
    }

    // ── Заповнити форму з вибраного рядка ────────────────────────────────────
    private void fillFormFromSelectedRow() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        titleField.setText(safe(tableModel.getValueAt(row, 0)));
        authorField.setText(safe(tableModel.getValueAt(row, 1)));
        publisherField.setText(safe(tableModel.getValueAt(row, 2)));
        genreField.setText(safe(tableModel.getValueAt(row, 3)));
        yearField.setText(safe(tableModel.getValueAt(row, 4)));
    }

    private String safe(Object o) { return o == null ? "" : o.toString(); }

    // ── Оновлення таблиці ────────────────────────────────────────────────────
    private void refreshTable(List<Publication> list) {
        tableModel.setRowCount(0);
        for (Publication p : list) {
            if (p instanceof Book b) {
                tableModel.addRow(new Object[]{
                        b.getTitle(), b.getAuthor(), b.getPublisher(), b.getGenre(), b.getYear()
                });
            } else {
                tableModel.addRow(new Object[]{p.getTitle(), "", "", "", p.getYear()});
            }
        }
        countLabel.setText(catalogue.getCount() + " записів  ");
    }

    // ── Допоміжні ────────────────────────────────────────────────────────────
    private void clearFields() {
        titleField.setText(""); authorField.setText(""); publisherField.setText("");
        genreField.setText(""); yearField.setText("");
        table.clearSelection();
    }

    private void setStatus(String msg) { statusLabel.setText("  " + msg); }
    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Помилка", JOptionPane.ERROR_MESSAGE);
    }
    private void showWarn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Увага", JOptionPane.WARNING_MESSAGE);
    }

    // ── Кнопка зі стилем ─────────────────────────────────────────────────────
    private JButton accentButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BTN);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btn.setPreferredSize(new Dimension(220, 36));

        Color dimmed = bg.darker();
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e)  { btn.setBackground(dimmed); }
            public void mouseExited(MouseEvent e)   { btn.setBackground(bg); }
            public void mousePressed(MouseEvent e)  { btn.setBackground(dimmed.darker()); }
            public void mouseReleased(MouseEvent e) { btn.setBackground(dimmed); }
        });
        return btn;
    }

    // ── Підписане поле ────────────────────────────────────────────────────────
    private JTextField styledField(String placeholder) {
        JTextField f = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !hasFocus()) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g2.setColor(TEXT_MUTED);
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    Insets ins = getInsets();
                    g2.drawString(placeholder, ins.left + 2, getHeight() / 2 + 5);
                }
            }
        };
        f.setFont(FONT_FIELD);
        f.setBackground(BG_FIELD);
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(TEXT_PRIMARY);
        f.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(5, 8, 5, 8)
        ));
        return f;
    }

    // ── Рядок форми ──────────────────────────────────────────────────────────
    private JPanel formRow(String labelText, JTextField field) {
        JPanel row = new JPanel(new BorderLayout(0, 3));
        row.setBackground(BG_PANEL);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(TEXT_MUTED);

        row.add(lbl, BorderLayout.NORTH);
        row.add(field, BorderLayout.CENTER);
        return row;
    }

    // ════════════════════════════════════════════════════════════════════════
    // Main
    // ════════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        // Намагаємось встановити FlatLaf або повертаємось до стандартного LookAndFeel
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            BookGUI app = new BookGUI();
            app.setVisible(true);
        });
    }
}
