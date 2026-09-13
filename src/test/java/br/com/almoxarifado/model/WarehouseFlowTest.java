/*package br.com.almoxarifado.model;

import br.com.almoxarifado.exception.InsufficientStockException;
import br.com.almoxarifado.model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WarehouseFlowTest {

    @Test
    void invoiceToStockThenRequestAttendance() {
        Branch branchSouth = new Branch("001", "Branch South");
        Invoice invoice = new Invoice("123", branchSouth);
        Product product1 = new Product("1", "Parafuso 1/2 x 1");
        Product product2 = new Product("2", "Parafuso 1/2 x 2");
        invoice.addProductInvoice(product1, 100, Destination.STOCK);
        invoice.addProductInvoice(product2, 100, Destination.STOCK);
        invoice.processInvoice();
        assertEquals(100, invoice.getProductInvoiceView().get(0).getQuantity());
        assertEquals(100, invoice.getProductInvoiceView().get(1).getQuantity());
        assertEquals(2, invoice.getProductInvoiceView().size());
        assertTrue(invoice.isProcessed());
        Request request = new Request("123", branchSouth);
        request.addProductRequest(product1, 120);
        request.addProductRequest(product2, 120);
        request.attendedProduct("1", 100);
        request.attendedProduct("2", 100);
        assertEquals(2, request.getProductRequestMap().size());
        assertEquals(120, request.getProductRequestMap().get("1").getRequestedQuantity());
        assertEquals(branchSouth.findBranchProduct("1"), request.getProductRequestMap().get("1").getBranchProduct());
        assertEquals(120, request.getProductRequestMap().get("2").getRequestedQuantity());
        assertEquals(branchSouth.findBranchProduct("2"), request.getProductRequestMap().get("2").getBranchProduct());
        assertEquals(0, request.getProductRequestMap().get("1").getBranchProduct().getQuantity());
        assertEquals(0, request.getProductRequestMap().get("2").getBranchProduct().getQuantity());
        assertEquals(100, request.getProductRequestMap().get("1").getAttendedQuantity());
        assertEquals(100, request.getProductRequestMap().get("2").getAttendedQuantity());
        assertTrue(request.getProductRequestMap().get("1").isProcessed());
        assertTrue(request.getProductRequestMap().get("2").isProcessed());
        assertFalse(request.getProductRequestMap().get("1").isReversed());
        assertFalse(request.getProductRequestMap().get("2").isReversed());
    }

    @Test
    void invoiceToStockThenRequestReversal() {
        Branch branchSouth = new Branch("001", "Branch South");
        Invoice invoice = new Invoice("123", branchSouth);
        Product product1 = new Product("1", "Parafuso 1/2 x 1");
        Product product2 = new Product("2", "Parafuso 1/2 x 2");
        invoice.addProductInvoice(product1, 100, Destination.STOCK);
        invoice.addProductInvoice(product2, 100, Destination.STOCK);
        invoice.processInvoice();
        assertEquals(100, invoice.getProductInvoiceView().get(0).getQuantity());
        assertEquals(100, invoice.getProductInvoiceView().get(1).getQuantity());
        assertEquals(2, invoice.getProductInvoiceView().size());
        assertTrue(invoice.isProcessed());
        Request request = new Request("123", branchSouth);
        request.addProductRequest(product1, 100);
        request.addProductRequest(product2, 100);
        request.attendedProduct("1", 100);
        request.attendedProduct("2", 100);
        assertEquals(0, branchSouth.findBranchProduct("1").getQuantity());
        assertEquals(0, branchSouth.findBranchProduct("2").getQuantity());
        assertTrue(request.findProductRequest("1").isProcessed());
        assertFalse(request.findProductRequest("1").isReversed());
        assertTrue(request.findProductRequest("2").isProcessed());
        assertFalse(request.findProductRequest("2").isReversed());
        request.reversalProduct("1");
        request.reversalProduct("2");
        assertEquals(100, branchSouth.findBranchProduct("1").getQuantity());
        assertEquals(100, branchSouth.findBranchProduct("2").getQuantity());
        assertTrue(request.findProductRequest("1").isProcessed());
        assertTrue(request.findProductRequest("1").isReversed());
        assertTrue(request.findProductRequest("2").isProcessed());
        assertTrue(request.findProductRequest("2").isReversed());
    }

    @Test
    void directInvoiceDoesNotAddProductToStock() {
        Branch branchSouth = new Branch("001", "Branch South");
        Invoice invoice = new Invoice("123", branchSouth);
        Product product1 = new Product("1", "Parafuso 1/2 x 1");
        Product product2 = new Product("2", "Parafuso 1/2 x 2");
        invoice.addProductInvoice(product1, 100, Destination.DIRECT);
        invoice.addProductInvoice(product2, 100, Destination.DIRECT);
        invoice.processInvoice();
        assertEquals(2, invoice.getProductInvoiceView().size());
        assertTrue(invoice.isProcessed());
        assertEquals(Destination.DIRECT, invoice.getProductInvoiceView().get(0).getDestination());
        assertEquals(Destination.DIRECT, invoice.getProductInvoiceView().get(1).getDestination());
        assertEquals(0, branchSouth.getProducts().size());
    }

    @Test
    void requestCannotRemoveMoreThanAvailableStock() {
        Branch branchSouth = new Branch("001", "Branch South");
        Invoice invoice = new Invoice("123", branchSouth);
        Product product1 = new Product("1", "Parafuso 1/2 x 1");
        Product product2 = new Product("2", "Parafuso 1/2 x 2");
        invoice.addProductInvoice(product1, 100, Destination.STOCK);
        invoice.addProductInvoice(product2, 100, Destination.STOCK);
        invoice.processInvoice();
        assertEquals(100, invoice.getProductInvoiceView().get(0).getQuantity());
        assertEquals(100, invoice.getProductInvoiceView().get(1).getQuantity());
        assertEquals(2, invoice.getProductInvoiceView().size());
        assertTrue(invoice.isProcessed());
        Request request = new Request("123", branchSouth);
        request.addProductRequest(product1, 150);
        request.addProductRequest(product2, 150);
        assertThrows(InsufficientStockException.class, () ->
                request.attendedProduct("1", 150));
        assertThrows(InsufficientStockException.class, () ->
                request.attendedProduct("2", 150));
        assertEquals(100,branchSouth.findBranchProduct("1").getQuantity());
        assertEquals(100,branchSouth.findBranchProduct("2").getQuantity());
        assertFalse(request.findProductRequest("1").isProcessed());
        assertFalse(request.findProductRequest("2").isProcessed());
        assertEquals(0, request.findProductRequest("1").getAttendedQuantity());
        assertEquals(0, request.findProductRequest("2").getAttendedQuantity());
    }
}*/