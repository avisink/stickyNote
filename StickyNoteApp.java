// author: Ayo😄

import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.rtf.RTFEditorKit;

public class StickyNoteApp {

    private static File currentFile = null; 

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // the sticky note frame!
            JFrame stickyNoteFrame = new JFrame("Sticky Note");
            stickyNoteFrame.setSize(400, 400);
            stickyNoteFrame.setLocationRelativeTo(null);
            stickyNoteFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            stickyNoteFrame.setResizable(true);

            JTextPane textPane = new JTextPane();
            textPane.setMargin(new Insets(20, 20, 20, 20)); 
            JScrollPane scrollPane = new JScrollPane(textPane);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            stickyNoteFrame.add(scrollPane, BorderLayout.CENTER);

            // menu bar and menus!
            JMenuBar menuBar = new JMenuBar();

            // the file menu
            JMenu fileMenu = new JMenu("File");
            JMenuItem saveItem = new JMenuItem("Save");
            JMenuItem saveAsItem = new JMenuItem("Save As");
            JMenuItem editAndSaveAsItem = new JMenuItem("Edit and Save As");
            JMenuItem openItem = new JMenuItem("Open");
            fileMenu.add(saveItem);
            fileMenu.add(saveAsItem);
            fileMenu.add(editAndSaveAsItem);
            fileMenu.add(openItem);

            // the background color option menu
            JMenu colorMenu = new JMenu("Background Color");
            JMenuItem customColorItem = new JMenuItem("Custom...");
            colorMenu.add(customColorItem);
            addColorOption(colorMenu, "Yellow", Color.YELLOW, textPane);
            addColorOption(colorMenu, "Green", Color.GREEN, textPane);
            addColorOption(colorMenu, "Pink", Color.PINK, textPane);
            addColorOption(colorMenu, "Blue", Color.BLUE, textPane);
            addColorOption(colorMenu, "White", Color.WHITE, textPane);

            // text style option menuuu
            JMenu styleMenu = new JMenu("Style");
            JMenuItem boldItem = new JMenuItem("Bold");
            JMenuItem italicItem = new JMenuItem("Italic");
            JMenuItem underlineItem = new JMenuItem("Underline");
            styleMenu.add(boldItem);
            styleMenu.add(italicItem);
            styleMenu.add(underlineItem);

            // font option menu
            JMenu fontMenu = new JMenu("Font");
            JMenuItem changeFontItem = new JMenuItem("Change Font");
            JMenu fontSizeMenu = new JMenu("Font Size");
            addFontSizeOption(fontSizeMenu, "Small (12)", 12, textPane);
            addFontSizeOption(fontSizeMenu, "Medium (16)", 16, textPane);
            addFontSizeOption(fontSizeMenu, "Large (20)", 20, textPane);
            fontMenu.add(changeFontItem);
            fontMenu.add(fontSizeMenu);

            // custom font size!
            JMenuItem customFontSizeItem = new JMenuItem("Custom Font Size");
            fontMenu.add(customFontSizeItem);

            menuBar.add(fileMenu);
            menuBar.add(colorMenu);
            menuBar.add(fontMenu);
            menuBar.add(styleMenu);
            stickyNoteFrame.setJMenuBar(menuBar);

            stickyNoteFrame.setVisible(true);

            
            saveItem.addActionListener(e -> saveNoteToFile(textPane));
            saveAsItem.addActionListener(e -> saveAsNoteToFile(textPane));
            editAndSaveAsItem.addActionListener(e -> editAndSaveAs(textPane));
            openItem.addActionListener(e -> loadNoteFromFile(textPane));
            customColorItem.addActionListener(e -> {
                Color chosenColor = JColorChooser.showDialog(null, "Choose Background Color", textPane.getBackground());
                if (chosenColor != null) {
                    textPane.setBackground(chosenColor);
                }
            });

            boldItem.addActionListener(e -> applyStyle(textPane, StyleConstants.Bold, true));
            italicItem.addActionListener(e -> applyStyle(textPane, StyleConstants.Italic, true));
            underlineItem.addActionListener(e -> applyStyle(textPane, StyleConstants.Underline, true));

            changeFontItem.addActionListener(e -> {
                String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment()
                                                    .getAvailableFontFamilyNames();
                String fontChoice = (String) JOptionPane.showInputDialog(null, "Choose Font",
                                      "Font", JOptionPane.PLAIN_MESSAGE, null, fonts, "Arial");
                if (fontChoice != null) {
                    applyFontFamily(textPane, fontChoice);
                }
            });

