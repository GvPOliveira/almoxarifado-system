package br.com.almoxarifado.jdbc;


import br.com.almoxarifado.exception.InsufficientStockException;
import br.com.almoxarifado.model.*;
import br.com.almoxarifado.service.InvoiceService;
import br.com.almoxarifado.service.RequestService;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;


public class Main {

    public static void main(String[] args) {
        BranchProductRepository branchProductRepository = new BranchProductRepository();

        Branch branch = new Branch("01", "Filial Sul");
        branch.assignId(1);
        Product product1 = new Product("001", "Caneta Azul");
//        product1.assignId(17);
        Product product2 = new Product("002", "Parafuso 1/2");
        product2.assignId(18);
        Product product3 = new Product("005", "Parafuso 3/4 x 2");
        product3.assignId(21);

        BranchProduct newBranchproduct = new BranchProduct(product1, branch, 100,
                OriginType.INVOICE, "006");
        newBranchproduct.assignId(1);

        BranchProduct newBranchproduct1 = new BranchProduct(product2, branch, 100,
                OriginType.INVOICE, "006");
        newBranchproduct1.assignId(2);


        BranchProduct newBranchproduct2 = new BranchProduct(product3, branch, 100,
                OriginType.INVOICE, "006");
        newBranchproduct2.assignId(3);

        Invoice invoice = new Invoice("NewRequest",branch);
        invoice.addProductInvoice(product1,100,Destination.STOCK);
        invoice.addProductInvoice(product2,100,Destination.DIRECT);
        invoice.addProductInvoice(product3,100,Destination.DIRECT);


        InvoiceRepository invoiceRepository = new InvoiceRepository();
        InvoiceService invoiceService = new InvoiceService(branchProductRepository,invoiceRepository);
        Invoice invoice1 = invoiceService.findByIdInvoice(8);
        System.out.println(invoice1);
        invoiceService.reverseInvoice(invoice1);


    }


}
