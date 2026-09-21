package br.com.almoxarifado.model;

import br.com.almoxarifado.exception.BranchAlreadyExists;
import br.com.almoxarifado.exception.InvalidProductIdException;
import br.com.almoxarifado.exception.ProductAlreadyExists;

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
            throw new InvalidProductIdException();
        }
        if (this.id != 0) {
            throw new BranchAlreadyExists();
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
        boolean search;
        String chave = product.getProduct().getCode();
        search = products.containsKey(chave);
        if (!search) {
            products.put(chave, product);
            return true;
        }
        return false;
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



/*   Methods commented out for future review. Their responsibilities changed after the creation of new classes.

    public boolean productReceipt(Product product, int quantity) {
        boolean search;
        String chave = product.getCode();
        search = products.containsKey(chave);
        if (!search) {
            if (quantity <= 0) {
                return false;
            }
            BranchProduct newProduct = new BranchProduct(product, this, quantity);
            products.put(chave, newProduct);
            return true;

        } else {
            BranchProduct existingProduct = findBranchProduct(chave);
            boolean result = existingProduct.addQuantity(quantity);
            return result;
        }
    }

    public boolean productOutPut(Product product, int quantity) {
        boolean search;
        String chave = product.getCode();
        search = products.containsKey(chave);
        if (!search) {
            return false;
        } else {
            BranchProduct existingProduct;
            existingProduct = findBranchProduct(chave);
            boolean result = existingProduct.removeQuantity(quantity);
            return result;
        }
    }
*/

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
