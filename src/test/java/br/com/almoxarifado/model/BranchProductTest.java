package br.com.almoxarifado.model;

import br.com.almoxarifado.exception.InsufficientStockException;
import br.com.almoxarifado.exception.InvalidQuantityException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BranchProductTest {
    @Test
    void addQuantity() {
        Product newProduct = new Product("1", "Parafuso 1/2 x 1");
        Branch branchSouth = new Branch("001", "Branch South");
        BranchProduct branchProduct = new BranchProduct(newProduct, branchSouth, 100,
                OriginType.INVOICE, "001");
        branchProduct.addQuantity(50, OriginType.INVOICE, "002");
        assertEquals(150, branchProduct.getQuantity());
    }

    @Test
    void addQuantityException() {
        Product newProduct = new Product("1", "Parafuso 1/2 x 1");
        Branch branchSouth = new Branch("001", "Branch South");
        BranchProduct branchProduct = new BranchProduct(newProduct, branchSouth, 100, OriginType.INVOICE, "002");
        assertThrows(InvalidQuantityException.class, () -> {
            branchProduct.addQuantity(0, OriginType.INVOICE, "123");
        });
    }

    @Test
    void addQuantityNegative() {
        Product newProduct = new Product("1", "Parafuso 1/2 x 1");
        Branch branchSouth = new Branch("001", "Branch South");
        BranchProduct branchProduct = new BranchProduct(newProduct, branchSouth, 100, OriginType.INVOICE, "122");
        assertThrows(InvalidQuantityException.class, () -> {
            branchProduct.addQuantity(-50, OriginType.INVOICE, "123");
        });
    }


    @Test
    void removeQuantity() {
        Product newProduct = new Product("1", "Parafuso 1/2 x 1");
        Branch branchSouth = new Branch("001", "Branch South");
        BranchProduct branchProduct = new BranchProduct(newProduct, branchSouth, 100, OriginType.INVOICE, "122");
        branchProduct.removeQuantity(70, OriginType.REQUEST, "123");
        assertEquals(30, branchProduct.getQuantity());
        assertThrows(InvalidQuantityException.class, () ->
                branchProduct.removeQuantity(-50, OriginType.REQUEST, "123"));
    }

    @Test
    void removeZeroQuantity() {
        Product newProduct = new Product("1", "Parafuso 1/2 x 1");
        Branch branchSouth = new Branch("001", "Branch South");
        BranchProduct branchProduct = new BranchProduct(newProduct, branchSouth, 100, OriginType.INVOICE, "1234");
        branchProduct.removeQuantity(0, OriginType.REQUEST, "123");
        assertEquals(100, branchProduct.getQuantity());
        branchProduct.removeQuantity(100, OriginType.REQUEST, "123");
        assertEquals(0, branchProduct.getQuantity());
    }

    @Test
    void removeQuantityExceedsStock() {
        Product newProduct = new Product("1", "Parafuso 1/2 x 1");
        Branch branchSouth = new Branch("001", "Branch South");
        BranchProduct branchProduct = new BranchProduct(newProduct, branchSouth, 100, OriginType.INVOICE, "1234");
        assertThrows(InsufficientStockException.class, () -> {
            branchProduct.removeQuantity(150, OriginType.REQUEST, "123");
        });
    }

    @Test
    void movementListSize() {
        Product newProduct = new Product("1", "Parafuso 1/2 x 1");
        Branch branchSouth = new Branch("001", "Branch South");
        BranchProduct branchProduct = new BranchProduct(newProduct, branchSouth,
                100, OriginType.INVOICE, "1234");
        branchProduct.addQuantity(50, OriginType.INVOICE, "123");
        assertEquals(2, branchProduct.getMovementList().size());
        assertEquals(100, branchProduct.getMovementList().get(0).getQuantity());
        assertEquals(50, branchProduct.getMovementList().get(1).getQuantity());
    }

    @Test
    void movimentsResults() {
        Product newProduct = new Product("1", "Parafuso 1/2 x 1");
        Branch branchSouth = new Branch("001", "Branch South");
        BranchProduct branchProduct = new BranchProduct(newProduct, branchSouth, 100, OriginType.INVOICE, "1234");
        branchProduct.addQuantity(50, OriginType.INVOICE, "123");
        assertEquals(OriginType.INVOICE, branchProduct.getMovementList().get(1).getOriginType());
        assertEquals("123", branchProduct.getMovementList().get(1).getOriginNumber());
    }

    @Test
    void removeQuantityMoviments() {
        Product newProduct = new Product("1", "Parafuso 1/2 x 1");
        Branch branchSouth = new Branch("001", "Branch South");
        BranchProduct branchProduct = new BranchProduct(newProduct, branchSouth, 100, OriginType.INVOICE, "1234");
        branchProduct.removeQuantity(50, OriginType.REQUEST, "123");
        assertEquals(OriginType.REQUEST, branchProduct.getMovementList().get(1).getOriginType());
        assertEquals("123", branchProduct.getMovementList().get(1).getOriginNumber());
        assertEquals(MovementType.OUTPUT, branchProduct.getMovementList().get(1).getMovementType());
        assertEquals(50, branchProduct.getMovementList().get(1).getQuantity());
    }

    @Test
    void createBranchProductWithZeroQuantity() {
        Product newProduct = new Product("1", "Parafuso 1/2 x 1");
        Branch branchSouth = new Branch("001", "Branch South");
        assertThrows(InvalidQuantityException.class, () ->
                new BranchProduct(newProduct, branchSouth, -10, OriginType.INVOICE, "1234"));
    }


}
