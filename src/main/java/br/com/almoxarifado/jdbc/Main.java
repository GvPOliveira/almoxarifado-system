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
//        BranchProductRepository branchProductRepository = new BranchProductRepository();
//       /* BranchProduct branchProduct = branchProductRepository.findBranchProduct(2);
//        System.out.println(branchProduct);
//        System.out.println("\n\n");
//        List<Movement> movementList = branchProduct.getMovementList();
//        String out = "";
//        for (Movement m : movementList) {
//            out += m + "\n";
//        }
//        System.out.println(out);*/
//
//    /*   ProductRepository productRepository = new ProductRepository();
//        BranchRepository branchRepository = new BranchRepository();
//
//        Product product = productRepository.findByCode("999");
//        Branch branch = branchRepository.findByCode("030");
//
//        Movement movement = new Movement(150, MovementType.ENTRY, OriginType.INVOICE, "500");
//
//        BranchProduct branchProduct = new BranchProduct(product, branch, 150, OriginType.INVOICE, "500");
//
//        boolean result = branchProductRepository.save(branchProduct, movement);
//
//        System.out.println(result);*/
//        Product product1 = new Product("001", "Caneta Azul");
//        product1.assignId(17);
//        Product product2 = new Product("002", "Parafuso 1/2");
//        product2.assignId(18);
//        Product product3 = new Product("005", "Parafuso 3/4 x 2");
//        product3.assignId(21);
//        Product product4 = new Product("999", "Parafuso");
//        Branch branch = new Branch("01", "Filial Sul");
//        branch.assignId(01);
//
//        BranchProduct newBranchproduct = new BranchProduct(product1, branch, 60,
//                OriginType.INVOICE, "006");
//        newBranchproduct.assignId(1);
//
//        branch.addProduct(newBranchproduct);
//
//        //   BranchProduct branchProduct2 = BranchProduct.reconstructor(2,product2,branch,60,"");
//        //  BranchProduct branchProduct3 = BranchProduct.reconstructor(3,product3,branch,60,"");
//
//
//        RequestService requestService = new RequestService(branchProductRepository);
//
//        Request request = new Request("118", branch);
//
//        request.addProductRequest(product1, 120);
//        System.out.println("Antes: " +
//                request.getProductRequestMap().get("001").isProcessed());
//        requestService.attendedProduct(request, "001", 30);
//        requestService.reversalProduct(request, "001");
//        requestService.reversalProduct(request, "001");
//
//
//
//        // BranchProduct branchProduct = branchProductRepository.findBranchProduct(34, 3);
//        //  System.out.println(branchProduct);

        Branch branch = new Branch("01", "Filial Sul");
        branch.assignId(1);
        Product product1 = new Product("001", "Caneta Azul");
        product1.assignId(17);
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

        Request request = new Request("0000", branch);

        ProductRequest productRequest1 = new ProductRequest(request, newBranchproduct, 40);
        request.addProductRequestList(productRequest1);
        ProductRequest productRequest2 = new ProductRequest(request, newBranchproduct1, 40);
        request.addProductRequestList(productRequest2);
        ProductRequest productRequest3 = new ProductRequest(request, newBranchproduct2, 40);
        request.addProductRequestList(productRequest3);

        RequestRepository requestRepository = new RequestRepository();

        DatabaseConnection databaseConnection = new DatabaseConnection();
        try (Connection connection = databaseConnection.connect()) {
            requestRepository.save(request,connection);
            System.out.println(request);
        } catch (SQLException errorConnection) {
            throw new RuntimeException(errorConnection);
        }


    }


}
