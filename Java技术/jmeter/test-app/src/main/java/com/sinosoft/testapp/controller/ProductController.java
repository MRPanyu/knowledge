package com.sinosoft.testapp.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sinosoft.testapp.model.AppResponse;
import com.sinosoft.testapp.model.ProductCondition;
import com.sinosoft.testapp.model.ProductModel;
import com.sinosoft.testapp.model.TransactionData;
import com.sinosoft.testapp.util.JsonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class ProductController {

	private Map<String, ProductModel> productMap = new LinkedHashMap<>();

	private AtomicInteger generator = new AtomicInteger(10000);

	@PostMapping("/product/queryProduct")
	public AppResponse<List<ProductModel>> queryProduct(@RequestBody ProductCondition condition) throws Exception {
		log.info("queryProduct - {}", JsonUtils.stringify(condition));
		List<ProductModel> list = new ArrayList<>();

		for (ProductModel product : productMap.values()) {
			if (StringUtils.hasText(condition.getProductName())) {
				if (!product.getProductName().contains(condition.getProductName())) {
					continue;
				}
			}
			list.add(product);
		}

		return AppResponse.ok(list);
	}

	@PostMapping("/product/saveProduct")
	public AppResponse<ProductModel> saveProduct(@RequestBody ProductModel product) throws Exception {
		log.info("saveProduct - {}", JsonUtils.stringify(product));
		if (!StringUtils.hasText(product.getProductNo())) {
			product.setProductNo("P" + generator.incrementAndGet());
		}
		product.setStatus("0");
		productMap.put(product.getProductNo(), product);
		return AppResponse.ok(product);
	}

	@PostMapping("/product/submitProduct")
	public AppResponse<String> submitProduct(@RequestBody ProductModel product) throws Exception {
		log.info("submitProduct - {}", JsonUtils.stringify(product));
		product = productMap.get(product.getProductNo());
		product.setStatus("1");
		return AppResponse.ok();
	}

	@PostMapping("/product/handleProduct")
	public AppResponse<String> handleProduct(@RequestBody ProductModel product) throws Exception {
		log.info("handleProduct - {}", JsonUtils.stringify(product));
		return AppResponse.ok();
	}

	@PostMapping("/product/transaction")
	public AppResponse<String> transaction(@RequestBody TransactionData transData) throws Exception {
		log.info("transaction - {}", JsonUtils.stringify(transData));
		return AppResponse.ok();
	}

}
