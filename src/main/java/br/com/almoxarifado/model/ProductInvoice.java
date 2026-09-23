package br.com.almoxarifado.model;

import br.com.almoxarifado.exception.InvalidQuantityException;

public class ProductInvoice {
    private int id;
    private Destination destination;
    private Product product;
    private int quantity;

    public int getId() {
        return id;
    }

    public ProductInvoice(Product product, int quantity, Destination destination) {
        if (quantity <= 0) {
            throw new InvalidQuantityException();
        }
        if(product == null || destination == null ){
            throw new NullPointerException();
        }
        this.product = product;
        this.quantity = quantity;
        this.destination = destination;
    }
    private ProductInvoice(int id, Product product, int quantity, Destination destination) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.destination = destination;
    }

    public static ProductInvoice reconstructorProductInvoice(int id, Product product, int quantity, Destination destination) {
        return new ProductInvoice(id,product,quantity,destination);
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public Destination getDestination() {
        return destination;
    }


}
