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
        productRequestMap.put(product.getCode(),newProductRequest);
    }

    public void attendedProduct(String code, int attendedQuantity) {
        ProductRequest findProductRequest = findProductRequest(code);
        if (findProductRequest != null) {
            findProductRequest.validateCanBeProcessed(attendedQuantity);
            return;
        }
        throw new ProductNotFoundInRequestException();
    }



    public ProductRequest findProductRequest(String code) {return productRequestMap.get(code);}


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
