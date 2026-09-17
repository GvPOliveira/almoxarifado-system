package br.com.almoxarifado.model;

import br.com.almoxarifado.exception.InvalidQuantityException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RequestTest {


    @Test
    void createRequest() {
        Branch branchSouth = new Branch("001", "Branch South");
        Request request = new Request("155", branchSouth);
        assertEquals("155", request.getNumberRequest());
        assertEquals("Branch South", request.getBranch().getName());
        assertEquals(0, request.getProductRequestMap().size());
    }

    @Test
    void addProductRequest() {
        Product newProduct = new Product("1", "Parafuso 1/2 x 1");
        Branch branchSouth = new Branch("001", "Branch South");
        BranchProduct branchProduct = new BranchProduct(newProduct, branchSouth, 100, OriginType.INVOICE, "1234");
        branchSouth.addProduct(branchProduct);
        Request request = new Request("155", branchSouth);
        request.addProductRequest(newProduct, 100);
        assertEquals(1, request.getProductRequestMap().size());
        assertEquals(100, request.getProductRequestMap().get("1").getRequestedQuantity());
        assertEquals(branchProduct, request.getProductRequestMap().get("1").getBranchProduct());
    }

    @Test
    void addRepeatProduct() {
        Product newProduct = new Product("1", "Parafuso 1/2 x 1");
        Branch branchSouth = new Branch("001", "Branch South");
        Request request = new Request("155", branchSouth);
        BranchProduct branchProduct = new BranchProduct(newProduct, branchSouth, 100, OriginType.INVOICE, "1234");
        branchSouth.addProduct(branchProduct);
        request.addProductRequest(newProduct, 100);
        request.addProductRequest(newProduct, 50);
        assertEquals(1, request.getProductRequestMap().size());
        assertEquals(150, request.getProductRequestMap().get("1").getRequestedQuantity());
    }


    @Test
    void addQuantityZero() {
        Product newProduct = new Product("1", "Parafuso 1/2 x 1");
        Branch branchSouth = new Branch("001", "Branch South");
        BranchProduct branchProduct = new BranchProduct(newProduct, branchSouth, 100, OriginType.INVOICE, "1234");
        branchSouth.addProduct(branchProduct);
        Request request = new Request("155", branchSouth);
        assertThrows(InvalidQuantityException.class, () ->
                request.addProductRequest(newProduct, 0));

        assertEquals(0, request.getProductRequestMap().size());
    }

    @Test
    void addInvalidQuantity() {
        Product newProduct = new Product("1", "Parafuso 1/2 x 1");
        Branch branchSouth = new Branch("001", "Branch South");
        BranchProduct branchProduct = new BranchProduct(newProduct, branchSouth, 100, OriginType.INVOICE, "1234");
        branchSouth.addProduct(branchProduct);
        Request request = new Request("155", branchSouth);
        assertThrows(InvalidQuantityException.class, () ->
                request.addProductRequest(newProduct, -50));
        assertEquals(0, request.getProductRequestMap().size());
    }

    @Test
    void findProductRequest() {
        Product newProduct = new Product("1", "Parafuso 1/2 x 1");
        Product newProduct2 = new Product("2", "Parafuso 1/2 x 2");
        Branch branchSouth = new Branch("001", "Branch South");
        Request request = new Request("155", branchSouth);
        BranchProduct branchProduct = new BranchProduct(newProduct, branchSouth, 100, OriginType.INVOICE, "1234");
        BranchProduct branchProduct2 = new BranchProduct(newProduct2, branchSouth, 100, OriginType.INVOICE, "1234");
        branchSouth.addProduct(branchProduct);
        branchSouth.addProduct(branchProduct2);
        request.addProductRequest(newProduct, 100);
        request.addProductRequest(newProduct2, 100);
        assertEquals(request.getProductRequestMap().get("1"), request.findProductRequest("1"));
        assertNull(request.findProductRequest("999"));
    }

    /*
        @Test
        void attendedProductRequest() {
            Product newProduct = new Product("1", "Parafuso 1/2 x 1");
            Branch branchSouth = new Branch("001", "Branch South");
            Request request = new Request("155", branchSouth);
            BranchProduct branchProduct = new BranchProduct(newProduct, branchSouth, 100, OriginType.INVOICE, "1234");
            branchSouth.addProduct(branchProduct);
            request.addProductRequest(newProduct, 80);
            request.attendedProduct("1", 50);
            assertEquals(50, request.findProductRequest("1").getBranchProduct().getQuantity());
            assertEquals(80, request.findProductRequest("1").getRequestedQuantity());
            assertEquals(50, request.findProductRequest("1").getAttendedQuantity());
            assertTrue(request.findProductRequest("1").isProcessed());
            assertFalse(request.findProductRequest("1").isReversed());
        }

        @Test
        void productNotFound() {
            Product newProduct = new Product("1", "Parafuso 1/2 x 1");
            Branch branchSouth = new Branch("001", "Branch South");
            Request request = new Request("155", branchSouth);
            BranchProduct branchProduct = new BranchProduct(newProduct, branchSouth, 100, OriginType.INVOICE, "1234");
            branchSouth.addProduct(branchProduct);
            request.addProductRequest(newProduct, 80);
            assertThrows(ProductNotFoundInRequestException.class, () ->
                    request.attendedProduct("999", 50));
            assertEquals(100, request.findProductRequest("1").getBranchProduct().getQuantity());
            assertEquals(0, request.findProductRequest("1").getAttendedQuantity());
            assertEquals(false, request.findProductRequest("1").isProcessed());
        }

        @Test
        void reversalProductRequest() {
            Product newProduct = new Product("1", "Parafuso 1/2 x 1");
            Branch branchSouth = new Branch("001", "Branch South");
            Request request = new Request("155", branchSouth);
            BranchProduct branchProduct = new BranchProduct(newProduct, branchSouth, 100, OriginType.INVOICE, "1234");
            branchSouth.addProduct(branchProduct);
            request.addProductRequest(newProduct, 80);
            request.attendedProduct("1", 50);
            request.reversalProduct("1");
            assertEquals(100, request.findProductRequest("1").getBranchProduct().getQuantity());
            assertEquals(80, request.findProductRequest("1").getRequestedQuantity());
            assertEquals(50, request.findProductRequest("1").getAttendedQuantity());
            assertEquals(true, request.findProductRequest("1").isProcessed());
            assertEquals(true, request.findProductRequest("1").isReversed());
        }

        @Test
        void reversalProductRequestCodeNotFound() {
            Product newProduct = new Product("1", "Parafuso 1/2 x 1");
            Branch branchSouth = new Branch("001", "Branch South");
            Request request = new Request("155", branchSouth);
            BranchProduct branchProduct = new BranchProduct(newProduct, branchSouth, 100, OriginType.INVOICE, "1234");
            branchSouth.addProduct(branchProduct);
            request.addProductRequest(newProduct, 80);
            request.attendedProduct("1", 50);
            assertThrows(ProductNotFoundInRequestException.class, () ->
                    request.reversalProduct("999"));
            assertEquals(50, request.findProductRequest("1").getBranchProduct().getQuantity());
            assertEquals(80, request.findProductRequest("1").getRequestedQuantity());
            assertEquals(50, request.findProductRequest("1").getAttendedQuantity());
            assertEquals(true, request.findProductRequest("1").isProcessed());
            assertEquals(false, request.findProductRequest("1").isReversed());
        }

    @Test
    void cannotAddProductNotFoundInBranch() {
        Product newProduct = new Product("1", "Parafuso 1/2 x 1");
        Branch branchSouth = new Branch("001", "Branch South");
        Request request = new Request("155", branchSouth);
        assertThrows(ProductNotFoundInBranchException.class, () ->
                request.addProductRequest(newProduct, 80));
        assertEquals(0, request.getProductRequestMap().size());
    }

    @Test
    void canRequestProductWithZeroStock() {
        Product newProduct = new Product("1", "Parafuso 1/2 x 1");
        Branch branchSouth = new Branch("001", "Branch South");
        Request request = new Request("155", branchSouth);
        BranchProduct branchProduct = new BranchProduct(newProduct, branchSouth, 100, OriginType.INVOICE, "1234");
        branchSouth.addProduct(branchProduct);
        request.addProductRequest(newProduct, 100);
        request.attendedProduct("1", 100);
        assertEquals(0, branchProduct.getQuantity());
        Request request2 = new Request("123", branchSouth);
        request2.addProductRequest(newProduct, 100);
        assertEquals(100, request2.getProductRequestMap().get("1").getRequestedQuantity());
        assertEquals(0, request2.getProductRequestMap().get("1").getAttendedQuantity());
        request2.attendedProduct("1", 0);
        assertEquals(0, request2.getProductRequestMap().get("1").getAttendedQuantity());
    }

    @Test
    void cannotAddProductRequestAfterAttended() {
        Product newProduct = new Product("1", "Parafuso 1/2 x 1");
        Branch branchSouth = new Branch("001", "Branch South");
        Request request = new Request("155", branchSouth);
        BranchProduct branchProduct = new BranchProduct(newProduct, branchSouth, 100, OriginType.INVOICE, "1234");
        branchSouth.addProduct(branchProduct);
        request.addProductRequest(newProduct, 100);
        request.attendedProduct("1", 100);
        assertTrue(request.findProductRequest("1").isProcessed());
        assertThrows(ProductRequestAlreadyProcessedException.class, () ->
                request.addProductRequest(newProduct, 100));
        assertEquals(100, request.findProductRequest("1").getRequestedQuantity());


    }

*/

}