            customFontSizeItem.addActionListener(e -> {
                String sizeInput = JOptionPane.showInputDialog(null,
                        "Enter font size:", "Custom Font Size", JOptionPane.PLAIN_MESSAGE);
                if (sizeInput != null) {
                    try {
                        int fontSize = Integer.parseInt(sizeInput);
                        if (fontSize > 0) {
                            applyFontSize(textPane, fontSize);
                        } else {
                            JOptionPane.showMessageDialog(null,
                                    "Font size must be a positive number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null,
                                "Invalid input. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        });
    }

    private static void saveNoteToFile(JTextPane textPane) {
        if (currentFile != null) {
            int confirmation = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to overwrite the file?", "Confirm to Save",
                    JOptionPane.YES_NO_OPTION);
            if (confirmation == JOptionPane.YES_OPTION) {
                try (FileOutputStream fos = new FileOutputStream(currentFile)) {
                    RTFEditorKit rtfEditorKit = new RTFEditorKit();
                    rtfEditorKit.write(fos, textPane.getDocument(), 0, textPane.getDocument().getLength());
                } catch (IOException | BadLocationException e) {
                    e.printStackTrace();
                }
            }
        } else {
            saveAsNoteToFile(textPane);
        }
    }

    private static void saveAsNoteToFile(JTextPane textPane) {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            currentFile = fileChooser.getSelectedFile();
            try (FileOutputStream fos = new FileOutputStream(currentFile)) {
                RTFEditorKit rtfEditorKit = new RTFEditorKit();
                rtfEditorKit.write(fos, textPane.getDocument(), 0, textPane.getDocument().getLength());
            } catch (IOException | BadLocationException e) {
                e.printStackTrace();
            }
        }
    }

    private static void editAndSaveAs(JTextPane textPane) {
        currentFile = null; 
        saveAsNoteToFile(textPane); 
    }

    private static void loadNoteFromFile(JTextPane textPane) {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            currentFile = fileChooser.getSelectedFile();
            try (FileInputStream fis = new FileInputStream(currentFile)) {
                RTFEditorKit rtfEditorKit = new RTFEditorKit();
                StyledDocument doc = (StyledDocument) rtfEditorKit.createDefaultDocument();
                rtfEditorKit.read(fis, doc, 0);
                textPane.setStyledDocument(doc);
            } catch (IOException | BadLocationException e) {
                e.printStackTrace();
            }
        }
    }

    private static void addColorOption(JMenu menu, String name, Color color, JTextPane textPane) {
        JMenuItem item = new JMenuItem(name);
        item.addActionListener(e -> textPane.setBackground(color));
        menu.add(item);
    }

    private static void addFontSizeOption(JMenu menu, String name, int size, JTextPane textPane) {
        JMenuItem item = new JMenuItem(name);
        item.addActionListener(e -> applyFontSize(textPane, size));
        menu.add(item);
    }

    private static void applyStyle(JTextPane textPane, Object style, boolean value) {
        StyledDocument doc = textPane.getStyledDocument();
        int start = textPane.getSelectionStart();
        int end = textPane.getSelectionEnd();
        if (start != end) {
            StyleContext sc = StyleContext.getDefaultStyleContext();
            AttributeSet attr = sc.addAttribute(SimpleAttributeSet.EMPTY, style, value);
            doc.setCharacterAttributes(start, end - start, attr, false);
        }
    }

    private static void applyFontFamily(JTextPane textPane, String fontFamily) {
        StyledDocument doc = textPane.getStyledDocument();
        int start = textPane.getSelectionStart();
        int end = textPane.getSelectionEnd();
        if (start != end) {
            StyleContext sc = StyleContext.getDefaultStyleContext();
            AttributeSet attr = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.FontFamily, fontFamily);
            doc.setCharacterAttributes(start, end - start, attr, false);
        }
    }

    private static void applyFontSize(JTextPane textPane, int size) {
        StyledDocument doc = textPane.getStyledDocument();
        int start = textPane.getSelectionStart();
        int end = textPane.getSelectionEnd();
        if (start != end) {
            StyleContext sc = StyleContext.getDefaultStyleContext();
            AttributeSet attr = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.FontSize, size);
            doc.setCharacterAttributes(start, end - start, attr, false);
        }
    }
}
