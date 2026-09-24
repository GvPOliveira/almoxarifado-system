package br.com.almoxarifado.model;

import br.com.almoxarifado.exception.InvalidProductIdException;
import br.com.almoxarifado.exception.ProductAlreadyExistsException;

public class Product {
    private String code, description;
    private int id;

    public Product(String code, String description) {
        this.code = code;
        this.description = description;
        id = 0;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void assignId(int id) {
        if (id <= 0) {
            throw new InvalidProductIdException();
        }
        if (this.id != 0) {
            throw new ProductAlreadyExistsException();
        } else {
            this.id = id;
        }
    }


}
