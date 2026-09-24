package br.com.almoxarifado.model;

import br.com.almoxarifado.exception.BranchAlreadyExistsException;
import br.com.almoxarifado.exception.InvalidBranchIdException;
import br.com.almoxarifado.exception.InvalidProductIdException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BranchTest {

    @Test
    void createBranch() {
        Branch branchSouth = new Branch("001", "Branch South");
        assertEquals("001", branchSouth.getCode());
        assertEquals("Branch South", branchSouth.getName());
        assertEquals(0, branchSouth.getProducts().size());
        assertEquals(0, branchSouth.getId());
    }

    @Test
    void addBranchProduct() {
        Branch branchSouth = new Branch("001", "Branch South");
        Product newProduct = new Product("1", "Parafuso 1/2 x 1");
        BranchProduct branchProduct = new BranchProduct(newProduct, branchSouth, 100, OriginType.INVOICE, "1234");
        branchSouth.addProduct(branchProduct);
        assertEquals(branchProduct, branchSouth.getProducts().get(0));
        assertEquals(branchProduct, branchSouth.findBranchProduct("1"));
        assertEquals(1, branchSouth.getProducts().size());
    }

    @Test
    void findProductNotFound() {
        Branch branchSouth = new Branch("001", "Branch South");
        Product newProduct = new Product("1", "Parafuso 1/2 x 1");
        BranchProduct branchProduct = new BranchProduct(newProduct, branchSouth, 100, OriginType.INVOICE, "1234");
        branchSouth.addProduct(branchProduct);
        assertEquals(null, branchSouth.findBranchProduct("001"));
        assertEquals(1, branchSouth.getProducts().size());
    }

    @Test
    void addProductRepeat() {
        Branch branchSouth = new Branch("001", "Branch South");
        Product newProduct = new Product("1", "Parafuso 1/2 x 1");
        BranchProduct branchProduct = new BranchProduct(newProduct, branchSouth, 100, OriginType.INVOICE, "1234");
        branchSouth.addProduct(branchProduct);
        assertFalse(branchSouth.addProduct(branchProduct));
        assertEquals(1, branchSouth.getProducts().size());
    }

    @Test
    void listUnmodifiable() {
        Branch branchSouth = new Branch("001", "Branch South");
        Product newProduct = new Product("1", "Parafuso 1/2 x 1");
        BranchProduct branchProduct = new BranchProduct(newProduct, branchSouth, 100, OriginType.INVOICE, "1234");
        branchSouth.addProduct(branchProduct);
        assertThrows(UnsupportedOperationException.class, () ->
                branchSouth.getProducts().clear());
        assertEquals(1, branchSouth.getProducts().size());
    }

    @Test
    void receiveProducts() {
        Branch branchSouth = new Branch("001", "Branch South");
        Product newProduct = new Product("1", "Parafuso 1/2 x 1");
        assertEquals(0, branchSouth.getProducts().size());
        branchSouth.receiveProduct(newProduct, 100, OriginType.INVOICE, "1234");
        assertEquals(1, branchSouth.getProducts().size());
        assertEquals(100, branchSouth.getProducts().get(0).getQuantity());
        assertEquals(OriginType.INVOICE, branchSouth.getProducts().get(0).getMovementList().get(0).getOriginType());

    }

    @Test
    void receiveExistingProduct() {
        Branch branchSouth = new Branch("001", "Branch South");
        Product newProduct = new Product("1", "Parafuso 1/2 x 1");
        assertEquals(0, branchSouth.getProducts().size());
        branchSouth.receiveProduct(newProduct, 100, OriginType.INVOICE, "1234");
        assertEquals(1, branchSouth.getProducts().size());
        assertEquals(100, branchSouth.getProducts().get(0).getQuantity());
        assertEquals(OriginType.INVOICE, branchSouth.getProducts().get(0).getMovementList().get(0).getOriginType());
        branchSouth.receiveProduct(newProduct, 50, OriginType.INVOICE, "12345");
        assertEquals(1, branchSouth.getProducts().size());
        assertEquals(150, branchSouth.getProducts().get(0).getQuantity());
        assertEquals(OriginType.INVOICE, branchSouth.getProducts().get(0).getMovementList().get(1).getOriginType());
        assertEquals(50, branchSouth.getProducts().get(0).getMovementList().get(1).getQuantity());
        assertEquals("12345", branchSouth.getProducts().get(0).getMovementList().get(1).getOriginNumber());
    }

    @Test
    void assignIdBranchAlreadyExists() {
        Branch branchSouth = new Branch("001", "Branch South");
        assertEquals(0, branchSouth.getId());
        branchSouth.assignId(28);
        assertEquals(28, branchSouth.getId());
        assertThrows(BranchAlreadyExistsException.class, () ->
                branchSouth.assignId(28));
    }


    @Test
    void assignIdWithInvalidId() {
        Branch branchSouth = new Branch("001", "Branch South");
        assertThrows(InvalidBranchIdException.class, () ->
                branchSouth.assignId(0));
        assertThrows(InvalidBranchIdException.class, () ->
                branchSouth.assignId(-10));
    }


}
