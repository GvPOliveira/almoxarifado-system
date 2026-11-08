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

}
