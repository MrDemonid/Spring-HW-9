package mr.demonid.web.client.controllers;

import feign.FeignException;
import lombok.AllArgsConstructor;
import mr.demonid.web.client.dto.ProductInfo;
import mr.demonid.web.client.service.CatalogService;
import mr.demonid.web.client.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Controller
@AllArgsConstructor
public class AppController {

    private CatalogService catalogService;
    private OrderService orderService;


    @GetMapping
    public String baseDir() {
        System.out.println("redirect to index...");
        return "redirect:/index";
    }

    @GetMapping("/index")
    public String index(Model model) {
        System.out.println("start index page...");
        List<ProductInfo> products = catalogService.getProducts();
        model.addAttribute("products", products);
        System.out.println("end index page...");
        return "/home";
    }

    @PostMapping("/order")
    public String placeOrder(@RequestParam("productId") Long productId,
                             @RequestParam("name") String name,
                             @RequestParam("quantity") int quantity,
                             @RequestParam("price") BigDecimal price,
                             Model model)
    {
        System.out.println("Product ID: " + productId);
        System.out.println("Name: " + name);
        System.out.println("Quantity: " + quantity);
        System.out.println("Price: " + price);
        // открываем заказ
        try {
            UUID uuid = orderService.addOrder(productId, 2, quantity, price);
            model.addAttribute("productName", name);
            model.addAttribute("quantity", quantity);
            model.addAttribute("totalCost", price.multiply(BigDecimal.valueOf(quantity)));
            System.out.println("Покупка совершена!");
            return "/confirmed";
        } catch (FeignException e) {
            System.out.println("Облом!");
            model.addAttribute("errorMessage", e.contentUTF8());
            return "/error-order";
        }

    }

}
