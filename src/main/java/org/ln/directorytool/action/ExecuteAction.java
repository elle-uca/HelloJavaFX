//package org.ln.directorytool.action;
//
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//
//import javax.swing.JOptionPane;
//
//import org.ln.directorytool.DirectoryToolController;
//
///**
// * Action that confirms and triggers the main directory operation.
// *
// * @author Luca Noale
// */
//public class ExecuteAction implements ActionListener {
//
//    private final DirectoryToolController controller;
//
//    /**
//     * Creates the action with a reference to the controller.
//     *
//     * @param controller the controller coordinating the execution
//     */
//    public ExecuteAction(DirectoryToolController controller) {
//        this.controller = controller;
//    }
//
//    @Override
//    public void actionPerformed(ActionEvent e) {
//
//        int confirm = JOptionPane.showConfirmDialog(
//                null,
//                "Sei sicuro di procedere?",
//                "Conferma",
//                JOptionPane.YES_NO_OPTION,
//                JOptionPane.WARNING_MESSAGE
//        );
//
//        // Proceed only when the user explicitly confirms execution.
//        if (confirm == JOptionPane.YES_OPTION) {
//            controller.executeMainAction();
//        }
//    }
//}a
