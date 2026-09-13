/*package br.com.almoxarifado.model;

import br.com.almoxarifado.exception.CannotProcessInvoiceWithoutProductsException;
import br.com.almoxarifado.exception.InvalidQuantityException;
import br.com.almoxarifado.exception.InvoiceAlreadyProcessedException;
import br.com.almoxarifado.jdbc.BranchProductRepository;
import br.com.almoxarifado.model.*;
import br.com.almoxarifado.service.InvoiceService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InvoiceTest {

    @Test
    void createInvoice() {
        Branch branchSouth = new Branch("001", "Branch South");
        Invoice invoice = new Invoice("123", branchSouth);
        assertEquals("123", invoice.getNumberInvoice());
        assertEquals("Branch South", invoice.getBranchDestination().getName());
        assertNotNull(invoice.getDate());
        assertEquals(0, invoice.getProductInvoiceView().size());
        assertFalse(invoice.isProcessed());
    }

    @Test
    void addProductInvoice() {
        Product newProduct = new Product("1", "Parafuso 1/2 x 1");
        Branch branchSouth = new Branch("001", "Branch South");
        Invoice invoice = new Invoice("123", branchSouth);
        invoice.addProductInvoice(newProduct, 100, Destination.STOCK);
        assertEquals(100, invoice.getProductInvoiceView().get(0).getQuantity());
        assertEquals("1", invoice.getProductInvoiceView().get(0).getProduct().getCode());
        assertEquals(Destination.STOCK, invoice.getProductInvoiceView().get(0).getDestination());
    }

    @Test
    void addProductInvoiceWithZeroQuantity() {
        Product newProduct = new Product("1", "Parafuso 1/2 x 1");
        Branch branchSouth = new Branch("001", "Branch South");
        Invoice invoice = new Invoice("123", branchSouth);
        assertThrows(InvalidQuantityException.class, () ->
                invoice.addProductInvoice(newProduct, 0, Destination.STOCK));
        assertEquals(0, invoice.getProductInvoiceView().size());
    }

    @Test
    void addProductInvoiceWithNegativeQuantity() {
        Product newProduct = new Product("1", "Parafuso 1/2 x 1");
        Branch branchSouth = new Branch("001", "Branch South");
        Invoice invoice = new Invoice("123", branchSouth);
        assertThrows(InvalidQuantityException.class, () ->
                invoice.addProductInvoice(newProduct, -50, Destination.STOCK));
        assertEquals(0, invoice.getProductInvoiceView().size());
    }

    @Test
    void cannotProcessInvoiceWithoutProducts() {
        BranchProductRepository branchProductRepository = new BranchProductRepository();
        InvoiceService invoiceService = new InvoiceService(branchProductRepository);
        Branch branchSouth = new Branch("001", "Branch South");
        Invoice invoice = new Invoice("123", branchSouth);
        assertThrows(CannotProcessInvoiceWithoutProductsException.class, () ->
                invoiceService.processInvoice(invoice));
        assertFalse(invoice.isProcessed());
    }

    @Test
    void cannotAddProductInvoiceWithInvoiceProcessed() {
        Product newProduct = new Product("1", "Parafuso 1/2 x 1");
        Branch branchSouth = new Branch("001", "Branch South");
        Invoice invoice = new Invoice("123", branchSouth);
        invoice.addProductInvoice(newProduct, 100, Destination.STOCK);
        invoice.processInvoice();
        assertEquals(1, invoice.getProductInvoiceView().size());
        assertThrows(InvoiceAlreadyProcessedException.class, () ->
                invoice.addProductInvoice(newProduct,100,Destination.STOCK));
        assertEquals(1, invoice.getProductInvoiceView().size());
    }


}
*/