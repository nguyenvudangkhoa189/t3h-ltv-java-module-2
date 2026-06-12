package vn.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import vn.demo.dto.ProductListResponse;
import vn.demo.service.ProductService;

@Controller
@RequiredArgsConstructor
public class ProductWebController {

	private final ProductService productService;

	@GetMapping("/")
	public String root() {
		return "redirect:/home";
	}

	@GetMapping("/home")
	public String home(Model model) {
		ProductListResponse response = productService.getAllProducts();
		model.addAttribute("products", response.getProducts());
		model.addAttribute("total", response.getTotal());
		return "home";
	}

	@GetMapping("/category")
	public String categories(Model model) {
		model.addAttribute("categories", productService.getCategories());
		return "category";
	}

	@GetMapping("/product_detail")
	public String productDetail(@RequestParam("id") long id, Model model) {
		model.addAttribute("product", productService.getProductById(id));
		return "product-detail";
	}

	@GetMapping("/product_search")
	public String productSearch(@RequestParam("keyword") String keyword, Model model) {
		ProductListResponse response = productService.searchProducts(keyword);
		model.addAttribute("keyword", keyword);
		model.addAttribute("products", response.getProducts());
		model.addAttribute("total", response.getTotal());
		return "product-search";
	}

	@GetMapping("/product_add")
	public String productAdd() {
		return "product-add";
	}

	@GetMapping("/product_edit")
	public String productEdit(@RequestParam("id") long id, Model model) {
		model.addAttribute("product", productService.getProductById(id));
		return "product-edit";
	}

}
