package com.project.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.project.entity.AdjustOrder;
import com.project.service.AdjService;

import jakarta.validation.Valid;

@Controller
public class AdjustOrderController {
    
    private AdjService adjService;
    
    public AdjustOrderController(AdjService theAdjService) {
        adjService = theAdjService;
    }
    
    @GetMapping("/adoDashboard")
    public String adjustOrderDashboard(Model model) {
        List<AdjustOrder> adjustOrders = adjService.findAll();
        model.addAttribute("adjustOrders", adjustOrders);
        return "AdjustOrderDashboard";
    }
    
    @GetMapping("/NewAdjustOrder")
    public String newAdjustOrder(Model model) {
        if (!model.containsAttribute("adjustOrder")) {
            model.addAttribute("adjustOrder", new AdjustOrder());
        }
        return "NewAdjustOrder";
    }

    @GetMapping("/BackAdjdashboard")
    public String backAdjDashboard(Model model) {
        List<AdjustOrder> adjustOrders = adjService.findAll();
        model.addAttribute("adjustOrders", adjustOrders);
        return "AdjustOrderDashboard";
    }
    
    @PostMapping("/Adjsave")
    public String saveAdjOrder(@Valid @ModelAttribute("adjustOrder") AdjustOrder theAdjustOrder, 
                                BindingResult bindingResult, 
                                RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            return "NewAdjustOrder";
        }
        
        adjService.save(theAdjustOrder);
        
        redirectAttributes.addFlashAttribute("successMessage", "Item record saved successfully!");
        
        return "redirect:/NewAdjustOrder";
    }
    
    @GetMapping("/adjFilter")
    public String showDashboard(@RequestParam(value = "item", required = false) String item, Model model) {
        List<AdjustOrder> adjustOrders;
        
        if (item != null && !item.isEmpty()) {
            adjustOrders = adjService.filterByItem(item);
            if (adjustOrders.isEmpty()) {
                model.addAttribute("message", "The item is not found.");
            }
        } else {
            adjustOrders = adjService.findAll();
        }
        
        model.addAttribute("adjustOrders", adjustOrders);
        model.addAttribute("item", item);
        return "AdjustOrderDashboard";
    }
    
    @GetMapping("/editAdjustOrder")
    public String editAdjustOrder(@RequestParam("id") String id, Model model) {
        AdjustOrder adjustOrder = adjService.findById(id);
        if (adjustOrder != null) {
            model.addAttribute("adjustOrder", adjustOrder);
            return "NewAdjustOrder"; // Return to your edit page view
        } else {
            model.addAttribute("message", "Adjust Order not found.");
            return "redirect:/adoDashboard"; // Redirect back to dashboard
        }
    }

    @GetMapping("/viewAdjustOrder")
    public String adjustOrder(@RequestParam("id") String item, 
                              @RequestParam(value = "action", required = false) String action, 
                              Model model) {
        AdjustOrder adjustOrder = adjService.findById(item); // Fetch AdjustOrder using id
        if (adjustOrder != null) {
            model.addAttribute("adjustOrder", adjustOrder);  // Add AdjustOrder to the model
            model.addAttribute("action", action); // Add action (view/edit) to the model
            return "NewAdjustOrder"; // Return the view that handles both view and edit actions
        } else {
            model.addAttribute("message", "Adjust Order not found.");
            return "redirect:/adoDashboard"; // Redirect to dashboard if not found
        }
    }

}

