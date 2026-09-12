package br.com.almoxarifado.jdbc;

import br.com.almoxarifado.exception.BranchProductNotFoundException;
import br.com.almoxarifado.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class BranchProductRepositoryTest {

    private BranchProductRepository branchProductRepository;

    @BeforeEach
    void setUp() {
        branchProductRepository = new BranchProductRepository();
    }

    @Test
    void shouldReturnProductWithAllMovements() {

        BranchProduct branchProduct = branchProductRepository.findBranchProduct(2);

        assertNotNull(branchProduct);
        assertNotNull(branchProduct.getMovementList());
        assertEquals(2, branchProduct.getId());
        assertEquals(2, branchProduct.getMovementList().size());
    }


    @Test
    void shouldReturnProductWithEmptyMovementListWhenNoMovementsExist() {

        BranchProduct branchProduct = branchProductRepository.findBranchProduct(3);

        assertNotNull(branchProduct);
        assertNotNull(branchProduct.getMovementList());
        assertTrue(branchProduct.getMovementList().isEmpty());
    }


    @Test
    void shouldThrowExceptionWhenProductIsNotFound() {

        assertThrows(BranchProductNotFoundException.class, () ->
                branchProductRepository.findBranchProduct(999));
    }

    @Test
    void addQuantityBranchProductRepository() {
        BranchProduct branchProduct = branchProductRepository.findBranchProduct(2);
        Movement movement = new Movement(100, MovementType.ENTRY, OriginType.INVOICE, "500");
        boolean rows = branchProductRepository.addQuantity(branchProduct, movement);
        assertTrue(rows);
        //não sei o que validar mais, por que todas as outras informações alteram quando eu rodo o teste...
    }


    @Test
    void cannotAddQuantityWithBranchProductNotFound() {
        BranchProduct branchProduct = new BranchProduct(new Product("1", "Parafuso"),
                new Branch("001", "South"), 100, OriginType.INVOICE, "300");
        Movement movement = new Movement(100, MovementType.ENTRY, OriginType.INVOICE, "500");
        assertThrows(BranchProductNotFoundException.class, () ->
                branchProductRepository.addQuantity(branchProduct, movement));
    }


}
