package br.com.almoxarifado.model;

import br.com.almoxarifado.exception.BranchAlreadyExistsException;
import br.com.almoxarifado.exception.InvalidBranchIdException;

import java.util.*;

public class Branch {
    private String code, name;
    private final Map<String, BranchProduct> products;
    private int id;

    public Branch(String code, String name) {
        this.code = code;
        this.name = name;
        id = 0;
        products = new HashMap<>();

    }

    public int getId() {
        return id;
    }

    public void assignId(int id) {
        if (id <= 0) {
            throw new InvalidBranchIdException();
        }
        if (this.id != 0) {
            throw new BranchAlreadyExistsException();
        } else {
            this.id = id;
        }
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public List<BranchProduct> getProducts() {
        return products.values().stream().toList();
    }

    public boolean addProduct(BranchProduct product) {
        String code = product.getProduct().getCode();
        if (products.containsKey(code)) {
            return false;
        }
        products.put(code, product);
        return true;
    }

    public void receiveProduct(Product product, int quantity, OriginType originType, String originNumber) {
        BranchProduct branchProduct = findBranchProduct(product.getCode());
        if (branchProduct != null) {
            branchProduct.addQuantity(quantity, originType, originNumber);
        } else {
            BranchProduct newBranchProduct = new BranchProduct(product, this, quantity, originType, originNumber);
            addProduct(newBranchProduct);
        }
    }


    @Override
    public String toString() {
        return "\nBranch{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", id=" + id +
                '}';
    }

    public BranchProduct findBranchProduct(String code) {
        return products.get(code);
    }


}
