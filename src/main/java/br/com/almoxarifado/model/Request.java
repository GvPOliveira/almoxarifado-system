package br.com.almoxarifado.model;

import br.com.almoxarifado.exception.InvalidQuantityException;
import br.com.almoxarifado.exception.ProductNotFoundInBranchException;
import br.com.almoxarifado.exception.ProductNotFoundInRequestException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Request {
    private String numberRequest;
    private Branch branch;
    private Map<String, ProductRequest> productRequestMap;
    private Map<String, ProductRequest> productRequestView;


    public Request(String numberRequest, Branch branch) {
        this.numberRequest = numberRequest;
        this.branch = branch;
        productRequestMap = new HashMap<>();
        productRequestView = Collections.unmodifiableMap(productRequestMap);
    }

    private Request(String numberRequest, Branch branch, Map<String, ProductRequest> productRequestMap) {
        this.numberRequest = numberRequest;
        this.branch = branch;
        this.productRequestMap = productRequestMap;
        productRequestView = Collections.unmodifiableMap(productRequestMap);
    }

    public static Request requestReconstructor(String numberRequest, Branch branch, Map<String, ProductRequest> productRequestMap) {
        return new Request(numberRequest, branch, productRequestMap);
    }

    public void addProductRequest(ProductRequest productRequest) {
        productRequestMap.put(productRequest.getBranchProduct().getProduct().getCode(), productRequest);
    }

    public void addProductRequest(Product product, int requestedQuantity) {
        BranchProduct branchProduct = branch.findBranchProduct(product.getCode());
        if (requestedQuantity <= 0) {
            throw new InvalidQuantityException();
        }
        if (branchProduct == null) {
            throw new ProductNotFoundInBranchException();
        }
        ProductRequest findProductRequest = findProductRequest(product.getCode());
        if (findProductRequest != null) {
            findProductRequest.addRequestQuantity(requestedQuantity);
            return;
        }
        ProductRequest newProductRequest = new ProductRequest(this, branchProduct, requestedQuantity);
        productRequestMap.put(product.getCode(), newProductRequest);
    }


    public ProductRequest findProductRequest(String code) {
        return productRequestMap.get(code);
    }


    public String getNumberRequest() {
        return numberRequest;
    }

    public Branch getBranch() {
        return branch;
    }

    public Map<String, ProductRequest> getProductRequestMap() {
        return productRequestView;
    }
}
