package br.com.almoxarifado.model;

import br.com.almoxarifado.exception.CannotProcessInvoiceWithoutProductsException;
import br.com.almoxarifado.exception.InvoiceAlreadyProcessedException;
import br.com.almoxarifado.exception.InvoiceAlreadyRevertedException;
import br.com.almoxarifado.exception.UnprocessedInvoiceException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class Invoice {
    private int id;

    public int getId() {
        return id;
    }

    private String numberInvoice;
    private LocalDateTime date;
    private boolean processed;
    private boolean reversed;
    private Branch branchDestination;
    private List<ProductInvoice> productInvoiceList;
    private List<ProductInvoice> productInvoiceView;

    public boolean isProcessed() {
        return processed;
    }

    public Branch getBranchDestination() {
        return branchDestination;
    }

    public Invoice(String numberInvoice, Branch branchDestination) {
        this.numberInvoice = numberInvoice;
        this.branchDestination = branchDestination;
        date = LocalDateTime.now();
        productInvoiceList = new ArrayList<>();
        productInvoiceView = Collections.unmodifiableList(productInvoiceList);
        processed = false;
        reversed = false;
    }

    private Invoice(int id, String numberInvoice, Branch branchDestination, LocalDateTime date, List<ProductInvoice> productInvoices,
                   boolean processed, boolean reversed) {
        this.id = id;
        this.numberInvoice = numberInvoice;
        this.branchDestination = branchDestination;
        this.date = date;
        productInvoiceList = productInvoices;
        productInvoiceView = Collections.unmodifiableList(productInvoiceList);
        this.processed = processed;
        this.reversed = reversed;
    }

    public static Invoice reconstructorInvoice(int id, String numberInvoice, Branch branchDestination, LocalDateTime date, List<ProductInvoice> productInvoices,
                                               boolean processed, boolean reversed) {
        return new Invoice(id,numberInvoice, branchDestination, date, productInvoices, processed, reversed);
    }

    public void addProductInvoice(Product product, int quantity, Destination destination) {
        if (processed) {
            throw new InvoiceAlreadyProcessedException();
        }
        ProductInvoice newProductInvoice = new ProductInvoice(product, quantity, destination);
        productInvoiceList.add(newProductInvoice);
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "id=" + id +
                ", numberInvoice='" + numberInvoice + '\'' +
                ", date=" + date +
                ", processed=" + processed +
                ", reversed=" + reversed +
                ", branchDestination=" + branchDestination +
                '}';
    }

    public void validateCanBeProcessed() {
        if (this.productInvoiceList.isEmpty()) {
            throw new CannotProcessInvoiceWithoutProductsException();
        }
        if (this.processed) {
            throw new InvoiceAlreadyProcessedException();
        }
    }

    public void validateCanBeReverted(){
        if(this.reversed){
            throw new InvoiceAlreadyRevertedException();
        }
        if(!this.processed){
            throw new UnprocessedInvoiceException();
        }
    }

    private void markAsProcessed() {
        processed = true;
        reversed = false;
    }

    public void completeProcessing() {
        markAsProcessed();
    }


    public String getNumberInvoice() {
        return numberInvoice;
    }

    public LocalDateTime getDate() {
        return date;
    }


    public List<ProductInvoice> getProductInvoiceView() {
        return productInvoiceView;
    }

    public boolean isReversed() {
        return reversed;
    }
}
