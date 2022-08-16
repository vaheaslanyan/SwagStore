package com.revature.controllers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;

import org.apache.tika.config.TikaConfig;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MimeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.revature.models.Product;
import com.revature.models.ProductImage;
import com.revature.repositories.ProductImageRepository;
import com.revature.services.ProductImageService;
import com.revature.services.ProductService;

@RestController
@RequestMapping("/api/product/image")
@CrossOrigin(origins = { "http://localhost:4200", "http://localhost:3000" }, allowCredentials = "true")
public class ProductImageController {

	private final ProductImageService productImageService;
	private final Logger logger = LoggerFactory.getLogger(ProductImageController.class);
	private final ProductImageRepository productImageRepository;
	private final ProductService productService;

	public ProductImageController(ProductImageService productImageService,
			ProductImageRepository productImageRepository, ProductService productService) {
		this.productImageService = productImageService;
		this.productImageRepository = productImageRepository;
		this.productService = productService;
	}

	@GetMapping("/byId/{product_id}")
	public ResponseEntity<InputStreamResource> getProductImageById(@PathVariable("product_id") int product_id) {
		Optional<ProductImage> getImage = productImageService.findById(product_id);
		try {
			if (getImage.isPresent()) {
				ProductImage pi = getImage.get();
				if (pi.getProductImage() != null) {

					TikaConfig tika = new TikaConfig();
					Metadata metaData = new Metadata();

					InputStream is = new ByteArrayInputStream(pi.getProductImage());
					InputStreamResource isr = new InputStreamResource(is);

					org.apache.tika.mime.MediaType mediaType = tika.getDetector().detect(TikaInputStream.get(is),
							metaData);
					MimeType mimeType = tika.getMimeRepository().forName(mediaType.toString());

					HttpHeaders responseHeaders = new HttpHeaders();
					responseHeaders.set(
							"Content-Disposition",
							"inline; filename=\"" + System.currentTimeMillis() + mimeType.getExtension() + "\"");
					responseHeaders.set(
							"Content-Type",
							mediaType.getType() + "/" + mediaType.getSubtype());

					return ResponseEntity.ok().headers(responseHeaders).body(isr);
				} else {
					// If no image found, serve default image.
					Resource resource = new ClassPathResource("imgs/default-product-image.png");

					InputStream input = resource.getInputStream();
					InputStreamResource isr = new InputStreamResource(input);

					HttpHeaders responseHeaders = new HttpHeaders();
					responseHeaders.set("Content-Disposition", "inline; filename=\"default-product-image.png\"");
					responseHeaders.set("Content-Type", "image/png");

					return ResponseEntity.ok().headers(responseHeaders).body(isr);
				}
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}
		return ResponseEntity.status(500).body(null);
	}

	@GetMapping("/byProductId/{product_id}")
	public ResponseEntity<InputStreamResource> getProductImageByProductId(@PathVariable("product_id") int product_id) {
		ProductImage getImage = null;
		try {
			getImage = productImageRepository.findByProduct_Id(product_id);
			if (getImage != null) {
				if (getImage.getProductImage() != null) {

					TikaConfig tika = new TikaConfig();
					Metadata metaData = new Metadata();

					InputStream is = new ByteArrayInputStream(getImage.getProductImage());
					InputStreamResource isr = new InputStreamResource(is);

					org.apache.tika.mime.MediaType mediaType = tika.getDetector().detect(TikaInputStream.get(is),
							metaData);
					MimeType mimeType = tika.getMimeRepository().forName(mediaType.toString());

					HttpHeaders responseHeaders = new HttpHeaders();
					responseHeaders.set(
							"Content-Disposition",
							"inline; filename=\"" + System.currentTimeMillis() + mimeType.getExtension() + "\"");
					responseHeaders.set(
							"Content-Type",
							mediaType.getType() + "/" + mediaType.getSubtype());

					return ResponseEntity.ok().headers(responseHeaders).body(isr);
				}
			}

			// If no image found, serve default image.
			Resource resource = new ClassPathResource("imgs/default-product-image.png");

			InputStream input = resource.getInputStream();
			InputStreamResource isr = new InputStreamResource(input);

			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.set("Content-Disposition", "inline; filename=\"default-product-image.png\"");
			responseHeaders.set("Content-Type", "image/png");

			return ResponseEntity.ok().headers(responseHeaders).body(isr);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}
		return ResponseEntity.status(500).body(null);
	}

	@PutMapping(value = "/{product_id}", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Integer> updateProductImageByProductId(@PathVariable("product_id") int product_id,
			@RequestPart("productimage") MultipartFile document) {
		Optional<Product> product = productService.findById(product_id);
		if (product.isPresent()) {
			try {
				return ResponseEntity.ok()
						.body(productImageRepository.updateProductImage(product_id, document.getBytes()));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return ResponseEntity.notFound().build();
	}

}
