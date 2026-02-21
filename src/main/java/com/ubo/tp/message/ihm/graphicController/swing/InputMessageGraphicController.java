package com.ubo.tp.message.ihm.graphicController.swing;

import com.ubo.tp.message.controller.service.IInputMessageController;
import com.ubo.tp.message.ihm.graphicController.service.IInputMessageGraphicController;
import com.ubo.tp.message.ihm.view.swing.InputMessageView;
import com.ubo.tp.message.logger.Logger;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultEditorKit;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class InputMessageGraphicController implements IInputMessageGraphicController {

    private final Logger LOGGER;
    private final InputMessageView inputMessageView;
    private final IInputMessageController inputMessageController;

    public InputMessageGraphicController(Logger logger, InputMessageView inputMessageView, IInputMessageController inputMessageController) {
        LOGGER = logger;
        this.inputMessageView = inputMessageView;
        this.inputMessageController = inputMessageController;

        createConnector();
        subscribeViewEvents();
    }

    private void createConnector() {
        InputMap im = this.inputMessageView.getInputField().getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap am = this.inputMessageView.getInputField().getActionMap();
        im.put(KeyStroke.getKeyStroke("shift ENTER"), DefaultEditorKit.insertBreakAction);
        im.put(KeyStroke.getKeyStroke("ctrl ENTER"),  DefaultEditorKit.insertBreakAction);
        im.put(KeyStroke.getKeyStroke("ENTER"), "sendMessage");

        am.put("sendMessage", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSendAction();
            }
        });

    }

    private void subscribeViewEvents() {
        // Attach DocumentListener directly to the JTextArea document
        inputMessageView.getInputField().getDocument().addDocumentListener(new DocumentListener() {
            private void notifyChange() {
                SwingUtilities.invokeLater(() -> {
                    String current = inputMessageView.getInputField().getText();
                    int actualLines = 1;
                    if (current != null && !current.isEmpty()) {
                        actualLines = current.split("\r\n|\r|\n", -1).length;
                        if (actualLines < 1) actualLines = 1;
                    }
                    int rows = Math.min(actualLines, 3);

                    inputMessageView.setInputRows(rows);
                    inputMessageView.setVerticalScrollBarPolicy(
                            actualLines > 3
                                    ? ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED
                                    : ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER
                    );
                });
            }
            @Override public void insertUpdate(DocumentEvent e) { notifyChange(); }
            @Override public void removeUpdate(DocumentEvent e) { notifyChange(); }
            @Override public void changedUpdate(DocumentEvent e) { notifyChange(); }
        });

        // Attach FocusListener directly to the inputField; view handles repaint when setFocused is called
        inputMessageView.getInputField().addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                inputMessageView.setFocused(true);
                if (LOGGER != null) LOGGER.debug("Input focus: true");
            }
            @Override
            public void focusLost(FocusEvent e) {
                inputMessageView.setFocused(false);
                if (LOGGER != null) LOGGER.debug("Input focus: false");
            }
        });

        // wire send requested runnable if present in view
        inputMessageView.setOnSendRequested(this::handleSendAction);
    }

    private void handleSendAction() {
        String message = inputMessageView.getInputField().getText().trim();
        if (LOGGER != null) LOGGER.debug("Enter pressed: " + message);
        if (message.isEmpty()) return;

        // delegate actual send to the business controller
        if (inputMessageController != null) {
            inputMessageController.sendMessage(message);
        }

        // clear and focus
        inputMessageView.getInputField().setText("");
        inputMessageView.getInputField().requestFocusInWindow();

        // after clearing, ensure UI updates rows/scroll
        SwingUtilities.invokeLater(() -> {
            inputMessageView.setInputRows(1);
            inputMessageView.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        });
    }

}
