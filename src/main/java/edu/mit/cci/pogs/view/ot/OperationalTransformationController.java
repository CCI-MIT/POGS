package edu.mit.cci.pogs.view.ot;

import edu.mit.cci.pogs.ot.OperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;

@Controller
public class OperationalTransformationController {

    private OperationService operationService;

    private AtomicInteger clientCount = new AtomicInteger(0);
    private AtomicInteger padCount = new AtomicInteger(0);

    @Autowired
    public OperationalTransformationController(OperationService operationService) {
        this.operationService = operationService;
    }

    @GetMapping("/ot/test-pad")
    public String showCreatePad() {
        return "ot/test-home";
    }

    @PostMapping("/ot/test-pad")
    public String createPad() {

        int padId;
        do {
            padId = padCount.getAndIncrement();
        } while(operationService.getState(padId) != null);

        operationService.initializeState(padId, "");
        return "redirect:/ot/test-pad/" + padId;
    }

    @GetMapping("/ot/test-pad/{padId}")
    public String showPad(HttpServletRequest request, Model model, @PathVariable long padId) {
        if (operationService.getState(padId) == null) {
            return "redirect:/ot/test-pad";
        }

        model.addAttribute("padId", padId);
        model.addAttribute("clientId", clientCount.getAndIncrement());
        return "ot/test-pad";
    }
}
