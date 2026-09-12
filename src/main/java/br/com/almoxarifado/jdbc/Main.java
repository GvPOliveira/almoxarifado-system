package br.com.almoxarifado.jdbc;


import br.com.almoxarifado.model.*;

import java.util.List;
import java.util.UUID;


public class Main {

    public static void main(String[] args) {
        BranchProductRepository branchProductRepository = new BranchProductRepository();
       /* BranchProduct branchProduct = branchProductRepository.findBranchProduct(2);
        System.out.println(branchProduct);
        System.out.println("\n\n");
        List<Movement> movementList = branchProduct.getMovementList();
        String out = "";
        for (Movement m : movementList) {
            out += m + "\n";
        }
        System.out.println(out);*/

        ProductRepository productRepository = new ProductRepository();
        BranchRepository branchRepository = new BranchRepository();

        Product product = productRepository.findByCode("999");
        Branch branch = branchRepository.findByCode("030");

        Movement movement = new Movement(150, MovementType.ENTRY, OriginType.INVOICE, "500");

        BranchProduct branchProduct = new BranchProduct(product, branch, 150, OriginType.INVOICE, "500");

        boolean result = branchProductRepository.save(branchProduct, movement);

        System.out.println(result);


    }


}
